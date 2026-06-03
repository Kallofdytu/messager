from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    phone = models.CharField("Телефон", max_length=20, unique=True, blank=True, null=True)
    avatar = models.ImageField("Аватар", upload_to="avatars/", blank=True, null=True)
    language = models.CharField("Забон", max_length=10, default="tg")
    is_verified = models.BooleanField("Тасдиқшуда", default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Корбар"
        verbose_name_plural = "Корбарон"

    def __str__(self):
        return self.username or self.phone or str(self.id)
