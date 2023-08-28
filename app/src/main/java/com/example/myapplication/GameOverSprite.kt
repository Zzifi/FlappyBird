package com.example.myapplication

import android.content.res.Resources
import android.graphics.BitmapFactory

class GameOverSprite(
    resources: Resources,
    coordinate: Coordinate = Coordinate()
) : Sprite(BitmapFactory.decodeResource(resources, R.drawable.game_over), coordinate) {
}