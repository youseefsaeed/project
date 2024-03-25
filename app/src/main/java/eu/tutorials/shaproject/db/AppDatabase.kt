package eu.tutorials.shaproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {

        private const val DATABASE_NAME = "note.db"

        private var instance: AppDatabase? = null

        private fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()


        fun getInstance(context: Context): AppDatabase =
            (instance ?: create(context)).also { instance = it }
    }
    abstract fun studentDao(): StudentDao
}