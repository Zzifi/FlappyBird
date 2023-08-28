package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class PlatinumMedalSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.platinum_medal), coordinate) {

    private val TAG = "PlatinumMedalSprite"
}