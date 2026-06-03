package com.zchat.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import javax.inject.Inject

class TextInjector @Inject constructor() {

    fun injectText(
        service: AccessibilityService,
        text: String
    ): Boolean {
        val rootNode = service.rootInActiveWindow ?: return false

        try {
            // Кӯшиш барои ёфтани майдони воридот
            val focusedNode = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            if (focusedNode != null) {
                return setTextOnNode(focusedNode, text)
            }

            // Агар фокус набошад, ҳама элементҳоро ҷустуҷӯ мекунем
            val editTexts = mutableListOf<AccessibilityNodeInfo>()
            rootNode.collectEditTexts(editTexts)

            for (node in editTexts) {
                if (node.isFocused || node.isFocusable) {
                    val success = setTextOnNode(node, text)
                    node.recycle()
                    if (success) return true
                }
                node.recycle()
            }
        } finally {
            rootNode.recycle()
        }

        // Усули алтернативӣ: ба буфер нусхабардорӣ
        return try {
            clipText(service, text)
            true
        } catch (e: Exception) {
            Log.e("TextInjector", "Хатогӣ дар ҷойгиркунии матн", e)
            false
        }
    }

    private fun setTextOnNode(node: AccessibilityNodeInfo, text: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val args = android.os.Bundle()
            args.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            return node.performAction(
                AccessibilityNodeInfo.ACTION_SET_TEXT,
                args
            )
        }
        return false
    }

    private fun clipText(service: AccessibilityService, text: String) {
        val clipboard = service.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("ZChat", text)
        clipboard.setPrimaryClip(clip)
    }
}

private fun AccessibilityNodeInfo.collectEditTexts(result: MutableList<AccessibilityNodeInfo>) {
    if (className == "android.widget.EditText" ||
        className == "android.widget.MultiAutoCompleteTextView" ||
        className?.contains("EditText") == true
    ) {
        result.add(this)
        return
    }

    for (i in 0 until childCount) {
        val child = getChild(i) ?: continue
        child.collectEditTexts(result)
        child.recycle()
    }
}
