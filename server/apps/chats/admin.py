from django.contrib import admin
from .models import Platform, Contact, Chat, Message, SuggestedReply

admin.site.register(Platform)
admin.site.register(Contact)
admin.site.register(Chat)
admin.site.register(Message)
admin.site.register(SuggestedReply)
