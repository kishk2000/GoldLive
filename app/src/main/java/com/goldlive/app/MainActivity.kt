package com.goldlive.app

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
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

    private val handler = Handler(Looper.getMainLooper())

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
    private lateinit var changeText: TextView
    private lateinit var karatSpinner: Spinner
    private lateinit var notificationButton: Button

    private val moneyFormat =
        DecimalFormat("#,##0.00")

    private val priceFormat =
        DecimalFormat("#,##0.00")

    private val updateRunnable = object : Runnable {

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

    private fun createInterface() {

        val root = LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.rgb(245, 236, 207)
        )

        root.setPadding(
            20,
            20,
            20,
            20
        )

        val scrollView = ScrollView(this)

        val content = LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.gravity =
            Gravity.CENTER_HORIZONTAL

        /*
         * HEADER
         */

        val header = LinearLayout(this)

        header.orientation =
            LinearLayout.VERTICAL

        header.gravity =
            Gravity.CENTER

        header.setPadding(
            20,
            25,
            20,
            25
        )

        val headerBackground =
            GradientDrawable()

        headerBackground.setColor(
            Color.rgb(35, 28, 12)
        )

        headerBackground.cornerRadius =
            35f

        header.background =
            headerBackground

        val title = TextView(this)

        title.text =
            "GOLD LIVE"

        title.textSize =
            32f

        title.setTextColor(
            Color.rgb(255, 215, 80)
        )

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        header.addView(title)

        val subtitle = TextView(this)

        subtitle.text =
            "أسعار الذهب لحظة بلحظة"

        subtitle.textSize =
            15f

        subtitle.setTextColor(
            Color.rgb(235, 220, 170)
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

        addSpace(content, 15)

        /*
         * GLOBAL GOLD CARD
         */

        val globalCard =
            createCard()

        val globalTitle =
            createSectionTitle(
                "🌍  الذهب العالمي"
            )

        globalCard.addView(
            globalTitle
        )

        globalPriceText =
            TextView(this)

        globalPriceText.text =
            "جاري التحميل..."

        globalPriceText.textSize =
            34f

        globalPriceText.setTextColor(
            Color.rgb(155, 115, 15)
        )

        globalPriceText.gravity =
            Gravity.CENTER

        globalPriceText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        globalPriceText.setPadding(
            0,
            18,
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

        changeText =
            TextView(this)

        changeText.text =
            "—"

        changeText.textSize =
            14f

        changeText.setTextColor(
            Color.GRAY
        )

        changeText.gravity =
            Gravity.CENTER

        changeText.setPadding(
            0,
            10,
            0,
            5
        )

        globalCard.addView(
            changeText
        )

        content.addView(
            globalCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(content, 12)

        /*
         * USD CARD
         */

        val dollarCard =
            createCard()

        val dollarTitle =
            createSectionTitle(
                "💵  سعر الدولار"
            )

        dollarCard.addView(
            dollarTitle
        )

        dollarPriceText =
            TextView(this)

        dollarPriceText.text =
            "-- جنيه"

        dollarPriceText.textSize =
            27f

        dollarPriceText.setTextColor(
            Color.rgb(115, 82, 5)
        )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarPriceText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        dollarPriceText.setPadding(
            0,
            15,
            0,
            10
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

        addSpace(content, 12)

        /*
         * EGYPT GOLD CARD
         */

        val egyptCard =
            createCard()

        val egyptTitle =
            createSectionTitle(
                "🇪🇬  سعر الذهب في مصر"
            )

        egyptCard.addView(
            egyptTitle
        )

        karatSpinner =
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

        karatSpinner.adapter =
            adapter

        karatSpinner.setSelection(1)

        karatSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
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
            karatSpinner
        )

        localPriceText =
            TextView(this)

        localPriceText.text =
            "-- ج / جرام"

        localPriceText.textSize =
            31f

        localPriceText.setTextColor(
            Color.rgb(130, 90, 5)
        )

        localPriceText.gravity =
            Gravity.CENTER

        localPriceText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        localPriceText.setPadding(
            0,
            20,
            0,
            15
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

        addSpace(content, 12)

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
            10,
            0,
            15
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
            "🔔 تفعيل شريط الأسعار"

        notificationButton.textSize =
            15f

        notificationButton.setTextColor(
            Color.WHITE
        )

        val buttonBackground =
            GradientDrawable()

        buttonBackground.setColor(
            Color.rgb(130, 95, 10)
        )

        buttonBackground.cornerRadius =
            30f

        notificationButton.background =
            buttonBackground

        notificationButton.setPadding(
            20,
            10,
            20,
            10
        )

        notificationButton.setOnClickListener {

            notificationEnabled =
                !notificationEnabled

            if (notificationEnabled) {

                notificationButton.text =
                    "🔕 إيقاف شريط الأسعار"

                updateNotification()

                Toast.makeText(
                    this,
                    "تم تفعيل شريط الأسعار",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                notificationButton.text =
                    "🔔 تفعيل شريط الأسعار"

                NotificationManagerCompat
                    .from(this)
                    .cancel(notificationId)

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

        addSpace(content, 15)

        /*
         * INFO
         */

        val info =
            TextView(this)

        info.text =
            "تحديث تلقائي كل ثانيتين\n\n" +
            "• سعر الأونصة العالمي بالدولار\n" +
            "• سعر الدولار مقابل الجنيه\n" +
            "• أسعار عيارات 24 و21 و18\n" +
            "• شريط أسعار اختياري في الإشعارات"

        info.textSize =
            13f

        info.setTextColor(
            Color.rgb(100, 90, 65)
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

        scrollView.addView(
            content
        )

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                -1,
                -1
            )
        )

        setContentView(root)
    }

    private fun createCard(): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.gravity =
            Gravity.CENTER_HORIZONTAL

        card.setPadding(
            20,
            18,
            20,
            20
        )

        val background =
            GradientDrawable()

        background.setColor(
            Color.rgb(255, 250, 235)
        )

        background.setStroke(
            2,
            Color.rgb(210, 175, 70)
        )

        background.cornerRadius =
            30f

        card.background =
            background

        return card
    }

    private fun createSectionTitle(
        text: String
    ): TextView {

        val title =
            TextView(this)

        title.text =
            text

        title.textSize =
            18f

        title.setTextColor(
            Color.rgb(70, 55, 20)
        )

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        title.setPadding(
            0,
            5,
            0,
            5
        )

        return title
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space =
            Space(this)

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

                if (
                    !json.optBoolean(
                        "success",
                        false
                    )
                ) {
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
                        moneyFormat.format(
                            ounceUsd
                        )

                    dollarPriceText.text =
                        priceFormat.format(
                            usdEgp
                        ) +
                        " جنيه"

                    statusText.text =
                        "● متصل — آخر تحديث الآن"

                    statusText.setTextColor(
                        Color.rgb(
                            40,
                            150,
                            70
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
                            50,
                            40
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
            priceFormat.format(
                price
            ) +
            " ج / جرام\nعيار " +
            selectedKarat
    }

    private fun getSelectedPrice(): String {

        val price =
            when (selectedKarat) {
                24 -> gram24
                21 -> gram21
                18 -> gram18
                else -> 0.0
            }

        return priceFormat.format(
            price
        )
    }

    private fun updateNotification() {

        if (!notificationEnabled) {
            return
        }

        if (ounceUsd <= 0.0) {
            return
        }

        val text =
            "🌍 $" +
            moneyFormat.format(
                ounceUsd
            ) +
            " • 🇪🇬 " +
            getSelectedPrice() +
            " ج"

        val notification =
            NotificationCompat
                .Builder(
                    this,
                    channelId
                )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "GoldLive • عيار $selectedKarat"
                )
                .setContentText(
                    text
                )
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setCategory(
                    NotificationCompat.CATEGORY_STATUS
                )
                .build()

        if (
            Build.VERSION.SDK_INT < 33 ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
    
