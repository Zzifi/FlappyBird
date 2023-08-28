package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Canvas

open class Sprite(bitmap: Bitmap, coordinate: Coordinate = Coordinate()) {

    private val TAG = "Sprite"
    protected var bitmap: Bitmap? = null
    protected var coords: Coordinate

    init {
        setNewBitmap(bitmap)
        coords = coordinate
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, coords.x.toFloat(), coords.y.toFloat(), null)
    }

    fun resizeBitmapByHeight(new_height: Int) {
        val ratio: Float = bitmap!!.width.toFloat() / bitmap!!.height.toFloat()
        val new_width: Int = (ratio * new_height).toInt()
        bitmap = Bitmap.createScaledBitmap(bitmap!!, new_width, new_height, false)
    }

    fun resizeBitmapByWidth(new_width: Int) {
        val ratio: Float = bitmap!!.height.toFloat() / bitmap!!.width.toFloat()
        val new_height: Int = (ratio * new_width).toInt()
        bitmap = Bitmap.createScaledBitmap(bitmap!!, new_width, new_height, false)
    }

    fun setNewBitmap(new_bitmap: Bitmap) {
        bitmap = new_bitmap
    }

    fun setWidth(new_width: Int) {
        bitmap!!.width = new_width
    }

    fun setHeight(new_height: Int) {
        bitmap!!.height = new_height
    }

    fun setCoordinates(coordinate: Coordinate) {
        coords = coordinate
    }

    fun setX(x: Int) {
        coords.x = x
    }

    fun setY(y: Int) {
        coords.y = y
    }

    fun getWidth(): Int {
        return bitmap!!.width
    }

    fun getHeight(): Int {
        return bitmap!!.height
    }

    fun getCoordinates(): Coordinate {
        return coords
    }

    fun getX(): Int {
        return coords.x
    }

    fun getY(): Int {
        return coords.y
    }
}