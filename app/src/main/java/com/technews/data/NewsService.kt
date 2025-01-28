package com.technews.data

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


enum class WebSource {
    Webtekno,
}

interface WebteknoServiceInterface {
    @GET("yapay-zeka")
    suspend fun getNews(@Query("s") page: Int? = null): ResponseBody

    @GET("{path}")
    suspend fun getNewsDetail(@Path("path") url: String): ResponseBody
}

object NewsService {
    fun parseDate(dateString: String, source: WebSource): ZonedDateTime {
        when (source) {
            WebSource.Webtekno -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                return ZonedDateTime.parse(dateString, formatter)
            }
            else -> {
                // Placeholder
                return ZonedDateTime.now()
            }
        }
    }

    private fun addLoggingInterceptor(builder: OkHttpClient.Builder) {
        val interceptorLogging = HttpLoggingInterceptor()
//        interceptorLogging.setLevel(
//          if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
//          else HttpLoggingInterceptor.Level.NONE
//        )

        builder.addInterceptor(interceptorLogging)
    }

    fun getWebteknoNewsService(): WebteknoServiceInterface {
        if (webteknoNewsService != null) return webteknoNewsService!!

        val client = OkHttpClient().newBuilder()
        addLoggingInterceptor(client)

        val retrofit = Retrofit.Builder()
            .baseUrl(webteknoBaseUrl)
            .client(client.build())
            .build()

        webteknoNewsService = retrofit.create(WebteknoServiceInterface::class.java)
        return webteknoNewsService!!
    }

    const val webteknoBaseUrl = "https://www.webtekno.com/"
    private var webteknoNewsService: WebteknoServiceInterface? = null

    suspend fun getNewsWebtekno(page: Int): List<News> {
        val pageHtml: String = getWebteknoNewsService().getNews(page = if (page > 1) page else null).string()
        val document = Jsoup.parse(pageHtml)
        val newsItemTags = document.select(".content-timeline__list > .content-timeline__item")
        val newsItems = newsItemTags.map { newsItem ->
            News(
                title = newsItem.selectFirst("h3.content-timeline__detail__title")?.text() ?: "",
                url = newsItem.selectFirst("a.content-timeline__link")?.attr("href") ?: "",
                image = newsItem.selectFirst("img.content-timeline__media__image")?.attr("data-original") ?: "",
                date = parseDate(
                    newsItem.selectFirst(".content-timeline__time__timeago > time")!!.attr("datetime"),
                    WebSource.Webtekno),
                source = "Webtekno",
            )
        }

        return newsItems
    }

    suspend fun getNewsDetailWebtekno(newsUrl: String): Array<NewsDetailItem> {
        var items = arrayOf<NewsDetailItem>()
        val pageHtml: String = getWebteknoNewsService().getNewsDetail(newsUrl).string()
        val document = Jsoup.parse(pageHtml)
        val pTags = document.select(".content-body__detail > p")

        pTags.forEach { pTag ->
            if (pTag.selectFirst("img") != null) {
                var imgSrc = pTag.selectFirst("img")!!.attr("data-original")
                if (imgSrc.startsWith("/")) {
                    imgSrc = webteknoBaseUrl + imgSrc.substring(1)
                }

                items += NewsDetailItem("img", imgSrc)
            } else {
                items += NewsDetailItem("p", pTag.text())
            }
        }

        return items
    }
}
