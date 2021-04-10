package com.george.movieapp.dp

import android.content.Context
import com.george.movieapp.models.now_playing.Result
import androidx.room.*

@Database(
    entities = [Result::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(MovieTypeConverters::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun getMoviesDao() : MoviesDao

    companion object {
        @Volatile //means that other threads can immediately see when a thread changes this instance
        private var instance: MovieDatabase? = null
        private val LOCK =  Any()

        /**
         * synchronized(LOCK) : means that everything that happens inside of this block of code here
         * can not be accessed by other threads at the same time
         */
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                "movies_database"
            ).build()

    }
}
