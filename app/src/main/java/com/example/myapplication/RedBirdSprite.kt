package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class RedBirdSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
    vel: Int = 0
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.red_bird_1), coordinate) {

    private val TAG = "RedBird"
    protected val RECTS = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.red_bird_1),
        BitmapFactory.decodeResource(resources, R.drawable.red_bird_2),
        BitmapFactory.decodeResource(resources, R.drawable.red_bird_3)
    )
    protected val PIC_QUANTITY = 3
    protected var pic_number = 0
    protected var velocity: Int = vel

    fun next() {
        pic_number = (pic_number + 1) % PIC_QUANTITY
        setNewBitmap(RECTS[pic_number])
    }

    fun getVelocityy(): Int {
        return velocity
    }

    fun setVelocityy(new_velocity: Int) {
        velocity = new_velocity
    }
}