package com.goldlive.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.round

class GoldPriceService : Service() {

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId =
        "gold_live"

    private val notificationId =
        1001

    private val executor:
        ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

    private val numberFormat =
        DecimalFormat(
            "#,##0",
            DecimalFormatSymbols(Locale.US)
        )

    private var selectedKarat =
        21

    private var gram24 =
        0.0

    private var gram21 =
        0.0

    private var gram18 =
        0.0

    private var gram14 =
        0.0

    override fun onCreate() {

        super.onCreate()

        createNotificationChannel()

        loadSelectedKarat()

        // يجب أن يصبح Foreground Service مباشرة
        // قبل تنفيذ أي عمل طويل.
        startForeground(
            notificationId,
            createNotification(
                "جاري تحديث السعر..."
            )
        )

        startPriceUpdates()
    }

    // =========================================================
    // START PRICE UPDATES
    // =========================================================

    private fun startPriceUpdates() {

        executor.scheduleWithFixedDelay(
            {

                loadPrices()

            },
            0,
            2,
            TimeUnit.SECONDS
        )
    }

    // =========================================================
    // LOAD SELECTED KARAT
    // =========================================================

    private fun loadSelectedKarat() {

        val preferences =
            getSharedPreferences(
                "gold_live_settings",
                Context.MODE_PRIVATE
            )

        selectedKarat =
            preferences.getInt(
                "selected_karat",
                21
            )
    }

    // =========================================================
    // LOAD PRICES
    // =========================================================

    private fun loadPrices() {

        var connection:
            HttpURLConnection? =
            null

        try {

            // اقرأ العيار كل مرة حتى لو المستخدم
            // غير الاختيار أثناء تشغيل الخدمة.
            loadSelectedKarat()

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

            gram24 =
                json.optDouble(
                    "gram24",
                    0.0
                )

            gram21 =
                json.optDouble(
                    "gram21",
                    0.0
                )

            gram18 =
                json.optDouble(
                    "gram18",
                    0.0
                )

            gram14 =
                json.optDouble(
                    "gram14",
                    gram18 * 14.0 / 18.0
                )

            val price =
                when (selectedKarat) {

                    24 -> gram24

                    21 -> gram21

                    18 -> gram18

                    14 -> gram14

                    else -> gram21
                }

            if (price > 0.0) {

                updateNotification(
                    "عيار $selectedKarat: ${formatNumber(price)} جنيه"
                )
            }

        } catch (
            e: Exception
        ) {

            // لا نوقف الخدمة عند فشل الشبكة.
            // المحاولة التالية بعد ثانيتين.
        } finally {

            connection?.disconnect()
        }
    }

    // =========================================================
    // CREATE CHANNEL
    // =========================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "GOLD",
                    NotificationManager.IMPORTANCE_LOW
                )

            channel.description =
                "سعر الذهب في شريط الإشعارات"

            channel.setShowBadge(
                false
            )

            channel.setSound(
                null,
                null
            )

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    private fun createNotification(
        text: String
    ): Notification {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
            )

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
                text
            )
            .setContentIntent(
                pendingIntent
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
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .setCategory(
                NotificationCompat.CATEGORY_SERVICE
            )
            .setShowWhen(
                false
            )
            .build()
    }

    // =========================================================
    // UPDATE NOTIFICATION
    // =========================================================

    private fun updateNotification(
        text: String
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permission =
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )

            if (
                permission !=
                PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
        }

        val notification =
            createNotification(
                text
            )

        NotificationManagerCompat
            .from(this)
            .notify(
                notificationId,
                notification
            )
    }

    // =========================================================
    // FORMAT NUMBER
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
    // START COMMAND
    // =========================================================

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        loadSelectedKarat()

        return START_STICKY
    }

    // =========================================================
    // BIND
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

        executor.shutdownNow()

        NotificationManagerCompat
            .from(this)
            .cancel(
                notificationId
            )

        super.onDestroy()
    }
}
