from rest_framework import status, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from drf_spectacular.utils import extend_schema

from .models import AnalysisRequest, AnalysisResult
from .serializers import AnalysisRequestSerializer, AnalyzeResponseSerializer
from .services.gemini_service import GeminiService


@extend_schema(tags=["AI"])
class AnalyzeChatView(APIView):
    serializer_class = AnalysisRequestSerializer

    def post(self, request):
        serializer = AnalysisRequestSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        req = serializer.save(user=request.user)

        service = GeminiService()
        result = service.analyze_chat(
            platform=req.platform,
            contact_name=req.contact_name,
            chat_history=req.chat_history,
            language=req.language or request.user.language,
        )

        AnalysisResult.objects.create(
            request=req,
            suggested_reply=result["suggested_reply"],
            context_summary=result.get("context_summary", ""),
            confidence=result.get("confidence", 0.0),
            raw_response=result,
        )

        return Response({
            "suggested_reply": result["suggested_reply"],
            "context_summary": result.get("context_summary", ""),
            "confidence": result.get("confidence", 0.0),
            "request_id": req.id,
        }, status=status.HTTP_200_OK)
