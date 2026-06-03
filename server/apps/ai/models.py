from django.db import models
from apps.users.models import User


class AnalysisRequest(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    platform = models.CharField(max_length=50)
    contact_name = models.CharField(max_length=255)
    chat_history = models.TextField()
    language = models.CharField(max_length=10, default="tg")
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Дархости таҳлил"
        verbose_name_plural = "Дархостҳои таҳлил"


class AnalysisResult(models.Model):
    request = models.OneToOneField(AnalysisRequest, on_delete=models.CASCADE, related_name="result")
    suggested_reply = models.TextField()
    context_summary = models.TextField(blank=True)
    confidence = models.FloatField(default=0.0)
    raw_response = models.JSONField(default=dict, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Натиҷаи таҳлил"
        verbose_name_plural = "Натиҷаҳои таҳлил"
