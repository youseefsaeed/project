package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.*

interface AdminApi {

    @GET("adminsauth")
    fun getAdmins(
        @Query("admin_email") param1: String,
        @Query("admin_pass") param2: String
    ): Call<Boolean>
}
interface TeacherApi {
    @GET("teachersauth")
    fun getTeachers(
        @Query("teacher_email") param1: String,
        @Query("teacher_password") param2: String
    ): Call<List<TeacherResponse>?>

}
interface StudentApi {
    @GET("students/id/{id}")
    fun getTeachers(
        @Path("id") courseId: Int
    ): Call<List<StudentResponse>?>
}
interface RoomApi {
    @GET("roomsauth")
    fun getRooms(
        @Query("room_name") param1: String,
        @Query("room_password") param2: String
    ): Call<Boolean>
}
interface create_lecuture {
    @POST("lectures")
    fun createLecture(@Body lectureData: LectureData): Call<LectureResponse?>
    @POST("attend/lecture")
    fun createLectureWithStudents(@Body body: RequestBody): Call<Void>
    @GET("lectures/teacher/{teacher_id}/course/{course_id}")
    fun getLectures(
        @Path("teacher_id") teacherId: Int,
        @Path("course_id") courseId: Int
    ): Call<List<Lecture>>
    @GET("lectures/{id}/attendance")
    fun getStudent(@Path("id") id: Int): Call<List<ApiResponse>>
}
interface CoursesApi {
    @GET("teachers/courses/{id}")
    fun getTeachers(
        @Path("id") courseId: Int
    ): Call<List<CoursesX>?>
}