package com.example.movies.model

class ItemModel {

    var name: String? = null
    var year: String? = null
    var imageUrl: String? = null
    var imdbID: String? = null
    var isFavourite: Boolean = false

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