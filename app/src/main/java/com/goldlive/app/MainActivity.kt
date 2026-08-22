package com.goldlive.app

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
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
    // DATA
    // =========================================================

    private var selectedKarat = 21

    private var ounceUsd = 0.0
    private var usdEgp = 0.0
    private var gram24 = 0.0
    private var gram21 = 0.0
    private var gram18 = 0.0
    private var gram14 = 0.0
    private var goldCoin = 0.0
    private var localOunce = 0.0

    // =========================================================
    // UI
    // =========================================================

    private lateinit var globalPriceText: TextView
    private lateinit var dollarPriceText: TextView
    private lateinit var heroKaratText: TextView
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
    // COLORS
    // =========================================================

    private val cream =
        Color.rgb(251, 243, 213)

    private val cream2 =
        Color.rgb(246, 237, 205)

    private val dark =
        Color.rgb(30, 41, 59)

    private val dark2 =
        Color.rgb(15, 23, 42)

    private val dark3 =
        Color.rgb(51, 65, 85)

    private val gold =
        Color.rgb(212, 175, 55)

    private val goldLight =
        Color.rgb(229, 169, 60)

    private val goldBright =
        Color.rgb(255, 215, 70)

    private val green =
        Color.rgb(34, 197, 94)

    private val red =
        Color.rgb(239, 68, 68)

    private val white =
        Color.WHITE

    private val lightText =
        Color.rgb(226, 232, 240)

    private val grayText =
        Color.rgb(100, 116, 139)

    // =========================================================
    // NUMBER FORMAT
    // =========================================================

    private val numberFormat =
        DecimalFormat(
            "#,##0.00",
            java.text.DecimalFormatSymbols(Locale.US)
        )

    private val wholeNumberFormat =
        DecimalFormat(
            "#,##0",
            java.text.DecimalFormatSymbols(Locale.US)
        )

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
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        window.statusBarColor =
            dark2

        window.navigationBarColor =
            cream

        createInterface()

        applySystemBarInsets()

        handler.post(updateTask)
    }

    // =========================================================
    // SYSTEM BAR INSETS
    // =========================================================

    private fun applySystemBarInsets() {

        val root =
            findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    // =========================================================
    // CREATE INTERFACE
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
            10,
            12,
            10
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

        content.setPadding(
            0,
            4,
            0,
            20
        )

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
            15,
            18,
            15
        )

        val headerTexts =
            LinearLayout(this)

        headerTexts.orientation =
            LinearLayout.VERTICAL

        val title =
            createText(
                "GOLD LIVE",
                24f,
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

        headerTexts.addView(title)

        headerTexts.addView(
            subtitle,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
            live
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
        // MARKET CARDS
        // =====================================================

        val marketRow =
            LinearLayout(this)

        marketRow.orientation =
            LinearLayout.HORIZONTAL

        // -----------------------------------------------------
        // GLOBAL OUNCE
        // -----------------------------------------------------

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
            12,
            16,
            12,
            16
        )

        val globalTitle =
            createText(
                "🌍  الأونصة العالمية",
                13f,
                dark,
                Typeface.BOLD
            )

        globalTitle.gravity =
            Gravity.CENTER

        globalCard.addView(
            globalTitle
        )

        globalPriceText =
            createText(
                "--",
                25f,
                dark2,
                Typeface.BOLD
            )

        globalPriceText.gravity =
            Gravity.CENTER

        globalCard.addView(
            globalPriceText
        )

        val globalUnit =
            createText(
                "USD / Troy Ounce",
                10f,
                grayText,
                Typeface.NORMAL
            )

        globalUnit.gravity =
            Gravity.CENTER

        globalCard.addView(
            globalUnit
        )

        marketRow.addView(
            globalCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        addHorizontalGap(
            marketRow,
            8
        )

        // -----------------------------------------------------
        // DOLLAR
        // -----------------------------------------------------

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
            12,
            16,
            12,
            16
        )

        val dollarTitle =
            createText(
                "💵  الدولار",
                13f,
                dark,
                Typeface.BOLD
            )

        dollarTitle.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarTitle
        )

        dollarPriceText =
            createText(
                "--",
                25f,
                dark2,
                Typeface.BOLD
            )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarPriceText
        )

        val dollarUnit =
            createText(
                "USD / EGP",
                10f,
                grayText,
                Typeface.NORMAL
            )

        dollarUnit.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarUnit
        )

        marketRow.addView(
            dollarCard,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        content.addView(
            marketRow,
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
        // HERO CARD
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
            20,
            20,
            20
        )

        heroKaratText =
            createText(
                "السعر الرئيسي • عيار 21",
                16f,
                lightText,
                Typeface.BOLD
            )

        heroKaratText.gravity =
            Gravity.CENTER

        hero.addView(
            heroKaratText
        )

        heroPriceText =
            createText(
                "--",
                52f,
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
                "شراء: --     بيع: --",
                14f,
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
            15,
            14,
            15
        )

        selectorCard.addView(
            createText(
                "اختار السعر الرئيسي",
                16f,
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
                56
            )
        )

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
            14
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

        pricesTitleRow.addView(
            createText(
                "أسعار الذهب",
                20f,
                dark,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        statusText =
            createText(
                "● جاري التحديث",
                11f,
                green,
                Typeface.BOLD
            )

        statusText.gravity =
            Gravity.CENTER

        pricesTitleRow.addView(
            statusText
        )

        content.addView(
            pricesTitleRow
        )

        addGap(
            content,
            8
        )
                // =====================================================
        // PRICES TABLE HEADER
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

        val headerKarat =
            createText(
                "العيار",
                13f,
                white,
                Typeface.BOLD
            )

        headerKarat.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerKarat,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.8f
            )
        )

        val headerBuy =
            createText(
                "شراء",
                13f,
                white,
                Typeface.BOLD
            )

        headerBuy.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerBuy,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val headerSell =
            createText(
                "بيع",
                13f,
                white,
                Typeface.BOLD
            )

        headerSell.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerSell,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val headerMove =
            createText(
                "الحركة",
                13f,
                white,
                Typeface.BOLD
            )

        headerMove.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerMove,
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
            14
        )

        // =====================================================
        // EXTRA MARKET CARDS
        // =====================================================

        val extraTitle =
            createText(
                "مؤشرات السوق",
                19f,
                dark,
                Typeface.BOLD
            )

        content.addView(
            extraTitle,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            8
        )

        val extraRow =
            LinearLayout(this)

        extraRow.orientation =
            LinearLayout.HORIZONTAL

        // -----------------------------------------------------
        // GOLD COIN CARD
        // -----------------------------------------------------

        val goldCoinCard =
            createRoundedLayout(
                dark,
                gold,
                20f
            )

        goldCoinCard.orientation =
            LinearLayout.VERTICAL

        goldCoinCard.gravity =
            Gravity.CENTER

        goldCoinCard.setPadding(
            14,
            14,
            14,
            14
        )

        goldCoinCard.addView(
            createText(
                "جنيه الذهب",
                12f,
                lightText,
                Typeface.BOLD
            )
        )

        goldCoinText =
            createText(
                "--",
                22f,
                goldBright,
                Typeface.BOLD
            )

        goldCoinText.gravity =
            Gravity.CENTER

        goldCoinCard.addView(
            goldCoinText
        )

        goldCoinCard.addView(
            createText(
                "8 جرام عيار 21",
                10f,
                lightText,
                Typeface.NORMAL
            )
        )

        extraRow.addView(
            goldCoinCard,
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

        // -----------------------------------------------------
        // LOCAL OUNCE CARD
        // -----------------------------------------------------

        val localOunceCard =
            createRoundedLayout(
                dark,
                gold,
                20f
            )

        localOunceCard.orientation =
            LinearLayout.VERTICAL

        localOunceCard.gravity =
            Gravity.CENTER

        localOunceCard.setPadding(
            14,
            14,
            14,
            14
        )

        localOunceCard.addView(
            createText(
                "الأونصة المحلية",
                12f,
                lightText,
                Typeface.BOLD
            )
        )

        localOunceText =
            createText(
                "--",
                22f,
                goldBright,
                Typeface.BOLD
            )

        localOunceText.gravity =
            Gravity.CENTER

        localOunceCard.addView(
            localOunceText
        )

        localOunceCard.addView(
            createText(
                "بالجنيه المصري",
                10f,
                lightText,
                Typeface.NORMAL
            )
        )

        extraRow.addView(
            localOunceCard,
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
            14
        )

        // =====================================================
        // INFORMATION CARD
        // =====================================================

        val infoCard =
            createRoundedLayout(
                cream2,
                gold,
                20f
            )

        infoCard.orientation =
            LinearLayout.VERTICAL

        infoCard.setPadding(
            16,
            15,
            16,
            15
        )

        infoCard.addView(
            createText(
                "معلومات الأسعار",
                15f,
                dark,
                Typeface.BOLD
            )
        )

        addGap(
            infoCard,
            6
        )

        infoCard.addView(
            createText(
                "الأسعار يتم تحديثها تلقائياً من مصدر البيانات.",
                11f,
                grayText,
                Typeface.NORMAL
            )
        )

        addGap(
            infoCard,
            4
        )

        infoCard.addView(
            createText(
                "السعر الرئيسي يتغير عند اختيار العيار من الأزرار بالأعلى.",
                11f,
                grayText,
                Typeface.NORMAL
            )
        )

        content.addView(
            infoCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // SCROLL + ROOT
        // =====================================================

        scroll.addView(
            content,
            ScrollView.LayoutParams(
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
            15f

        button.setTextColor(
            if (
                karat ==
                selectedKarat
            ) {
                dark2
            } else {
                white
            }
        )

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.gravity =
            Gravity.CENTER

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

            refreshKaratButtons()

            updateLocalPrice()
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
                karatButtonsContainer.getChildAt(
                    i
                )

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

                view.text =
                    karat.toString()

                view.setTextColor(
                    if (
                        karat ==
                        selectedKarat
                    ) {
                        dark2
                    } else {
                        white
                    }
                )

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

        // =====================================================
        // IMPORTANT:
        // Force English digits
        // =====================================================

        textView.setTextLocale(
            Locale.US
        )

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
                // 14 KARAT
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

                    if (
                        ::statusText.isInitialized
                    ) {

                        statusText.text =
                            "● تعذر تحديث الأسعار"

                        statusText.setTextColor(
                            red
                        )
                    }
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

        if (
            !::globalPriceText.isInitialized
        ) {
            return
        }

        globalPriceText.text =
            formatNumber(
                ounceUsd
            )

        dollarPriceText.text =
            formatNumber(
                usdEgp
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
    }

    // =========================================================
    // UPDATE MAIN PRICE
    // =========================================================

    private fun updateLocalPrice() {

        if (
            !::heroPriceText.isInitialized
        ) {
            return
        }

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

        heroKaratText.text =
            "السعر الرئيسي • عيار $selectedKarat"

        heroPriceText.text =
            formatNumber(
                price
            )

        heroUnitText.text =
            "جنيه / جرام"

        selectedKaratText.text =
            "السعر الحالي لعيار $selectedKarat: " +
            "${formatNumber(price)} جنيه / جرام"

        selectedPriceText.text =
            "السعر الحالي: ${formatNumber(price)} جنيه"

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
            gram24
        )

        addPriceRow(
            "21",
            gram21
        )

        addPriceRow(
            "18",
            gram18
        )

        addPriceRow(
            "14",
            gram14
        )
    }

    // =========================================================
    // ADD PRICE ROW
    // =========================================================

    private fun addPriceRow(
        name: String,
        price: Double
    ) {

        val row =
            createRoundedLayout(
                white,
                Color.rgb(
                    203,
                    213,
                    225
                ),
                14f
            )

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            14,
            12,
            14,
            12
        )

        val nameText =
            createText(
                name,
                14f,
                dark,
                Typeface.BOLD
            )

        nameText.gravity =
            Gravity.CENTER

        row.addView(
            nameText,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.8f
            )
        )

        val buyText =
            createText(
                formatNumber(price),
                15f,
                dark2,
                Typeface.BOLD
            )

        buyText.gravity =
            Gravity.CENTER

        row.addView(
            buyText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val sellPrice =
            if (
                price > 0.0
            ) {
                price
            } else {
                0.0
            }

        val sellText =
            createText(
                formatNumber(sellPrice),
                15f,
                dark2,
                Typeface.BOLD
            )

        sellText.gravity =
            Gravity.CENTER

        row.addView(
            sellText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val movement =
            createText(
                "▲",
                17f,
                green,
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
    // ON DESTROY
    // =========================================================

    override fun onDestroy() {

        handler.removeCallbacks(
            updateTask
        )

        super.onDestroy()
    }
}
