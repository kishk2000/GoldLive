package com.goldlive.app

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : Activity() {

    private val channelId = "gold_live"

    private val goldColor = Color.rgb(212, 175, 55)
    private val backgroundColor = Color.rgb(10, 10, 10)
    private val whiteColor = Color.WHITE
    private val grayColor = Color.rgb(160, 160, 160)
    private val greenColor = Color.rgb(60, 200, 110)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 30)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 30f
            setTextColor(goldColor)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 25)
        }

        root.addView(title)

        val globalTitle = TextView(this).apply {
            text = "الذهب العالمي"
            textSize = 18f
            setTextColor(whiteColor)
            setPadding(0, 10, 0, 5)
        }

        root.addView(globalTitle)

        val globalPrice = TextView(this).apply {
            text = "$3,400.25 / oz"
            textSize = 30f
            setTextColor(goldColor)
            setPadding(0, 5, 0, 20)
        }

        root.addView(globalPrice)

        val globalChange = TextView(this).apply {
            text = "+0.00%   تجريبي"
            textSize = 14f
            setTextColor(greenColor)
            setPadding(0, 0, 0, 25)
        }

        root.addView(globalChange)

        val egyptTitle = TextView(this).apply {
            text = "🇪🇬 أسعار الذهب في مصر"
            textSize = 19f
            setTextColor(whiteColor)
            setPadding(0, 10, 0, 15)
        }

        root.addView(egyptTitle)

        addGoldRow(root, "عيار 24", "6,686 ج")
        addGoldRow(root, "عيار 21", "5,850 ج")
        addGoldRow(root, "عيار 18", "5,014 ج")

        val update = TextView(this).apply {
            text = "آخر تحديث: تجريبي"
            textSize = 13f
            setTextColor(grayColor)
            setPadding(0, 25, 0, 20)
        }

        root.addView(update)

        val notificationButton = Button(this).apply {
            text = "🔔 تفعيل شريط الأسعار"
            setOnClickListener {
                showGoldNotification()
            }
        }

        root.addView(notificationButton)

        setContentView(root)
    }

    private fun addGoldRow(
        root: LinearLayout,
        karat: String,
        price: String
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(15, 15, 15, 15)
        }

        val name = TextView(this).apply {
            text = karat
            textSize = 20f
            setTextColor(whiteColor)
        }

        val value = TextView(this).apply {
            text = price
            textSize = 20f
            setTextColor(goldColor)
            gravity = Gravity.END
        }

        row.addView(
            name,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            value,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(row)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "GoldLive",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description = "أسعار الذهب في شريط الإشعارات"

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showGoldNotification() {

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("GoldLive")
                .setContentText(
                    "🌍 $3,400.25 | 🇪🇬 عيار 21: 5,850 ج"
                )
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
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
