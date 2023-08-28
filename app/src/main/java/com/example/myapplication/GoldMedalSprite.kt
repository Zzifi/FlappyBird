package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class GoldMedalSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.gold_medal), coordinate) {

    private val TAG = "GoldMedalSprite"
}