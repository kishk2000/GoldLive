package com.goldlive.app

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.view.Gravity
import android.widget.*
import androidx.core.app.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val api =
        "https://goldlive-api.tonetone200060.workers.dev/"

    private val channelId = "gold_live"
    private val notificationId = 1001

    private val handler =
        Handler(Looper.getMainLooper())

    private var karat = 21

    private var ounceUsd = 0.0
    private var previousOunce = 0.0
    private var usdEgp = 0.0

    private var gram24 = 0.0
    private var gram21 = 0.0
    private var gram18 = 0.0

    private var notificationOn = false

    private lateinit var globalText: TextView
    private lateinit var dollarText: TextView
    private lateinit var localText: TextView
    private lateinit var changeText: TextView
    private lateinit var statusText: TextView
    private lateinit var updateText: TextView
    private lateinit var notifyButton: Button

    private lateinit var karat24Button: Button
    private lateinit var karat21Button: Button
    private lateinit var karat18Button: Button

    private val usd =
        DecimalFormat("#,##0.00")

    private val egp =
        DecimalFormat("#,##0.00")

    private val updater =
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
        state: Bundle?
    ) {
        super.onCreate(state)

        createChannel()
        requestNotifications()
        createScreen()

        handler.post(updater)
    }

    private fun dp(value: Int): Int {

        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }

    private fun goldGradient(): GradientDrawable {

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(255, 224, 120),
                Color.rgb(218, 173, 52)
            )
        ).apply {

            cornerRadius =
                dp(24).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    255,
                    239,
                    170
                )
            )
        }
    }

    private fun darkGoldCard(): GradientDrawable {

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(116, 83, 20),
                Color.rgb(72, 48, 10)
            )
        ).apply {

            cornerRadius =
                dp(22).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    225,
                    183,
                    67
                )
            )
        }
    }

    private fun lightGoldCard(): GradientDrawable {

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(255, 239, 181),
                Color.rgb(238, 205, 112)
            )
        ).apply {

            cornerRadius =
                dp(20).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    191,
                    145,
                    35
                )
            )
        }
    }

    private fun createScreen() {

        val root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.rgb(
                83,
                57,
                13
            )
        )

        val scroll =
            ScrollView(this)

        scroll.isFillViewport =
            true

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.setPadding(
            dp(16),
            dp(20),
            dp(16),
            dp(30)
        )

        val logo =
            TextView(this)

        logo.text =
            "✦  GOLD LIVE  ✦"

        logo.textSize =
            30f

        logo.setTypeface(
            null,
            Typeface.BOLD
        )

        logo.setTextColor(
            Color.rgb(
                255,
                239,
                170
            )
        )

        logo.gravity =
            Gravity.CENTER

        content.addView(
            logo
        )

        val subtitle =
            TextView(this)

        subtitle.text =
            "أسعار الذهب لحظة بلحظة"

        subtitle.textSize =
            14f

        subtitle.setTextColor(
            Color.rgb(
                255,
                239,
                190
            )
        )

        subtitle.gravity =
            Gravity.CENTER

        subtitle.setPadding(
            0,
            dp(5),
            0,
            dp(20)
        )

        content.addView(
            subtitle
        )

        val worldCard =
            LinearLayout(this)

        worldCard.orientation =
            LinearLayout.VERTICAL

        worldCard.setPadding(
            dp(18),
            dp(18),
            dp(18),
            dp(18)
        )

        worldCard.background =
            goldGradient()

        val worldTitle =
            TextView(this)

        worldTitle.text =
            "🌍  الذهب العالمي"

        worldTitle.textSize =
            17f

        worldTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        worldTitle.setTextColor(
            Color.rgb(
                75,
                52,
                8
            )
        )

        worldTitle.gravity =
            Gravity.CENTER

        worldCard.addView(
            worldTitle
        )

        globalText =
            TextView(this)

        globalText.text =
            "جاري التحميل..."

        globalText.textSize =
            36f

        globalText.setTypeface(
            null,
            Typeface.BOLD
        )

        globalText.setTextColor(
            Color.rgb(
                67,
                45,
                5
            )
        )

        globalText.gravity =
            Gravity.CENTER

        globalText.setPadding(
            0,
            dp(12),
            0,
            dp(2)
        )

        worldCard.addView(
            globalText
        )

        val ounceLabel =
            TextView(this)

        ounceLabel.text =
            "USD / Troy Ounce"

        ounceLabel.textSize =
            12f

        ounceLabel.setTextColor(
            Color.rgb(
                100,
                73,
                15
            )
        )

        ounceLabel.gravity =
            Gravity.CENTER

        worldCard.addView(
            ounceLabel
        )

        changeText =
            TextView(this)

        changeText.text =
            "—"

        changeText.textSize =
            14f

        changeText.gravity =
            Gravity.CENTER

        changeText.setTextColor(
            Color.rgb(
                80,
                55,
                8
            )
        )

        changeText.setPadding(
            0,
            dp(10),
            0,
            0
        )

        worldCard.addView(
            changeText
        )

        content.addView(
            worldCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {

                bottomMargin =
                    dp(14)
            }
        )

        val dollarCard =
            LinearLayout(this)

        dollarCard.orientation =
            LinearLayout.VERTICAL

        dollarCard.setPadding(
            dp(16),
            dp(15),
            dp(16),
            dp(15)
        )

        dollarCard.background =
            darkGoldCard()

        val dollarTitle =
            TextView(this)

        dollarTitle.text =
            "💵  سعر الدولار مقابل الجنيه"

        dollarTitle.textSize =
            15f

        dollarTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        dollarTitle.setTextColor(
            Color.rgb(
                255,
                231,
                145
            )
        )

        dollarTitle.gravity =
            Gravity.CENTER

        dollarCard.addView(
            dollarTitle
        )

        dollarText =
            TextView(this)

        dollarText.text =
            "-- جنيه"

        dollarText.textSize =
            25f

        dollarText.setTypeface(
            null,
            Typeface.BOLD
        )

        dollarText.setTextColor(
            Color.WHITE
        )

        dollarText.gravity =
            Gravity.CENTER

        dollarText.setPadding(
            0,
            dp(7),
            0,
            0
        )

        dollarCard.addView(
            dollarText
        )

        content.addView(
            dollarCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {

                bottomMargin =
                    dp(18)
            }
        )

        val egyptTitle =
            TextView(this)

        egyptTitle.text =
            "🇪🇬  أسعار الذهب في مصر"

        egyptTitle.textSize =
            20f

        egyptTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        egyptTitle.setTextColor(
            Color.rgb(
                255,
                239,
                174
            )
        )

        egyptTitle.gravity =
            Gravity.CENTER

        egyptTitle.setPadding(
            0,
            dp(4),
            0,
            dp(12)
        )

        content.addView(
            egyptTitle
        )

        val karatRow =
            LinearLayout(this)

        karatRow.orientation =
            LinearLayout.HORIZONTAL

        karatRow.gravity =
            Gravity.CENTER

        karat24Button =
            makeKaratButton(
                "عيار 24"
            )

        karat21Button =
            makeKaratButton(
                "عيار 21"
            )

        karat18Button =
            makeKaratButton(
                "عيار 18"
            )

        karatRow.addView(
            karat24Button
        )

        karatRow.addView(
            karat21Button
        )

        karatRow.addView(
            karat18Button
        )

        karat24Button.setOnClickListener {
            selectKarat(24)
        }

        karat21Button.setOnClickListener {
            selectKarat(21)
        }

        karat18Button.setOnClickListener {
            selectKarat(18)
        }

        content.addView(
            karatRow,
            LinearLayout.LayoutParams(
                -1,
                dp(56)
            ).apply {

                bottomMargin =
                    dp(14)
            }
        )

        val localCard =
            LinearLayout(this)

        localCard.orientation =
            LinearLayout.VERTICAL

        localCard.gravity =
            Gravity.CENTER

        localCard.setPadding(
            dp(15),
            dp(20),
            dp(15),
            dp(20)
        )

        localCard.background =
            lightGoldCard()

        localText =
            TextView(this)

        localText.text =
            "-- ج / جرام"

        localText.textSize =
            33f

        localText.setTypeface(
            null,
            Typeface.BOLD
        )

        localText.setTextColor(
            Color.rgb(
                65,
                43,
                5
            )
        )

        localText.gravity =
            Gravity.CENTER

        localCard.addView(
            localText
        )

        val gramLabel =
            TextView(this)

        gramLabel.text =
            "سعر الجرام"

        gramLabel.textSize =
            13f

        gramLabel.setTextColor(
            Color.rgb(
                105,
                76,
                12
            )
        )

        gramLabel.gravity =
            Gravity.CENTER

        gramLabel.setPadding(
            0,
            dp(5),
            0,
            0
        )

        localCard.addView(
            gramLabel
        )

        content.addView(
            localCard,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {

                bottomMargin =
                    dp(15)
            }
        )

        statusText =
            TextView(this)

        statusText.text =
            "● جاري الاتصال..."

        statusText.textSize =
            13f

        statusText.setTextColor(
            Color.rgb(
                255,
                235,
                160
            )
        )

        statusText.gravity =
            Gravity.CENTER

        content.addView(
            statusText
        )

        updateText =
            TextView(this)

        updateText.text =
            "تحديث تلقائي كل ثانيتين"

        updateText.textSize =
            11f

        updateText.setTextColor(
            Color.rgb(
                220,
                190,
                110
            )
        )

        updateText.gravity =
            Gravity.CENTER

        updateText.setPadding(
            0,
            dp(4),
            0,
            dp(15)
        )

        content.addView(
            updateText
        )

        notifyButton =
            Button(this)

        notifyButton.text =
            "🔔  تفعيل شريط الأسعار"

        notifyButton.textSize =
            14f

        notifyButton.setTypeface(
            null,
            Typeface.BOLD
        )

        notifyButton.setTextColor(
            Color.rgb(
                67,
                45,
                5
            )
        )

        notifyButton.background =
            goldGradient()

        notifyButton.setOnClickListener {

            notificationOn =
                !notificationOn

            if (notificationOn) {

                notifyButton.text =
                    "🔕  إيقاف شريط الأسعار"

                showNotification()

                Toast.makeText(
                    this,
                    "تم تفعيل شريط الأسعار",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                notifyButton.text =
                    "🔔  تفعيل شريط الأسعار"

                NotificationManagerCompat
                    .from(this)
                    .cancel(
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
            notifyButton,
            LinearLayout.LayoutParams(
                -1,
                dp(54)
            )
        )

        val footer =
            TextView(this)

        footer.text =
            "✦ GoldLive ✦\n" +
            "أسعار الذهب العالمية والمحلية"

        footer.textSize =
            11f

        footer.setTextColor(
            Color.rgb(
                220,
                190,
                110
            )
        )

        footer.gravity =
            Gravity.CENTER

        footer.setPadding(
            0,
            dp(22),
            0,
            0
        )

        content.addView(
            footer
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

        setContentView(root)

        selectKarat(21)
    }

    private fun makeKaratButton(
        text: String
    ): Button {

        val button =
            Button(this)

        button.text =
            text

        button.textSize =
            12f

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.setPadding(
            0,
            0,
            0,
            0
        )

        button.setTextColor(
            Color.rgb(
                255,
                236,
                170
            )
        )

        button.background =
            darkGoldCard()

        val params =
            LinearLayout.LayoutParams(
                0,
                -1
            )

        params.weight =
            1f

        params.marginStart =
            dp(4)

        params.marginEnd =
            dp(4)

        button.layoutParams =
            params

        return button
    }

    private fun selectKarat(
        value: Int
    ) {

        karat =
            value

        setKaratStyle(
            karat24Button,
            value == 24
        )

        setKaratStyle(
            karat21Button,
            value == 21
        )

        setKaratStyle(
            karat18Button,
            value == 18
        )

        showLocal()
        showNotification()
    }

    private fun setKaratStyle(
        button: Button,
        selected: Boolean
    ) {

        if (selected) {

            button.background =
                goldGradient()

            button.setTextColor(
                Color.rgb(
                    67,
                    45,
                    5
                )
            )

        } else {

            button.background =
                darkGoldCard()

            button.setTextColor(
                Color.rgb(
                    255,
                    236,
                    170
                )
            )
        }
    }

    private fun loadPrices() {

        thread {

            try {

                val connection =
                    URL(api)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod =
                    "GET"

                connection.connectTimeout =
                    5000

                connection.readTimeout =
                    5000

                val response =
                    connection
                        .inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val json =
                    JSONObject(
                        response
                    )

                if (
                    !json.optBoolean(
                        "success"
                    )
                ) {
                    throw Exception()
                }

                val newOunce =
                    json.getDouble(
                        "ounceUsd"
                    )

                val newDollar =
                    json.getDouble(
                        "usdEgp"
                    )

                val new24 =
                    json.getDouble(
                        "gram24"
                    )

                val new21 =
                    json.getDouble(
                        "gram21"
                    )

                val new18 =
                    json.getDouble(
                        "gram18"
                    )

                runOnUiThread {

                    if (
                        previousOunce > 0
                    ) {

                        val difference =
                            newOunce -
                                previousOunce

                        if (
                            difference > 0
   
