from django.test import TestCase
from .models import User


class UserModelTest(TestCase):
    def test_create_user(self):
        user = User.objects.create_user(
            username="testuser",
            password="testpass123",
            phone="+992900000000"
        )
        self.assertEqual(user.username, "testuser")
        self.assertTrue(user.check_password("testpass123"))
