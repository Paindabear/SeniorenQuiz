package com.example.seniorenquiz

import android.content.Context
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    var currentQuestionIndex = 0
    var score = 0
    var isJokerUsed = false
    var questionList: List<Question> = emptyList()

    fun loadQuestionsIfNeeded(context: Context, category: String, mode: QuizMode) {
        if (questionList.isNotEmpty()) return

        val allQuestions = QuizRepository.getQuestions(context, mode)
        val filteredQuestions = if (mode == QuizMode.IMAGE || mode == QuizMode.AUDIO) {
             // Bei Image/Audio nehmen wir erstmal alle oder filtern optional nach Kategorie
             if (category.endsWith("_MIX")) allQuestions else allQuestions.filter { it.category == category }
        } else {
             // Text / Märchen
             when (category) {
                "ALL" -> allQuestions.filter { !it.category.startsWith("GRIMM") }
                "GRIMM_ALL" -> allQuestions.filter { it.category.startsWith("GRIMM") }
                else -> allQuestions.filter { it.category == category }
            }
        }
        questionList = filteredQuestions.shuffled().take(10)
    }

    fun getCurrentQuestion(): Question? {
        if (currentQuestionIndex in questionList.indices) {
            return questionList[currentQuestionIndex]
        }
        return null
    }
}
