package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.*

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