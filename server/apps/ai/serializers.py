from rest_framework import serializers
from .models import AnalysisRequest, AnalysisResult


class AnalysisRequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = AnalysisRequest
        fields = ("id", "platform", "contact_name", "chat_history", "language")


class AnalysisResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = AnalysisResult
        fields = "__all__"


class AnalyzeResponseSerializer(serializers.Serializer):
    suggested_reply = serializers.CharField()
    context_summary = serializers.CharField()
    confidence = serializers.FloatField()
    request_id = serializers.IntegerField()
