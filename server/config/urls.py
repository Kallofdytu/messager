from django.contrib import admin
from django.urls import path, include
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

urlpatterns = [
    path("admin/", admin.site.urls),
    # ─═══ API docs ═══─
    path("api/schema/", SpectacularAPIView.as_view(), name="schema"),
    path("api/docs/", SpectacularSwaggerView.as_view(url_name="schema"), name="docs"),
    # ─═══ API v1 ═══─
    path("api/v1/", include("apps.users.urls")),
    path("api/v1/", include("apps.chats.urls")),
    path("api/v1/", include("apps.ai.urls")),
    # ─═══ Frontend ═══─
    path("", include("apps.frontend.urls")),
]
