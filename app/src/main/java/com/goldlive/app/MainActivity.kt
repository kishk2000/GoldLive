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

class MainActivity : Activity() {

    private val channelId = "gold_live"

    private val gold = Color.rgb(212, 175, 55)
    private val dark = Color.rgb(10, 10, 10)
    private val card = Color.rgb(25, 25, 25)
    private val white = Color.WHITE
    private val gray = Color.rgb(170, 170, 170)
    private val green = Color.rgb(70, 210, 120)
    private val red = Color.rgb(230, 80, 80)

    private var selectedKarat = "21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(dark)
            setPadding(20, 30, 20, 20)
        }

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(dark)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 30f
            setTextColor(gold)
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 5)
        }

        content.addView(title)

        val subtitle = TextView(this).apply {
            text = "أسعار الذهب لحظة بلحظة"
            textSize = 14f
            setTextColor(gray)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 25)
        }

        content.addView(subtitle)

        val globalCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(card)
        }

        val globalLabel = TextView(this).apply {
            text = "🌍  الذهب العالمي"
            textSize = 18f
            setTextColor(white)
        }

        globalCard.addView(globalLabel)

        val globalPrice = TextView(this).apply {
            text = "$3,400.25"
            textSize = 36f
            setTextColor(gold)
            setPadding(0, 15, 0, 5)
        }

        globalCard.addView(globalPrice)

        val globalUnit = TextView(this).apply {
            text = "USD / Ounce"
            textSize = 13f
            setTextColor(gray)
        }

        globalCard.addView(globalUnit)

        val change = TextView(this).apply {
            text = "▲  +0.00%   تجريبي"
            textSize = 14f
            setTextColor(green)
            setPadding(0, 15, 0, 0)
        }

        globalCard.addView(change)

        content.addView(globalCard)

        addSpace(content, 18)

        val egyptTitle = TextView(this).apply {
            text = "🇪🇬  الذهب في مصر"
            textSize = 20f
            setTextColor(white)
            setPadding(5, 5, 5, 12)
        }

        content.addView(egyptTitle)

        addGoldRow(content, "عيار 24", "6,686 ج")
        addGoldRow(content, "عيار 21", "5,850 ج")
        addGoldRow(content, "عيار 18", "5,014 ج")

        addSpace(content, 20)

        val notificationTitle = TextView(this).apply {
            text = "🔔  إعداد شريط الأسعار"
            textSize = 20f
            setTextColor(white)
            setPadding(5, 5, 5, 10)
        }

        content.addView(notificationTitle)

        val notificationCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(card)
        }

        val chooseText = TextView(this).apply {
            text = "اختر العيار الذي تريد ظهوره في الإشعار:"
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

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            karats
        )

        spinner.adapter = adapter

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
                ) {
                }
            }

        notificationCard.addView(spinner)

        addSpace(notificationCard, 12)

        val activateButton = Button(this).apply {
            text = "🔔 تفعيل شريط الأسعار"

            setOnClickListener {
                showGoldNotification()
            }
        }

        notificationCard.addView(activateButton)

        addSpace(notificationCard, 8)

        val disableButton = Button(this).apply {
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

        notificationCard.addView(disableButton)

        content.addView(notificationCard)

        addSpace(content, 20)

        val update = TextView(this).apply {
            text = "آخر تحديث: تجريبي"
            textSize = 13f
            setTextColor(gray)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 20)
        }

        content.addView(update)

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
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
            textSize = 20f
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

        addSpace(root, 4)
    }

    private fun addSpace(
        root: LinearLayout,
        height: Int
    ) {
        val space = Space(this)

        root.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
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

    private fun showGoldNotification() {

        val karatPrice = when (selectedKarat) {
            "24" -> "6,686 ج"
            "21" -> "5,850 ج"
            else -> "5,014 ج"
        }

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle("GoldLive")
                .setContentText(
                    "🌍 $3,400.25 | 🇪🇬 ${selectedKarat}K $karatPrice"
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
            "تم تفعيل شريط الأسعار لعيار $selectedKarat",
            Toast.LENGTH_SHORT
        ).show()
    }
}
