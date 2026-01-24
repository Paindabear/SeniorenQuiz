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
    val category: String,
    val imagePath: String? = null,
    val audioPath: String? = null
)

enum class QuizMode {
    TEXT,
    IMAGE,
    AUDIO,
    FAIRYTALE
}

object QuizRepository {

    // Bei Repo-Root: .../master/questions.json
    private const val QUESTIONS_JSON_URL = "https://raw.githubusercontent.com/Petlus/SeniorenQuiz/master/app/src/main/assets/questions.json"
    private const val LOCAL_QUESTIONS_FILE = "questions.json"

    private var cachedQuestions: List<Question>? = null
    private val listType = object : TypeToken<List<Question>>() {}.type
    private val gson = Gson()

    fun getQuestions(context: Context, mode: QuizMode): List<Question> {
        val fileName = when (mode) {
            QuizMode.IMAGE -> "images.json"
            QuizMode.AUDIO -> "audio.json"
            else -> "questions.json" // TEXT und FAIRYTALE nutzen questions.json (filter später)
        }

        // 1. Image/Audio erstmal nur aus Assets laden (einfacher Start)
        if (mode == QuizMode.IMAGE || mode == QuizMode.AUDIO) {
             return loadFromAssets(context, fileName)
        }

        // 2. Text-Fragen wie bisher (Cache/Update-Logik)
        if (cachedQuestions != null) {
            return cachedQuestions!!
        }

        // Bevorzugt: lokale Datei (von GitHub-Update)
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

        // Fallback: Assets
        cachedQuestions = loadFromAssets(context, "questions.json")
        return cachedQuestions ?: emptyList()
    }

    private fun loadFromAssets(context: Context, fileName: String): List<Question> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
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
                    
                    // Prüfen ob bereits vorhanden und identisch
                    if (file.exists() && file.readText() == jsonString) {
                         // Keine Änderung, nichts tun
                         return@Thread
                    }

                    file.writeText(jsonString)
                    cachedQuestions = null // Cache invalidieren, nächster getQuestions nutzt neue Datei

                    // Benachrichtigung auf dem Main Thread
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.questions_updated_success),
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
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
