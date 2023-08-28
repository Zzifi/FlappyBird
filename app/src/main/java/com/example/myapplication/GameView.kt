package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private val TAG = "GameView"
    private var game_thread: GameThread? = null

    init {
        val holder = holder
        holder.addCallback(this)
        isFocusable = true
        game_thread = GameThread(holder, resources, context!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    fun destroy() {
        game_thread!!.setRunning(false)
    }

    fun restart() {
        game_thread!!.menuPage()
        game_thread!!.reset()
    }

    fun goPlay() {
        game_thread!!.gameProcessPage()
        game_thread!!.setGameState(GameThread.GameState.GAME_PROCESS)
        doJump()
    }

    fun doJump() {
        game_thread!!.setJump(true)
    }

    fun getState(): GameThread.GameState {
        return game_thread!!.getGameState()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        if (!game_thread!!.isRunning()) {
            game_thread = p0.let {
                GameThread(it, resources, context)
            }
        } else {
            game_thread!!.start()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        if (game_thread!!.isRunning()) {
            game_thread!!.setRunning(false)
            game_thread!!.join()
        }
    }
}
