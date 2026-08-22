package com.goldlive.app

import android.app.Activity
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
import android.widget.Toast
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

    private lateinit var heroPriceText: TextView

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
        )

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

    private val grayText =
        Color.rgb(
            100,
            116,
            139
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

        super.onCreate(
            savedInstanceState
        )

        // -----------------------------------------------------
        // STATUS BAR
        // -----------------------------------------------------

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        window.statusBarColor =
            dark2

        window.navigationBarColor =
            dark2

        window.decorView.systemUiVisibility =
            0

        // -----------------------------------------------------
        // CREATE UI
        // -----------------------------------------------------

        createInterface()

        // -----------------------------------------------------
        // START AUTO UPDATE
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
        // SCROLL
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

        val headerTexts =
            LinearLayout(this)

        headerTexts.orientation =
            LinearLayout.VERTICAL

        val title =
            createText(
                "GOLD LIVE",
                25f,
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
        // GLOBAL + DOLLAR CARDS
        // =====================================================

        val miniRow =
            LinearLayout(this)

        miniRow.orientation =
            LinearLayout.HORIZONTAL

        // -----------------------------------------------------
        // GLOBAL CARD
        // -----------------------------------------------------

        val globalCard =
            createRoundedLayout(
                white,
                gold,
                22f
            )

        globalCard.orientation =
            LinearLayout.VERTICAL

        globalCard.gravity =
            Gravity.CENTER

        globalCard.setPadding(
            10,
            16,
            10,
            16
        )

        globalCard.addView(
            createText(
                "🌍 الأونصة العالمية",
                13f,
                dark,
                Typeface.BOLD
            )
        )

        globalPriceText =
            createText(
                "--",
                24f,
                dark2,
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
                grayText,
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

        // -----------------------------------------------------
        // DOLLAR CARD
        // -----------------------------------------------------

        val dollarCard =
            createRoundedLayout(
                white,
                gold,
                22f
            )

        dollarCard.orientation =
            LinearLayout.VERTICAL

        dollarCard.gravity =
            Gravity.CENTER

        dollarCard.setPadding(
            10,
            16,
            10,
            16
        )

        dollarCard.addView(
            createText(
                "💵 سعر الدولار",
                13f,
                dark,
                Typeface.BOLD
            )
        )

        dollarPriceText =
            createText(
                "--",
                24f,
                dark2,
                Typeface.BOLD
            )

        dollarPriceText.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarPriceText
        )

        dollarCard.addView(
            createText(
                "USD / EGP",
                10f,
                grayText,
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
        // HERO CARD
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
            18,
            24,
            18,
            24
        )

        hero.addView(
            createText(
                "السعر الرئيسي",
                15f,
                lightText,
                Typeface.BOLD
            )
        )

        heroPriceText =
            createText(
                "--",
                52f,
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
                "عيار 21",
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
            14,
            14,
            14
        )

        selectorCard.addView(
            createText(
                "اختار العيار الرئيسي",
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
            tableHeader
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
            10
        )

        // =====================================================
        // EXTRA INFO CARDS
        // =====================================================

        val extraTitle =
            createText(
                "معلومات إضافية",
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

        // -----------------------------------------------------
        // GOLD COIN
        // -----------------------------------------------------

        val goldCoinCard =
            createRoundedLayout(
                dark,
                Color.rgb(
                    71,
                    85,
                    105
                ),
                16f
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

        goldCoinCard.addView(            createText(
                "🪙",
                24f,
                gold,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                42,
                42
            )
        )

        val goldCoinTexts =
            LinearLayout(this)

        goldCoinTexts.orientation =
            LinearLayout.VERTICAL

        goldCoinTexts.gravity =
            Gravity.CENTER_VERTICAL

        goldCoinTexts.addView(
            createText(
                "جنيه الذهب",
                13f,
                lightText,
                Typeface.BOLD
            )
        )

        goldCoinText =
            createText(
                "--",
                20f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )

        goldCoinTexts.addView(
            goldCoinText
        )

        goldCoinCard.addView(
            goldCoinTexts,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            ).apply {
                setMargins(
                    10,
                    0,
                    0,
                    0
                )
            }
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
            6,
            0,
            12
        )

        content.addView(
            statusText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        // =====================================================
        // SCROLL VIEW
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

            updateNotification()
        }

        karatButtonsContainer.addView(
            button,
            LinearLayout.LayoutParams(
                0,
                50,
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
    // UPDATE LOCAL PRICE
    // =========================================================

    private fun updateLocalPrice() {

        val price =
            when (
                selectedKarat
            ) {
                24 -> gram24
                21 -> gram21
                18 -> gram18
                14 -> gram14
                else -> gram21
            }

        heroPriceText.text =
            formatNumber(
                price
            )

        selectedKaratText.text =
            "السعر الرئيسي: عيار $selectedKarat"

        selectedPriceText.text =
            "${formatNumber(price)} جنيه / جرام"

        refreshKaratButtons()
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

        goldCoinText.text =
            formatNumber(
                goldCoin
            )

        localOunceText.text =
            formatNumber(
                localOunce
            )

        updateLocalPrice()

        updatePricesTable()
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
                    6
                )
            }
        )
    }
  
