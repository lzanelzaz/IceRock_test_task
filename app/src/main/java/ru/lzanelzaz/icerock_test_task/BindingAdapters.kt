package ru.lzanelzaz.icerock_test_task

import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

/*
@BindingAdapter("android:textColor")
fun TextView.setLanguageColor(language: String?) {
    if (language != null) {
        // Parse json (language objects)
        val fileContent: String = context.assets
            .open("values/github_lang_colors.json")
            .bufferedReader().use { it.readText() }

        val languageColor: String = JSONObject(fileContent)
            .getJSONObject(language).optString("color")
            // In case of null value (R.color.accent [green])
            .replace("null", "#438440")

        setTextColor(Color.parseColor(languageColor))
    }
}

}*/
