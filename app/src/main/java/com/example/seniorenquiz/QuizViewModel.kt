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
                "ALL" -> allQuestions.filter { !it.category.startsWith("GRIMM") && !it.category.startsWith("FEST_") }
                "GRIMM_ALL" -> allQuestions.filter { it.category.startsWith("GRIMM") }
                else -> allQuestions.filter { it.category == category }
            }
        }
        questionList = filteredQuestions.shuffled().take(10).map { shuffleAnswers(it) }
    }

    private fun shuffleAnswers(question: Question): Question {
        // Create a list of the answers
        val displayedAnswers = question.answers.toMutableList()
        // Identify the correct answer string
        val correctAnswer = displayedAnswers[question.correctAnswerIndex]
        // Shuffle the answers
        displayedAnswers.shuffle()
        // Find the new index of the correct answer
        val newIndex = displayedAnswers.indexOf(correctAnswer)
        // Return a copy of the question with shuffled answers and updated index
        return question.copy(answers = displayedAnswers, correctAnswerIndex = newIndex)
    }

    fun getCurrentQuestion(): Question? {
        if (currentQuestionIndex in questionList.indices) {
            return questionList[currentQuestionIndex]
        }
        return null
    }
}
