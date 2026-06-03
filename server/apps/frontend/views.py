from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.shortcuts import render, redirect
from django.contrib import messages
from apps.users.models import User


def login_view(request):
    if request.user.is_authenticated:
        return redirect("home")
    if request.method == "POST":
        username = request.POST.get("username")
        password = request.POST.get("password")
        user = authenticate(request, username=username, password=password)
        if user:
            login(request, user)
            return redirect("home")
        messages.error(request, "Логин ё парол хатост!")
    return render(request, "auth/login.html")


def register_view(request):
    if request.user.is_authenticated:
        return redirect("home")
    if request.method == "POST":
        username = request.POST.get("username")
        email = request.POST.get("email", "")
        phone = request.POST.get("phone", "")
        password = request.POST.get("password")
        language = request.POST.get("language", "tg")
        if User.objects.filter(username=username).exists():
            messages.error(request, "Ин ном аллакай истифода шудааст!")
        else:
            user = User.objects.create_user(
                username=username,
                email=email,
                phone=phone,
                password=password,
                language=language,
            )
            login(request, user)
            messages.success(request, f"Хуш омадед, {username}!")
            return redirect("home")
    return render(request, "auth/register.html")


@login_required
def logout_view(request):
    logout(request)
    return redirect("login")


@login_required
def home_view(request):
    return render(request, "home.html")


@login_required
def chat_view(request):
    platform = request.GET.get("platform", "web")

    # ─═══ Темаи ҳар платформа ═══─
    themes = {
        "whatsapp": {
            "header": "#075E54", "header2": "#128C7E",
            "bg": "#ECE5DD", "bubble_mine": "#DCF8C6", "bubble_theirs": "#FFFFFF",
            "text_mine": "#000000", "text_theirs": "#000000",
            "accent": "#25D366", "input_bg": "#F0F0F0", "tab_bg": "#E8E8E8",
            "chat_bg": "#ECE5DD",
        },
        "instagram": {
            "header": "#833AB4", "header2": "#FD1D1D",
            "bg": "#FAFAFA", "bubble_mine": "#3797F0", "bubble_theirs": "#EFEFEF",
            "text_mine": "#FFFFFF", "text_theirs": "#262626",
            "accent": "#E1306C", "input_bg": "#FFFFFF", "tab_bg": "#F5F5F5",
            "chat_bg": "#FAFAFA",
        },
        "telegram": {
            "header": "#0088cc", "header2": "#00A3E0",
            "bg": "#e8f3f8", "bubble_mine": "#E6F3FF", "bubble_theirs": "#FFFFFF",
            "text_mine": "#000000", "text_theirs": "#000000",
            "accent": "#0088cc", "input_bg": "#F0F0F0", "tab_bg": "#E0E0E0",
            "chat_bg": "#e8f3f8",
        },
        "viber": {
            "header": "#665CAC", "header2": "#7B6FC0",
            "bg": "#F5F5FA", "bubble_mine": "#D4C9F0", "bubble_theirs": "#FFFFFF",
            "text_mine": "#000000", "text_theirs": "#000000",
            "accent": "#665CAC", "input_bg": "#F0F0F5", "tab_bg": "#E8E8F0",
            "chat_bg": "#F5F5FA",
        },
        "tiktok": {
            "header": "#000000", "header2": "#1A1A1A",
            "bg": "#121212", "bubble_mine": "#FE2C55", "bubble_theirs": "#2A2A2A",
            "text_mine": "#FFFFFF", "text_theirs": "#FFFFFF",
            "accent": "#FE2C55", "input_bg": "#1E1E1E", "tab_bg": "#1E1E1E",
            "chat_bg": "#121212",
        },
    }

    platform_names = {
        "whatsapp": "WhatsApp", "instagram": "Instagram",
        "telegram": "Telegram", "viber": "Viber",
        "tiktok": "TikTok", "web": "Web",
    }

    theme = themes.get(platform, themes["whatsapp"])
    platform_name = platform_names.get(platform, "Чат")

    # ─═══ Маълумоти сохта барои дизайн ═══─
    import json
    avatar_colors = ["#6c5ce7","#f59e0b","#10b981","#ef4444","#3b82f6","#ec4899","#8b5cf6","#22c55e"]

    chats_data = [
        {"name": "Абдулло Маҷидов", "initials": "АМ", "color": avatar_colors[0], "last_msg": "Ташаккур, ҳама чиз хуб аст", "time": "14:23",
         "ai_suggestion": "Хуш омадед, ҷаноби Абдулло. Барои саволи шумо ҷавоб тайёр аст.", "messages": [
            {"side": "theirs", "text": "Салом! Лоиҳа омода аст?", "time": "14:20"},
            {"side": "mine", "text": "Салом. Ҳа, тайёр аст. Барои шумо мефиристам.", "time": "14:21"},
            {"side": "theirs", "text": "Ташаккур, ҳама чиз хуб аст", "time": "14:23"},
        ]},
        {"name": "Фирӯз Раҳмонов", "initials": "ФР", "color": avatar_colors[1], "last_msg": "Лоиҳаи навро дидед?", "time": "15:07",
         "ai_suggestion": "Лоиҳаи нав хеле хуб аст. Ба шумо маслиҳат медиҳам, ки онро қабул кунед.", "messages": [
            {"side": "theirs", "text": "Салом. Лоиҳаи навро дидед?", "time": "15:05"},
            {"side": "mine", "text": "Ҳа, дидам. Фикри шумо чӣ?", "time": "15:06"},
            {"side": "theirs", "text": "Ман фикр мекунам, ки хеле фоиданок аст", "time": "15:07"},
        ]},
        {"name": "Мадина Каримова", "initials": "МК", "color": avatar_colors[2], "last_msg": "Реклама барои моҳи июн", "time": "Кеча",
         "ai_suggestion": "Барои рекламаи моҳи июн, ман пешниҳод мекунам, ки видеои кӯтоҳ тайёр кунем.", "messages": [
            {"side": "theirs", "text": "Ассалому алайкум. Реклама барои моҳи июн", "time": "Кеча"},
            {"side": "mine", "text": "Валекум ассалом. Чӣ нақша доред?", "time": "Кеча"},
            {"side": "theirs", "text": "Мехоҳем видеои нав барои Instagram тайёр кунем", "time": "Кеча"},
        ]},
        {"name": "Умед Гуломов", "initials": "УГ", "color": avatar_colors[3], "last_msg": "Фармоиш барои 20 дона", "time": "Дирӯз",
         "ai_suggestion": "Фармоиши 20 донаро тасдиқ мекунам. Нарх: $200. Маросими расонидан чӣ гуна аст?", "messages": [
            {"side": "theirs", "text": "Салом. Фармоиш барои 20 дона", "time": "Дирӯз"},
            {"side": "mine", "text": "Салом. Ташаккур барои фармоиш.", "time": "Дирӯз"},
        ]},
        {"name": "Шаҳло Саидаҳмадова", "initials": "ШС", "color": avatar_colors[4], "last_msg": "Нархномаро фиристед", "time": "Дирӯз",
         "ai_suggestion": "Нархномаро фиристодам. Барои шумо тахфифи махсус дорем - 10%!", "messages": [
            {"side": "theirs", "text": "Нархномаро фиристед, лутфан", "time": "Дирӯз"},
            {"side": "mine", "text": "Ҳозир мефиристам. Як дақиқа.", "time": "Дирӯз"},
        ]},
        {"name": "Дамир Ҳотамов", "initials": "ДҲ", "color": avatar_colors[5], "last_msg": "Видеои нав омода аст", "time": "2 рӯз",
         "ai_suggestion": "Видео хеле хуб баромад. Барои TikTok формати кӯтоҳтар лозим аст.", "messages": [
            {"side": "theirs", "text": "Салом. Видеои нав омода аст", "time": "2 рӯз"},
            {"side": "mine", "text": "Аҷоиб. Ба ман нишон диҳед.", "time": "2 рӯз"},
        ]},
        {"name": "Муҳаммад Юсуфзод", "initials": "МЮ", "color": avatar_colors[6], "last_msg": "Вохуриро ба соати 15:00 гузоштем", "time": "3 рӯз",
         "ai_suggestion": "Соати 15:00 мувофиқ аст. Дар офис вохӯрем.", "messages": [
            {"side": "theirs", "text": "Вохуриро ба соати 15:00 гузоштем", "time": "3 рӯз"},
            {"side": "mine", "text": "Хуб, ман ҳозир мешавам.", "time": "3 рӯз"},
        ]},
    ]

    return render(request, "chat.html", {
        "platform": platform,
        "platform_name": platform_name,
        "theme": theme,
        "chats": chats_data,
        "chats_json": json.dumps(chats_data, ensure_ascii=False),
        "view": "chats",
        "language": request.user.language,
    })


@login_required
def settings_view(request):
    if request.method == "POST":
        language = request.POST.get("language")
        if language:
            request.user.language = language
            request.user.save(update_fields=["language"])
            messages.success(request, "Забон иваз карда шуд!")
        return redirect("settings_page")
    return render(request, "settings.html")
