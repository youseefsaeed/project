package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TeacherApi {
    @GET("teachersauth")
    fun getTeachers(
        @Query("teacher_email") param1: String,
        @Query("teacher_password") param2: String
    ): Call<List<TeacherResponse>?>

}