package com.example.seniorenquiz

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

data class Question(
    val text: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val category: String
)

object QuizRepository {
    private var cachedQuestions: List<Question>? = null

    fun getQuestions(context: Context): List<Question> {
        if (cachedQuestions != null) {
            return cachedQuestions!!
        }

        val jsonString: String
        try {
            jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Question>>() {}.type
            cachedQuestions = Gson().fromJson(jsonString, listType)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }
        return cachedQuestions ?: emptyList()
    }
}
