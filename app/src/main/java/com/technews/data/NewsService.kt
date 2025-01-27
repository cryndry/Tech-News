package com.technews.data

import com.technews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NewsServiceInterface {
    @GET("yapay-zeka")
    suspend fun getNews(@Query("s") page: Int? = null): ResponseBody

    @GET("{path}")
    suspend fun getNewsDetail(@Path("path") url: String): ResponseBody
}

object NewsService {
    val baseUrl = "https://www.webtekno.com/"

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
            .baseUrl(baseUrl)
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

        return newsItems
    }

    suspend fun getNewsDetail(newsUrl: String): Array<NewsDetailItem> {
        var items = arrayOf<NewsDetailItem>()
        val pageHtml: String = getService().getNewsDetail(newsUrl).string()
        val document = Jsoup.parse(pageHtml)
        val pTags = document.select(".content-body__detail > p")

        pTags.forEach { pTag ->
            if (pTag.selectFirst("img") != null) {
                var imgSrc = pTag.selectFirst("img")!!.attr("data-original")
                if (imgSrc.startsWith("/")) {
                    imgSrc = baseUrl + imgSrc.substring(1)
                    println(imgSrc)
                }

                items += NewsDetailItem("img", imgSrc)
            } else {
                items += NewsDetailItem("p", pTag.text())
            }
        }

        println(items.map { item -> item.content })
        return items
    }
}