import logging
from django.conf import settings
from .prompt_templates import ANALYZE_CHAT_PROMPT

logger = logging.getLogger(__name__)


class GeminiService:
    def __init__(self):
        self.api_key = settings.GEMINI_API_KEY
        self.model_name = settings.GEMINI_MODEL

    def analyze_chat(self, platform, contact_name, chat_history, language="tg"):
        if not self.api_key:
            return self._mock_response(contact_name, language)

        try:
            import google.generativeai as genai
            genai.configure(api_key=self.api_key)
            model = genai.GenerativeModel(self.model_name)

            prompt = ANALYZE_CHAT_PROMPT.format(
                contact_name=contact_name,
                platform=platform,
                language=language,
                chat_history=chat_history,
            )

            response = model.generate_content(prompt)

            return {
                "suggested_reply": response.text.strip(),
                "context_summary": f"Муошират бо {contact_name} дар {platform}",
                "confidence": 0.85,
            }

        except Exception as e:
            logger.error(f"Gemini API error: {e}")
            return self._mock_response(contact_name, language)

    def _mock_response(self, contact_name, language):
        replics = {
            "tg": f"Ассалому алайкум, {contact_name}. Паёми шуморо гирифтам. Ба зудӣ ҷавоб медиҳам.",
            "ru": f"Здравствуйте, {contact_name}. Я получил ваше сообщение. Скоро отвечу.",
            "en": f"Hello {contact_name}. I received your message. Will reply soon.",
        }
        reply = replics.get(language, replics["tg"])
        return {
            "suggested_reply": reply,
            "context_summary": f"Conversation with {contact_name}",
            "confidence": 0.5,
        }
