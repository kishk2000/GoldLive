package com.goldlive.app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.round

class GoldPriceService : Service() {

    // =========================================================
    // CONFIG
    // =========================================================

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId =
        "gold_live"

    private val notificationId =
        1001

    private val updateInterval =
        2000L

    // =========================================================
    // STATE
    // =========================================================

    @Volatile
    private var running =
        false

    private var workerThread:
        Thread? = null

    private val preferences by lazy {

        getSharedPreferences(
            "gold_live_settings",
            Context.MODE_PRIVATE
        )
    }

    // =========================================================
    // NUMBER FORMAT
    // =========================================================

    private val numberFormat =
        DecimalFormat(
            "#,##0",
            DecimalFormatSymbols(Locale.US)
        )

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate() {

        super.onCreate()

        createNotificationChannel()

        /*
         * مهم جدًا:
         * يجب استدعاء startForeground بسرعة بعد تشغيل الخدمة.
         */

        startForeground(
            notificationId,
            createInitialNotification()
        )

        running =
            true

        startWorker()
    }

    // =========================================================
    // START COMMAND
    // =========================================================

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        /*
         * START_STICKY:
         * إذا أوقف Android الخدمة بسبب ضغط الذاكرة،
         * سيحاول إعادة تشغيلها.
         */

        return START_STICKY
    }

    // =========================================================
    // INITIAL NOTIFICATION
    // =========================================================

    private fun createInitialNotification():
        Notification {

        return NotificationCompat.Builder(
            this,
            channelId
        )
            .setSmallIcon(
                android.R.drawable.ic_dialog_info
            )
            .setContentTitle(
                "GOLD"
            )
            .setContentText(
                "جاري تحديث أسعار الذهب..."
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "جاري تحديث أسعار الذهب..."
                    )
            )
            .setOngoing(
                true
            )
            .setAutoCancel(
                false
            )
            .setOnlyAlertOnce(
                true
            )
            .setSilent(
                true
            )
            .setShowWhen(
                false
            )
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .build()
    }

    // =========================================================
    // START WORKER
    // =========================================================

    private fun startWorker() {

        if (
            workerThread?.isAlive == true
        ) {
            return
        }

        workerThread =
            thread(
                start = true,
                name = "GoldPriceWorker"
            ) {

                while (running) {

                    try {

                        fetchAndUpdate()

                    } catch (
                        e: Exception
                    ) {

                        updateErrorNotification()
                    }

                    /*
                     * ننتظر ثانيتين بعد انتهاء طلب السعر.
                     *
                     * بذلك لا توجد عدة Requests متزامنة.
                     */

                    try {

                        Thread.sleep(
                            updateInterval
                        )

                    } catch (
                        e: InterruptedException
                    ) {

                        break
                    }
                }
            }
    }

    // =========================================================
    // FETCH PRICE
    // =========================================================

    private fun fetchAndUpdate() {

        var connection:
            HttpURLConnection? =
            null

        try {

            connection =
                URL(apiUrl)
                    .openConnection()
                        as HttpURLConnection

            connection.requestMethod =
                "GET"

            connection.connectTimeout =
                7000

            connection.readTimeout =
                7000

            connection.useCaches =
                false

            connection.setRequestProperty(
                "Cache-Control",
                "no-cache"
            )

            connection.connect()

            val response =
                connection
                    .inputStream
                    .bufferedReader()
                    .use {
                        it.readText()
                    }

            val json =
                JSONObject(
                    response
                )

            val success =
                json.optBoolean(
                    "success",
                    false
                )

            if (!success) {
                return
            }

            val ounceUsd =
                json.optDouble(
                    "ounceUsd",
                    0.0
                )

            val gram24 =
                json.optDouble(
                    "gram24",
                    0.0
                )

            val gram21 =
                json.optDouble(
                    "gram21",
                    0.0
                )

            val gram18 =
                json.optDouble(
                    "gram18",
                    0.0
                )

            val gram14 =
                json.optDouble(
                    "gram14",
                    gram18 * 14.0 / 18.0
                )

            updateNotification(
                ounceUsd,
                gram24,
                gram21,
                gram18,
                gram14
            )

        } finally {

            connection?.disconnect()
        }
    }

    // =========================================================
    // UPDATE NOTIFICATION
    // =========================================================

    private fun updateNotification(
        ounceUsd: Double,
        gram24: Double,
        gram21: Double,
        gram18: Double,
        gram14: Double
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permission =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )

            if (
                permission !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val selectedKarat =
            preferences.getInt(
                "selected_karat",
                21
            )

        val price =
            when (selectedKarat) {

                24 ->
                    gram24

                21 ->
                    gram21

                18 ->
                    gram18

                14 ->
                    gram14

                else ->
                    gram21
            }

        if (
            price <= 0.0 &&
            ounceUsd <= 0.0
        ) {
            return
        }

        /*
         * السطر الأول:
         * عيار 21: 6579 ج
         *
         * السطر الثاني:
         * الأونصة: $3350
         */

        val notificationText =
            "عيار $selectedKarat: " +
                formatNumber(price) +
                " ج\n" +
                "الأونصة: $" +
                formatNumber(ounceUsd)

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "GOLD"
                )
                .setContentText(
                    notificationText
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            notificationText
                        )
                )
                .setOngoing(
                    true
                )
                .setAutoCancel(
                    false
                )
                .setOnlyAlertOnce(
                    true
                )
                .setSilent(
                    true
                )
                .setShowWhen(
                    false
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                notificationId,
                notification
            )
    }

    // =========================================================
    // ERROR NOTIFICATION
    // =========================================================

    private fun updateErrorNotification() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permission =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )

            if (
                permission !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "GOLD"
                )
                .setContentText(
                    "جاري تحديث الأسعار..."
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "جاري محاولة تحديث أسعار الذهب..."
                        )
                )
                .setOngoing(
                    true
                )
                .setAutoCancel(
                    false
                )
                .setOnlyAlertOnce(
                    true
                )
                .setSilent(
                    true
                )
                .setShowWhen(
                    false
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                notificationId,
                notification
            )
    }

    // =========================================================
    // CHANNEL
    // =========================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            val existingChannel =
                manager.getNotificationChannel(
                    channelId
                )

            if (
                existingChannel == null
            ) {

                val channel =
                    NotificationChannel(
                        channelId,
                        "GOLD",
                        NotificationManager.IMPORTANCE_LOW
                    )

                channel.description =
                    "أسعار الذهب والأونصة"

                channel.setShowBadge(
                    false
                )

                manager.createNotificationChannel(
                    channel
                )
            }
        }
    }

    // =========================================================
    // FORMAT
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (value <= 0.0) {
            return "--"
        }

        return numberFormat.format(
            round(value)
        )
    }

    // =========================================================
    // BINDER
    // =========================================================

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }

    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        /*
         * لا يوجد cancelNotification() هنا.
         *
         * الإشعار تتم إزالته فقط عندما يطلب المستخدم
         * إيقاف شريط الأسعار من داخل التطبيق.
         */

        running =
            false

        workerThread?.interrupt()

        workerThread =
            null

        super.onDestroy()
    }
}
