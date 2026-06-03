package com.zchat.accessibility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.zchat.R
import com.zchat.data.repository.AiRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FloatingButtonService : Service() {

    @Inject
    lateinit var aiRepository: AiRepository

    @Inject
    lateinit var textInjector: TextInjector

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: View
    private lateinit var replyPopup: View
    private var isPopupVisible = false
    private var isProcessing = false

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    companion object {
        private const val CHANNEL_ID = "floating_button_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, FloatingButtonService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingButtonService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        createFloatingButton()
        createReplyPopup()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        removeAllViews()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating ZChat Button",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows floating button over other apps"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ZChat")
            .setContentText("Floating button is active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun createFloatingButton() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingButton = inflater.inflate(
            com.zchat.R.layout.floating_button, null
        )

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200
        }

        floatingButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingButton, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    if (dx * dx + dy * dy < 100) {
                        onFloatingButtonClick()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(floatingButton, params)
    }

    private fun createReplyPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        replyPopup = inflater.inflate(
            com.zchat.R.layout.reply_popup, null
        )

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        replyPopup.findViewById<Button>(com.zchat.R.id.btn_close_popup).setOnClickListener {
            hideReplyPopup()
        }

        replyPopup.findViewById<Button>(com.zchat.R.id.btn_paste).setOnClickListener {
            val replyText = replyPopup.findViewById<TextView>(com.zchat.R.id.tv_reply_text).text.toString()
            if (replyText.isNotBlank()) {
                textInjector.injectText(this@FloatingButtonService, replyText)
                hideReplyPopup()
            }
        }

        replyPopup.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                hideReplyPopup()
                true
            } else false
        }

        windowManager.addView(replyPopup, params)
        replyPopup.visibility = View.GONE
    }

    private fun onFloatingButtonClick() {
        if (isProcessing) return

        if (!AccessibilityHelper.isServiceRunning()) {
            showReply("Хидмати дастрасӣ фаъол нест.\nЛутфан онро дар Танзимот фаъол кунед.")
            openAccessibilitySettings()
            return
        }

        isProcessing = true
        showProgress()

        serviceScope.launch(Dispatchers.IO) {
            try {
                val chatData = AccessibilityHelper.readCurrentChat()
                if (chatData == null || chatData.messages.isEmpty()) {
                    launch(Dispatchers.Main) {
                        showReply("Чат ёфт нашуд.\nЛутфан ба сӯҳбат гузаред.")
                        hideProgress()
                        isProcessing = false
                    }
                    return@launch
                }

                val reply = aiRepository.getSuggestedReply(
                    contactName = chatData.contactName,
                    platform = chatData.platform,
                    chatHistory = chatData.messages.joinToString("\n"),
                    language = chatData.language
                )

                launch(Dispatchers.Main) {
                    showReply(reply.ifBlank { "Ҷавоб ёфт нашуд" })
                    hideProgress()
                    isProcessing = false
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    showReply("Хатогӣ: ${e.message}")
                    hideProgress()
                    isProcessing = false
                }
            }
        }
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun showReply(text: String) {
        replyPopup.findViewById<TextView>(com.zchat.R.id.tv_reply_text).text = text
        replyPopup.findViewById<Button>(com.zchat.R.id.btn_paste).visibility =
            if (AccessibilityHelper.isServiceRunning()) View.VISIBLE else View.GONE
        replyPopup.visibility = View.VISIBLE
        replyPopup.bringToFront()
    }

    private fun hideReplyPopup() {
        replyPopup.visibility = View.GONE
    }

    private fun showProgress() {
        replyPopup.findViewById<ProgressBar>(com.zchat.R.id.progress_bar).visibility = View.VISIBLE
        replyPopup.findViewById<TextView>(com.zchat.R.id.tv_reply_text).visibility = View.GONE
        replyPopup.findViewById<Button>(com.zchat.R.id.btn_paste).visibility = View.GONE
        replyPopup.findViewById<TextView>(com.zchat.R.id.tv_popup_title).text = "Таҳлил..."
        replyPopup.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        replyPopup.findViewById<ProgressBar>(com.zchat.R.id.progress_bar).visibility = View.GONE
        replyPopup.findViewById<TextView>(com.zchat.R.id.tv_reply_text).visibility = View.VISIBLE
        replyPopup.findViewById<TextView>(com.zchat.R.id.tv_popup_title).text = "Ҷавоби AI"
    }

    private fun removeAllViews() {
        try {
            if (::floatingButton.isInitialized && floatingButton.isAttachedToWindow) {
                windowManager.removeView(floatingButton)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error removing floating button")
        }
        try {
            if (::replyPopup.isInitialized && replyPopup.isAttachedToWindow) {
                windowManager.removeView(replyPopup)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error removing reply popup")
        }
    }
}
