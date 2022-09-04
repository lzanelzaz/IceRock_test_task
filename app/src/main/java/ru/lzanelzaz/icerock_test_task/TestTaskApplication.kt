package ru.lzanelzaz.icerock_test_task

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

lateinit var context: Context

@HiltAndroidApp
class TestTaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}