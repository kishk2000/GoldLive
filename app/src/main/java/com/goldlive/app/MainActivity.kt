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
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val channelId = "gold_live"

    private val gold = Color.rgb(212, 175, 55)
    private val dark = Color.rgb(10, 10, 10)
    private val card = Color.rgb(25, 25, 25)
    private val white = Color.WHITE
    private val gray = Color.rgb(170, 170, 170)
    private val green = Color.rgb(70, 210, 120)

    private var selectedKarat = "21"
    private var globalPrice = 0.0

    private lateinit var globalPriceView: TextView
    private lateinit var updateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        buildInterface()
        loadGoldPrice()
    }

    private fun buildInterface() {

        val scroll = ScrollView(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(dark)
            setPadding(20, 30, 20, 30)
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 30f
            setTextColor(gold)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 5)
        }

        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "أسعار الذهب لحظة بلحظة"
            textSize = 14f
            setTextColor(gray)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 25)
        }

        root.addView(subtitle)

        val globalCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(card)
        }

        val globalTitle = TextView(this).apply {
            text = "🌍 الذهب العالمي"
            textSize = 18f
            setTextColor(white)
        }

        globalCard.addView(globalTitle)

        globalPriceView = TextView(this).apply {
            text = "جاري التحميل..."
            textSize = 34f
            setTextColor(gold)
            setPadding(0, 15, 0, 5)
        }

        globalCard.addView(globalPriceView)

        val unit = TextView(this).apply {
            text = "USD / Ounce"
            textSize = 13f
            setTextColor(gray)
        }

        globalCard.addView(unit)

        updateView = TextView(this).apply {
            text = "جاري الاتصال بمصدر الأسعار..."
            textSize = 13f
            setTextColor(gray)
            setPadding(0, 15, 0, 0)
        }

        globalCard.addView(updateView)

        root.addView(globalCard)

        addSpace(root, 20)

        val egyptTitle = TextView(this).apply {
            text = "🇪🇬 الذهب في مصر"
            textSize = 20f
            setTextColor(white)
            setPadding(5, 5, 5, 12)
        }

        root.addView(egyptTitle)

        addGoldRow(root, "عيار 24", "سيتم تحديثه")
        addGoldRow(root, "عيار 21", "سيتم تحديثه")
        addGoldRow(root, "عيار 18", "سيتم تحديثه")

        addSpace(root, 20)

        val notificationTitle = TextView(this).apply {
            text = "🔔 شريط الأسعار"
            textSize = 20f
            setTextColor(white)
            setPadding(5, 5, 5, 10)
        }

        root.addView(notificationTitle)

        val notificationCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(card)
        }

        val chooseText = TextView(this).apply {
            text = "اختر العيار:"
            textSize = 15f
            setTextColor(gray)
        }

        notificationCard.addView(chooseText)

        val spinner = Spinner(this)

        val karats = arrayOf(
            "عيار 24",
            "عيار 21",
            "عيار 18"
        )

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            karats
        )

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedKarat = when (position) {
                        0 -> "24"
                        1 -> "21"
                        else -> "18"
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        notificationCard.addView(spinner)

        addSpace(notificationCard, 12)

        val activate = Button(this).apply {
            text = "🔔 تفعيل شريط الأسعار"

            setOnClickListener {
                showGoldNotification()
            }
        }

        notificationCard.addView(activate)

        val disable = Button(this).apply {
            text = "إيقاف شريط الأسعار"

            setOnClickListener {
                NotificationManagerCompat
                    .from(this@MainActivity)
                    .cancel(1001)

                Toast.makeText(
                    this@MainActivity,
                    "تم إيقاف شريط الأسعار",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        notificationCard.addView(disable)

        root.addView(notificationCard)

        addSpace(root, 20)

        val refresh = Button(this).apply {
            text = "🔄 تحديث السعر"

            setOnClickListener {
                loadGoldPrice()
            }
        }

        root.addView(refresh)

        addSpace(root, 15)

        updateView = TextView(this).apply {
            text = "آخر تحديث: لم يتم بعد"
            textSize = 13f
            setTextColor(gray)
            gravity = Gravity.CENTER
        }

        root.addView(updateView)

        scroll.addView(root)

        setContentView(scroll)
    }

    private fun addGoldRow(
        root: LinearLayout,
        name: String,
        price: String
    ) {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(card)
        }

        val nameView = TextView(this).apply {
            text = name
            textSize = 19f
            setTextColor(white)
        }

        val priceView = TextView(this).apply {
            text = price
            textSize = 18f
            setTextColor(gold)
            gravity = Gravity.END
        }

        row.addView(
            nameView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            priceView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(row)
    }

    private fun addSpace(
        root: LinearLayout,
        height: Int
    ) {

        root.addView(
            Space(this),
            LinearLayout.LayoutParams(1, height)
        )
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "GoldLive",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description =
                "أسعار الذهب في شريط الإشعارات"

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun loadGoldPrice() {

        updateView.text = "جاري تحديث السعر..."

        thread {

            try {

                val url = URL("https://api.gold-api.com/price/XAU")

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                val priceRegex =
                    Regex("\"price\"\\s*:\\s*([0-9.]+)")

                val match =
                    priceRegex.find(response)

                if (match != null) {

                    val price =
                        match.groupValues[1].toDouble()

                    runOnUiThread {

                        globalPrice = price

                        globalPriceView.text =
                            String.format(
                                "$%,.2f",
                                price
                            )

                        updateView.text =
                            "تم تحديث السعر العالمي"
                    }

                } else {

                    runOnUiThread {

                        globalPriceView.text =
                            "تعذر قراءة السعر"

                        updateView.text =
                            "مصدر الأسعار لم يُرجع السعر المتوقع"
                    }
                }

                connection.disconnect()

            } catch (e: Exception) {

                runOnUiThread {

                    globalPriceView.text =
                        "غير متاح"

                    updateView.text =
                        "تعذر الاتصال بالإنترنت"
                }
            }
        }
    }

    private fun showGoldNotification() {

        if (globalPrice <= 0) {

            Toast.makeText(
                this,
                "انتظر حتى يتم تحديث السعر العالمي",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle("GoldLive")
                .setContentText(
                    "🌍 $%.2f | 🇪🇬 عيار %s".format(
                        globalPrice,
                        selectedKarat
                    )
                )
                .setOngoing(true)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setCategory(
                    NotificationCompat.CATEGORY_STATUS
                )
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(1001, notification)

        Toast.makeText(
            this,
            "تم تفعيل شريط الأسعار",
            Toast.LENGTH_SHORT
        ).show()
    }
}
