from django.contrib import admin
from .models import AnalysisRequest, AnalysisResult

admin.site.register(AnalysisRequest)
admin.site.register(AnalysisResult)
