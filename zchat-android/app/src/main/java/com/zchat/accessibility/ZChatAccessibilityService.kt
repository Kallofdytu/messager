package com.zchat.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.zchat.accessibility.platform.InstagramParser
import com.zchat.accessibility.platform.TelegramParser
import com.zchat.accessibility.platform.TikTokParser
import com.zchat.accessibility.platform.ViberParser
import com.zchat.accessibility.platform.WhatsAppParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ZChatAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var textInjector: TextInjector

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _parsedData = MutableSharedFlow<ParsedChatData>(replay = 1)
    val parsedData: SharedFlow<ParsedChatData> = _parsedData.asSharedFlow()

    private val platformParsers = mutableMapOf<String, ScreenParser>()
    private var currentPackage: String = ""

    override fun onCreate() {
        super.onCreate()
        platformParsers["com.whatsapp"] = WhatsAppParser()
        platformParsers["com.instagram.android"] = InstagramParser()
        platformParsers["org.telegram.messenger"] = TelegramParser()
        platformParsers["com.viber.voip"] = ViberParser()
        platformParsers["com.zhiliaoapp.musically"] = TikTokParser()
        platformParsers["com.ss.android.ugc.trill"] = TikTokParser()

        AccessibilityHelper.register(this)
        Timber.d("Хидмати дастрасӣ оғоз шуд")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                currentPackage = event.packageName?.toString() ?: ""
                Timber.d("Барномаи ҷорӣ: $currentPackage")
            }

            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                val parser = platformParsers[currentPackage]
                if (parser != null) {
                    val parsed = parser.parse(event)
                    if (parsed != null) {
                        serviceScope.launch {
                            _parsedData.emit(parsed)
                        }
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        AccessibilityHelper.unregister()
        Timber.d("Хидмати дастрасӣ қатъ карда шуд")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun injectText(text: String): Boolean {
        return textInjector.injectText(this, text)
    }

    fun getCurrentPackage(): String = currentPackage

    fun getChatData(): ParsedChatData? {
        val parser = platformParsers[currentPackage] ?: return null
        // Эмулятсияи ҳодиса барои гирифтани маълумот
        val rootNode = rootInActiveWindow ?: return null
        try {
            val event = AccessibilityEvent.obtain(
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            )
            event.packageName = currentPackage.let { android.content.ComponentName.unflattenFromString(it)?.packageName?.let { pkg -> android.content.ComponentName.unflattenFromString(it)?.packageName } ?: return@let currentPackage }
            return parser.parse(event)
        } finally {
            rootNode.recycle()
        }
    }
}
