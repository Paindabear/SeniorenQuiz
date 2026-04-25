package com.example.seniorenquiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seniorenquiz.QuizMode
import com.google.android.material.button.MaterialButton

class ImageStartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_start, container, false)

        view.findViewById<MaterialButton>(R.id.btnImageMix).setOnClickListener { startImageQuiz("IMAGE_MIX") }
        view.findViewById<MaterialButton>(R.id.btnImageNature).setOnClickListener { startImageQuiz("NATURE") }
        view.findViewById<MaterialButton>(R.id.btnImageHistory).setOnClickListener { startImageQuiz("HISTORY") }
        view.findViewById<MaterialButton>(R.id.btnImageGeography).setOnClickListener { startImageQuiz("GEOGRAPHY") }
        view.findViewById<MaterialButton>(R.id.btnImageMusic).setOnClickListener { startImageQuiz("MUSIC") }
        view.findViewById<MaterialButton>(R.id.btnImageTV).setOnClickListener { startImageQuiz("TV") }
        view.findViewById<MaterialButton>(R.id.btnImageFarm).setOnClickListener { startImageQuiz("FARM") }

        return view
    }

    private fun startImageQuiz(category: String) {
        val intent = Intent(activity, QuizActivity::class.java)
        intent.putExtra("QUIZ_MODE", QuizMode.IMAGE.name)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
