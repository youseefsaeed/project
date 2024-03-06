package eu.tutorials.shaproject

import com.google.gson.annotations.SerializedName

data class StudentResponse(
    @SerializedName("student_id")
    val teacher_id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("grade")
    val grade: Int,
    @SerializedName("faculty")
    val faculty: String
)
