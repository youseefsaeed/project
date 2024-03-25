package eu.tutorials.shaproject.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface StudentDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(student: StudentEntity)
}