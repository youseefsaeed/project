package eu.tutorials.shaproject.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(student: StudentEntity)
    @Delete
    fun delete(student: StudentEntity)
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentEntity>
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: Int): StudentEntity?
}