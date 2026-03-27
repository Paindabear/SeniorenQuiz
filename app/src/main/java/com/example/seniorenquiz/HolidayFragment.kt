package com.example.seniorenquiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class HolidayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_holiday, container, false)

        view.findViewById<MaterialButton>(R.id.btnEaster).setOnClickListener { startQuiz("FEST_EASTER") }
        view.findViewById<MaterialButton>(R.id.btnPentecost).setOnClickListener { startQuiz("FEST_PENTECOST") }
        view.findViewById<MaterialButton>(R.id.btnChristmas).setOnClickListener { startQuiz("FEST_CHRISTMAS") }

        return view
    }

    private fun startQuiz(category: String) {
        val intent = Intent(activity, QuizActivity::class.java)
        intent.putExtra("CATEGORY", category)
        intent.putExtra("QUIZ_MODE", QuizMode.TEXT.name)
        startActivity(intent)
    }
}
