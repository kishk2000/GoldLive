package com.goldlive.app

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.Gravity
import android.widget.*
import androidx.core.app.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val api =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId = "gold_live"
    private val notificationId = 1001

    private val handler =
        Handler(Looper.getMainLooper())

    private var karat = 21
    private var ounceUsd = 0.0
    private var usdEgp = 0.0
    private var gram24 = 0.0
    private var gram21 = 0.0
    private var gram18 = 0.0
    private var notificationOn = false

    private lateinit var globalText: TextView
    private lateinit var dollarText: TextView
    private lateinit var localText: TextView
    private lateinit var statusText: TextView
    private lateinit var changeText: TextView
    private lateinit var spinner: Spinner
    private lateinit var notifyButton: Button

    private val usd =
        DecimalFormat("#,##0.00")

    private val egp =
        DecimalFormat("#,##0.00")

    private val updater =
        object : Runnable {

            override fun run() {
                loadPrices()
                handler.postDelayed(
                    this,
                    2000
                )
            }
        }

    override fun onCreate(
        state: Bundle?
    ) {
        super.onCreate(state)

        createChannel()
        requestNotifications()
        createScreen()

        handler.post(updater)
    }

    private fun createScreen() {

        val root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.rgb(10, 10, 12)
        )

        root.setPadding(
            24,
            25,
            24,
            24
        )

        val scroll =
            ScrollView(this)

        val box =
            LinearLayout(this)

        box.orientation =
            LinearLayout.VERTICAL

        val title =
            TextView(this)

        title.text =
            "GOLD LIVE"

        title.textSize =
            30f

        title.setTextColor(
            Color.rgb(
                212,
                175,
                55
            )
        )

        title.gravity =
            Gravity.CENTER

        box.addView(title)

        val subtitle =
            TextView(this)

        subtitle.text =
            "أسعار الذهب لحظة بلحظة"

        subtitle.textSize =
            15f

        subtitle.setTextColor(
            Color.LTGRAY
        )

        subtitle.gravity =
            Gravity.CENTER

        subtitle.setPadding(
            0,
            5,
            0,
            25
        )

        box.addView(subtitle)

        val world =
            TextView(this)

        world.text =
            "🌍 الذهب العالمي"

        world.textSize =
            18f

        world.setTextColor(
            Color.WHITE
        )

        box.addView(world)

        globalText =
            TextView(this)

        globalText.text =
            "جاري التحميل..."

        globalText.textSize =
            32f

        globalText.setTextColor(
            Color.rgb(
                212,
                175,
                55
            )
        )

        globalText.gravity =
            Gravity.CENTER

        globalText.setPadding(
            0,
            15,
            0,
            5
        )

        box.addView(globalText)

        val ounce =
            TextView(this)

        ounce.text =
            "USD / Troy Ounce"

        ounce.textSize =
            13f

        ounce.setTextColor(
            Color.GRAY
        )

        ounce.gravity =
            Gravity.CENTER

        box.addView(ounce)

        dollarText =
            TextView(this)

        dollarText.text =
            "الدولار: -- جنيه"

        dollarText.textSize =
            18f

        dollarText.setTextColor(
            Color.LTGRAY
        )

        dollarText.gravity =
            Gravity.CENTER

        dollarText.setPadding(
            0,
            15,
            0,
            20
        )

        box.addView(dollarText)

        val egypt =
            TextView(this)

        egypt.text =
            "🇪🇬 سعر الذهب في مصر"

        egypt.textSize =
            18f

        egypt.setTextColor(
            Color.WHITE
        )

        box.addView(egypt)

        spinner =
            Spinner(this)

        val names =
            arrayOf(
                "عيار 24",
                "عيار 21",
                "عيار 18"
            )

        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )

        spinner.setSelection(1)

        spinner.onItemSelectedListener =
            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    p: android.widget.AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    p: android.widget.AdapterView<*>?,
                    v: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    karat =
                        when (position) {
                            0 -> 24
                            1 -> 21
                            else -> 18
                        }

                    showLocal()
                    showNotification()
                }
            }

        box.addView(spinner)

        localText =
            TextView(this)

        localText.text =
            "-- ج / جرام"

        localText.textSize =
            29f

        localText.setTextColor(
            Color.WHITE
        )

        localText.gravity =
            Gravity.CENTER

        localText.setPadding(
            0,
            18,
            0,
            5
        )

        box.addView(localText)

        changeText =
            TextView(this)

        changeText.text =
            "—"

        changeText.textSize =
            15f

        changeText.gravity =
            Gravity.CENTER

        box.addView(changeText)

        statusText =
            TextView(this)

        statusText.text =
            "جاري الاتصال..."

        statusText.textSize =
            13f

        statusText.setTextColor(
            Color.GRAY
        )

        statusText.gravity =
            Gravity.CENTER

        statusText.setPadding(
            0,
            10,
            0,
            20
        )

        box.addView(statusText)

        notifyButton =
            Button(this)

        notifyButton.text =
            "🔔 تفعيل شريط الأسعار"

        notifyButton.setOnClickListener {

            notificationOn =
                !notificationOn

            if (notificationOn) {

                notifyButton.text =
                    "🔕 إيقاف شريط الأسعار"

                showNotification()

            } else {

                notifyButton.text =
                    "🔔 تفعيل شريط الأسعار"

                NotificationManagerCompat
                    .from(this)
                    .cancel(
                        notificationId
                    )
            }
        }

        box.addView(
            notifyButton
        )

        val info =
            TextView(this)

        info.text =
            "التحديث: كل ثانيتين\n\n" +
            "• الذهب العالمي بالدولار\n" +
            "• سعر الدولار بالجنيه\n" +
            "• عيار 24 و21 و18"

        info.textSize =
            13f

        info.setTextColor(
            Color.GRAY
        )

        info.setPadding(
            0,
            25,
            0,
            20
        )

        box.addView(info)

        scroll.addView(box)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                -1
            )
        )

        setContentView(root)
    }

    private fun loadPrices() {

        thread {

            try {

                val connection =
                    URL(api)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "GET"

                connection.connectTimeout =
                    5000

                connection.readTimeout =
                    5000

                val response =
                    connection
                        .inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val json =
                    JSONObject(response)

                if (
                    !json.optBoolean(
                        "success"
                    )
                ) {
                    throw Exception()
                }

                val newOunce =
                    json.getDouble(
                        "ounceUsd"
                    )

                val newDollar =
                    json.getDouble(
                        "usdEgp"
                    )

                val new24 =
                    json.getDouble(
                        "gram24"
                    )

                val new21 =
                    json.getDouble(
                        "gram21"
                    )

                val new18 =
                    json.getDouble(
                        "gram18"
                    )

                runOnUiThread {

                    ounceUsd =
                        newOunce

                    usdEgp =
                        newDollar

                    gram24 =
                        new24

                    gram21 =
                        new21

                    gram18 =
                        new18

                    globalText.text =
                        "$${
                            usd.format(
                                ounceUsd
                            )
                        }"

                    dollarText.text =
                        "الدولار: ${
                            egp.format(
                                usdEgp
                            )
                        } جنيه"

                    statusText.text =
                        "● متصل — آخر تحديث الآن"

                    statusText.setTextColor(
                        Color.rgb(
                            70,
                            200,
                            110
                        )
                    )

                    showLocal()
                    showNotification()
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر الاتصال"

                    statusText.setTextColor(
                        Color.RED
                    )
                }
            }
        }
    }

    private fun showLocal() {

        val price =
            when (karat) {
                24 -> gram24
                21 -> gram21
                else -> gram18
            }

        if (price <= 0) {
            localText.text =
                "-- ج / جرام"
            return
        }

        localText.text =
            "${
                egp.format(
                    price
                )
            } ج / جرام\nعيار $karat"
    }

    private fun selectedPrice():
        String {

        val price =
            when (karat) {
                24 -> gram24
                21 -> gram21
                else -> gram18
            }

        return egp.format(price)
    }

    private fun showNotification() {

        if (!notificationOn)
            return

        if (ounceUsd <= 0)
            return

        val text =
            "🌍 $${
                usd.format(
                    ounceUsd
                )
            } • 🇪🇬 ${
                selectedPrice()
            } ج"

        val notification =
            NotificationCompat
                .Builder(
                    this,
                    channelId
                )
                .setSmallIcon(
                    android.R.drawable
                        .ic_dialog_info
                )
                .setContentTitle(
                    "GoldLive • عيار $karat"
                )
                .setContentText(
                    text
                )
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(
                    NotificationCompat
                        .PRIORITY_LOW
                )
                .build()

        if (
            Build.VERSION.SDK_INT < 33 ||
            ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .POST_NOTIFICATIONS
                ) ==
                PackageManager
                    .PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    notificationId,
                    notification
                )
        }
    }

    private fun createChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "GoldLive",
                    NotificationManager
                        .IMPORTANCE_LOW
                )

            channel.description =
                "أسعار الذهب"

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun requestNotifications() {

        if (
            Build.VERSION.SDK_INT >= 33
        ) {

            if (
                ActivityCompat
                    .checkSelfPermission(
                        this,
                        Manifest.permission
                            .POST_NOTIFICATIONS
                    ) !=
                    PackageManager
                        .PERMISSION_GRANTED
            ) {

                ActivityCompat
                    .requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission
                                .POST_NOTIFICATIONS
                        ),
                        200
                    )
            }
        }
    }

    override fun onDestroy() {

        handler.removeCallbacks(
            updater
        )

        super.onDestroy()
    }
}
