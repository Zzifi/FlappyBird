package com.example.myapplication

class DoubleTubes(
    top_tube: TopTubeSprite,
    bottom_tube: BottomTubeSprite
) {
    private var top: TopTubeSprite = top_tube
    private var bottom: BottomTubeSprite = bottom_tube

    fun isPassed(): Boolean {
        return top.isPassed()
    }

    fun getTopTube(): TopTubeSprite {
        return top
    }

    fun getBottomTube(): BottomTubeSprite {
        return bottom
    }

    fun setTopTube(new_top: TopTubeSprite) {
        top = new_top
    }

    fun setBottomTube(new_bottom: BottomTubeSprite) {
        bottom = new_bottom
    }

    fun setPassed(flag: Boolean) {
        top.setPassed(flag)
        bottom.setPassed(flag)
    }
}