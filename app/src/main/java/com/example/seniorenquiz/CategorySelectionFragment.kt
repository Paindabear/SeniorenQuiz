package com.example.seniorenquiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class CategorySelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_selection, container, false)

        // Buttons finden
        val btnMixed = view.findViewById<MaterialButton>(R.id.btnMixed)
        val btnNature = view.findViewById<MaterialButton>(R.id.btnNature)
        val btnProverbs = view.findViewById<MaterialButton>(R.id.btnProverbs)
        val btnHistory = view.findViewById<MaterialButton>(R.id.btnHistory)
        val btnGeography = view.findViewById<MaterialButton>(R.id.btnGeography)
        val btnMusic = view.findViewById<MaterialButton>(R.id.btnMusic)
        val btnTV = view.findViewById<MaterialButton>(R.id.btnTV)
        val btnSpace = view.findViewById<MaterialButton>(R.id.btnSpace)
        val btnFood = view.findViewById<MaterialButton>(R.id.btnFood)
        val btnSport = view.findViewById<MaterialButton>(R.id.btnSport)

        // Click Listener setzen
        btnMixed?.setOnClickListener { startQuiz("ALL") }
        btnNature?.setOnClickListener { startQuiz("NATURE") }
        btnProverbs?.setOnClickListener { startQuiz("PROVERBS") }
        btnHistory?.setOnClickListener { startQuiz("HISTORY") }
        btnGeography?.setOnClickListener { startQuiz("GEOGRAPHY") }
        btnMusic?.setOnClickListener { startQuiz("MUSIC") }
        btnTV?.setOnClickListener { startQuiz("TV") }
        btnSpace?.setOnClickListener { startQuiz("SPACE") }
        btnFood?.setOnClickListener { startQuiz("FOOD") }
        btnSport?.setOnClickListener { startQuiz("SPORT") }

        return view
    }

    private fun startQuiz(category: String) {
        val intent = Intent(activity, QuizActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
