package eu.tutorials.shaproject

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Constants {
    val students = mutableListOf<String>()!!
    const val teacher_name:String="doctor_name"
    const val teacher_id:String="y"
    const val course_name:String="a"
    const val course_id :String="1"
    const val base_url:String="http://3.71.111.29/"

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
    }
}