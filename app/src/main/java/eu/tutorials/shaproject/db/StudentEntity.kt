package eu.tutorials.shaproject.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    val name: String,
    val grade: Int,
    val faculty: String,
    val studentId: Int,
    val percentage: String,
    val lectures: Int
)
