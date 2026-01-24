package com.example.seniorenquiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class FairyTaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fairy_tale, container, false)

        val btnGrimmMixed = view.findViewById<MaterialButton>(R.id.btnGrimmMixed)
        val btnHansel = view.findViewById<MaterialButton>(R.id.btnHansel)
        val btnRedRidingHood = view.findViewById<MaterialButton>(R.id.btnRedRidingHood)
        val btnSnowWhite = view.findViewById<MaterialButton>(R.id.btnSnowWhite)
        val btnFrogPrince = view.findViewById<MaterialButton>(R.id.btnFrogPrince)
        val btnCinderella = view.findViewById<MaterialButton>(R.id.btnCinderella)
        val btnSleeping = view.findViewById<MaterialButton>(R.id.btnSleeping)
        val btnHolle = view.findViewById<MaterialButton>(R.id.btnHolle)
        val btnRumpel = view.findViewById<MaterialButton>(R.id.btnRumpel)
        val btnGoats = view.findViewById<MaterialButton>(R.id.btnGoats)

        btnGrimmMixed?.setOnClickListener { startQuiz("GRIMM_ALL") }
        btnHansel?.setOnClickListener { startQuiz("GRIMM_HANSEL") }
        btnRedRidingHood?.setOnClickListener { startQuiz("GRIMM_RED") }
        btnSnowWhite?.setOnClickListener { startQuiz("GRIMM_SNOW") }
        btnFrogPrince?.setOnClickListener { startQuiz("GRIMM_FROG") }
        btnCinderella?.setOnClickListener { startQuiz("GRIMM_CINDERELLA") }
        btnSleeping?.setOnClickListener { startQuiz("GRIMM_SLEEPING") }
        btnHolle?.setOnClickListener { startQuiz("GRIMM_HOLLE") }
        btnRumpel?.setOnClickListener { startQuiz("GRIMM_RUMPEL") }
        btnGoats?.setOnClickListener { startQuiz("GRIMM_GOATS") }

        return view
    }

    private fun startQuiz(category: String) {
        val intent = Intent(activity, QuizActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
