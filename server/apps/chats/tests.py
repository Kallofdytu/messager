from django.test import TestCase
from django.contrib.auth import get_user_model
from .models import Platform, Chat

User = get_user_model()


class ChatModelTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username="test", password="test")
        self.platform = Platform.objects.create(name="WhatsApp")

    def test_create_chat(self):
        chat = Chat.objects.create(
            user=self.user,
            platform=self.platform,
            title="Test Chat"
        )
        self.assertEqual(chat.title, "Test Chat")
