from django.test import TestCase
from .services.gemini_service import GeminiService
from .services.prompt_templates import ANALYZE_CHAT_PROMPT


class PromptTemplateTest(TestCase):
    def test_analyze_prompt_contains_variables(self):
        prompt = ANALYZE_CHAT_PROMPT.format(
            contact_name="Али",
            platform="WhatsApp",
            language="tg",
            chat_history="Салом, чӣ хелед?",
        )
        self.assertIn("Али", prompt)
        self.assertIn("WhatsApp", prompt)
