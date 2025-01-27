package com.technews.data

import com.technews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsServiceInterface {
    @GET("yapay-zeka")
    suspend fun getNews(@Query("s") page: Int? = null): ResponseBody
}

object NewsService {
    private fun addLoggingInterceptor(builder: OkHttpClient.Builder) {
        val interceptorLogging = HttpLoggingInterceptor()
//        interceptorLogging.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

        builder.addInterceptor(interceptorLogging)
    }

    fun getService(): NewsServiceInterface {
        if (newsService != null) return newsService!!

        val client = OkHttpClient().newBuilder()
        addLoggingInterceptor(client)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.webtekno.com/")
            .client(client.build())
            .build()

        newsService = retrofit.create(NewsServiceInterface::class.java)
        return newsService!!
    }

    private var newsService: NewsServiceInterface? = null

    suspend fun getNews(page: Int): List<News> {
        val pageHtml: String = getService().getNews(page = if (page > 1) page else null).string()
        val document = Jsoup.parse(pageHtml)
        val newsItemTags = document.select(".content-timeline__list > .content-timeline__item")
        val newsItems = newsItemTags.map { newsItem ->
            News(
                title = newsItem.selectFirst("h3.content-timeline__detail__title")?.text() ?: "",
                url = newsItem.selectFirst("a.content-timeline__link")?.attr("href") ?: "",
                image = newsItem.selectFirst("img.content-timeline__media__image")?.attr("data-original") ?: "",
            )
        }

        println("******************")
        println(newsItems)
        println("******************")

        return newsItems
    }
}