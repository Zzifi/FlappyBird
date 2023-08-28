package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class NewRecordSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate(),
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.new_record), coordinate) {

    private val TAG = "NewRecordSprite"
}