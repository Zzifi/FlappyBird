package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class TopTubeSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
    vel: Int = 0,
    flag: Boolean = false
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.top_tube), coordinate) {

    private val TAG = "TopTubes"
    protected var is_passed: Boolean = flag
    protected var velocity: Int = vel

    fun isPassed(): Boolean {
        return is_passed
    }

    fun setVelocityy(new_velocity: Int) {
        velocity = new_velocity
    }

    fun setPassed(flag: Boolean) {
        is_passed = flag
    }

    fun getVelocityy(): Int {
        return velocity
    }
}