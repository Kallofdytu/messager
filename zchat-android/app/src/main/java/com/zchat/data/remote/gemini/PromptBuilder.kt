package com.zchat.data.remote.gemini

object PromptBuilder {

    fun buildPrompt(
        contactName: String,
        platform: String,
        chatHistory: String,
        language: String
    ): String {
        val langInstruction = when (language) {
            "tg" -> "Бо забони тоҷикӣ ҷавоб диҳ."
            "ru" -> "Ответь на русском языке."
            "en" -> "Answer in English."
            else -> "Бо забони тоҷикӣ ҷавоб диҳ."
        }

        return """
Ту дастёри муошират барои платформаи $platform ҳастӣ.
Номи шахсе, ки бо ӯ муошират мекунӣ: $contactName
$langInstruction

Таърихи муошират:
$chatHistory

Ба таърихи муошират дар боло нигоҳ карда, як ҷавоби мувофиқ ва табиӣ пешниҳод кун.
Ҷавоб бояд кӯтоҳ ва ба мавзӯъ мувофиқ бошад.
Танҳо матни ҷавобро навис, бе ягон шарҳи иловагӣ.
        """.trimIndent()
    }
}
