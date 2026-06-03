from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import PlatformViewSet, ContactViewSet, ChatViewSet, MessageViewSet, SuggestedReplyViewSet

router = DefaultRouter()
router.register("platforms", PlatformViewSet, basename="platform")
router.register("contacts", ContactViewSet, basename="contact")
router.register("chats", ChatViewSet, basename="chat")
router.register("messages", MessageViewSet, basename="message")
router.register("suggested-replies", SuggestedReplyViewSet, basename="suggested-reply")

urlpatterns = [
    path("", include(router.urls)),
]
