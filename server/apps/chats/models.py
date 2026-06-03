from django.db import models
from apps.users.models import User


class Platform(models.Model):
    name = models.CharField("Ном", max_length=50, unique=True)
    icon = models.CharField(max_length=50, blank=True)

    class Meta:
        verbose_name = "Платформа"
        verbose_name_plural = "Платформаҳо"

    def __str__(self):
        return self.name


class Contact(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="contacts")
    platform = models.ForeignKey(Platform, on_delete=models.CASCADE)
    name = models.CharField("Ном", max_length=255)
    username = models.CharField(max_length=255, blank=True)
    phone = models.CharField(max_length=30, blank=True)
    avatar_url = models.URLField(blank=True)
    platform_contact_id = models.CharField("ID дар платформа", max_length=255, blank=True)
    last_seen = models.DateTimeField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Контакт"
        verbose_name_plural = "Контактҳо"
        unique_together = ("user", "platform", "platform_contact_id")

    def __str__(self):
        return f"{self.name} ({self.platform})"


class Chat(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="chats")
    platform = models.ForeignKey(Platform, on_delete=models.CASCADE)
    contact = models.ForeignKey(Contact, on_delete=models.CASCADE, null=True, blank=True)
    title = models.CharField("Ном", max_length=255, blank=True)
    platform_chat_id = models.CharField("ID дар платформа", max_length=255, blank=True)
    last_message_at = models.DateTimeField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = "Чат"
        verbose_name_plural = "Чатҳо"

    def __str__(self):
        return self.title or f"{self.contact} ({self.platform})"


class Message(models.Model):
    chat = models.ForeignKey(Chat, on_delete=models.CASCADE, related_name="messages")
    sender = models.CharField("Фиристанда", max_length=255)
    content = models.TextField("Матн")
    message_type = models.CharField("Навъ", max_length=20, default="text")
    sent_at = models.DateTimeField()
    is_from_user = models.BooleanField("Аз ҷониби корбар", default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Паём"
        verbose_name_plural = "Паёмҳо"
        ordering = ("sent_at",)

    def __str__(self):
        return f"{self.sender}: {self.content[:50]}"


class SuggestedReply(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    chat = models.ForeignKey(Chat, on_delete=models.CASCADE, null=True, blank=True)
    original_text = models.TextField("Матни аслӣ", blank=True)
    suggested_text = models.TextField("Матни пешниҳодшуда")
    context = models.JSONField("Контекст", default=dict, blank=True)
    is_used = models.BooleanField("Истифода шуд", default=False)
    rating = models.IntegerField("Рейтинг", null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Ҷавоби пешниҳодшуда"
        verbose_name_plural = "Ҷавобҳои пешниҳодшуда"

    def __str__(self):
        return self.suggested_text[:50]
