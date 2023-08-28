package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class FlyHintSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.fly_hint), coordinate) {

    private val TAG = "FlyHintSprite"
}