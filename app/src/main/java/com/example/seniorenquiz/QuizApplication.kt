package com.example.seniorenquiz

import android.app.Application

class QuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        QuizRepository.updateQuestionsFromUrl(this)
    }
}
