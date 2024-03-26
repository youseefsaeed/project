package eu.tutorials.shaproject.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface StudentDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentEntity>
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: Int): StudentEntity?
}