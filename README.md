# ZChat — AI Unified Messaging Assistant

ZChat — асистенти AI барои паёмнависӣ дар WhatsApp, Instagram, Telegram, Viber ва TikTok. Ба шумо дар таҳлили сӯҳбатҳо ва пешниҳоди ҷавобҳо кӯмак мекунад.

## 🏗 Сохтор

```
├── server/          # Django бэкенд (API, WebSocket, AI)
├── zchat-web/       # React веб-прототип (Vite)
├── zchat-android/   # Android app (Kotlin, Jetpack Compose)
└── requirements.txt # Вобастагиҳои Python
```

---

## 🐍 Бэкенд (server/)

### Технологияҳо
- Django 5.2, DRF 3.17
- JWT (SimpleJWT)
- WebSocket (Channels + Redis)
- Google Gemini AI
- PostgreSQL / SQLite
- Swagger документатсия (drf-spectacular)

### Тез оғоз кардан

```bash
cd server
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env  # Танзими калидҳо
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
```

### Истифода

| Линк | Тавсиф |
|------|--------|
| `/` | Саҳифаи асосӣ |
| `/login/` | Воридшавӣ |
| `/register/` | Сабти ном |
| `/chat/?platform=whatsapp` | Чат бо AI |
| `/settings/` | Танзимот |
| `/admin/` | Админ панел |
| `/api/docs/` | Swagger API документатсия |
| `/api/schema/` | OpenAPI схема |

### API Endpoints

- `POST /api/v1/auth/register/` — Сабти ном
- `POST /api/v1/auth/login/` — Воридшавӣ
- `POST /api/v1/auth/refresh/` — Навсозии токен
- `GET/PATCH /api/v1/profile/` — Профили корбар
- `GET /api/v1/platforms/` — Рӯйхати платформаҳо
- `GET/POST /api/v1/chats/` — Чатҳо
- `GET/POST /api/v1/messages/` — Паёмҳо
- `GET/POST /api/v1/contacts/` — Контактҳо
- `POST /api/v1/ai/analyze/` — AI таҳлили чат

---

## 🌐 Веб-прототип (zchat-web)

### Технологияҳо
- React 18 + Vite
- Tailwind CSS
- Lucide иконкаҳо

```bash
cd zchat-web
npm install
npm run dev
```

Дастрас: `http://localhost:3000/`

---

## 📱 Android (zchat-android)

### Технологияҳо
- Kotlin, Jetpack Compose, Material 3
- Hilt (DI), Room (маълумотгоҳ)
- Retrofit + OkHttp (шабака)
- Google Gemini AI
- Accessibility Service (хондани чатҳо)

### Кушодан дар Android Studio
1. **File → Open** → интихоби папкаи `zchat-android/`
2. Интизори синхронизатсияи Gradel
3. **Run → Run 'app'** (ё Shift+F10)

### Танзимот
Дар `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "DJANGO_BASE_URL", "\"http://IP:8000/api/v1/\"")
buildConfigField("String", "GEMINI_API_KEY", "\"your-key-here\"")
```

### Хусусиятҳо
- **Accessibility Service** — хондани паёмҳо дар WhatsApp/Instagram/Telegram/Viber/TikTok
- **Floating Button** — тугмаи шинокунанда барои AI ёрдам
- **AI Chat Analysis** — таҳлили сӯҳбат тавассути Google Gemini

---

## 🔧 Ислоҳи хатогиҳо

| Маскала | Эҳтимолияти сабаб |
|---------|-------------------|
| `Отсутствует .env` | `cp server/.env.example server/.env` |
| `Redis connection error` | Redis кор намекунад → `redis-server` |
| `Gemini API error` | `GEMINI_API_KEY` холӣ аст |
| `gradlew: Permission denied` | `chmod +x gradlew` |
| `Android build error` | Кушодан тавассути **File → Open**, на Import |

---

## 🧪 Тестҳо

```bash
# Django
cd server && source .venv/bin/activate && python manage.py test

# React
cd zchat-web && npm run build
```
