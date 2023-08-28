package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.MotionEventCompat
import com.google.firebase.auth.FirebaseAuth

class GameActivity : AppCompatActivity() {

    private val TAG = "GameActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.exit -> {
                Log.d(TAG, "Exit clicked.")
                val game_view = findViewById<GameView>(R.id.game_view)
                game_view.destroy()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action: Int = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Screen is touched.")
            val game_view = findViewById<GameView>(R.id.game_view)
            when (game_view.getState()) {
                GameThread.GameState.MENU -> game_view.goPlay()
                GameThread.GameState.GAME_PROCESS -> game_view.doJump()
                GameThread.GameState.GAME_OVER -> game_view.restart()
            }
        }
        return false
    }
}