package eu.tutorials.shaproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RoomApi {
    @GET("roomsauth")
    fun getRooms(
        @Query("room_name") param1: String,
        @Query("room_password") param2: String
    ): Call<Boolean>
}