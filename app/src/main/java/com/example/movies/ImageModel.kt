package com.example.movies

class ImageModel {

    var name: String? = null
    var year: String? = null
    var image_drawable: Int = 0

    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

    fun getYears(): String {
        return year.toString()
    }

    fun setYears(year: String) {
        this.year = year
    }

    fun getImage_drawables(): Int {
        return image_drawable
    }

    fun setImage_drawables(image_drawable: Int) {
        this.image_drawable = image_drawable
    }

}