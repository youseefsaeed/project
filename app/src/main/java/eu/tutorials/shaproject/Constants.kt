package eu.tutorials.shaproject

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Constants {
    val students = mutableListOf<String>()!!
    const val teacher_name:String="doctor_name"
    const val teacher_id:String="y"
    const val course_name:String="a"
    const val course_id :String="1"
    const val base_url:String="http://3.71.111.29/"
    const val student_name:String="doctor_name"
    const val student_id:String="y"
    const val student_grade:String="a"
    const val student_attended :String="1"
    const val student_faculty:String=""

}
class RetrofitClient {
    companion object {
        fun getRetrofitObject(): Retrofit {
            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
            return Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Adjust timeout values as needed
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

}