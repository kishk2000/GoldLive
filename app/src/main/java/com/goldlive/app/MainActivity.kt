package com.goldlive.app

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val channelId = "gold_live"
    private val notificationId = 1001

    private val handler = Handler(Looper.getMainLooper())

    private var selectedKarat = 21

    private var goldUsd = 0.0
    private var goldEgp24 = 0.0
    private var goldEgp21 = 0.0
    private var goldEgp18 = 0.0

    private var lastUsdPrice = 0.0

    private lateinit var globalPriceText: TextView
    private lateinit var localPriceText: TextView
    private lateinit var changeText: TextView
    private lateinit var statusText: TextView
    private lateinit var karatSpinner: Spinner
    private lateinit var notificationButton: Button

    private var notificationEnabled = false

    private val formatter =
        DecimalFormat("#,##0.00")

    private val egpFormatter =
        DecimalFormat("#,##0.00")

    /*
     * تحديث الشاشة كل ثانيتين.
     */
    private val uiRunnable = object : Runnable {
        override fun run() {
            updateLocalPrice()
            updateNotification()
            handler.postDelayed(this, 2000)
        }
    }

    /*
     * جلب الأسعار من الإنترنت كل 30 ثانية
     * حتى لا نستهلك API بسرعة.
     */
    private val networkRunnable = object : Runnable {
        override fun run() {
            loadGoldPrice()
            handler.postDelayed(this, 30000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestNotificationPermission()

        buildInterface()

        loadGoldPrice()

        handler.postDelayed(uiRunnable, 2000)
        handler.postDelayed(networkRunnable, 30000)
    }

    private fun buildInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(10, 10, 12))
            setPadding(24, 28, 24, 24)
        }

        val scroll = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "GOLD LIVE"
            textSize = 30f
            setTextColor(Color.rgb(212, 175, 55))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 4)
        }

        content.addView(title)

        val subtitle = TextView(this).apply {
            text = "أسعار الذهب لحظة بلحظة"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 25)
        }

        content.addView(subtitle)

        val globalTitle = TextView(this).apply {
            text = "🌍  الذهب العالمي"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 10, 0, 5)
        }

        content.addView(globalTitle)

        globalPriceText = TextView(this).apply {
            text = "جاري تحميل السعر..."
            textSize = 32f
            setTextColor(Color.rgb(212, 175, 55))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 4)
        }

        content.addView(globalPriceText)

        val ounce = TextView(this).apply {
            text = "USD / Troy Ounce"
            textSize = 13f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }

        content.addView(ounce)

        val localTitle = TextView(this).apply {
            text = "🇪🇬  سعر الذهب في مصر"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 10, 0, 5)
        }

        content.addView(localTitle)

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
        karatSpinner.setSelection(1)

        karatSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    selectedKarat = when (position) {
                        0 -> 24
                        1 -> 21
                        else -> 18
                    }

                    updateLocalPrice()
                    updateNotification()
                }
            }

        content.addView(karatSpinner)

        localPriceText = TextView(this).apply {
            text = "-- ج / جرام"
            textSize = 29f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 5)
        }

        content.addView(localPriceText)

        changeText = TextView(this).apply {
            text = "—"
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 10)
            setTextColor(Color.GRAY)
        }

        content.addView(changeText)

        statusText = TextView(this).apply {
            text = "جاري الاتصال بمصدر الأسعار..."
            textSize = 13f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 20)
        }

        content.addView(statusText)

        notificationButton = Button(this).apply {

            text = "🔔 تفعيل شريط الأسعار"

            setOnClickListener {

                notificationEnabled = !notificationEnabled

                if (notificationEnabled) {

                    text = "🔕 إيقاف شريط الأسعار"

                    updateNotification()

                    Toast.makeText(
                        this@MainActivity,
                        "تم تفعيل شريط الأسعار",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    text = "🔔 تفعيل شريط الأسعار"

                    NotificationManagerCompat
                        .from(this@MainActivity)
                        .cancel(notificationId)

                    Toast.makeText(
                        this@MainActivity,
                        "تم إيقاف شريط الأسعار",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        content.addView(notificationButton)

        val info = TextView(this).apply {

            text = """
                
                🔄 تحديث الشاشة كل ثانيتين
                
                🌍 السعر العالمي بالدولار
                
                🇪🇬 السعر المحلي بالجنيه المصري
                
                • عيار 24
                • عيار 21
                • عيار 18
                
                🔔 يمكن إظهار السعر في شريط الإشعارات
            """.trimIndent()

            textSize = 13f
            setTextColor(Color.GRAY)
            setPadding(0, 25, 0, 20)
        }

        content.addView(info)

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
    }

    private fun loadGoldPrice() {

        thread {

            var connection: HttpURLConnection? = null

            try {

                val workerUrl = URL(
                    "https://goldlive-api.tonetone200060.workers.dev/gold"
                )

                connection =
                    workerUrl.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode != 200) {
                    throw Exception("HTTP $responseCode")
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                val json = JSONObject(response)

                if (!json.optBoolean("success", false)) {
                    throw Exception("API returned success=false")
                }

                /*
                 * قراءة أسعار مصر من الـWorker.
                 */
                val hasEgyptObject = json.has("egypt")

                if (hasEgyptObject) {

                    val egypt = json.getJSONObject("egypt")

                    goldEgp24 =
                        egypt.optDouble("gram24", 0.0)

                    goldEgp21 =
                        egypt.optDouble("gram21", 0.0)

                    goldEgp18 =
                        egypt.optDouble("gram18", 0.0)

                } else {

                    /*
                     * دعم النتيجة القديمة للـWorker.
                     */
                    goldEgp24 =
                        json.optDouble("gram24", 0.0)

                    goldEgp21 =
                        json.optDouble("gram21", 0.0)

                    goldEgp18 =
                        json.optDouble("gram18", 0.0)
                }

                /*
                 * محاولة قراءة السعر العالمي إذا كان
                 * الـWorker يرجعه.
                 */
                if (json.has("global")) {

                    val global =
                        json.getJSONObject("global")

                    val usd =
                        global.optDouble("ounce", 0.0)

                    if (usd > 0) {

                        runOnUiThread {
                            updateGlobalPrice(usd)
                        }
                    }

                } else {

                    /*
                     * الـWorker الحالي عندك لا يرجع
                     * السعر العالمي، لذلك نجلبه من
                     * المصدر العالمي الموجود في الكود
                     * القديم.
                     */
                    loadGlobalUsdPrice()
                }

                runOnUiThread {

                    updateLocalPrice()

                    statusText.text =
                        "● متصل — آخر تحديث الآن"

                    statusText.setTextColor(
                        Color.rgb(70, 200, 110)
                    )

                    updateNotification()
                }

            } catch (e: Exception) {

                runOnUiThread {

                    statusText.text =
                        "● تعذر الاتصال بمصدر الأسعار"

                    statusText.setTextColor(
                        Color.rgb(230, 80, 80)
                    )
                }

            } finally {

                connection?.disconnect()
            }
        }
    }

    private fun loadGlobalUsdPrice() {

        thread {

            var connection: HttpURLConnection? = null

            try {

                val url = URL(
                    "https://xaus.com/api/v1/spot?currency=USD&unit=oz"
                )

                connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode =
                    connection.responseCode

                if (responseCode != 200) {
                    throw Exception(
                        "Global API HTTP $responseCode"
                    )
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                val json = JSONObject(response)

                val price = when {

                    json.has("price") ->
                        json.getDouble("price")

                    json.has("spot_usd_oz") ->
                        json.getDouble("spot_usd_oz")

                    json.has("xau") ->
                        json.getJSONObject("xau")
                            .optDouble("price", 0.0)

                    else ->
                        0.0
                }

                if (price <= 0) {
                    throw Exception(
                        "Invalid global price"
                    )
                }

                runOnUiThread {
                    updateGlobalPrice(price)
                }

            } catch (e: Exception) {

                runOnUiThread {

                    /*
                     * لا نمسح السعر القديم إذا فشل
                     * المصدر العالمي.
                     */
                    if (goldUsd <= 0) {

                        globalPriceText.text =
                            "-- USD"
                    }

                    statusText.text =
                        "● تم تحديث سعر مصر — تعذر تحديث العالمي"

                    statusText.setTextColor(
                        Color.rgb(230, 180, 60)
                    )
                }

            } finally {

                connection?.disconnect()
            }
        }
    }

    private fun updateGlobalPrice(price: Double) {

        if (price <= 0) {
            return
        }

        if (lastUsdPrice > 0) {

            val difference =
                price - lastUsdPrice

            changeText.text = when {

                difference > 0 ->
                    "▲ +${formatter.format(difference)} USD"

                difference < 0 ->
                    "▼ ${formatter.format(difference)} USD"

                else ->
                    "— ثابت"
            }

            changeText.setTextColor(
                when {

                    difference > 0 ->
                        Color.rgb(70, 200, 110)

                    difference < 0 ->
                        Color.rgb(230, 80, 80)

                    else ->
                        Color.GRAY
                }
            )
        }

        lastUsdPrice = price
        goldUsd = price

        globalPriceText.text =
            "$${formatter.format(goldUsd)}"

        updateNotification()
    }

    private fun updateLocalPrice() {

        val price = when (selectedKarat) {

            24 ->
                goldEgp24

            21 ->
                goldEgp21

            18 ->
                goldEgp18

            else ->
                0.0
        }

        if (price <= 0) {

            localPriceText.text =
                "-- ج / جرام\nعيار $selectedKarat"

            return
        }

        localPriceText.text =
            "${egpFormatter.format(price)} ج / جرام\nعيار $selectedKarat"
    }

    private fun getSelectedLocalPrice(): String {

        val price = when (selectedKarat) {

            24 ->
                goldEgp24

            21 ->
                goldEgp21

            18 ->
                goldEgp18

            else ->
                0.0
        }

        return egpFormatter.format(price)
    }

    private fun updateNotification() {

        if (!notificationEnabled) {
            return
        }

        if (
            goldUsd <= 0 &&
            goldEgp24 <= 0
        ) {
            return
        }

        val localPrice =
            getSelectedLocalPrice()

        val globalPart =
            if (goldUsd > 0) {
                "🌍 $${formatter.format(goldUsd)}"
            } else {
                "🌍 -- USD"
            }

        val text =
            "$globalPart  •  🇪🇬 عيار $selectedKarat $localPrice ج"

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "GoldLive"
                )
                .setContentText(text)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setCategory(
                    NotificationCompat.CATEGORY_STATUS
                )
                .build()

        if (
            Build.VERSION.SDK_INT < 33 ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    notificationId,
                    notification
                )
        }
    }

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
                "سعر الذهب في شريط الإشعارات"

            channel.setShowBadge(false)

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    200
                )
            }
        }
    }

    override fun onDestroy() {

        handler.removeCallbacks(uiRunnable)
        handler.removeCallbacks(networkRunnable)

        super.onDestroy()
    }
}
