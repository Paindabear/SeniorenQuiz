package com.example.seniorenquiz

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.seniorenquiz.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private var toneGenerator: ToneGenerator? = null
    private var mediaPlayer: android.media.MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bildschirm wach halten
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Töne initialisieren
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val category = intent.getStringExtra("CATEGORY") ?: "ALL"
        val quizModeName = intent.getStringExtra("QUIZ_MODE") ?: "TEXT"
        val quizMode = try {
            QuizMode.valueOf(quizModeName)
        } catch (e: IllegalArgumentException) {
            QuizMode.TEXT
        }

        // ViewModel init (lädt Fragen nur beim ersten Mal)
        viewModel.loadQuestionsIfNeeded(this, category, quizMode)

        if (viewModel.questionList.isEmpty()) {
            finish()
            return
        }

        setupUI(category)
        updateUI()
    }

    private fun setupUI(category: String) {
        // Initiale UI Settings
        binding.tvCategory.text = getCategoryTitle(category)
        
        binding.btnAnswer1.setOnClickListener { checkAnswer(0) }
        binding.btnAnswer2.setOnClickListener { checkAnswer(1) }
        binding.btnAnswer3.setOnClickListener { checkAnswer(2) }

        binding.btnJoker.setOnClickListener { useJoker() }

        binding.btnNext.setOnClickListener {
            viewModel.currentQuestionIndex++
            if (viewModel.currentQuestionIndex < viewModel.questionList.size) {
                updateUI()
            } else {
                finishQuiz()
            }
        }
    }

    private fun updateUI() {
        val question = viewModel.getCurrentQuestion() ?: return
        
        // Reset State
        viewModel.isJokerUsed = false // Reset local joker usage for this question? 
        // Warten: "isJokerUsed" im ViewModel war global? 
        // Im alten Code: private var isJokerUsed = false (Klassen-Level, aber wurde in showQuestion() resettet?)
        // Alter Code Zeile 113: isJokerUsed = false. -> Ja, Joker ist pro Frage verfügbar?
        // Nein, Joker ist normalerweise einmal PRO SPIEL.
        // User Code Zeile 33: private var isJokerUsed = false.
        // User Code Zeile 113: isJokerUsed = false (in showQuestion).
        // Das bedeutet beim alten Code: Joker resettet sich JEDE Frage. Das ist sehr großzügig.
        // Wenn ich das ViewModel nutze, ist isJokerUsed dort ein State.
        // Ich behalte das Verhalten bei: Joker reset pro Frage?
        // ACHTUNG: Alter Code Zeile 113 setzt es zurück. Also ja, jede Frage neu.
        // (Wäre es ein 50:50 Joker wie in WWM, wäre er nur einmalig. Aber hier scheint er oft nutzbar).
        // Ich werde es im ViewModel zurücksetzen, wenn die Frage wechselt.
        // Oder ich setze es hier manuell zurück, aber das ViewModel hält den Status.
        // ViewModel.isJokerUsed wird beim Next-Click nicht automatisch resettet, ich muss es tun.
        viewModel.isJokerUsed = false

        binding.tvProgress.text = getString(R.string.question_progress, viewModel.currentQuestionIndex + 1, viewModel.questionList.size)
        
        binding.tvCategory.text = getCategoryTitle(question.category)
        binding.tvCategory.setTextColor(getCategoryColor(question.category))

        binding.tvQuestion.text = question.text
        binding.btnAnswer1.text = question.answers[0]
        binding.btnAnswer2.text = question.answers[1]
        binding.btnAnswer3.text = question.answers[2]

        // Media Reset & Loading
        binding.ivQuizImage.visibility = View.GONE
        binding.btnPlayAudio.visibility = View.GONE
        stopAudio()

        if (!question.imagePath.isNullOrEmpty()) {
            try {
                // Remove prefix if present (e.g., if JSON has "assets/...")
                val path = question.imagePath.removePrefix("assets/")
                val inputStream = assets.open(path)
                val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
                binding.ivQuizImage.setImageDrawable(drawable)
                binding.ivQuizImage.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!question.audioPath.isNullOrEmpty()) {
            binding.btnPlayAudio.visibility = View.VISIBLE
            binding.btnPlayAudio.setOnClickListener {
                playAudio(question.audioPath)
            }
        }

        // Reset Visuals
        binding.tvStatus.text = ""
        binding.tvExplanation.visibility = View.GONE
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnJoker.isEnabled = true
        binding.btnJoker.alpha = 1.0f
        
        binding.btnNext.clearAnimation()

        resetButtonStyles(binding.btnAnswer1)
        resetButtonStyles(binding.btnAnswer2)
        resetButtonStyles(binding.btnAnswer3)
        
        binding.btnAnswer1.visibility = View.VISIBLE
        binding.btnAnswer2.visibility = View.VISIBLE
        binding.btnAnswer3.visibility = View.VISIBLE
    }

    private fun resetButtonStyles(button: com.google.android.material.button.MaterialButton) {
        button.isEnabled = true
        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#90CAF9")) // Standard Blau
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0) // Icons entfernen
    }

    private fun useJoker() {
        if (viewModel.isJokerUsed) return
        val question = viewModel.getCurrentQuestion() ?: return
        
        // Remove 2 wrong answers (leaving only correct + maybe 0 wrong? No, code logic below)
        // User request: "Korrigierte Joker-Logik (entfernt 2 Falsche)"
        
        val wrongIndices = mutableListOf(0, 1, 2)
        wrongIndices.remove(question.correctAnswerIndex)
        
        // Remove ALL wrong indices to be super easy (as requested interpretation)
        for (index in wrongIndices) {
            when (index) {
                0 -> binding.btnAnswer1.visibility = View.INVISIBLE
                1 -> binding.btnAnswer2.visibility = View.INVISIBLE
                2 -> binding.btnAnswer3.visibility = View.INVISIBLE
            }
        }

        viewModel.isJokerUsed = true
        binding.btnJoker.isEnabled = false
        binding.btnJoker.alpha = 0.5f
    }

    private fun checkAnswer(selectedIndex: Int) {
        val question = viewModel.getCurrentQuestion() ?: return

        // Disable Buttons
        binding.btnAnswer1.isEnabled = false
        binding.btnAnswer2.isEnabled = false
        binding.btnAnswer3.isEnabled = false
        binding.btnJoker.isEnabled = false

        val selectedButton = when (selectedIndex) {
            0 -> binding.btnAnswer1
            1 -> binding.btnAnswer2
            else -> binding.btnAnswer3
        }

        if (selectedIndex == question.correctAnswerIndex) {
            // Richtig
            selectedButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A5D6A7")) // Grün
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0) // Icon Check
            binding.tvStatus.text = getString(R.string.answer_correct)
            viewModel.score++
            
            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 150)

            // Visual Effect
            val colorAnim = ObjectAnimator.ofObject(binding.root, "backgroundColor", ArgbEvaluator(), Color.parseColor("#2E7D32"), Color.parseColor("#1F1B24"))
            colorAnim.duration = 500
            colorAnim.start()

            vibrate(100)
        } else {
            // Falsch
            selectedButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EF9A9A")) // Rot
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0) // Icon Cross
            binding.tvStatus.text = getString(R.string.answer_wrong)
            
            val correctAnswerText = question.answers[question.correctAnswerIndex]
            binding.tvExplanation.text = getString(R.string.correct_answer_info, correctAnswerText)
            binding.tvExplanation.visibility = View.VISIBLE
            vibrate(300)
            
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 300)

            val colorAnim = ObjectAnimator.ofObject(binding.root, "backgroundColor", ArgbEvaluator(), Color.parseColor("#C62828"), Color.parseColor("#1F1B24"))
            colorAnim.duration = 500
            colorAnim.start()
        }

        binding.btnNext.visibility = View.VISIBLE
        
        val anim = android.view.animation.AlphaAnimation(0.5f, 1.0f)
        anim.duration = 500
        anim.startOffset = 20
        anim.repeatMode = android.view.animation.Animation.REVERSE
        anim.repeatCount = android.view.animation.Animation.INFINITE
        binding.btnNext.startAnimation(anim)
    }

    private fun finishQuiz() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("SCORE", viewModel.score)
        intent.putExtra("TOTAL_QUESTIONS", viewModel.questionList.size)
        startActivity(intent)
        finish()
    }

    private fun vibrate(duration: Long) {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null
        stopAudio()
    }

    private fun playAudio(filename: String) {
        stopAudio()
        try {
            val path = filename.removePrefix("assets/")
            val afd = assets.openFd(path)
            mediaPlayer = android.media.MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener {
                    // Optional: Reset button visual state if we had one
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(this, "Audiofehler: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun getCategoryTitle(categoryCode: String): String {
        return when (categoryCode) {
            "ALL" -> getString(R.string.category_mix)
            "NATURE" -> getString(R.string.category_nature)
            "PROVERBS" -> getString(R.string.category_proverbs)
            "HISTORY" -> getString(R.string.category_history)
            "GEOGRAPHY" -> getString(R.string.category_geography)
            "MUSIC" -> getString(R.string.category_music)
            "TV" -> getString(R.string.category_tv)
            "SPACE" -> getString(R.string.category_space)
            "FOOD" -> getString(R.string.category_food)
            "SPORT" -> getString(R.string.category_sport)
            "GRIMM_ALL" -> getString(R.string.category_fairy_mix)
            "GRIMM_HANSEL" -> getString(R.string.category_hansel)
            "GRIMM_RED" -> getString(R.string.category_red)
            "GRIMM_SNOW" -> getString(R.string.category_snow)
            "GRIMM_FROG" -> getString(R.string.category_frog)
            "GRIMM_CINDERELLA" -> getString(R.string.category_cinderella)
            "GRIMM_SLEEPING" -> getString(R.string.category_sleeping)
            "GRIMM_HOLLE" -> getString(R.string.category_holle)
            "GRIMM_RUMPEL" -> getString(R.string.category_rumpel)
            "GRIMM_GOATS" -> getString(R.string.category_goats)
            else -> getString(R.string.default_quiz_title)
        }
    }
    
    private fun getCategoryColor(categoryCode: String): Int {
        return when (categoryCode) {
            "NATURE" -> Color.parseColor("#81C784")
            "PROVERBS" -> Color.parseColor("#64B5F6")
            "HISTORY" -> Color.parseColor("#BCAAA4")
            "GEOGRAPHY" -> Color.parseColor("#80CBC4")
            "MUSIC" -> Color.parseColor("#CE93D8")
            "TV" -> Color.parseColor("#EF9A9A")
            "SPACE" -> Color.parseColor("#9FA8DA")
            "FOOD" -> Color.parseColor("#FFCC80")
            "SPORT" -> Color.parseColor("#C5E1A5")
            // Removed duplicate SPORT
            "GRIMM_ALL" -> Color.parseColor("#FFCC80")
            "GRIMM_HANSEL" -> Color.parseColor("#8D6E63")
            "GRIMM_RED" -> Color.parseColor("#EF9A9A")
            "GRIMM_SNOW" -> Color.parseColor("#90CAF9")
            "GRIMM_FROG" -> Color.parseColor("#81C784")
            "GRIMM_CINDERELLA" -> Color.parseColor("#FFF9C4")
            "GRIMM_SLEEPING" -> Color.parseColor("#F8BBD0")
            "GRIMM_HOLLE" -> Color.parseColor("#B3E5FC")
            "GRIMM_RUMPEL" -> Color.parseColor("#D7CCC8")
            "GRIMM_GOATS" -> Color.parseColor("#FFCCBC")
            else -> Color.parseColor("#90A4AE")
        }
    }
}
