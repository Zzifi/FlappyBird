package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

open class BackGroundSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
    vel: Int = 0
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.run_background_day), coordinate) {

    private val TAG = "BottomTubes"
    protected var velocity: Int = vel

    fun getVelocityy(): Int {
        return velocity
    }

    fun setVelocityy(new_velocity: Int) {
        velocity = new_velocity
    }
}