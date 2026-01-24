package com.example.seniorenquiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)

        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvScoreMessage = findViewById<TextView>(R.id.tvScoreMessage)
        val btnBackToMenu = findViewById<MaterialButton>(R.id.btnBackToMenu)

        tvScore.text = "$score von $totalQuestions"

        // Lob je nach Punktzahl
        val message = when {
            score == totalQuestions -> "Perfekt! Alles richtig!"
            score >= totalQuestions - 2 -> "Hervorragend gemacht!"
            score >= totalQuestions / 2 -> "Gut gemacht!"
            else -> "Übung macht den Meister!"
        }
        tvScoreMessage.text = message

        btnBackToMenu.setOnClickListener {
            // Zurück zum Hauptmenü und Backstack leeren
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
