package com.example.gemble3


import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast


import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.PopupWindow

import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var gameLayout: LinearLayout
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft: Long = 45000
    private lateinit var gameOverPopup: PopupWindow
    private lateinit var winPopup: PopupWindow

    private val random = Random()
    private val imageResources = arrayOf(R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4)
    private var screenWidth: Int = 0

    private var score = 0
    private val maxGameTimeInSeconds: Long = 60
    private var gameTimeInSeconds = maxGameTimeInSeconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameLayout = findViewById(R.id.game_layout)
        scoreTextView = findViewById(R.id.score_text_view)
        timerTextView = findViewById(R.id.timer_text_view)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x - 50

        countDownTimer = object : CountDownTimer(maxGameTimeInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                gameTimeInSeconds = millisUntilFinished / 1000
                updateTimerText()
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()

        startFallingObjects()
    }

    private fun startFallingObjects() {
        val fallingTimer = object : CountDownTimer(maxGameTimeInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                createFallingImage()
            }

            override fun onFinish() {
            }
        }
        fallingTimer.start()
    }

    private fun createFallingImage() {
        val imageLayoutParams = LinearLayout.LayoutParams(100, 100)

        val image = ImageView(this)
        image.layoutParams = imageLayoutParams
        image.setImageResource(imageResources[random.nextInt(imageResources.size)])

        image.setOnClickListener {
            score++
            scoreTextView.text = "Score: $score"
            gameLayout.removeView(image)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL

        gameLayout.addView(image, layoutParams)

        val startX = random.nextInt(screenWidth - image.width)
        val startY = -image.height

        val minX = 0
        val maxX = screenWidth - image.width
        val clampedStartX = startX.coerceIn(minX, maxX)

        image.x = clampedStartX.toFloat()
        image.y = startY.toFloat()

        val fallingSpeed = random.nextInt(5) + 3
        val fallingInterval = 16L

        val fallingTimer = object : CountDownTimer(maxGameTimeInSeconds * 1000, fallingInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val newY = image.y + fallingSpeed
                image.y = newY

                if (newY >= gameLayout.height) {
                    gameLayout.removeView(image)
                }
            }

            override fun onFinish() {

            }
        }
        fallingTimer.start()
    }

    private fun updateTimerText() {
        val minutes = gameTimeInSeconds / 60
        val seconds = gameTimeInSeconds % 60
        timerTextView.text = String.format("Time: %02d:%02d", minutes, seconds)
    }

    private fun endGame() {
        countDownTimer.cancel()

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gameOverView = inflater.inflate(R.layout.game_over_popup, null)

        val scoreTextViewGameOver = gameOverView.findViewById<TextView>(R.id.scoreTextViewGameOver)
        scoreTextViewGameOver.text = "Score: $score"

        val retryButton = gameOverView.findViewById<Button>(R.id.retryButton)
        retryButton.setOnClickListener {
            resetGame()
            gameOverPopup.dismiss()
        }

        gameOverPopup = PopupWindow(gameOverView, 600, 400, true)
        gameOverPopup.showAtLocation(gameOverView, Gravity.CENTER, 0, 0)
    }

    private fun resetGame() {
        score = 0
        timeLeft = 45000
        scoreTextView.text = "Score: $score"
        timerTextView.text = "Time: ${timeLeft / 1000}"
        countDownTimer.cancel()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}