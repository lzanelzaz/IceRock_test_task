package ru.lzanelzaz.icerock_test_task

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueStorage @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("USER_API_TOKEN", Context.MODE_PRIVATE)

    var authToken: String? = prefs.getString("authToken", null)
    set(value) {
        field = value
        prefs.edit().putString("authToken", value).commit()
    }

    fun logOut() {
        authToken = null
        prefs.edit().clear().commit()
    }
}