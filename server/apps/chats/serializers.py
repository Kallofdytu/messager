from rest_framework import serializers
from .models import Platform, Contact, Chat, Message, SuggestedReply


class PlatformSerializer(serializers.ModelSerializer):
    class Meta:
        model = Platform
        fields = ("id", "name", "icon")


class ContactSerializer(serializers.ModelSerializer):
    class Meta:
        model = Contact
        fields = "__all__"


class MessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Message
        fields = "__all__"


class ChatSerializer(serializers.ModelSerializer):
    messages = MessageSerializer(many=True, read_only=True)
    platform_name = serializers.CharField(source="platform.name", read_only=True)

    class Meta:
        model = Chat
        fields = "__all__"


class ChatListSerializer(serializers.ModelSerializer):
    platform_name = serializers.CharField(source="platform.name", read_only=True)
    contact_name = serializers.CharField(source="contact.name", read_only=True)
    last_message = serializers.SerializerMethodField()

    class Meta:
        model = Chat
        fields = ("id", "title", "platform_name", "contact_name", "last_message_at")

    def get_last_message(self, obj):
        msg = obj.messages.last()
        return msg.content[:100] if msg else None


class SuggestedReplySerializer(serializers.ModelSerializer):
    class Meta:
        model = SuggestedReply
        fields = "__all__"
