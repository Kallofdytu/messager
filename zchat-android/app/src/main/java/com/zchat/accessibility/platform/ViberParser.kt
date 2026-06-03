package com.zchat.accessibility.platform

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.zchat.accessibility.ParsedChatData
import com.zchat.accessibility.ScreenParser

class ViberParser : ScreenParser {

    override fun getPlatformName(): String = "Viber"

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
                !text.contains("Viber") &&
                !text.contains("Search") &&
                !text.contains("Поиск")
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
        val listItems = mutableListOf<AccessibilityNodeInfo>()
        collectItems(root, listItems)

        for (item in listItems) {
            val text = item.text?.toString()
            if (!text.isNullOrBlank() && text.length < 500) {
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

    private fun collectItems(node: AccessibilityNodeInfo, result: MutableList<AccessibilityNodeInfo>) {
        if (node.className?.contains("RecyclerView") == true ||
            node.className?.contains("ListView") == true ||
            node.className?.contains("ChatMessage") == true
        ) {
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                if (!child.text.isNullOrBlank()) {
                    result.add(child)
                }
                child.recycle()
            }
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectItems(child, result)
            child.recycle()
        }
    }
}
