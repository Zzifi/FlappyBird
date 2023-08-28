package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class ResultBarSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate()
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.result_bar), coordinate) {
}