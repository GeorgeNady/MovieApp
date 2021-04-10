package com.george.movieapp.dp

import androidx.lifecycle.LiveData
import com.george.movieapp.models.now_playing.Result
import androidx.room.*

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovie(movie : Result) : Long

    @Delete
    suspend fun deleteMovie(movie: Result)

    @Query("SELECT * FROM movies;")
    fun getAllMovies(): LiveData<List<Result>>

}
