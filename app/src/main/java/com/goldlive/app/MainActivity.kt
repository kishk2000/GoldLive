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
    private val goldColor = Color.rgb(212, 175, 55)
    private val backgroundColor = Color.rgb(10, 10, 12)
    private val cardColor = Color.rgb(25, 25, 29)
    private val greenColor = Color.rgb(55, 210, 120)

    private var selectedKarats = "21K"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        buildInterface()
    }

    private fun buildInterface() {

        val scrollView = ScrollView(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 30)
            setBackgroundColor(backgroundColor)
        }

        // العنوان
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 28f
            setTextColor(goldColor)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val status = TextView(this).apply {
            text = "● مباشر"
            textSize = 14f
            setTextColor(greenColor)
            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(
            title,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        header.addView(status)

        root.addView(header)

        addSpace(root, 20)

        // السعر العالمي
        root.addView(
            createCard(
                "🌍  الذهب العالمي",
                "$3,400.25",
                "دولار / أونصة",
                goldColor
            )
        )

        addSpace(root, 16)

        // عنوان مصر
        root.addView(
            createSectionTitle("🇪🇬 أسعار الذهب في مصر")
        )

        addSpace(root, 8)

        // عيار 24
        root.addView(
            createGoldRow(
                "عيار 24",
                "6,686 ج",
                "+0.42%"
            )
        )

        addSpace(root, 8)

        // عيار 21
        root.addView(
            createGoldRow(
                "عيار 21",
                "5,850 ج",
                "+0.38%"
            )
        )

        addSpace(root, 8)

        // عيار 18
        root.addView(
            createGoldRow(
                "عيار 18",
                "5,014 ج",
                "+0.35%"
            )
        )

        addSpace(root, 20)

        // آخر تحديث
        val update = TextView(this).apply {
            text = "آخر تحديث: تجريبي"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(update)

        addSpace(root, 22)

        // عنوان الإشعار
        root.addView(
            createSectionTitle("🔔 شريط الأسعار")
        )

        addSpace(root, 8)

        val notificationInfo = TextView(this).apply {
            text = "اختر العيار الذي تريد ظهوره في شريط الإشعارات"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(4, 4, 4, 12)
        }

        root.addView(notificationInfo)

        // اختيار العيار
        val spinner = Spinner(this)

        val options = arrayOf(
            "عيار 24",
            "عيار 21",
            "عيار 18"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )

        spinner.adapter = adapter

        spinner.setSelection(1)

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    selectedKarats = when (position) {
                        0 -> "24K"
                        1 -> "21K"
                        else -> "18K"
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }

        root.addView(spinner)

        addSpace(root, 14)

        // زر الإشعار
        val notificationButton = Button(this).apply {

            text = "🔔 تفعيل شريط الأسعار"

            textSize = 16f

            setTextColor(Color.BLACK)

            setBackgroundColor(goldColor)

            setOnClickListener {
                showGoldNotification()
            }
        }

        root.addView(
            notificationButton,
            LinearLayout.LayoutParams(
                -1,
                60
            )
        )

        addSpace(root, 25)

        // ملاحظة
        val note = TextView(this).apply {
            text =
                "GoldLive\n" +
                "أسعار تجريبية — سيتم
