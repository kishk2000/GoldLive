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

    private lateinit var globalPriceText: TextView
    private lateinit var dollarPriceText: TextView
    private lateinit var localPriceText: TextView
    private lateinit var changeText: TextView
    private lateinit var statusText: TextView
    private lateinit var karatSpinner: Spinner
    private lateinit var notificationButton: Button

    private var notificationEnabled = false

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


        // =========================
        // الذهب العالمي
        // =========================

        val globalTitle =
            TextView(this).apply {

                text =
                    "🌍  الذهب العالمي"

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

                setPadding(
                    0,
                    0,
                    0,
                    15
                )
            }

        content.addView(
            ounceLabel
        )


        // =========================
        // سعر الدولار
        // =========================

        dollarPriceText =
            TextView(this).apply {

                text =
                    "الدولار: -- جنيه"

                textSize = 17f

                setTextColor(
                    Color.LTGRAY
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    0,
                    0,
                    20
                )
            }

        content.addView(
            dollarPriceText
        )


        // =========================
        // الذهب في مصر
        // =========================

        val localTitle =
            TextView(this).apply {

                text =
                    "🇪🇬  سعر الذهب في مصر"

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
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    parent:
                    android.widget.AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    parent:
                    android.widget.AdapterView<*>?,
                    view:
                    android.view.View?,
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
                    "جاري الاتصال بمصدر الأسعار..."

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


        // =========================
        // الإشعارات
        // =========================

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

                text = """
                    
                    التحديث: كل ثانيتين
                    
                    • السعر العالمي بالدولار
                    • سعر الدولار بالجنيه المصري
                    • أسعار الذهب في مصر
                    • اختر العيار الذي تريد عرضه
                    • يمكن إظهار السعر في شريط الإشعارات
                """.trimIndent()

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


    // =========================
    // تحميل البيانات
    // =========================

    private fun loadGoldPrice() {

        thread {

            try {

                val url =
                    URL(workerUrl)

                val connection =
                    url.openConnection()
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

                val success =
                    json.optBoolean(
                        "success",
                        false
                    )

                if (!success) {

                    throw Exception(
                        "API returned success=false"
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

                    // =====================
                    // التغير في السعر العالمي
                    // =====================

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


                    // =====================
                    // عرض السعر العالمي
                    // =====================

                    globalPriceText.text =
                        "$${
                            usdFormatter.format(
                                goldUsd
                            )
                        }"


                    // =====================
                    // عرض الدولار
                    // =====================

                    dollarPriceText.text =
                        "الدولار: ${
                            egpFormatter.format(
                                usdEgp
                            )
                        } جنيه"


                    // =====================
                    // الحالة
                    // =====================

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

            } catch (e: Exception) {

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


    // =========================
    // تحديث السعر المحلي
    // =========================

    private fun updateLocalPrice() {

        val price =
            when (selectedKarat) {

                24 ->
                    goldEgp24

                21 ->
                    goldEgp21

                18 ->
                    goldEgp18

                else ->
                    goldEgp21
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


    // =========================
    // سعر العيار المختار
    // =========================

    private fun getSelectedLocalPrice():
            String {

        val price =
            when (selectedKarat) {

                24 ->
                    goldEgp24

                21 ->
                    goldEgp21

                18 ->
                    goldEgp18

                else ->
                    goldEgp21
            }

        return egpFormatter.format(
            price
        )
    }


    // =========================
    // الإشعار
    // =========================

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
            }  •  🇪🇬 $localPrice ج"


        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    and
