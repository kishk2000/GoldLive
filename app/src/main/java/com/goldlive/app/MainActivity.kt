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
import androidx.core.view.WindowCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val apiUrl =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId =
        "gold_live"

    private val notificationId =
        1001

    private val notificationPermissionCode =
        200

    private val handler =
        Handler(Looper.getMainLooper())

    private var selectedKarat =
        21

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

    private var notificationEnabled =
        false

    private lateinit var globalPriceText: TextView
    private lateinit var dollarPriceText: TextView
    private lateinit var heroPriceText: TextView
    private lateinit var selectedKaratText: TextView
    private lateinit var statusText: TextView
    private lateinit var spinner: Spinner
    private lateinit var notificationButton: Button
    private lateinit var pricesContainer: LinearLayout
    private lateinit var localOunceText: TextView
    private lateinit var goldCoinText: TextView

    private val numberFormat =
        DecimalFormat("#,##0.00")

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

    private val grayText =
        Color.GRAY

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
            Build.VERSION.SDK_INT >= 23
        ) {

            window.decorView.systemUiVisibility =
                0
        }

        window.navigationBarColor =
            cream

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
        // TOP HEADER
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
                Color.rgb(
                    226,
                    232,
                    240
                ),
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
        // GLOBAL / DOLLAR MINI CARDS
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
                "USD",
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
                "سعر الذهب عيار 21",
                15f,
                Color.rgb(
                    226,
                    232,
                    240
                ),
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

        val heroStatus =
            createText(
                "▲ السعر الحالي",
                12f,
                green,
                Typeface.BOLD
            )

        heroStatus.gravity =
            Gravity.CENTER

        heroStatus.setPadding(
            0,
            10,
            0,
            0
        )

        hero.addView(
            heroStatus
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
                "اختر عيار الذهب",
                14f,
                dark,
                Typeface.BOLD
            )
        )

        addGap(
            selectorCard,
            8
        )

        spinner =
    Spinner(this)

spinner.setBackgroundColor(
    Color.rgb(
val spinnerBackground =
    GradientDrawable().apply {

        setColor(
            Color.rgb(
                241,
                245,
                249
            )
        )

        setStroke(
            2,
            gold
        )

        cornerRadius =
            14f *
            resources.displayMetrics.density
    }

spinner.background =
    spinnerBackground

spinner.setPadding(
    18,
    0,
    18,
    0
)

val karatNames =
    arrayOf(
        "عيار 24",
        "عيار 21",
        "عيار 18",
        "عيار 14"
    )

val adapter =
    ArrayAdapter(
        this,
        android.R.layout.simple_spinner_item,
        karatNames
    )

adapter.setDropDownViewResource(
    android.R.layout.simple_spinner_dropdown_item
)

spinner.adapter =
    adapter

spinner.setSelection(
    1,
    false
)

selectorCard.addView(
    spinner,
    LinearLayout.LayoutParams(
        -1,
        52
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

        spinner.setOnItemSelectedListener(
            object :
                AdapterView.OnItemSelectedListener {

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
                            2 -> 18
                            else -> 14
                        }

                    updateLocalPrice()

                    updateNotification()
                }
            }
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

        content.addView(
            createText(
                "أسعار الذهب",
                20f,
                dark,
                Typeface.BOLD
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
                1f
            )
        )

        val headerPrice =
            createText(
                "السعر",
                13f,
                white,
                Typeface.BOLD
            )

        headerPrice.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerPrice,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val headerMovement =
            createText(
                "الحركة",
                13f,
                white,
                Typeface.BOLD
            )

        headerMovement.gravity =
            Gravity.CENTER

        tableHeader.addView(
            headerMovement,
            LinearLayout.LayoutParams(
                0,
                -2,
                0.6f
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

        // =====================================================
        // STATUS
        // =====================================================

        addGap(
            content,
            12
        )

        statusText =
            createText(
                "● جاري الاتصال بمصدر الأسعار...",
                12f,
                grayText,
                Typeface.NORMAL
            )

        statusText.gravity =
            Gravity.CENTER

        content.addView(
            statusText,
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

        notificationButton.isAllCaps =
            false

        val notificationBackground =
            GradientDrawable()

        notificationBackground.setColor(
            dark2
        )

        notificationBackground.setStroke(
            2,
            gold
        )

        notificationBackground.cornerRadius =
            22f

        notificationButton.background =
            notificationBackground

        notificationButton.setPadding(
            10,
            4,
            10,
            4
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
                52
            )
        )

        addGap(
            content,
            15
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
            15
        )

        // =====================================================
        // INFORMATION CARD
        // =====================================================

        val infoCard =
            createRoundedLayout(
                dark,
                gold,
                20f
            )

        infoCard.orientation =
            LinearLayout.VERTICAL

        infoCard.setPadding(
            16,
            16,
            16,
            16
        )

        infoCard.addView(
            createText(
                "معلومات التطبيق",
                15f,
                Color.rgb(
                    255,
                    215,
                    70
                ),
                Typeface.BOLD
            )
        )

        addGap(
            infoCard,
            8
        )

        val infoText =
            createText(
                "• تحديث تلقائي للأسعار\n" +
                "• الأونصة العالمية بالدولار\n" +
                "• سعر الدولار مقابل الجنيه\n" +
                "• أسعار عيارات الذهب المختلفة\n" +
                "• شريط أسعار اختياري",
                12f,
                Color.rgb(
                    226,
                    232,
                    240
                ),
                Typeface.NORMAL
            )

        infoText.gravity =
            Gravity.RIGHT

        infoCard.addView(
            infoText
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
    android.view.ViewGroup.LayoutParams(
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
    // CREATE ROUNDED LAYOUT
    // =========================================================

    private fun createRoundedLayout(
        backgroundColor: Int,
        strokeColor: Int,
        radius: Float
    ): LinearLayout {

        val layout =
            LinearLayout(this)

        val drawable =
            GradientDrawable()

        drawable.setColor(
            backgroundColor
        )

        drawable.setStroke(
            1,
            strokeColor
        )

        drawable.cornerRadius =
            radius *
            resources.displayMetrics.density

        layout.background =
            drawable

        return layout
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

        val tv =
            TextView(this)

        tv.text =
            text

        tv.textSize =
            size

        tv.setTextColor(
            color
        )

        tv.setTypeface(
            null,
            style
        )

        tv.gravity =
            Gravity.CENTER_VERTICAL

        tv.includeFontPadding =
            true

        return tv
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

                if (!success) {

                    runOnUiThread {

                        statusText.text =
                            "● فشل الحصول على الأسعار"

                        statusText.setTextColor(
                            red
                        )
                    }

                    return@thread
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
                    json.optDouble(
                        "gram14",
                        gram18 * 14.0 / 18.0
                    )

                goldCoin =
                    json.optDouble(
                        "goldCoin",
                        gram21 * 8.0
                    )

                localOunce =
                    json.optDouble(
                        "ounce",
                        0.0
                    )

                runOnUiThread {

                    updateInterface()

                    statusText.text =
                        "● آخر تحديث: الآن"

                    statusText.setTextColor(
                        green
                    )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر الاتصال بمصدر الأسعار"

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

        updateLocalPrice()

        updatePricesTable()

        updateNotification()
    }
        // =========================================================
    // UPDATE LOCAL PRICE
    // =========================================================

private fun updateLocalPrice() {

    val price =
        when (selectedKarat) {
            24 -> gram24
            21 -> gram21
            18 -> gram18
            14 -> gram14
            else -> gram21
        }

    if (::localOunceText.isInitialized) {
        localOunceText.text =
            formatNumber(localOunce)
    }

    if (::goldCoinText.isInitialized) {
        goldCoinText.text =
            formatNumber(goldCoin)
    }

    if (::selectedKaratText.isInitialized) {
        selectedKaratText.text =
            "السعر الحالي لعيار $selectedKarat"
    }

    if (::heroPriceText.isInitialized) {
        heroPriceText.text =
            formatNumber(price)
    }
}

    // =========================================================
    // UPDATE PRICES TABLE
    // =========================================================

    private fun updatePricesTable() {

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
            "أونصة محلي",
            localOunce,
            "▲",
            green
        )
    }

    // =========================================================
    // ADD PRICE ROW
    // =========================================================

    private fun addPriceRow(
        karat: String,
        price: Double,
        movement: String,
        movementColor: Int
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
            12,
            14,
            12
        )

        val karatText =
            createText(
                if (
                    karat == "جنيه الذهب" ||
                    karat == "أونصة محلي"
                ) {
                    karat
                } else {
                    "عيار $karat"
                },
                13f,
                white,
                Typeface.BOLD
            )

        karatText.gravity =
            Gravity.CENTER

        row.addView(
            karatText,
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
                1f
            )
        )

        val movementText =
            createText(
                movement,
                17f,
                movementColor,
                Typeface.BOLD
            )

        movementText.gravity =
            Gravity.CENTER

        row.addView(
            movementText,
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
            )
        )

        addGap(
            pricesContainer,
            5
        )
    }

    // =========================================================
    // FORMAT NUMBER
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (value <= 0.0) {
            return "--"
        }

        return DecimalFormat("#,##0.00")
            .format(value)
    }

    // =========================================================
    // NOTIFICATION
    // =========================================================

    private fun updateNotification() {

        if (!notificationEnabled) {
            return
        }

        if (gram21 <= 0.0) {
            return
        }

        if (Build.VERSION.SDK_INT >= 33) {

            val permission =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )

            if (
                permission !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val price =
            when (selectedKarat) {
                24 -> gram24
                21 -> gram21
                18 -> gram18
                14 -> gram14
                else -> gram21
            }

        val notificationText =
            "عيار $selectedKarat: " +
            formatNumber(price) +
            " جنيه / جرام"

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "GOLD LIVE"
                )
                .setContentText(
                    notificationText
                )
                .setOngoing(
                    true
                )
                .setOnlyAlertOnce(
                    true
                )
                .setSilent(
                    true
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setShowWhen(
                    false
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
    // LIFECYCLE
    // =========================================================

    override fun onDestroy() {

        handler.removeCallbacks(
            updateTask
        )

        cancelNotification()

        super.onDestroy()
    }
}
