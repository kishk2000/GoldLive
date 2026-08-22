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
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    // =========================================================
    // API
    // =========================================================

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    // =========================================================
    // NOTIFICATION
    // =========================================================

    private val channelId =
        "gold_live"

    private val notificationId =
        1001

    private val notificationPermissionCode =
        200

    // =========================================================
    // HANDLER
    // =========================================================

    private val handler =
        Handler(Looper.getMainLooper())

    // =========================================================
    // SELECTED KARAT
    // =========================================================

    private var selectedKarat =
        21

    // =========================================================
    // API DATA
    // =========================================================

    private var ounceUsd =
        0.0

    private var usdEgp =
        0.0

    private var gram24 =
        0.0

    private var gram21 =
        0.0

    private var gram18 =
        0.0

    private var gram14 =
        0.0

    private var goldCoin =
        0.0

    private var localOunce =
        0.0

    // =========================================================
    // NOTIFICATION STATE
    // =========================================================

    private var notificationEnabled =
        false

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private lateinit var globalPriceText: TextView

    private lateinit var dollarPriceText: TextView

    private lateinit var heroPriceText: TextView

    private lateinit var selectedPriceText: TextView

    private lateinit var selectedKaratText: TextView

    private lateinit var localOunceText: TextView

    private lateinit var goldCoinText: TextView

    private lateinit var statusText: TextView

    private lateinit var notificationButton: Button

    private lateinit var pricesContainer: LinearLayout

    private lateinit var karatButtonsContainer: LinearLayout

    // =========================================================
    // NUMBER FORMAT
    // =========================================================

    private val numberFormat =
        DecimalFormat("#,##0.00")

    private val wholeNumberFormat =
        DecimalFormat("#,##0")

    // =========================================================
    // AUTO UPDATE
    // =========================================================

    private val updateTask =
        object : Runnable {

            override fun run() {

                loadPrices()

                handler.postDelayed(
                    this,
                    2000L
                )
            }
        }

    // =========================================================
    // COLORS
    // =========================================================

    private val cream =
        Color.rgb(
            251,
            243,
            213
        )

    private val dark =
        Color.rgb(
            30,
            41,
            59
        )

    private val dark2 =
        Color.rgb(
            15,
            23,
            42
        )

    private val gold =
        Color.rgb(
            212,
            175,
            55
        )

    private val goldLight =
        Color.rgb(
            229,
            169,
            60
        )

    private val green =
        Color.rgb(
            34,
            197,
            94
        )

    private val red =
        Color.rgb(
            239,
            68,
            68
        )

    private val white =
        Color.WHITE

    private val lightText =
        Color.rgb(
            226,
            232,
            240
        )

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        window.statusBarColor =
            dark2

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M
        ) {

            window.decorView.systemUiVisibility =
                0
        }

        createNotificationChannel()

        createInterface()

        requestNotificationPermission()

        handler.post(
            updateTask
        )
    }

    // =========================================================
    // NOTIFICATION CHANNEL
    // =========================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "GoldLive",
                    NotificationManager.IMPORTANCE_LOW
                )

            channel.description =
                "أسعار الذهب في شريط الإشعارات"

            channel.setShowBadge(
                false
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
    // NOTIFICATION PERMISSION
    // =========================================================

    private fun requestNotificationPermission() {

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

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    notificationPermissionCode
                )
            }
        }
    }

    // =========================================================
    // MAIN INTERFACE
    // =========================================================

    private fun createInterface() {

        val root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            cream
        )

        root.setPadding(
            12,
            12,
            12,
            12
        )

        val scroll =
            ScrollView(this)

        scroll.isFillViewport =
            true

        scroll.setBackgroundColor(
            cream
        )

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        // =====================================================
        // HEADER
        // =====================================================

        val header =
            createRoundedLayout(
                dark2,
                gold,
                24f
            )

        header.orientation =
            LinearLayout.HORIZONTAL

        header.gravity =
            Gravity.CENTER_VERTICAL

        header.setPadding(
            18,
            16,
            18,
            16
        )

        val headerTexts =
            LinearLayout(this)

        headerTexts.orientation =
            LinearLayout.VERTICAL

        val title =
            createText(
                "GOLD LIVE",
                24f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        val subtitle =
            createText(
                "أسعار الذهب لحظة بلحظة",
                12f,
                lightText,
                Typeface.NORMAL
            )

        headerTexts.addView(
            title
        )

        headerTexts.addView(
            subtitle
        )

        header.addView(
            headerTexts,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val live =
            createText(
                "● LIVE",
                12f,
                green,
                Typeface.BOLD
            )

        live.gravity =
            Gravity.CENTER

        header.addView(
            live,
            LinearLayout.LayoutParams(
                -2,
                -2
            )
        )

        content.addView(
            header,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // GLOBAL + DOLLAR
        // =====================================================

        val miniRow =
            LinearLayout(this)

        miniRow.orientation =
            LinearLayout.HORIZONTAL

        val globalCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        globalCard.orientation =
            LinearLayout.VERTICAL

        globalCard.gravity =
            Gravity.CENTER

        globalCard.setPadding(
            10,
            14,
            10,
            14
        )

        globalCard.addView(
            createText(
                "🌍 الأونصة العالمية",
                12f,
                dark,
                Typeface.BOLD
            )
        )

        globalPriceText =
            createText(
                "--",
                21f,
                dark,
                Typeface.BOLD
            )

        globalPriceText.gravity =
            Gravity.CENTER

        globalCard.addView(
            globalPriceText
        )

        globalCard.addView(
            createText(
                "USD / Troy Ounce",
                10f,
                Color.GRAY,
                Typeface.NORMAL
            )
        )

        miniRow.addView(
            globalCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        addHorizontalGap(
            miniRow,
            8
        )

        val dollarCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        dollarCard.orientation =
            LinearLayout.VERTICAL

        dollarCard.gravity =
            Gravity.CENTER

        dollarCard.setPadding(
            10,
            14,
            10,
            14
        )

        dollarCard.addView(
            createText(
                "💵 الدولار",
                12f,
                dark,
                Typeface.BOLD
            )
        )

        dollarPriceText =
            createText(
                "--",
                21f,
                dark,
                Typeface.BOLD
            )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarPriceText
        )

        dollarCard.addView(
            createText(
                "EGP",
                10f,
                Color.GRAY,
                Typeface.NORMAL
            )
        )

        miniRow.addView(
            dollarCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        content.addView(
            miniRow,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // HERO
        // =====================================================

        val hero =
            createRoundedLayout(
                dark2,
                gold,
                28f
            )

        hero.orientation =
            LinearLayout.VERTICAL

        hero.gravity =
            Gravity.CENTER

        hero.setPadding(
            20,
            22,
            20,
            22
        )

        hero.addView(
            createText(
                "السعر الرئيسي • عيار 21",
                15f,
                lightText,
                Typeface.BOLD
            )
        )

        heroPriceText =
            createText(
                "--",
                48f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        heroPriceText.gravity =
            Gravity.CENTER

        hero.addView(
            heroPriceText
        )

        hero.addView(
            createText(
                "جنيه / جرام",
                13f,
                Color.rgb(
                    203,
                    213,
                    225
                ),
                Typeface.NORMAL
            )
        )

        selectedPriceText =
            createText(
                "شراء / بيع: --",
                13f,
                white,
                Typeface.BOLD
            )

        selectedPriceText.gravity =
            Gravity.CENTER

        selectedPriceText.setPadding(
            0,
            10,
            0,
            0
        )

        hero.addView(
            selectedPriceText
        )

        content.addView(
            hero,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )
                // =====================================================
        // KARAT SELECTOR
        // =====================================================

        val selectorCard =
            createRoundedLayout(
                white,
                gold,
                22f
            )

        selectorCard.orientation =
            LinearLayout.VERTICAL

        selectorCard.setPadding(
            14,
            14,
            14,
            14
        )

        selectorCard.addView(
            createText(
                "اختار عيار الذهب",
                15f,
                dark,
                Typeface.BOLD
            )
        )

        addGap(
            selectorCard,
            10
        )

        karatButtonsContainer =
            LinearLayout(this)

        karatButtonsContainer.orientation =
            LinearLayout.HORIZONTAL

        karatButtonsContainer.gravity =
            Gravity.CENTER

        createKaratButton(
            "24",
            24
        )

        createKaratButton(
            "21",
            21
        )

        createKaratButton(
            "18",
            18
        )

        createKaratButton(
            "14",
            14
        )

        selectorCard.addView(
            karatButtonsContainer,
            LinearLayout.LayoutParams(
                -1,
                55
            )
        )

        selectedKaratText =
            createText(
                "السعر الحالي لعيار 21",
                12f,
                Color.GRAY,
                Typeface.NORMAL
            )

        selectedKaratText.gravity =
            Gravity.CENTER

        selectedKaratText.setPadding(
            0,
            8,
            0,
            0
        )

        selectorCard.addView(
            selectedKaratText
        )

        content.addView(
            selectorCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // PRICES TITLE
        // =====================================================

        val pricesTitleRow =
            LinearLayout(this)

        pricesTitleRow.orientation =
            LinearLayout.HORIZONTAL

        pricesTitleRow.gravity =
            Gravity.CENTER_VERTICAL

        val pricesTitle =
            createText(
                "أسعار الذهب",
                20f,
                dark,
                Typeface.BOLD
            )

        pricesTitleRow.addView(
            pricesTitle,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val liveLabel =
            createText(
                "● مباشر",
                11f,
                green,
                Typeface.BOLD
            )

        pricesTitleRow.addView(
            liveLabel,
            LinearLayout.LayoutParams(
                -2,
                -2
            )
        )

        content.addView(
            pricesTitleRow,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            8
        )

        // =====================================================
        // TABLE HEADER
        // =====================================================

        val tableHeader =
            createRoundedLayout(
                dark2,
                gold,
                16f
            )

        tableHeader.orientation =
            LinearLayout.HORIZONTAL

        tableHeader.gravity =
            Gravity.CENTER_VERTICAL

        tableHeader.setPadding(
            14,
            12,
            14,
            12
        )

        tableHeader.addView(
            createText(
                "العيار",
                13f,
                white,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        tableHeader.addView(
            createText(
                "السعر",
                13f,
                white,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                0,
                -2,
                1.4f
            )
        )

        tableHeader.addView(
            createText(
                "الحركة",
                13f,
                white,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                0,
                -2,
                0.7f
            )
        )

        content.addView(
            tableHeader,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            5
        )

        pricesContainer =
            LinearLayout(this)

        pricesContainer.orientation =
            LinearLayout.VERTICAL

        content.addView(
            pricesContainer,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // EXTRA MARKET CARDS
        // =====================================================

        val extraTitle =
            createText(
                "معلومات السوق",
                18f,
                dark,
                Typeface.BOLD
            )

        content.addView(
            extraTitle
        )

        addGap(
            content,
            8
        )

        val extraRow =
            LinearLayout(this)

        extraRow.orientation =
            LinearLayout.HORIZONTAL

        // Local ounce
        val ounceCard =
            createRoundedLayout(
                dark,
                gold,
                18f
            )

        ounceCard.orientation =
            LinearLayout.VERTICAL

        ounceCard.gravity =
            Gravity.CENTER

        ounceCard.setPadding(
            8,
            14,
            8,
            14
        )

        ounceCard.addView(
            createText(
                "الأونصة محلي",
                11f,
                lightText,
                Typeface.BOLD
            )
        )

        localOunceText =
            createText(
                "--",
                17f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        localOunceText.gravity =
            Gravity.CENTER

        ounceCard.addView(
            localOunceText
        )

        ounceCard.addView(
            createText(
                "جنيه",
                10f,
                lightText,
                Typeface.NORMAL
            )
        )

        extraRow.addView(
            ounceCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        addHorizontalGap(
            extraRow,
            8
        )

        // Gold coin
        val coinCard =
            createRoundedLayout(
                dark,
                gold,
                18f
            )

        coinCard.orientation =
            LinearLayout.VERTICAL

        coinCard.gravity =
            Gravity.CENTER

        coinCard.setPadding(
            8,
            14,
            8,
            14
        )

        coinCard.addView(
            createText(
                "جنيه الذهب",
                11f,
                lightText,
                Typeface.BOLD
            )
        )

        goldCoinText =
            createText(
                "--",
                17f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        goldCoinText.gravity =
            Gravity.CENTER

        coinCard.addView(
            goldCoinText
        )

        coinCard.addView(
            createText(
                "عيار 21",
                10f,
                lightText,
                Typeface.NORMAL
            )
        )

        extraRow.addView(
            coinCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        content.addView(
            extraRow,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // STATUS
        // =====================================================

        statusText =
            createText(
                "● جاري الاتصال بمصدر الأسعار...",
                12f,
                Color.GRAY,
                Typeface.NORMAL
            )

        statusText.gravity =
            Gravity.CENTER

        statusText.setPadding(
            0,
            5,
            0,
            10
        )

        content.addView(
            statusText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // NOTIFICATION BUTTON
        // =====================================================

        notificationButton =
            Button(this)

        notificationButton.text =
            "🔔 تفعيل شريط الأسعار"

        notificationButton.textSize =
            14f

        notificationButton.setTextColor(
            white
        )

        notificationButton.setTypeface(
            null,
            Typeface.BOLD
        )

        notificationButton.background =
            createBackground(
                gold,
                goldLight,
                26f
            )

        notificationButton.setOnClickListener {

            notificationEnabled =
                !notificationEnabled

            if (
                notificationEnabled
            ) {

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

                cancelNotification()

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
                55
            )
        )

        addGap(
            content,
            12
        )

        // =====================================================
        // INFORMATION
        // =====================================================

        val info =
            createRoundedLayout(
                white,
                Color.rgb(
                    225,
                    210,
                    165
                ),
                18f
            )

        info.orientation =
            LinearLayout.VERTICAL

        info.setPadding(
            14,
            14,
            14,
            14
        )

        info.addView(
            createText(
                "GoldLive",
                16f,
                dark,
                Typeface.BOLD
            )
        )

        info.addView(
            createText(
                "تحديث تلقائي للأسعار كل ثانيتين",
                12f,
                Color.GRAY,
                Typeface.NORMAL
            )
        )

        info.addView(
            createText(
                "الأرقام المعروضة بالإنجليزية",
                12f,
                Color.GRAY,
                Typeface.NORMAL
            )
        )

        content.addView(
            info,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            20
        )

        // =====================================================
        // SCROLL
        // =====================================================

        scroll.addView(
            content,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        setContentView(
            root
        )
    }

    // =========================================================
    // KARAT BUTTON
    // =========================================================

    private fun createKaratButton(
        text: String,
        karat: Int
    ) {

        val button =
            Button(this)

        button.text =
            text

        button.textSize =
            15f

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.setTextColor(
            white
        )

        button.background =
            createBackground(
                if (
                    karat ==
                    selectedKarat
                ) {
                    gold
                } else {
                    dark
                },
                if (
                    karat ==
                    selectedKarat
                ) {
                    goldLight
                } else {
                    dark
                },
                18f
            )

        button.setOnClickListener {

            selectedKarat =
                karat

            refreshKaratButtons()

            updateLocalPrice()

            updateNotification()
        }

        karatButtonsContainer.addView(
            button,
            LinearLayout.LayoutParams(
                0,
                -1,
                1f
            ).apply {

                setMargins(
                    3,
                    0,
                    3,
                    0
                )
            }
        )
    }
        // =========================================================
    // REFRESH KARAT BUTTONS
    // =========================================================

    private fun refreshKaratButtons() {

        if (
            !::karatButtonsContainer.isInitialized
        ) {
            return
        }

        for (
            i in
            0 until
            karatButtonsContainer.childCount
        ) {

            val view =
                karatButtonsContainer
                    .getChildAt(i)

            if (
                view is Button
            ) {

                val karat =
                    when (i) {
                        0 -> 24
                        1 -> 21
                        2 -> 18
                        else -> 14
                    }

                view.background =
                    createBackground(
                        if (
                            karat ==
                            selectedKarat
                        ) {
                            gold
                        } else {
                            dark
                        },
                        if (
                            karat ==
                            selectedKarat
                        ) {
                            goldLight
                        } else {
                            dark
                        },
                        18f
                    )
            }
        }
    }

    // =========================================================
    // CREATE ROUNDED LAYOUT
    // =========================================================

    private fun createRoundedLayout(
        backgroundColor: Int,
        strokeColor: Int,
        radius: Float
    ): LinearLayout {

        val layout =
            LinearLayout(this)

        val background =
            GradientDrawable()

        background.setColor(
            backgroundColor
        )

        background.setStroke(
            1,
            strokeColor
        )

        background.cornerRadius =
            radius

        layout.background =
            background

        return layout
    }

    // =========================================================
    // CREATE BACKGROUND
    // =========================================================

    private fun createBackground(
        backgroundColor: Int,
        strokeColor: Int,
        radius: Float
    ): GradientDrawable {

        val background =
            GradientDrawable()

        background.setColor(
            backgroundColor
        )

        background.setStroke(
            1,
            strokeColor
        )

        background.cornerRadius =
            radius

        return background
    }

    // =========================================================
    // CREATE TEXT
    // =========================================================

    private fun createText(
        text: String,
        size: Float,
        color: Int,
        style: Int
    ): TextView {

        val textView =
            TextView(this)

        textView.text =
            text

        textView.textSize =
            size

        textView.setTextColor(
            color
        )

        textView.setTypeface(
            null,
            style
        )

        textView.gravity =
            Gravity.CENTER_VERTICAL

        return textView
    }

    // =========================================================
    // GAP
    // =========================================================

    private fun addGap(
        parent: LinearLayout,
        height: Int
    ) {

        val gap =
            View(this)

        parent.addView(
            gap,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    // =========================================================
    // HORIZONTAL GAP
    // =========================================================

    private fun addHorizontalGap(
        parent: LinearLayout,
        width: Int
    ) {

        val gap =
            View(this)

        parent.addView(
            gap,
            LinearLayout.LayoutParams(
                width,
                1
            )
        )
    }

    // =========================================================
    // FORMAT NUMBER
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (
            value <= 0.0
        ) {
            return "--"
        }

        return numberFormat.format(
            value
        )
    }

    // =========================================================
    // FORMAT WHOLE NUMBER
    // =========================================================

    private fun formatWholeNumber(
        value: Double
    ): String {

        if (
            value <= 0.0
        ) {
            return "--"
        }

        return wholeNumberFormat.format(
            value
        )
    }

    // =========================================================
    // LOAD PRICES
    // =========================================================

    private fun loadPrices() {

        thread {

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

                if (
                    !success
                ) {

                    throw Exception(
                        "API returned success=false"
                    )
                }

                ounceUsd =
                    json.optDouble(
                        "ounceUsd",
                        0.0
                    )

                usdEgp =
                    json.optDouble(
                        "usdEgp",
                        0.0
                    )

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

                // =================================================
                // عيار 14
                // =================================================

                gram14 =
                    if (
                        gram24 > 0.0
                    ) {

                        gram24 *
                            14.0 /
                            24.0

                    } else {

                        gram21 *
                            14.0 /
                            21.0
                    }

                // =================================================
                // جنيه الذهب
                // =================================================

                goldCoin =
                    gram21 *
                    8.0

                // =================================================
                // الأونصة المحلية
                // =================================================

                localOunce =
                    ounceUsd *
                    usdEgp

                runOnUiThread {

                    updateInterface()

                    statusText.text =
                        "● آخر تحديث ناجح • مباشر"

                    statusText.setTextColor(
                        green
                    )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر تحديث الأسعار"

                    statusText.setTextColor(
                        red
                    )
                }

            } finally {

                connection?.disconnect()
            }
        }
    }

    // =========================================================
    // UPDATE INTERFACE
    // =========================================================

    private fun updateInterface() {

        globalPriceText.text =
            formatNumber(
                ounceUsd
            )

        dollarPriceText.text =
            formatNumber(
                usdEgp
            )

        heroPriceText.text =
            formatNumber(
                gram21
            )

        localOunceText.text =
            formatNumber(
                localOunce
            )

        goldCoinText.text =
            formatNumber(
                goldCoin
            )

        updateLocalPrice()

        updatePricesTable()

        updateNotification()
    }

    // =========================================================
    // UPDATE SELECTED PRICE
    // =========================================================

    private fun updateLocalPrice() {

        val price =
            when (
                selectedKarat
            ) {

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

        selectedKaratText.text =
            "السعر الحالي لعيار $selectedKarat: " +
            "${formatNumber(price)} جنيه / جرام"

        selectedPriceText.text =
            "شراء / بيع: ${formatNumber(price)} جنيه"

        if (
            selectedKarat ==
            21
        ) {

            heroPriceText.text =
                formatNumber(
                    gram21
                )
        }
    }

    // =========================================================
    // UPDATE PRICES TABLE
    // =========================================================

    private fun updatePricesTable() {

        if (
            !::pricesContainer.isInitialized
        ) {
            return
        }

        pricesContainer.removeAllViews()

        addPriceRow(
            "24",
            gram24,
            "▲",
            green
        )

        addPriceRow(
            "21",
            gram21,
            "▲",
            green
        )

        addPriceRow(
            "18",
            gram18,
            "▼",
            red
        )

        addPriceRow(
            "14",
            gram14,
            "▲",
            green
        )

        addPriceRow(
            "جنيه الذهب",
            goldCoin,
            "▲",
            green
        )

        addPriceRow(
            "الأونصة محلي",
            localOunce,
            "▲",
            green
        )
    }

    // =========================================================
    // ADD PRICE ROW
    // =========================================================

    private fun addPriceRow(
        name: String,
        price: Double,
        arrow: String,
        arrowColor: Int
    ) {

        val row =
            createRoundedLayout(
                dark,
                Color.rgb(
                    71,
                    85,
                    105
                ),
                14f
            )

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            14,
            13,
            14,
            13
        )

        val nameText =
            createText(
                name,
                13f,
                white,
                Typeface.BOLD
            )

        row.addView(
            nameText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val priceText =
            createText(
                formatNumber(price),
                14f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        priceText.gravity =
            Gravity.CENTER

        row.addView(
            priceText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.4f
            )
        )

        val movement =
            createText(
                arrow,
                18f,
                arrowColor,
                Typeface.BOLD
            )

        movement.gravity =
            Gravity.CENTER

        row.addView(
            movement,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.7f
            )
        )

        pricesContainer.addView(
            row,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {

                setMargins(
                    0,
                    0,
                    0,
                    5
                )
            }
        )
    }
        // =========================================================
    // UPDATE NOTIFICATION
    // =========================================================

    private fun updateNotification() {

        if (!notificationEnabled) {
            return
        }

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

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

        if (price <= 0.0) {
            return
        }

        val notificationText =
            "عيار $selectedKarat: ${formatNumber(price)} جنيه/جرام"

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_menu_info_details
                )
                .setContentTitle(
                    "GOLD LIVE"
                )
                .setContentText(
                    notificationText
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "الذهب عيار $selectedKarat\n" +
                            "السعر: ${formatNumber(price)} جنيه/جرام\n" +
                            "الأونصة: ${formatNumber(ounceUsd)} USD\n" +
                            "الدولار: ${formatNumber(usdEgp)} EGP"
                        )
                )
                .setOngoing(
                    true
                )
                .setOnlyAlertOnce(
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
    // CANCEL NOTIFICATION
    // =========================================================

    private fun cancelNotification() {

        NotificationManagerCompat
            .from(this)
            .cancel(
                notificationId
            )
    }

    // =========================================================
    // ACTIVITY DESTROY
    // =========================================================

    override fun onDestroy() {

        handler.removeCallbacks(
            updateTask
        )

        cancelNotification()

        super.onDestroy()
    }
}
