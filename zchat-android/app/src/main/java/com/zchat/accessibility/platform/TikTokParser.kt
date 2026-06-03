package com.zchat.accessibility.platform

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.zchat.accessibility.ParsedChatData
import com.zchat.accessibility.ScreenParser

class TikTokParser : ScreenParser {

    override fun getPlatformName(): String = "TikTok"

    override fun parse(event: AccessibilityEvent): ParsedChatData? {
        val rootNode = event.source ?: return null

        try {
            val contactName = findContactName(rootNode)
            val messages = findMessages(rootNode)

            if (messages.isNotEmpty()) {
                return ParsedChatData(
                    platform = getPlatformName(),
                    contactName = contactName ?: "Номаълум",
                    messages = messages
                )
            }
        } finally {
            rootNode.recycle()
        }

        return null
    }

    private fun findContactName(root: AccessibilityNodeInfo): String? {
        val textViews = mutableListOf<AccessibilityNodeInfo>()
        collectTextViews(root, textViews)

        for (tv in textViews) {
            val text = tv.text?.toString() ?: continue
            if (text.length in 2..40 &&
                !text.contains("TikTok") &&
                !text.contains("Search") &&
                !text.contains("Поиск") &&
                !text.contains("Messages")
            ) {
                tv.recycle()
                return text.trim()
            }
            tv.recycle()
        }
        return null
    }

    private fun findMessages(root: AccessibilityNodeInfo): List<String> {
        val messages = mutableListOf<String>()
        val textViews = mutableListOf<AccessibilityNodeInfo>()
        collectTextViews(root, textViews)

        for (tv in textViews) {
            val text = tv.text?.toString()
            if (!text.isNullOrBlank() && text.length < 300) {
                messages.add(text)
            }
        }

        return messages.takeLast(20)
    }

    private fun collectTextViews(node: AccessibilityNodeInfo, result: MutableList<AccessibilityNodeInfo>) {
        if (node.className == "android.widget.TextView" ||
            node.className?.contains("TextView") == true
        ) {
            result.add(node)
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectTextViews(child, result)
            child.recycle()
        }
    }
}
