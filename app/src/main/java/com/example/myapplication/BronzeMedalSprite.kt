package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class BronzeMedalSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.bronze_medal), coordinate) {

    private val TAG = "BronzeMedalSprite"
}