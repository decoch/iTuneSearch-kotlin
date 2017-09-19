package com.example.itunexmusicsearch

import retrofit2.Call
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author tominaga
 */
interface iTunesService {

    @GET("search")
    fun search(@Query("term") query: String,
               @Query("country") country: String = "JP",
               @Query("media") media: String = "music",
               @Query("lang") lang: String = "ja_jp"): Call<Musics>

    /**
     * Companion object for the factory
     */
    companion object {
        fun create(): iTunesService {
            val retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl("https://itunes.apple.com/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            return retrofit.create(iTunesService::class.java)
        }
    }
}

data class Musics(
        var results: List<Music>
)

data class Music(
        var artworkUrl100: String?,
        var trackName: String?,
        var artistName: String?,
        var previewUrl: String
)
