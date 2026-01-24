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

        val btnStart = view.findViewById<MaterialButton>(R.id.btnStartImageQuiz)
        btnStart.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            intent.putExtra("QUIZ_MODE", QuizMode.IMAGE.name)
            intent.putExtra("CATEGORY", "IMAGE_MIX") // Optional, falls wir filtern wollen
            startActivity(intent)
        }

        return view
    }
}
