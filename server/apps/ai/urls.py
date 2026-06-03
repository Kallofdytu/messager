from django.urls import path
from .views import AnalyzeChatView

urlpatterns = [
    path("ai/analyze/", AnalyzeChatView.as_view(), name="ai-analyze"),
]
