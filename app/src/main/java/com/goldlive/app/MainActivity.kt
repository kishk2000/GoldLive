package com.goldlive.app

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : Activity() {

    private val channelId = "gold_live"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(28, 35, 28, 28)
            setBackgroundColor(Color.rgb(9, 9, 9))
        }

        fun text(
            value: String,
            size: Float,
            color: Int = Color.WHITE
        ): TextView {
            return TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                setPadding(0, 12, 0, 12)
            }
        }

        root.addView(
            text(
                "GOLD LIVE",
                30f,
                Color.rgb(212, 175, 55)
            )
        )

        root.addView(
            text(
                "🌍 الذهب العالمي",
                18f
            )
        )

        root.addView(
            text(
                "$3,400.25 / oz",
                32f,
                Color.rgb(212, 175, 55)
            )
        )

        root.addView(
            text(
                "🇪🇬 مصر — السعر المحلي",
                18f,
                Color.rgb(60, 200, 110)
            )
        )

        root.addView(
            text(
                "عيار 24   6,686 ج\n" +
                "عيار 21   5,850 ج\n" +
                "عيار 18   5,014 ج",
                22f
            )
        )

        root.addView(
            text(
                "آخر تحديث: تجريبي",
                14f,
                Color.LTGRAY
            )
        )

        val button = Button(this).apply {
            text = "🔔 تفعيل شريط الأسعار"

            setOnClickListener {
                showGoldNotification()
            }
        }

        root.addView(button)

        setContentView(root)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "GoldLive",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description =
                "سعر الذهب في شريط الإشعارات"

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showGoldNotification() {

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle("🟡 GoldLive")
                .setContentText(
                    "🌍 $3,400.25 | 🇪🇬 21K 5,850 ج | 24K 6,686 ج"
                )
                .setOngoing(true)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(1001, notification)

        Toast.makeText(
            this,
            "تم تفعيل الإشعار الدائم",
            Toast.LENGTH_SHORT
        ).show()
    }
}
