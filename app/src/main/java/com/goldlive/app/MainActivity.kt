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
    // AUTO UPDATE
    // =========================================================

    private val handler =
        Handler(Looper.getMainLooper())

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
        Color.rgb(
            251,
            243,
            213
        )

    private val cream2 =
        Color.rgb(
            246,
            237,
            205
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
    // NUMBER FORMAT
    // =========================================================

    private val numberFormat =
        DecimalFormat(
            "#,##0.00",
            java.text.DecimalFormatSymbols(
                Locale.US
            )
        )

    private val wholeNumberFormat =
        DecimalFormat(
            "#,##0",
            java.text.DecimalFormatSymbols(
                Locale.US
            )
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
        // Keep the application below the phone Status Bar.
        // -----------------------------------------------------

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        window.statusBarColor =
            dark2

        window.navigationBarColor =
            cream

        createInterface()

        handler.post(
            updateTask
        )
    }

    // =========================================================
    // CREATE MAIN INTERFACE
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
        // Scroll View
        // -----------------------------------------------------

        val scroll =
            ScrollView(this)

        scroll.isFillViewport =
            true

        scroll.setBackgroundColor(
            cream
        )

        // -----------------------------------------------------
        // Main Content
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // Header Texts
        // -----------------------------------------------------

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

        headerTexts.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

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

        // -----------------------------------------------------
        // Live Indicator
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
        // MARKET CARDS
        // =====================================================

        val marketRow =
            LinearLayout(this)

        marketRow.orientation =
            LinearLayout.HORIZONTAL

        // =====================================================
        // GLOBAL OUNCE CARD
        // =====================================================

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
            globalTitle,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        globalPriceText =
            createText(
                "--",
                26f,
                dark2,
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
            globalUnit,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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

        // =====================================================
        // DOLLAR CARD
        // =====================================================

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
            dollarTitle,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        dollarPriceText =
            createText(
                "--",
                26f,
                dark2,
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
            dollarUnit,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
        // HERO PRICE CARD
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
            heroKaratText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
            heroPriceText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
            heroUnitText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        selectedPriceText =
            createText(
                "السعر الحالي: -- جنيه",
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
            selectedPriceText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
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
            ),
            LinearLayout.LayoutParams(
                -1,
                -2
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

        // -----------------------------------------------------
        // Karat 24
        // -----------------------------------------------------

        createKaratButton(
            "24",
            24
        )

        // -----------------------------------------------------
        // Karat 21
        // -----------------------------------------------------

        createKaratButton(
            "21",
            21
        )

        // -----------------------------------------------------
        // Karat 18
        // -----------------------------------------------------

        createKaratButton(
            "18",
            18
        )

        // -----------------------------------------------------
        // Karat 14
        // -----------------------------------------------------

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
            statusText,
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
        // EXTRA PRICES
        // =====================================================

        val extraTitle =
            createText(
                "أسعار إضافية",
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

        // =====================================================
        // LOCAL OUNCE CARD
        // =====================================================

        val localOunceCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        localOunceCard.orientation =
            LinearLayout.HORIZONTAL

        localOunceCard.gravity =
            Gravity.CENTER_VERTICAL

        localOunceCard.setPadding(
            14,
            14,
            14,
            14
        )

        val localOunceTitle =
            createText(
                "الأونصة المحلية",
                14f,
                dark,
                Typeface.BOLD
            )

        localOunceCard.addView(
            localOunceTitle,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        localOunceText =
            createText(
                "--",
                20f,
                dark2,
                Typeface.BOLD
            )

        localOunceText.gravity =
            Gravity.CENTER

        localOunceCard.addView(
            localOunceText,
            LinearLayout.LayoutParams(
                -2,
                -2
            )
        )

        content.addView(
            localOunceCard,
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
        // GOLD COIN CARD
        // =====================================================

        val goldCoinCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        goldCoinCard.orientation =
            LinearLayout.HORIZONTAL

        goldCoinCard.gravity =
            Gravity.CENTER_VERTICAL

        goldCoinCard.setPadding(
            14,
            14,
            14,
            14
        )

        val goldCoinTitle =
            createText(
                "الجنيه الذهب",
                14f,
                dark,
                Typeface.BOLD
            )

        goldCoinCard.addView(
            goldCoinTitle,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        goldCoinText =
            createText(
                "--",
                20f,
                dark2,
                Typeface.BOLD
            )

        goldCoinText.gravity =
            Gravity.CENTER

        goldCoinCard.addView(
            goldCoinText,
            LinearLayout.LayoutParams(
                -2,
                -2
            )
        )

        content.addView(
            goldCoinCard,
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
        // UPDATE INFORMATION CARD
        // =====================================================

        val infoCard =
            createRoundedLayout(
                dark3,
                gold,
                18f
            )

        infoCard.orientation =
            LinearLayout.VERTICAL

        infoCard.setPadding(
            14,
            14,
            14,
            14
        )

        val infoTitle =
            createText(
                "معلومات التحديث",
                14f,
                goldBright,
                Typeface.BOLD
            )

        infoCard.addView(
            infoTitle,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            infoCard,
            6
        )

        val infoText =
            createText(
                "يتم تحديث الأسعار تلقائياً كل ثانيتين",
                12f,
                lightText,
                Typeface.NORMAL
            )

        infoCard.addView(
            infoText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        content.addView(
            infoCard,
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
        // ADD CONTENT TO SCROLL
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

        // =====================================================
        // ADD ROOT TO ACTIVITY
        // =====================================================

        setContentView(
            root
        )
    }

    // =========================================================
    // CREATE KARAT BUTTON
    // =========================================================

    private fun createKaratButton(
        title: String,
        karat: Int
    ) {

        val button =
            Button(this)

        button.text =
            title

        button.textSize =
            17f

        button.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        button.setTextColor(
            white
        )

        button.isAllCaps =
            false

        button.gravity =
            Gravity.CENTER

        button.setPadding(
            0,
            0,
            0,
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

            refreshKaratButtons()

            updateSelectedKarat()
        }

        karatButtonsContainer.addView(
            button,
            LinearLayout.LayoutParams(
                0,
                52,
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

        for (
            i in 0 until
            karatButtonsContainer.childCount
        ) {

            val view =
                karatButtonsContainer.getChildAt(
                    i
                )

            if (
                view !is Button
            ) {
                continue
            }

            val karat =
                view.text
                    .toString()
                    .toIntOrNull()
                    ?: continue

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

    // =========================================================
    // UPDATE SELECTED KARAT
    // =========================================================

    private fun updateSelectedKarat() {

        val price =
            getSelectedPrice()

        heroKaratText.text =
            "السعر الرئيسي • عيار $selectedKarat"

        selectedKaratText.text =
            "السعر الحالي لعيار $selectedKarat"

        if (
            price > 0.0
        ) {

            heroPriceText.text =
                formatNumber(price)

            selectedPriceText.text =
                "السعر: ${formatNumber(price)} جنيه"
        } else {

            heroPriceText.text =
                "--"

            selectedPriceText.text =
                "السعر: -- جنيه"
        }
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
    // NUMBER FORMAT
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
    // WHOLE NUMBER FORMAT
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
            extraTitle
        )

        addGap(
            content,
            8
        )

        // -----------------------------------------------------
        // LOCAL OUNCE
        // -----------------------------------------------------

        val localOunceCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        localOunceCard.orientation =
            LinearLayout.HORIZONTAL

        localOunceCard.gravity =
            Gravity.CENTER_VERTICAL

        localOunceCard.setPadding(
            14,
            14,
            14,
            14
        )

        val localOunceTitle =
            createText(
                "الأونصة المحلية",
                14f,
                dark,
                Typeface.BOLD
            )

        localOunceCard.addView(
            localOunceTitle,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        localOunceText =
            createText(
                "--",
                21f,
                dark2,
                Typeface.BOLD
            )

        localOunceText.gravity =
            Gravity.CENTER

        localOunceCard.addView(
            localOunceText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val localOunceUnit =
            createText(
                "EGP",
                11f,
                grayText,
                Typeface.NORMAL
            )

        localOunceUnit.gravity =
            Gravity.CENTER

        localOunceCard.addView(
            localOunceUnit,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.5f
            )
        )

        content.addView(
            localOunceCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addGap(
            content,
            8
        )

        // -----------------------------------------------------
        // GOLD COIN
        // -----------------------------------------------------

        val goldCoinCard =
            createRoundedLayout(
                white,
                gold,
                20f
            )

        goldCoinCard.orientation =
            LinearLayout.HORIZONTAL

        goldCoinCard.gravity =
            Gravity.CENTER_VERTICAL

        goldCoinCard.setPadding(
            14,
            14,
            14,
            14
        )

        val goldCoinTitle =
            createText(
                "الجنيه الذهب",
                14f,
                dark,
                Typeface.BOLD
            )

        goldCoinCard.addView(
            goldCoinTitle,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        goldCoinText =
            createText(
                "--",
                21f,
                dark2,
                Typeface.BOLD
            )

        goldCoinText.gravity =
            Gravity.CENTER

        goldCoinCard.addView(
            goldCoinText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1.2f
            )
        )

        val goldCoinUnit =
            createText(
                "EGP",
                11f,
                grayText,
                Typeface.NORMAL
            )

        goldCoinUnit.gravity =
            Gravity.CENTER

        goldCoinCard.addView(
            goldCoinUnit,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.5f
            )
        )

        content.addView(
            goldCoinCard,
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
        // FOOTER
        // =====================================================

        val footer =
            createRoundedLayout(
                dark2,
                gold,
                18f
            )

        footer.orientation =
            LinearLayout.VERTICAL

        footer.gravity =
            Gravity.CENTER

        footer.setPadding(
            14,
            14,
            14,
            14
        )

        footer.addView(
            createText(
                "GOLD LIVE",
                14f,
                goldBright,
                Typeface.BOLD
            )
        )

        val footerText =
            createText(
                "يتم تحديث الأسعار تلقائياً",
                11f,
                lightText,
                Typeface.NORMAL
            )

        footerText.gravity =
            Gravity.CENTER

        footer.addView(
            footerText
        )

        content.addView(
            footer,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // SCROLL
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
            17f

        button.setTextColor(
            white
        )

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.gravity =
            Gravity.CENTER

        button.setPadding(
            0,
            0,
            0,
            0
        )

        button.minHeight =
            0

        button.minimumHeight =
            0

        button.minWidth =
            0

        button.background =
            createBackground(
                if (karat == selectedKarat) {
                    gold
                } else {
                    dark
                },
                if (karat == selectedKarat) {
                    goldLight
                } else {
                    dark
                },
                18f
            )

        button.setOnClickListener {

            selectedKarat =
                karat

            updateSelectedKarat()

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

        for (
            i in 0 until
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
                    view.text
                        .toString()
                        .toIntOrNull()
                        ?: continue

                view.background =
                    createBackground(
                        if (karat == selectedKarat) {
                            gold
                        } else {
                            dark
                        },
                        if (karat == selectedKarat) {
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
    // UPDATE SELECTED KARAT
    // =========================================================

    private fun updateSelectedKarat() {

        val price =
            getSelectedPrice()

        heroKaratText.text =
            "السعر الرئيسي • عيار $selectedKarat"

        selectedKaratText.text =
            "السعر الحالي لعيار $selectedKarat"

        if (price > 0.0) {

            heroPriceText.text =
                formatNumber(price)

            selectedPriceText.text =
                "شراء: ${formatNumber(price)}    بيع: ${formatNumber(price)}"
        }
    }

    // =========================================================
    // GET SELECTED PRICE
    // =========================================================

    private fun getSelectedPrice(): Double {

        return when (selectedKarat) {

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

            try {

                val connection =
                    URL(apiUrl)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "GET"

                connection.connectTimeout =
                    10000

                connection.readTimeout =
                    10000

                val responseCode =
                    connection.responseCode

                if (
                    responseCode !in
                    200..299
                ) {

                    connection.disconnect()

                    runOnUiThread {

                        statusText.text =
                            "● خطأ في الاتصال"

                        statusText.setTextColor(
                            red
                        )
                    }

                    return@thread
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val json =
                    JSONObject(response)

                runOnUiThread {

                    parseApiData(
                        json
                    )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر التحديث"

                    statusText.setTextColor(
                        red
                    )
                }
            }
        }
    }

    // =========================================================
    // PARSE API DATA
    // =========================================================

    private fun parseApiData(
        json: JSONObject
    ) {

        ounceUsd =
            getDouble(
                json,
                "ounceUsd",
                "ounce",
                "globalOunce",
                "goldOunce"
            )

        usdEgp =
            getDouble(
                json,
                "usdEgp",
                "dollar",
                "usd",
                "dollarPrice"
            )

        gram24 =
            getDouble(
                json,
                "gram24",
                "price24",
                "karat24",
                "gold24"
            )

        gram21 =
            getDouble(
                json,
                "gram21",
                "price21",
                "karat21",
                "gold21"
            )

        gram18 =
            getDouble(
                json,
                "gram18",
                "price18",
                "karat18",
                "gold18"
            )

        gram14 =
            getDouble(
                json,
                "gram14",
                "price14",
                "karat14",
                "gold14"
            )

        goldCoin =
            getDouble(
                json,
                "goldCoin",
                "coin",
                "gold_coin"
            )

        localOunce =
            getDouble(
                json,
                "localOunce",
                "ounceLocal",
                "local_ounce"
            )

        updateInterface()
    }

    // =========================================================
    // GET DOUBLE SAFELY
    // =========================================================

    private fun getDouble(
        json: JSONObject,
        vararg keys: String
    ): Double {

        for (
            key in keys
        ) {

            if (
                json.has(key) &&
                !json.isNull(key)
            ) {

                try {

                    return when (
                        val value =
                            json.get(key)
                    ) {

                        is Number ->
                            value.toDouble()

                        is String ->
                            value
                                .replace(
                                    ",",
                                    ""
                                )
                                .toDoubleOrNull()
                                ?: 0.0

                        else ->
                            0.0
                    }

                } catch (
                    _: Exception
                ) {
                }
            }
        }

        return 0.0
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

        localOunceText.text =
            formatNumber(
                localOunce
            )

        goldCoinText.text =
            formatNumber(
                goldCoin
            )

        updateSelectedKarat()

        updatePricesTable()

        statusText.text =
            "● مباشر"

        statusText.setTextColor(
            green
        )
    }

    // =========================================================
    // UPDATE PRICES TABLE
    // =========================================================

    private fun updatePricesTable() {

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
        karat: String,
        price: Double
    ) {

        val row =
            createRoundedLayout(
                if (
                    karat.toInt() ==
                    selectedKarat
                ) {
                    cream2
                } else {
                    white
                },
                if (
                    karat.toInt() ==
                    selectedKarat
                ) {
                    gold
                } else {
                    Color.LTGRAY
                },
                14f
            )

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            10,
            12,
            10,
            12
        )

        val karatText =
            createText(
                "عيار $karat",
                14f,
                dark,
                Typeface.BOLD
            )

        karatText.gravity =
            Gravity.CENTER

        row.addView(
            karatText,
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
                price > 0
            ) {
                price
            } else {
                0.0
            }

        val sellText =
            createText(
                formatNumber(
                    sellPrice
                ),
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
                "●",
                15f,
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

        row.setOnClickListener {

            selectedKarat =
                karat.toInt()

            updateSelectedKarat()

            refreshKaratButtons()

            updatePricesTable()
        }

        pricesContainer.addView(
            row,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {

                setMargins(
                    0,
                    3,
                    0,
                    3
                )
            }
        )
    }

    // =========================================================
    // FORMAT NUMBER
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (
            value == 0.0
        ) {

            return "--"
        }

        return numberFormat.format(
            value
        )
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

        val view =
            TextView(this)

        view.text =
            text

        view.textSize =
                size

        view.setTextColor(
            color
        )

        view.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                style
            )

        view.gravity =
            Gravity.CENTER_VERTICAL

        view.includeFontPadding =
            true

        return view
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

        layout.background =
            createBackground(
                backgroundColor,
                strokeColor,
                radius
            )

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

        return GradientDrawable().apply {

            setColor(
                backgroundColor
            )

            cornerRadius =
                radius *
                    resources
                        .displayMetrics
                        .density

            setStroke(
                1,
                strokeColor
            )
        }
    }

    // =========================================================
    // ADD GAP
    // =========================================================

    private fun addGap(
        parent: LinearLayout,
        dp: Int
    ) {

        val space =
            View(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                dpToPx(dp)
            )
        )
    }

    // =========================================================
    // ADD HORIZONTAL GAP
    // =========================================================

    private fun addHorizontalGap(
        parent: LinearLayout,
        dp: Int
    ) {

        val space =
            View(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                dpToPx(dp),
                1
            )
        )
    }

    // =========================================================
    // DP TO PX
    // =========================================================

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
            dp *
                resources
                    .displayMetrics
                    .density
            ).toInt()
    }

    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        handler.removeCallbacks(
            updateTask
        )

        super.onDestroy()
    }
}
