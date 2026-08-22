package com.goldlive.app

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId = "gold_live"
    private val notificationId = 1001

    private val handler =
        Handler(Looper.getMainLooper())

    private var selectedKarat = 21

    private var ounceUsd = 0.0
    private var usdEgp = 0.0
    private var gram24 = 0.0
    private var gram21 = 0.0
    private var gram18 = 0.0

    private var notificationEnabled = false

    private lateinit var globalPriceText: TextView
    private lateinit var dollarPriceText: TextView
    private lateinit var localPriceText: TextView
    private lateinit var statusText: TextView
    private lateinit var spinner: Spinner
    private lateinit var notificationButton: Button

    private val numberFormat =
        DecimalFormat("#,##0.00")

    private val updateRunnable =
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
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestNotificationPermission()

        createInterface()

        handler.post(updateRunnable)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    channelId,
                    "GoldLive",
                    NotificationManager.IMPORTANCE_LOW
                )

            channel.description =
                "أسعار الذهب في شريط الإشعارات"

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

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            val granted =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    200
                )
            }
        }
    }

    private fun createInterface() {

        val root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.rgb(
                244,
                232,
                198
            )
        )

        root.setPadding(
            16,
            16,
            16,
            16
        )

        val scroll =
            ScrollView(this)

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        /*
         * HEADER
         */

        val header =
            LinearLayout(this)

        header.orientation =
            LinearLayout.VERTICAL

        header.gravity =
            Gravity.CENTER

        header.setPadding(
            20,
            28,
            20,
            28
        )

        val headerBackground =
            GradientDrawable()

        headerBackground.setColor(
            Color.rgb(
                48,
                35,
                10
            )
        )

        headerBackground.cornerRadius =
            32f

        header.background =
            headerBackground

        val title =
            TextView(this)

        title.text =
            "GOLD LIVE"

        title.textSize =
            34f

        title.setTextColor(
            Color.rgb(
                255,
                215,
                70
            )
        )

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        header.addView(title)

        val subtitle =
            TextView(this)

        subtitle.text =
            "أسعار الذهب لحظة بلحظة"

        subtitle.textSize =
            15f

        subtitle.setTextColor(
            Color.rgb(
                235,
                220,
                175
            )
        )

        subtitle.gravity =
            Gravity.CENTER

        subtitle.setPadding(
            0,
            8,
            0,
            0
        )

        header.addView(subtitle)

        content.addView(
            header,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(
            content,
            14
        )

        /*
         * GLOBAL GOLD CARD
         */

        val globalCard =
            createCard()

        globalCard.addView(
            createCardTitle(
                "الذهب العالمي"
            )
        )

        globalPriceText =
            TextView(this)

        globalPriceText.text =
            "جاري التحميل..."

        globalPriceText.textSize =
            34f

        globalPriceText.setTextColor(
            Color.rgb(
                145,
                100,
                5
            )
        )

        globalPriceText.gravity =
            Gravity.CENTER

        globalPriceText.setTypeface(
            null,
            Typeface.BOLD
        )

        globalPriceText.setPadding(
            0,
            15,
            0,
            5
        )

        globalCard.addView(
            globalPriceText
        )

        val ounceLabel =
            TextView(this)

        ounceLabel.text =
            "USD / Troy Ounce"

        ounceLabel.textSize =
            13f

        ounceLabel.setTextColor(
            Color.GRAY
        )

        ounceLabel.gravity =
            Gravity.CENTER

        globalCard.addView(
            ounceLabel
        )

        content.addView(
            globalCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(
            content,
            12
        )

        /*
         * DOLLAR CARD
         */

        val dollarCard =
            createCard()

        dollarCard.addView(
            createCardTitle(
                "سعر الدولار مقابل الجنيه"
            )
        )

        dollarPriceText =
            TextView(this)

        dollarPriceText.text =
            "-- جنيه"

        dollarPriceText.textSize =
            28f

        dollarPriceText.setTextColor(
            Color.rgb(
                125,
                85,
                5
            )
        )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarPriceText.setTypeface(
            null,
            Typeface.BOLD
        )

        dollarPriceText.setPadding(
            0,
            15,
            0,
            15
        )

        dollarCard.addView(
            dollarPriceText
        )

        content.addView(
            dollarCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(
            content,
            12
        )

        /*
         * EGYPT GOLD CARD
         */

        val egyptCard =
            createCard()

        egyptCard.addView(
            createCardTitle(
                "سعر الذهب في مصر"
            )
        )

        spinner =
            Spinner(this)

        val karatNames =
            arrayOf(
                "عيار 24",
                "عيار 21",
                "عيار 18"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                karatNames
            )

        spinner.adapter =
            adapter

        spinner.setSelection(1)

        spinner.onItemSelectedListener =
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

        egyptCard.addView(
            spinner
        )

        localPriceText =
            TextView(this)

        localPriceText.text =
            "-- ج / جرام"

        localPriceText.textSize =
            32f

        localPriceText.setTextColor(
            Color.rgb(
                125,
                85,
                5
            )
        )

        localPriceText.gravity =
            Gravity.CENTER

        localPriceText.setTypeface(
            null,
            Typeface.BOLD
        )

        localPriceText.setPadding(
            0,
            22,
            0,
            18
        )

        egyptCard.addView(
            localPriceText
        )

        content.addView(
            egyptCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(
            content,
            12
        )

        /*
         * STATUS
         */

        statusText =
            TextView(this)

        statusText.text =
            "● جاري الاتصال بمصدر الأسعار..."

        statusText.textSize =
            13f

        statusText.setTextColor(
            Color.GRAY
        )

        statusText.gravity =
            Gravity.CENTER

        statusText.setPadding(
            0,
            8,
            0,
            14
        )

        content.addView(
            statusText
        )

        /*
         * NOTIFICATION BUTTON
         */

        notificationButton =
            Button(this)

        notificationButton.text =
            "تفعيل شريط الأسعار"

        notificationButton.textSize =
            15f

        notificationButton.setTextColor(
            Color.WHITE
        )

        notificationButton.setTypeface(
            null,
            Typeface.BOLD
        )

        val buttonBackground =
            GradientDrawable()

        buttonBackground.setColor(
            Color.rgb(
                125,
                88,
                8
            )
        )

        buttonBackground.cornerRadius =
            28f

        notificationButton.background =
            buttonBackground

        notificationButton.setOnClickListener {

            notificationEnabled =
                !notificationEnabled

            if (notificationEnabled) {

                notificationButton.text =
                    "إيقاف شريط الأسعار"

                updateNotification()

                Toast.makeText(
                    this,
                    "تم تفعيل شريط الأسعار",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                notificationButton.text =
                    "تفعيل شريط الأسعار"

                val manager =
                    getSystemService(
                        Context.NOTIFICATION_SERVICE
                    ) as NotificationManager

                manager.cancel(
                    notificationId
                )

                Toast.makeText(
                    this,
                    "تم إيقاف شريط الأسعار",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        content.addView(
            notificationButton,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(
            content,
            15
        )

        /*
         * INFO
         */

        val info =
            TextView(this)

        info.text =
            "تحديث تلقائي كل ثانيتين\n\n" +
            "• الأونصة العالمية بالدولار\n" +
            "• سعر الدولار مقابل الجنيه\n" +
            "• أسعار عيار 24 و21 و18\n" +
            "• شريط أسعار اختياري"

        info.textSize =
            13f

        info.setTextColor(
            Color.rgb(
                105,
                90,
                55
            )
        )

        info.gravity =
            Gravity.CENTER

        info.setPadding(
            10,
            10,
            10,
            25
        )

        content.addView(
            info
        )

        scroll.addView(
            content
        )

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                -1
            )
        )

        setContentView(
            root
        )
    }

    private fun createCard():
        LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.gravity =
            Gravity.CENTER_HORIZONTAL

        card.setPadding(
            18,
            18,
            18,
            18
        )

        val background =
            GradientDrawable()

        background.setColor(
            Color.rgb(
                255,
                251,
                239
            )
        )

        background.setStroke(
            2,
            Color.rgb(
                205,
                165,
                55
            )
        )

        background.cornerRadius =
            28f

        card.background =
            background

        return card
    }

    private fun createCardTitle(
        text: String
    ): TextView {

        val title =
            TextView(this)

        title.text =
            text

        title.textSize =
            18f

        title.setTextColor(
            Color.rgb(
                70,
                55,
                20
            )
        )

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setPadding(
            0,
            4,
            0,
            8
        )

        return title
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space =
            TextView(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    private fun loadPrices() {

        thread {

            try {

                val connection =
                    URL(apiUrl)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "GET"

                connection.connectTimeout =
                    7000

                connection.readTimeout =
                    7000

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

                val success =
                    json.optBoolean(
                        "success",
                        false
                    )

                if (!success) {
                    throw Exception(
                        "API returned failure"
                    )
                }

                val newOunce =
                    json.optDouble(
                        "ounceUsd",
                        0.0
                    )

                val newDollar =
                    json.optDouble(
                        "usdEgp",
                        0.0
                    )

                val new24 =
                    json.optDouble(
                        "gram24",
                        0.0
                    )

                val new21 =
                    json.optDouble(
                        "gram21",
                        0.0
                    )

                val new18 =
                    json.optDouble(
                        "gram18",
                        0.0
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

                    globalPriceText.text =
                        "$" +
                        numberFormat.format(
                            ounceUsd
                        )

                    dollarPriceText.text =
                        numberFormat.format(
                            usdEgp
                        ) +
                        " جنيه"

                    statusText.text =
                        "● متصل — آخر تحديث الآن"

                    statusText.setTextColor(
                        Color.rgb(
                            35,
                            145,
                            65
                        )
                    )

                    updateLocalPrice()

                    updateNotification()
                }

            } catch (
                error: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر الاتصال بمصدر الأسعار"

                    statusText.setTextColor(
                        Color.rgb(
                            190,
                            45,
                            35
                        )
                    )
                }
            }
        }
    }

    private fun updateLocalPrice() {

        val price =
            when (selectedKarat) {

                24 -> gram24

                21 -> gram21

                18 -> gram18

                else -> 0.0
            }

        if (price <= 0.0) {

            localPriceText.text =
                "-- ج / جرام"

            return
        }

        localPriceText.text =
            numberFormat.format(
                price
            ) +
            " ج / جرام\nعيار " +
            selectedKarat
    }

    private fun getSelectedPrice():
        String {

        val price =
            
