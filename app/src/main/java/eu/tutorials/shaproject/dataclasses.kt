package eu.tutorials.shaproject

import com.google.gson.annotations.SerializedName

data class LectureData(
    @SerializedName("lecture_date")
    val lecture_date: String,
    @SerializedName("lecture_time")
    val lecture_time: String,
    @SerializedName("teacher_id")
    val teacherId: Int,
    @SerializedName("course_id")
    val course_id2: Int

)
data class LectureResponse(
    val lecture_id: LectureDataResponse
)

data class LectureDataResponse(
    val data: List<Lecture>,
    val count: Int?
)

data class Lecture(
    val lecture_id: Int,
    val lecture_date: String,
    val lecture_time: String,
    val teacher_id: Int,
    val course_id: Int,
    val studentcount: Int
)


data class ApiResponse(
    val students: Student
)

data class Student(
    val name: String,
    val grade: Int,
    val faculty: String,
    val student_id: Int
)
data class CoursesX(
    val name: String,
    val faculty: String,
    val course_id: Int
)
data class RequestBody(
    val lecture_id: Int,
    val students: List<Int>
)
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
data class TeacherResponse(
    @SerializedName("teacher_id")
    val teacher_id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String

)