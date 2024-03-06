package eu.tutorials.shaproject

import com.google.gson.annotations.SerializedName

data class TeacherResponse(
    @SerializedName("teacher_id")
    val teacher_id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String

)