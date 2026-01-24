package com.example.seniorenquiz

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.net.URL

data class Question(
    val text: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val category: String
)

object QuizRepository {

    // Bei Repo-Root: .../master/questions.json
    private const val QUESTIONS_JSON_URL = "https://raw.githubusercontent.com/Petlus/SeniorenQuiz/master/app/src/main/assets/questions.json"
    private const val LOCAL_QUESTIONS_FILE = "questions.json"

    private var cachedQuestions: List<Question>? = null
    private val listType = object : TypeToken<List<Question>>() {}.type
    private val gson = Gson()

    fun getQuestions(context: Context): List<Question> {
        if (cachedQuestions != null) {
            return cachedQuestions!!
        }

        // 1. Bevorzugt: lokale Datei (von GitHub-Update)
        val localFile = File(context.filesDir, LOCAL_QUESTIONS_FILE)
        if (localFile.exists()) {
            try {
                val json = localFile.readText()
                if (json.startsWith("[")) {
                    val list = gson.fromJson<List<Question>>(json, listType)
                    if (list.isNotEmpty()) {
                        cachedQuestions = list
                        return cachedQuestions!!
                    }
                }
            } catch (e: Exception) {
                // Fall-through zu Assets
            }
        }

        // 2. Fallback: Fragen aus Assets (Basis-Version in der APK)
        try {
            val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            cachedQuestions = gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cachedQuestions ?: emptyList()
    }

    /**
     * Lädt questions.json vom GitHub Raw-Link und speichert sie lokal.
     * Wird in QuizApplication.onCreate aufgerufen.
     * Benötigt INTERNET-Permission.
     */
    fun updateQuestionsFromUrl(context: Context) {
        Thread {
            try {
                if (!hasInternet(context)) return@Thread

                val jsonString = URL(QUESTIONS_JSON_URL).readText()

                // Validieren: muss als JSON-Array starten
                if (jsonString.startsWith("[")) {
                    val file = File(context.filesDir, LOCAL_QUESTIONS_FILE)
                    file.writeText(jsonString)
                    cachedQuestions = null // Cache invalidieren, nächster getQuestions nutzt neue Datei
                }
            } catch (e: Exception) {
                // Kein Internet oder Fehler – unkritisch, es gibt die Asset-Fragen
                e.printStackTrace()
            }
        }.start()
    }

    private fun hasInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
