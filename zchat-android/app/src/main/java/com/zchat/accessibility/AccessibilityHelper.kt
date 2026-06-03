package com.zchat.accessibility

object AccessibilityHelper {
    private var service: ZChatAccessibilityService? = null
    var lastParsedData: ParsedChatData? = null
        private set

    fun register(service: ZChatAccessibilityService) {
        this.service = service
    }

    fun unregister() {
        service = null
    }

    fun isServiceRunning(): Boolean = service != null

    fun getCurrentPackage(): String = service?.currentPackage ?: ""

    fun injectText(text: String): Boolean {
        return service?.injectText(text) ?: false
    }

    fun readCurrentChat(): ParsedChatData? {
        lastParsedData = service?.getChatData()
        return lastParsedData
    }
}
