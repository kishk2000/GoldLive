package com.goldlive.app

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val workerUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId = "gold_live"
    private val notificationId = 1001

    private val handler =
        Handler(Looper.getMainLooper())

    private var selectedKarat = 21

    private var goldUsd = 0.0
    private var usdEgp = 0.0

    private var goldEgp24 = 0.0
    private var goldEgp21 = 0.0
    private var goldEgp18 = 0.0

    private var lastGoldUsd = 0.0

    private var notificationEnabled = false

    private lateinit var globalPriceText: TextView
    private lateinit var dollarPriceText: TextView
    private lateinit var localPriceText: TextView
    private lateinit var changeText: TextView
    private lateinit var statusText: TextView
    private lateinit var karatSpinner: Spinner
    private lateinit var notificationButton: Button

    private val usdFormatter =
        DecimalFormat("#,##0.00")

    private val egpFormatter =
        DecimalFormat("#,##0.00")

    private val updateRunnable =
        object : Runnable {

            override fun run() {
                loadGoldPrice()

                handler.postDelayed(
                    this,
                    2000
                )
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestNotificationPermission()

        buildInterface()

        handler.post(updateRunnable)
    }

    private fun buildInterface() {

        val root =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setBackgroundColor(
                    Color.rgb(10, 10, 12)
                )

                setPadding(
                    24,
                    28,
                    24,
                    24
                )
            }

        val scroll =
            ScrollView(this)

        val content =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
            }

        val title =
            TextView(this).apply {
                text = "GOLD LIVE"
                textSize = 30f

                setTextColor(
                    Color.rgb(
                        212,
                        175,
                        55
                    )
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    10,
                    0,
                    4
                )
            }

        content.addView(title)

        val subtitle =
            TextView(this).apply {
                text =
                    "أسعار الذهب لحظة بلحظة"

                textSize = 15f

                setTextColor(
                    Color.LTGRAY
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    0,
                    0,
                    25
                )
            }

        content.addView(subtitle)

        val globalTitle =
            TextView(this).apply {
                text =
                    "🌍 الذهب العالمي"

                textSize = 18f

                setTextColor(
                    Color.WHITE
                )

                setPadding(
                    0,
                    10,
                    0,
                    5
                )
            }

        content.addView(globalTitle)

        globalPriceText =
            TextView(this).apply {
                text =
                    "جاري تحميل السعر..."

                textSize = 32f

                setTextColor(
                    Color.rgb(
                        212,
                        175,
                        55
                    )
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    12,
                    0,
                    4
                )
            }

        content.addView(
            globalPriceText
        )

        val ounceLabel =
            TextView(this).apply {
                text =
                    "USD / Troy Ounce"

                textSize = 13f

                setTextColor(
                    Color.GRAY
                )

                gravity =
                    Gravity.CENTER
            }

        content.addView(
            ounceLabel
        )

        dollarPriceText =
            TextView(this).apply {
                text =
                    "الدولار: -- جنيه"

                textSize = 18f

                setTextColor(
                    Color.LTGRAY
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    15,
                    0,
                    20
                )
            }

        content.addView(
            dollarPriceText
        )

        val localTitle =
            TextView(this).apply {
                text =
                    "🇪🇬 سعر الذهب في مصر"

                textSize = 18f

                setTextColor(
                    Color.WHITE
                )

                setPadding(
                    0,
                    10,
                    0,
                    5
                )
            }

        content.addView(localTitle)

        karatSpinner =
            Spinner(this)

        val karats =
            arrayOf(
                "عيار 24",
                "عيار 21",
                "عيار 18"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                karats
            )

        karatSpinner.adapter =
            adapter

        karatSpinner.setSelection(1)

        karatSpinner.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    selectedKarat =
                        when (position) {
                            0 -> 24
                            1 -> 21
                            else -> 18
                        }

                    updateLocalPrice()
                    updateNotification()
                }
            }

        content.addView(
            karatSpinner
        )

        localPriceText =
            TextView(this).apply {
                text =
                    "-- ج / جرام"

                textSize = 29f

                setTextColor(
                    Color.WHITE
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    18,
                    0,
                    5
                )
            }

        content.addView(
            localPriceText
        )

        changeText =
            TextView(this).apply {
                text = "—"
                textSize = 15f

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    4,
                    0,
                    10
                )
            }

        content.addView(
            changeText
        )

        statusText =
            TextView(this).apply {
                text =
                    "جاري الاتصال..."

                textSize = 13f

                setTextColor(
                    Color.GRAY
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    8,
                    0,
                    20
                )
            }

        content.addView(
            statusText
        )

        notificationButton =
            Button(this).apply {

                text =
                    "🔔 تفعيل شريط الأسعار"

                setOnClickListener {

                    notificationEnabled =
                        !notificationEnabled

                    if (
                        notificationEnabled
                    ) {

                        text =
                            "🔕 إيقاف شريط الأسعار"

                        updateNotification()

                        Toast.makeText(
                            this@MainActivity,
                            "تم تفعيل شريط الأسعار",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        text =
                            "🔔 تفعيل شريط الأسعار"

                        NotificationManagerCompat
                            .from(
                                this@MainActivity
                            )
                            .cancel(
                                notificationId
                            )

                        Toast.makeText(
                            this@MainActivity,
                            "تم إيقاف شريط الأسعار",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        content.addView(
            notificationButton
        )

        val info =
            TextView(this).apply {

                text =
                    "التحديث: كل ثانيتين\n\n" +
                    "• السعر العالمي بالدولار\n" +
                    "• سعر الدولار بالجنيه المصري\n" +
                    "• أسعار عيار 24 و21 و18\n" +
                    "• شريط أسعار في الإشعارات"

                textSize = 13f

                setTextColor(
                    Color.GRAY
                )

                setPadding(
                    0,
                    25,
                    0,
                    20
                )
            }

        content.addView(info)

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
    }

    private fun loadGoldPrice() {

        thread {

            try {

                val connection =
                    URL(workerUrl)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "GET"

                connection.connectTimeout =
                    5000

                connection.readTimeout =
                    5000

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val json =
                    JSONObject(response)

                if (
                    !json.optBoolean(
                        "success",
                        false
                    )
                ) {
                    throw Exception(
                        "API error"
                    )
                }

                val newGoldUsd =
                    json.getDouble(
                        "ounceUsd"
                    )

                val newUsdEgp =
                    json.getDouble(
                        "usdEgp"
                    )

                val newGoldEgp24 =
                    json.getDouble(
                        "gram24"
                    )

                val newGoldEgp21 =
                    json.getDouble(
                        "gram21"
                    )

                val newGoldEgp18 =
                    json.getDouble(
                        "gram18"
                    )

                runOnUiThread {

                    if (
                        lastGoldUsd > 0
                    ) {

                        val difference =
                            newGoldUsd -
                                lastGoldUsd

                        changeText.text =
                            when {
                                difference > 0 ->
                                    "▲ +${
                                        usdFormatter.format(
                                            difference
                                        )
                                    } USD"

                                difference < 0 ->
                                    "▼ ${
                                        usdFormatter.format(
                                            difference
                                        )
                                    } USD"

                                else ->
                                    "— ثابت"
                            }

                        changeText.setTextColor(
                            when {
                                difference > 0 ->
                                    Color.rgb(
                                        70,
                                        200,
                                        110
                                    )

                                difference < 0 ->
                                    Color.rgb(
                                        230,
                                        80,
                                        80
                                    )

                                else ->
                                    Color.GRAY
                            }
                        )
                    }

                    lastGoldUsd =
                        newGoldUsd

                    goldUsd =
                        newGoldUsd

                    usdEgp =
                        newUsdEgp

                    goldEgp24 =
                        newGoldEgp24

                    goldEgp21 =
                        newGoldEgp21

                    goldEgp18 =
                        newGoldEgp18

                    globalPriceText.text =
                        "$${
                            usdFormatter.format(
                                goldUsd
                            )
                        }"

                    dollarPriceText.text =
                        "الدولار: ${
                            egpFormatter.format(
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

                    updateLocalPrice()
                    updateNotification()
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر الاتصال بمصدر الأسعار"

                    statusText.setTextColor(
                        Color.rgb(
                            230,
                            80,
                            80
                        )
                    )
                }
            }
        }
    }

    private fun updateLocalPrice() {

        val price =
            when (selectedKarat) {
                24 -> goldEgp24
                21 -> goldEgp21
                18 -> goldEgp18
                else -> goldEgp21
            }

        if (price <= 0) {

            localPriceText.text =
                "-- ج / جرام"

            return
        }

        localPriceText.text =
            "${
                egpFormatter.format(
                    price
                )
            } ج / جرام\nعيار $selectedKarat"
    }

    private fun getSelectedLocalPrice():
        String {

        val price =
            when (selectedKarat) {
                24 -> goldEgp24
                21 -> goldEgp21
                18 -> goldEgp18
                else -> goldEgp21
            }

        return egpFormatter.format(price)
    }

    private fun updateNotification() {

        if (!notificationEnabled)
            return

        if (goldUsd <= 0)
            return

        val localPrice =
            getSelectedLocalPrice()

        val text =
            "🌍 $${
                usdFormatter.format(
                    goldUsd
                )
            } • 🇪🇬 $localPrice ج"

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
                    "GoldLive • عيار $selectedKarat"
                )
                .setContentText(text)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(
                    NotificationCompat
                        .PRIORITY_LOW
                )
                .setCategory(
                    NotificationCompat
                        .CATEGORY_STATUS
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

    private fun createNotificationChannel() {

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
                "سعر الذهب في شريط الإشعارات"

            channel.setShowBadge(false)

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun requestNotifica
