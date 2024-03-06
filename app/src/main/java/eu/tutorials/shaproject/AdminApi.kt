package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminApi {

        @GET("adminsauth")
        fun getAdmins(
            @Query("admin_email") param1: String,
            @Query("admin_pass") param2: String
        ): Call<Boolean>
}