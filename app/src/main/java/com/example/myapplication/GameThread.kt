package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.max
import kotlin.random.Random

class GameThread : Thread {

    enum class GameState {
        MENU,
        GAME_PROCESS,
        GAME_OVER
    }

    private val TAG = "GameThread"
    private var firebaseDatabase: DatabaseReference
    private var holder: SurfaceHolder
    private var resources: Resources
    private var context: Context
    private var game_state = GameState.MENU
    private var start_time: Long = 0
    private var frame_time: Long = 0
    private var is_running = false
    private var is_new_record = false
    private var jump = false
    private val FPS: Int = 45
    private val VELOCITY = 8
    private val BIRD_JUMP_VELOCITY = 30
    private val BIRD_FALL_VELOCITY = 2
    private var tubes = ArrayList<DoubleTubes>()
    private var background_sprite: BackGroundSprite
    private var top_tube_sprite: TopTubeSprite
    private var bottom_tube_sprite: BottomTubeSprite
    private var bird_sprite: RedBirdSprite
    private var fly_hint_sprite: FlyHintSprite
    private var result_bar_sprite: ResultBarSprite
    private var game_over_sprite: GameOverSprite
    private var bronze_medal_sprite: BronzeMedalSprite
    private var silver_medal_sprite: SilverMedalSprite
    private var gold_medal_sprite: GoldMedalSprite
    private var platinum_medal_sprite: PlatinumMedalSprite
    private var new_record_sprite: NewRecordSprite
    private var points: Int = 0
    private var best_points: Int = 0

    constructor(holder: SurfaceHolder, resources: Resources, context: Context) {
        val user_id = FirebaseAuth.getInstance().uid.toString()
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user_id)

        this.holder = holder
        this.resources = resources
        this.context = context
        is_running = true

        background_sprite = BackGroundSprite(resources)
        background_sprite.resizeBitmapByHeight(resources.displayMetrics.heightPixels)

        bird_sprite = RedBirdSprite(resources)
        bird_sprite.resizeBitmapByWidth(3 * resources.displayMetrics.widthPixels / 25)
        setBirdDefaultCoordinate()

        top_tube_sprite = TopTubeSprite(resources)
        top_tube_sprite.resizeBitmapByWidth(resources.displayMetrics.widthPixels / 5)

        bottom_tube_sprite = BottomTubeSprite(resources)
        bottom_tube_sprite.resizeBitmapByWidth(resources.displayMetrics.widthPixels / 5)

        val hint_x = bird_sprite.getX() + bird_sprite.getWidth()
        val hint_y = bird_sprite.getY() + bird_sprite.getHeight()
        fly_hint_sprite = FlyHintSprite(resources, Coordinate(hint_x, hint_y))
        fly_hint_sprite.resizeBitmapByWidth(3 * bird_sprite.getWidth())

        result_bar_sprite = ResultBarSprite(resources)
        result_bar_sprite.resizeBitmapByWidth(4 * resources.displayMetrics.widthPixels / 5)
        result_bar_sprite.setX(resources.displayMetrics.widthPixels / 10)
        result_bar_sprite.setY((resources.displayMetrics.heightPixels - result_bar_sprite.getHeight()) / 2)

        game_over_sprite = GameOverSprite(resources)
        game_over_sprite.resizeBitmapByWidth(result_bar_sprite.getWidth())
        game_over_sprite.setX(result_bar_sprite.getX())
        game_over_sprite.setY(result_bar_sprite.getY() - game_over_sprite.getHeight() - resources.displayMetrics.heightPixels / 100)

        val medal_x = result_bar_sprite.getX() + 3 * result_bar_sprite.getWidth() / 29
        val medal_y = result_bar_sprite.getY() + 5 * result_bar_sprite.getHeight() / 13
        bronze_medal_sprite = BronzeMedalSprite(resources, Coordinate(medal_x, medal_y))
        bronze_medal_sprite.resizeBitmapByHeight(result_bar_sprite.getHeight() / 2)

        silver_medal_sprite = SilverMedalSprite(resources, Coordinate(medal_x, medal_y))
        silver_medal_sprite.resizeBitmapByHeight(result_bar_sprite.getHeight() / 2)

        gold_medal_sprite = GoldMedalSprite(resources, Coordinate(medal_x, medal_y))
        gold_medal_sprite.resizeBitmapByHeight(result_bar_sprite.getHeight() / 2)

        platinum_medal_sprite = PlatinumMedalSprite(resources, Coordinate(medal_x, medal_y))
        platinum_medal_sprite.resizeBitmapByHeight(result_bar_sprite.getHeight() / 2)

        new_record_sprite = NewRecordSprite(resources)
        new_record_sprite.resizeBitmapByHeight(3 * result_bar_sprite.getHeight() / 25)
        val record_x =
            result_bar_sprite.getX() + 3 * result_bar_sprite.getWidth() / 4 - new_record_sprite.getWidth()
        val record_y = result_bar_sprite.getY() + 13 * result_bar_sprite.getHeight() / 25
        new_record_sprite.setCoordinates(Coordinate(record_x, record_y))
    }

    fun menuPage() {
        val game_name_img = (context as Activity).findViewById<ImageView>(R.id.title)
        game_name_img.visibility = View.VISIBLE
        val exit_btn = (context as Activity).findViewById<Button>(R.id.exit)
        exit_btn.visibility = View.VISIBLE
    }

    fun gameProcessPage() {
        val game_name_img = (context as Activity).findViewById<ImageView>(R.id.title)
        game_name_img.visibility = View.INVISIBLE
        val exit_btn = (context as Activity).findViewById<Button>(R.id.exit)
        exit_btn.visibility = View.INVISIBLE
    }

    fun reset() {
        game_state = GameState.MENU
        start_time = 0
        frame_time = 0
        is_running = true
        is_new_record = false
        jump = false
        tubes.clear()
        background_sprite.setCoordinates(Coordinate())
        points = 0
        setBirdDefaultCoordinate()
        runBackground()
    }

    fun makeTopTube(): TopTubeSprite {
        val tube = TopTubeSprite(resources)
        tube.resizeBitmapByWidth(resources.displayMetrics.widthPixels / 5)
        return tube
    }

    fun makeBottomTube(): BottomTubeSprite {
        val tube = BottomTubeSprite(resources)
        tube.resizeBitmapByWidth(resources.displayMetrics.widthPixels / 5)
        return tube
    }

    fun setRunning(flag: Boolean) {
        is_running = flag
    }

    fun setGameState(state: GameState) {
        game_state = state
    }

    fun setJump(flag: Boolean) {
        jump = flag
    }

    fun isRunning(): Boolean {
        return is_running
    }

    fun getGameState(): GameState {
        return game_state
    }

    override fun run() {
        Log.d(TAG, "Thread started.")
        menuPage()
        runBackground()
        while (is_running) {
            if (holder == null) {
                return
            }
            start_time = SystemClock.uptimeMillis()
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                try {
                    synchronized(holder) {
                        render(canvas)
                    }
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            frame_time = SystemClock.uptimeMillis() - start_time
            if (frame_time < FPS) {
                try {
                    Thread.sleep(FPS - frame_time)
                } catch (e: InterruptedException) {
                    Log.e("Interrupted", "Thread sleep error.")
                }
            }
        }
        Log.d(TAG, "Thread finish.")
    }

    private fun render(canvas: Canvas?) {
        Log.d(TAG, "Render canvas.")
        when (game_state) {
            GameState.MENU -> {
                renderBackground(canvas)
                renderBird(canvas)
                renderFlyHint(canvas)
            }

            GameState.GAME_PROCESS -> {
                renderBackground(canvas)
                renderTubes(canvas)
                renderBird(canvas)
                renderPoints(canvas)
                if (isLoose()) {
                    if (game_state == GameState.GAME_PROCESS) {
                        stopSprites()
                        val best_points_ref = firebaseDatabase.child("points")
                        best_points_ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val pnt = snapshot.value.toString().toInt()
                                best_points = pnt
                                if (points > pnt) {
                                    is_new_record = true
                                    best_points = points
                                    val update = mapOf("points" to points.toString())
                                    firebaseDatabase.updateChildren(update)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    game_state = GameState.GAME_OVER
                }
            }

            GameState.GAME_OVER -> {
                renderBackground(canvas)
                renderTubes(canvas)
                renderBird(canvas)
                renderResultBar(canvas)
            }
        }
    }

    private fun renderBackground(canvas: Canvas?) {
        background_sprite.setX(background_sprite.getX() - background_sprite.getVelocityy())
        if (background_sprite.getX() + background_sprite.getWidth() < resources.displayMetrics.widthPixels) {
            background_sprite.setX(0)
        }
        background_sprite.draw(canvas!!)
    }

    private fun renderBird(canvas: Canvas?) {
        if (game_state == GameState.GAME_PROCESS) {
            if (jump) {
                bird_sprite.setVelocityy(-BIRD_JUMP_VELOCITY)
                jump = false
            } else {
                bird_sprite.setVelocityy(bird_sprite.getVelocityy() + BIRD_FALL_VELOCITY)
            }
        }
        bird_sprite.setY(max(0, bird_sprite.getY() + bird_sprite.getVelocityy()))
        if (bird_sprite.getY() == 0) {
            bird_sprite.setVelocityy(0)
        }
        bird_sprite.draw(canvas!!)
        bird_sprite.next()
    }

    private fun renderFlyHint(canvas: Canvas?) {
        fly_hint_sprite.draw(canvas!!)
    }

    private fun renderPoints(canvas: Canvas?) {
        for (i in tubes.indices) {
            if (!tubes[i].isPassed() && bird_sprite.getX() + bird_sprite.getWidth() > tubes[i].getTopTube()
                    .getX() + tubes[i].getTopTube().getWidth()
            ) {
                tubes[i].setPassed(true)
                ++points
            }
        }
        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = (resources.displayMetrics.heightPixels / 10).toFloat()
        val point_x =
            (resources.displayMetrics.widthPixels - paint.measureText(points.toString())) / 2
        val point_y = (resources.displayMetrics.heightPixels / 10).toFloat()
        canvas!!.drawText(
            points.toString(),
            point_x,
            point_y,
            paint
        )
    }

    private fun renderTubes(canvas: Canvas?) {
        for (i in tubes.indices) {
            val top = tubes[i].getTopTube()
            top.setX(top.getX() - top.getVelocityy())
            tubes[i].setTopTube(top)
            val bottom = tubes[i].getBottomTube()
            bottom.setX(bottom.getX() - bottom.getVelocityy())
            tubes[i].setBottomTube(bottom)
            if (top.getX() + top.getWidth() > 0) {
                top.draw(canvas!!)
                bottom.draw(canvas)
            }
        }
        val dist_between_tubes = 2 * resources.displayMetrics.widthPixels / 5
        if (tubes.isEmpty() || tubes.last().getTopTube()
                .getX() + top_tube_sprite.getWidth() + dist_between_tubes < resources.displayMetrics.widthPixels
        ) {
            val h = Random(System.nanoTime()).nextInt(
                resources.displayMetrics.heightPixels / 8,
                5 * resources.displayMetrics.heightPixels / 8
            )
            val new_top_tube = makeTopTube()
            val new_bottom_tube = makeBottomTube()
            new_top_tube.setX(resources.displayMetrics.widthPixels)
            new_top_tube.setY(h - top_tube_sprite.getHeight())
            new_top_tube.setVelocityy(VELOCITY)
            val height_between_tubes: Int = resources.displayMetrics.heightPixels / 4
            new_bottom_tube.setX(resources.displayMetrics.widthPixels)
            new_bottom_tube.setY(new_top_tube.getY() + top_tube_sprite.getHeight() + height_between_tubes)
            new_bottom_tube.setVelocityy(VELOCITY)
            tubes.add(DoubleTubes(new_top_tube, new_bottom_tube))
            return
        }
        if (tubes[0].getTopTube().getX() + top_tube_sprite.getWidth() < 0) {
            tubes.removeAt(0)
        }
    }

    private fun renderResultBar(canvas: Canvas?) {
        result_bar_sprite.draw(canvas!!)

        val paint = Paint()
        paint.color = resources.getColor(R.color.white_red)
        paint.textSize = (result_bar_sprite.getHeight() / 4).toFloat()
        val cur_pnt_x =
            result_bar_sprite.getX() + 11 * result_bar_sprite.getWidth() / 12 - paint.measureText(
                points.toString()
            )
        val cur_pnt_y = result_bar_sprite.getY() + result_bar_sprite.getHeight() / 2
        canvas.drawText(points.toString(), cur_pnt_x.toFloat(), cur_pnt_y.toFloat(), paint)

        val best_pnt_x =
            result_bar_sprite.getX() + 11 * result_bar_sprite.getWidth() / 12 - paint.measureText(
                best_points.toString()
            )
        val best_pnt_y =
            result_bar_sprite.getY() + 7 * result_bar_sprite.getHeight() / 8
        canvas.drawText(best_points.toString(), best_pnt_x.toFloat(), best_pnt_y.toFloat(), paint)

        when (points) {
            in 0..9 -> {}
            in 10..19 -> bronze_medal_sprite.draw(canvas)
            in 20..29 -> silver_medal_sprite.draw(canvas)
            in 30..39 -> gold_medal_sprite.draw(canvas)
            else -> platinum_medal_sprite.draw(canvas)
        }

        if (is_new_record) {
            new_record_sprite.draw(canvas)
        }

        game_over_sprite.draw(canvas)
    }

    private fun isLoose(): Boolean {
        if (bird_sprite.getY() + bird_sprite.getHeight() > resources.displayMetrics.heightPixels) {
            return true
        }
        val rec_bird = Rect(
            bird_sprite.getX(),
            bird_sprite.getY(),
            bird_sprite.getX() + bird_sprite.getWidth(),
            bird_sprite.getY() + bird_sprite.getHeight()
        )
        for (i in tubes.indices) {
            val rec_tube1 = Rect(
                tubes[i].getTopTube().getX(),
                tubes[i].getTopTube().getY(),
                tubes[i].getTopTube().getX() + top_tube_sprite.getWidth(),
                tubes[i].getTopTube().getY() + top_tube_sprite.getHeight()
            )
            val rec_tube2 = Rect(
                tubes[i].getBottomTube().getX(),
                tubes[i].getBottomTube().getY(),
                tubes[i].getBottomTube().getX() + bottom_tube_sprite.getWidth(),
                tubes[i].getBottomTube().getY() + bottom_tube_sprite.getHeight()
            )
            if (rec_bird.intersect(rec_tube1) || rec_bird.intersect(rec_tube2)) {
                return true
            }
        }
        return false
    }

    private fun stopSprites() {
        bird_sprite.setVelocityy(0)
        background_sprite.setVelocityy(0)
        for (i in tubes.indices) {
            val top = tubes[i].getTopTube()
            top.setVelocityy(0)
            tubes[i].setTopTube(top)

            val bottom = tubes[i].getBottomTube()
            bottom.setVelocityy(0)
            tubes[i].setBottomTube(bottom)
        }
    }

    private fun runBackground() {
        background_sprite.setVelocityy(VELOCITY)
    }

    private fun setBirdDefaultCoordinate() {
        val x = (resources.displayMetrics.widthPixels - bird_sprite.getWidth()) / 2
        val y = (resources.displayMetrics.heightPixels - bird_sprite.getHeight()) / 2
        bird_sprite.setCoordinates(Coordinate(x, y))
    }
}