from django.urls import path
from .views import login_view, register_view, logout_view, home_view, chat_view, settings_view

urlpatterns = [
    path("", home_view, name="home"),
    path("login/", login_view, name="login"),
    path("register/", register_view, name="register"),
    path("logout/", logout_view, name="logout"),
    path("chat/", chat_view, name="chat_page"),
    path("settings/", settings_view, name="settings_page"),
]
