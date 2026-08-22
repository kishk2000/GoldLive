package com.goldlive.app

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val channelId = "gold_live"

    private lateinit var globalPriceText: TextView
    private lateinit var selectedPriceText: TextView
    private lateinit var changeText: TextView
    private lateinit var updateText: TextView
    private lateinit var karatSpinner: Spinner

    private var lastGoldPrice = 0.0

    // سعر الدولار مقابل الجنيه مؤقتًا
    // سنربطه بمصدر مباشر في الخطوة التالية
    private val usdToEgp = 49.00

    private val updateInterval = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        buildInterface()
        startPriceUpdates()
    }

    private fun buildInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 35, 28, 28)
            setBackgroundColor(Color.rgb(10, 10, 10))
        }

        val scrollView = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 30f
            setTextColor(Color.rgb(212, 175, 55))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 20)
        }

        content.addView(title)

        val subtitle = TextView(this).apply {
            text = "أسعار الذهب لحظيًا"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 25)
        }

        content.addView(subtitle)

        content.addView(
            createCard(
                "🌍 الذهب العالمي",
                "جارٍ تحميل السعر..."
            ).also {
                globalPriceText = it.findViewWithTag("price")
            }
        )

        addSpace(content, 18)

        val selectedTitle = TextView(this).apply {
            text = "🇪🇬 السعر المحلي"
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(0, 10, 0, 10)
        }

        content.addView(selectedTitle)

        karatSpinner = Spinner(this)

        val karats = arrayOf(
            "عيار 24",
            "عيار 21",
            "عيار 18"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            karats
        )

        karatSpinner.adapter = adapter

        content.addView(karatSpinner)

        addSpace(content, 10)

        selectedPriceText = TextView(this).apply {
            text = "جارٍ تحميل السعر..."
            textSize = 30f
            setTextColor(Color.rgb(212, 175, 55))
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 15)
        }

        content.addView(selectedPriceText)

        changeText = TextView(this).apply {
            text = "—"
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 5, 0, 5)
        }

        content.addView(changeText)

        addSpace(content, 15)

        updateText = TextView(this).apply {
            text = "جارٍ الاتصال بمصدر الأسعار..."
            textSize = 13f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 10)
        }

        content.addView(updateText)

        addSpace(content, 20)

        val notificationButton = Button(this).apply {

            text = "🔔 تفعيل شريط الأسعار"

            setOnClickListener {
                showGoldNotification(
                    lastGoldPrice
                )
            }
        }

        content.addView(notificationButton)

        addSpace(content, 15)

        val info = TextView(this).apply {
            text =
                "يتم تحديث السعر تلقائيًا كل ثانيتين\n" +
                "السعر المحلي تقديري حسب السعر العالمي وسعر الدولار."
            textSize = 13f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(10, 10, 10, 10)
        }

        content.addView(info)

        scrollView.addView(content)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)

        karatSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateLocalPrice(lastGoldPrice)
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun createCard(
        title: String,
        price: String
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 25, 25, 25)
            setBackgroundColor(Color.rgb(25, 25, 25))
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        card.addView(titleView)

        val priceView = TextView(this).apply {
            text = price
            textSize = 30f
            setTextColor(Color.rgb(212, 175, 55))
            gravity = Gravity.CENTER
            tag = "price"
            setPadding(0, 15, 0, 5)
        }

        card.addView(priceView)

        return card
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space = Space(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    private fun startPriceUpdates() {

        thread {

            while (true) {

                try {

                    val price = getGoldPrice()

                    runOnUiThread {

                        if (price > 0) {

                            updatePrices(price)

                        } else {

                            updateText.text =
                                "تعذر الحصول على السعر"
                        }
                    }

                } catch (e: Exception) {

                    runOnUiThread {

                        updateText.text =
                            "خطأ في الاتصال"
                    }
                }

                Thread.sleep(updateInterval)
            }
        }
    }

    private fun getGoldPrice(): Double {

        val url =
            URL("https://api.gold-api.com/price/XAU")

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val response =
            connection.inputStream
                .bufferedReader()
                .use {
                    it.readText()
                }

        connection.disconnect()

        val regex =
            Regex("\"price\"\\s*:\\s*([0-9.]+)")

        val match =
            regex.find(response)

        return match
            ?.groupValues
            ?.get(1)
            ?.toDoubleOrNull()
            ?: 0.0
    }

    private fun updatePrices(
        goldPrice: Double
    ) {

        globalPriceText.text =
            String.format(
                Locale.US,
                "$%,.2f / oz",
                goldPrice
            )

        updateLocalPrice(goldPrice)

        if (lastGoldPrice > 0) {

            val difference =
                goldPrice - lastGoldPrice

            if (difference > 0) {

                changeText.text =
                    String.format(
                        Locale.US,
                        "▲ +%.2f",
                        difference
                    )

                changeText.setTextColor(
                    Color.rgb(60, 200, 110)
                )

            } else if (difference < 0) {

                changeText.text =
                    String.format(
                        Locale.US,
                        "▼ %.2f",
                        difference
                    )

                changeText.setTextColor(
                    Color.rgb(230, 70, 70)
                )

            } else {

                changeText.text = "—"

                changeText.setTextColor(
                    Color.LTGRAY
                )
            }
        }

        lastGoldPrice = goldPrice

        updateText.text =
            "آخر تحديث: الآن"
    }

    private fun updateLocalPrice(
        goldPrice: Double
    ) {

        if (goldPrice <= 0) {
            return
        }

        val position =
            karatSpinner.selectedItemPosition

        val purity =
            when (position) {
                0 -> 0.999
                1 -> 0.875
                else -> 0.750
            }

        val gram24 =
            (goldPrice * usdToEgp) / 31.1034768

        val gramKarat =
            gram24 * purity

        selectedPriceText.text =
            String.format(
                Locale.US,
                "%,.0f ج / جرام",
                gramKarat
            )
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

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showGoldNotification(
        goldPrice: Double
    ) {

        if (goldPrice <= 0) {
            Toast.makeText(
                this,
                "انتظر تحميل السعر أولًا",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val position =
            karatSpinner.selectedItemPosition

        val karat =
            when (position) {
                0 -> 24
                1 -> 21
                else -> 18
            }

        val purity =
            when (karat) {
                24 -> 0.999
                21 -> 0.875
                else -> 0.750
            }

        val gram24 =
            (goldPrice * usdToEgp) / 31.1034768

        val localPrice =
            gram24 * purity

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "🟡 GoldLive"
                )
                .setContentText(
                    String.format(
                        Locale.US,
                        "🌍 $%,.2f | 🇪🇬 %dK %,.0f ج",
                        goldPrice,
                        karat,
                        localPrice
                    )
                )
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                1001,
                notification
            )

        Toast.makeText(
            this,
            "تم تفعيل شريط الأسعار",
            Toast.LENGTH_SHORT
        ).show()
    }
}
