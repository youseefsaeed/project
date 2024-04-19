package eu.tutorials.shaproject

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

object Constants {
    val students = ArrayList<String>()!!
    val studentsforfile = ArrayList<String>()!!
     val students_ids = mutableListOf<Int>()
    val students_idsforcheck = mutableListOf<Int>()
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

    const val exam_id:String="sad"
    const val exam_date:String="a"
    const val exam_time:String="d"
    const val exam_pass:String="s"
    const val exam_name:String="x"
    const val course_id_e:String="bbbbb"
    const val exam_code:String="Y"


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
            .connectTimeout(60, TimeUnit.SECONDS) // Adjust timeout values as needed
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

}