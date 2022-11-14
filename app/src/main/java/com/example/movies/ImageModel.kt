package com.example.movies

class ImageModel {

    var name: String? = null
    var year: String? = null
    var imageUrl: String? = null

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

    fun getImagesUrl(): String {
        return imageUrl.toString()
    }

    fun setImagesUrl(image_drawable: String) {
        this.imageUrl = image_drawable
    }

}