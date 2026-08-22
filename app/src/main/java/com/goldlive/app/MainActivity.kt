package com.goldlive.app

import android.app.Activity
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
import androidx.core.view.WindowCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : Activity() {

    // =========================================================
    // API
    // =========================================================

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

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
    // UI REFERENCES
    // =========================================================

    private lateinit var globalPriceText: TextView

    private lateinit var dollarPriceText: TextView

    private lateinit var heroTitleText: TextView

    private lateinit var heroPriceText: TextView

    private lateinit var heroUnitText: TextView

    private lateinit var selectedPriceText: TextView

    private lateinit var selectedKaratText: TextView

    private lateinit var localOunceText: TextView

    private lateinit var goldCoinText: TextView

    private lateinit var statusText: TextView

    private lateinit var pricesContainer: LinearLayout

    private lateinit var karatButtonsContainer: LinearLayout

    // =========================================================
    // NUMBER FORMAT
    // =========================================================

    private val numberFormat =
        DecimalFormat(
            "#,##0.00"
        ).apply {

            decimalFormatSymbols =
                decimalFormatSymbols.apply {
                    decimalSeparator = '.'
                    groupingSeparator = ','
                }
        }

    private val wholeNumberFormat =
        DecimalFormat(
            "#,##0"
        ).apply {

            decimalFormatSymbols =
                decimalFormatSymbols.apply {
                    decimalSeparator = '.'
                    groupingSeparator = ','
                }
        }

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

    private val dark3 =
        Color.rgb(
            51,
            65,
            85
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

    private val goldBright =
        Color.rgb(
            255,
            215,
            70
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

    private val grayText =
        Color.rgb(
            100,
            116,
            139
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

        // -----------------------------------------------------
        // Keep application below phone Status Bar
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // Create interface
        // -----------------------------------------------------

        createInterface()

        // -----------------------------------------------------
        // Start automatic updates
        // -----------------------------------------------------

        handler.post(
            updateTask
        )
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

        // -----------------------------------------------------
        // SCROLL VIEW
        // -----------------------------------------------------

        val scroll =
            ScrollView(this)

        scroll.isFillViewport =
            true

        scroll.setBackgroundColor(
            cream
        )

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

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
                26f
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

        // -----------------------------------------------------
        // HEADER TEXTS
        // -----------------------------------------------------

        val headerTexts =
            LinearLayout(this)

        headerTexts.orientation =
            LinearLayout.VERTICAL

        val title =
            createText(
                "GOLD LIVE",
                25f,
                goldBright,
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

        // -----------------------------------------------------
        // LIVE INDICATOR
        // -----------------------------------------------------

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
        // TOP INFORMATION CARDS
        // =====================================================

        val miniRow =
            LinearLayout(this)

        miniRow.orientation =
            LinearLayout.HORIZONTAL

        // =====================================================
        // GLOBAL GOLD CARD
        // =====================================================

        val globalCard =
            createRoundedLayout(
                dark,
                gold,
                22f
            )

        globalCard.orientation =
            LinearLayout.VERTICAL

        globalCard.gravity =
            Gravity.CENTER

        globalCard.setPadding(
            10,
            15,
            10,
            15
        )

        globalCard.addView(
            createText(
                "🌍 الأونصة العالمية",
                12f,
                lightText,
                Typeface.BOLD
            )
        )

        globalPriceText =
            createText(
                "--",
                23f,
                goldBright,
                Typeface.BOLD
            )

        globalPriceText.gravity =
            Gravity.CENTER

        globalCard.addView(
            globalPriceText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        globalCard.addView(
            createText(
                "USD / Troy Ounce",
                10f,
                Color.rgb(
                    148,
                    163,
                    184
                ),
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

        // =====================================================
        // DOLLAR CARD
        // =====================================================

        val dollarCard =
            createRoundedLayout(
                dark,
                gold,
                22f
            )

        dollarCard.orientation =
            LinearLayout.VERTICAL

        dollarCard.gravity =
            Gravity.CENTER

        dollarCard.setPadding(
            10,
            15,
            10,
            15
        )

        dollarCard.addView(
            createText(
                "💵 الدولار",
                12f,
                lightText,
                Typeface.BOLD
            )
        )

        dollarPriceText =
            createText(
                "--",
                23f,
                goldBright,
                Typeface.BOLD
            )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarPriceText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        dollarCard.addView(
            createText(
                "EGP",
                10f,
                Color.rgb(
                    148,
                    163,
                    184
                ),
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
        // HERO CARD - START
        // =====================================================

        val hero =
            createRoundedLayout(
                dark2,
                gold,
                30f
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
                "السعر الرئيسي",
                14f,
                lightText,
                Typeface.BOLD
            )
        )

        heroTitleText =
            createText(
                "عيار 21",
                17f,
                goldLight,
                Typeface.BOLD
            )

        heroTitleText.gravity =
            Gravity.CENTER

        heroTitleText.setPadding(
            0,
            5,
            0,
            2
        )

        hero.addView(
            heroTitleText
        )

        heroPriceText =
            createText(
                "--",
                50f,
                goldBright,
                Typeface.BOLD
            )

        heroPriceText.gravity =
            Gravity.CENTER

        hero.addView(
            heroPriceText
        )

        heroUnitText =
            createText(
                "جنيه / جرام",
                13f,
                lightText,
                Typeface.NORMAL
            )

        heroUnitText.gravity =
            Gravity.CENTER

        hero.addView(
            heroUnitText
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
                24f
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
                "اختر السعر الرئيسي",
                15f,
                dark,
                Typeface.BOLD
            )
        )

        addGap(
            selectorCard,
            10
        )

        // -----------------------------------------------------
        // KARAT BUTTONS
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // SELECTED KARAT INFORMATION
        // -----------------------------------------------------

        selectedKaratText =
            createText(
                "السعر الحالي لعيار 21",
                12f,
                grayText,
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
            selectedKaratText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
        // PRICES SECTION TITLE
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

        // -----------------------------------------------------
        // KARAT HEADER
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // PRICE HEADER
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // MOVEMENT HEADER
        // -----------------------------------------------------

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

        // =====================================================
        // PRICES CONTAINER
        // =====================================================

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
        // MARKET INFORMATION CARD
        // =====================================================

        val marketCard =
            createRoundedLayout(
                white,
                gold,
                22f
            )

        marketCard.orientation =
            LinearLayout.VERTICAL

        marketCard.setPadding(
            15,
            15,
            15,
            15
        )

        marketCard.addView(
            createText(
                "معلومات السوق",
                16f,
                dark,
                Typeface.BOLD
            )
        )

        addGap(
            marketCard,
            5
        )

        // -----------------------------------------------------
        // LOCAL OUNCE
        // -----------------------------------------------------

        localOunceText =
            createText(
                "الأونصة المحلية: --",
                14f,
                dark3,
                Typeface.NORMAL
            )

        localOunceText.setPadding(
            0,
            5,
            0,
            5
        )

        marketCard.addView(
            localOunceText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // -----------------------------------------------------
        // GOLD COIN
        // -----------------------------------------------------

        goldCoinText =
            createText(
                "جنيه الذهب: --",
                14f,
                dark3,
                Typeface.NORMAL
            )

        goldCoinText.setPadding(
            0,
            5,
            0,
            5
        )

        marketCard.addView(
            goldCoinText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        statusText =
            createText(
                "● جاري الاتصال بمصدر الأسعار...",
                12f,
                grayText,
                Typeface.NORMAL
            )

        statusText.gravity =
            Gravity.CENTER

        statusText.setPadding(
            0,
            8,
            0,
            0
        )

        marketCard.addView(
            statusText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        content.addView(
            marketCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            15
        )

        // =====================================================
        // FOOTER
        // =====================================================

        val footer =
            createText(
                "GoldLive • تحديث تلقائي للأسعار",
                11f,
                grayText,
                Typeface.NORMAL
            )

        footer.gravity =
            Gravity.CENTER

        footer.setPadding(
            0,
            5,
            0,
            20
        )

        content.addView(
            footer,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // ADD CONTENT TO SCROLL
        // =====================================================

        scroll.addView(
            content,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // ADD SCROLL TO ROOT
        // =====================================================

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        // =====================================================
        // SET CONTENT VIEW
        // =====================================================

        setContentView(
            root
        )
    }

    // =========================================================
    // CREATE KARAT BUTTON
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
            16f

        button.setTextColor(
            white
        )

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.gravity =
            Gravity.CENTER

        button.isAllCaps =
            false

        button.setPadding(
            4,
            0,
            4,
            0
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

            updateLocalPrice()

            refreshKaratButtons()
        }

        karatButtonsContainer.addView(
            button,
            LinearLayout.LayoutParams(
                0,
                -1,
                1f
            ).apply {

                setMargins(
                    4,
                    0,
                    4,
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
            i in 0 until
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
                            dark3
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

        // منع تحويل الأرقام إلى أرقام عربية/هندية
        textView.textLocale =
            Locale.US

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
    // ENGLISH NUMBERS
    // =========================================================

    private fun englishDigits(
        value: String
    ): String {

        return value
            .replace(
                '٠',
                '0'
            )
            .replace(
                '١',
                '1'
            )
            .replace(
                '٢',
                '2'
            )
            .replace(
                '٣',
                '3'
            )
            .replace(
                '٤',
                '4'
            )
            .replace(
                '٥',
                '5'
            )
            .replace(
                '٦',
                '6'
            )
            .replace(
                '٧',
                '7'
            )
            .replace(
                '٨',
                '8'
            )
            .replace(
                '٩',
                '9'
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

        val formatted =
            numberFormat.format(
                value
            )

        return englishDigits(
            formatted
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

        val formatted =
            wholeNumberFormat.format(
                value
            )

        return englishDigits(
            formatted
        )
    }

    // =========================================================
    // GET SELECTED PRICE
    // =========================================================

    private fun getSelectedPrice(): Double {

        return when (
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

                // =================================================
                // GLOBAL GOLD
                // =================================================

                ounceUsd =
                    json.optDouble(
                        "ounceUsd",
                        0.0
                    )

                // =================================================
                // USD / EGP
                // =================================================

                usdEgp =
                    json.optDouble(
                        "usdEgp",
                        0.0
                    )

                // =================================================
                // GOLD GRAMS
                // =================================================

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
                // CALCULATE 14K
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
                // GOLD COIN
                // =================================================

                goldCoin =
                    gram21 *
                    8.0

                // =================================================
                // LOCAL OUNCE
                // =================================================

                localOunce =
                    ounceUsd *
                    usdEgp

                // =================================================
                // UPDATE UI
                // =================================================

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
                getSelectedPrice()
            )

        localOunceText.text =
            "الأونصة المحلية: " +
            formatNumber(
                localOunce
            ) +
            " جنيه"

        goldCoinText.text =
            "جنيه الذهب: " +
            formatNumber(
                goldCoin
            ) +
            " جنيه"

        updateLocalPrice()

        updatePricesTable()
    }

    // =========================================================
    // UPDATE MAIN SELECTED PRICE
    // =========================================================

    private fun updateLocalPrice() {

        val price =
            getSelectedPrice()

        heroTitleText.text =
            "عيار $selectedKarat"

        heroPriceText.text =
            formatNumber(
                price
            )

        selectedKaratText.text =
            "السعر الحالي لعيار " +
            englishDigits(
                selectedKarat.toString()
            )

        selectedPriceText.text =
            "السعر: " +
            formatNumber(
                price
            ) +
            " جنيه / جرام"
    }
        // =========================================================
    // UPDATE INTERFACE
    // =========================================================

    private fun updateInterface() {

        globalPriceText.text =
            formatNumber(ounceUsd)

        dollarPriceText.text =
            formatNumber(usdEgp)

        updateHeroPrice()

        localOunceText.text =
            formatNumber(localOunce)

        goldCoinText.text =
            formatNumber(goldCoin)

        updateSelectedKaratText()

        updatePricesTable()

        refreshKaratButtons()
    }

    // =========================================================
    // UPDATE HERO PRICE
    // =========================================================

    private fun updateHeroPrice() {

        val price =
            when (selectedKarat) {

                24 -> gram24
                21 -> gram21
                18 -> gram18
                14 -> gram14

                else -> gram21
            }

        heroPriceText.text =
            formatNumber(price)

        selectedPriceText.text =
            "السعر الحالي: ${formatNumber(price)} جنيه / جرام"
    }

    // =========================================================
    // UPDATE SELECTED KARAT TEXT
    // =========================================================

    private fun updateSelectedKaratText() {

        val price =
            when (selectedKarat) {

                24 -> gram24
                21 -> gram21
                18 -> gram18
                14 -> gram14

                else -> gram21
            }

        selectedKaratText.text =
            "السعر الرئيسي: عيار $selectedKarat • " +
            "${formatNumber(price)} جنيه / جرام"
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
                14f,
                white,
                Typeface.BOLD
            )

        nameText.gravity =
            Gravity.CENTER

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
                15f,
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
                1.5f
            )
        )

        val movement =
            createText(
                arrow,
                19f,
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
                0.6f
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
                    6
                )
            }
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
            16f

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.setTextColor(
            white
        )

        button.isAllCaps =
            false

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

            updateHeroPrice()

            updateSelectedKaratText()

            refreshKaratButtons()
        }

        karatButtonsContainer.addView(
            button,
            LinearLayout.LayoutParams(
                0,
                -1,
                1f
            ).apply {

                setMargins(
                    4,
                    0,
                    4,
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
    // FORMAT NUMBER - ENGLISH DIGITS
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (
            value <= 0.0
        ) {
            return "--"
        }

        val formatted =
            numberFormat.format(
                value
            )

        return formatted
            .replace(
                '٠',
                '0'
            )
            .replace(
                '١',
                '1'
            )
            .replace(
                '٢',
                '2'
            )
            .replace(
                '٣',
                '3'
            )
            .replace(
                '٤',
                '4'
            )
            .replace(
                '٥',
                '5'
            )
            .replace(
                '٦',
                '6'
            )
            .replace(
                '٧',
                '7'
            )
            .replace(
                '٨',
                '8'
            )
            .replace(
                '٩',
                '9'
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

                goldCoin =
                    gram21 *
                    8.0

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

        updateLocalPrice()

        updatePricesTable()

    }

    // =========================================================
    // UPDATE MAIN PRICE
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

        heroPriceText.text =
            formatNumber(
                price
            )

        selectedKaratText.text =
            "السعر الحالي لعيار " +
                selectedKarat +
                ": " +
                formatNumber(
                    price
                ) +
                " جنيه / جرام"

        selectedPriceText.text =
            "السعر الرئيسي • عيار " +
                selectedKarat
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
                15f,
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
    // STOP UPDATES
    // =========================================================

    override fun onDestroy() {

        handler.removeCallbacks(
            updateTask
        )

        super.onDestroy()
    }
}
