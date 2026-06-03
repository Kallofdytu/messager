from rest_framework import viewsets, permissions
from drf_spectacular.utils import extend_schema

from .models import Platform, Contact, Chat, Message, SuggestedReply
from .serializers import (
    PlatformSerializer,
    ContactSerializer,
    ChatSerializer,
    ChatListSerializer,
    MessageSerializer,
    SuggestedReplySerializer,
)


@extend_schema(tags=["Platforms"])
class PlatformViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Platform.objects.all()
    serializer_class = PlatformSerializer
    permission_classes = (permissions.AllowAny,)


@extend_schema(tags=["Contacts"])
class ContactViewSet(viewsets.ModelViewSet):
    serializer_class = ContactSerializer

    def get_queryset(self):
        return Contact.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)


@extend_schema(tags=["Chats"])
class ChatViewSet(viewsets.ModelViewSet):
    def get_queryset(self):
        return Chat.objects.filter(user=self.request.user).prefetch_related("messages")

    def get_serializer_class(self):
        if self.action == "list":
            return ChatListSerializer
        return ChatSerializer

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)


@extend_schema(tags=["Messages"])
class MessageViewSet(viewsets.ModelViewSet):
    serializer_class = MessageSerializer

    def get_queryset(self):
        return Message.objects.filter(chat__user=self.request.user)


@extend_schema(tags=["Suggested Replies"])
class SuggestedReplyViewSet(viewsets.ModelViewSet):
    serializer_class = SuggestedReplySerializer

    def get_queryset(self):
        return SuggestedReply.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)
