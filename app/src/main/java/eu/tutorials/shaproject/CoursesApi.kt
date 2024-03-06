package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoursesApi {
    @GET("teachers/courses/{id}")
    fun getTeachers(
        @Path("id") courseId: Int
    ): Call<List<CoursesX>?>
}
