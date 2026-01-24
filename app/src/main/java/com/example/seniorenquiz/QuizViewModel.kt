package com.example.seniorenquiz

import android.content.Context
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    var currentQuestionIndex = 0
    var score = 0
    var isJokerUsed = false
    var questionList: List<Question> = emptyList()

    fun loadQuestionsIfNeeded(context: Context, category: String) {
        if (questionList.isNotEmpty()) return

        val allQuestions = QuizRepository.getQuestions(context)
        val filteredQuestions = when (category) {
            "ALL" -> allQuestions.filter { !it.category.startsWith("GRIMM") }
            "GRIMM_ALL" -> allQuestions.filter { it.category.startsWith("GRIMM") }
            else -> allQuestions.filter { it.category == category }
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
