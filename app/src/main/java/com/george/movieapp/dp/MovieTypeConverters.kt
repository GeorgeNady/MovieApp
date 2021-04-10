package com.george.movieapp.dp

import androidx.room.TypeConverter

class MovieTypeConverters {

    @TypeConverter
    fun fromList(genreIds: List<Int>): String = genreIds.toString()

    @TypeConverter
    fun toList(genreIdString: String): List<Int> {
        return genreIdString.map { it.toInt() }
    }

}