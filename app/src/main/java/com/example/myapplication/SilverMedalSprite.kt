package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class SilverMedalSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.silver_medal), coordinate) {

    private val TAG = "SilverMedalSprite"
}