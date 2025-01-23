package com.technews.data

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query



interface NewsServiceInterface {
    @GET("yapay-zeka")
    suspend fun getNews(@Query("s") page: Int): ResponseBody
}

class NewsService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.webtekno.com/")
        .build()

    val newsService = retrofit.create(NewsServiceInterface::class.java)

    suspend fun getNews(page: Int): List<News> {
//        val pageHtml: String = newsService.getNews(page = page).string()
//        println(pageHtml)

        val mockData = List<News>(5) { index ->
            News(
                title = index.toString(),
                url = "",
                image = "",
            )
        }
        return mockData
    }
}