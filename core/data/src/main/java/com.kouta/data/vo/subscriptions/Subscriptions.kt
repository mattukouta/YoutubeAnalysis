package com.kouta.data.vo.subscriptions

import androidx.annotation.IntRange
import com.kouta.data.vo.PageInfo
import com.squareup.moshi.JsonClass

class Subscriptions {
    @JsonClass(generateAdapter = true)
    data class Response(
        val nextPageToken: String?,
        val prevPageToken: String?,
        val pageInfo: PageInfo,
        val items: List<Subscription>
    ) {
        @JsonClass(generateAdapter = true)
        data class Subscription(
            val id: String?,
            val snippet: Snippet?,
            val contentDetails: ContentDetails?,
            val subscriberSnippet: SubscriberSnippet?
        ) {
            @JsonClass(generateAdapter = true)
            data class Snippet(
                val publishedAt: String,
                val channelTitle: String?,
                val title: String,
                val description: String,
                val resourceId: ResourceId,
                val channelId: String,
                val thumbnails: Thumbnails,

            ) {
                @JsonClass(generateAdapter = true)
                data class ResourceId(
                    val channelId: String
                )
            }

            @JsonClass(generateAdapter = true)
            data class ContentDetails(
                val totalItemCount: Int,
                val newItemCount: Int,
                val activityType: Type
            ) {
                enum class Type(val value: String) {
                    ALL("all"),
                    UPLOADS("uploads"),
                    UNKNOWN("");
                }
            }

            @JsonClass(generateAdapter = true)
            data class SubscriberSnippet(
                val title: String,
                val description: String,
                val channelId: String,
                val thumbnails: Thumbnails
            )

            @JsonClass(generateAdapter = true)
            data class Thumbnails(
                val default: Thumbnail,
                val medium: Thumbnail,
                val high: Thumbnail
            ) {
                @JsonClass(generateAdapter = true)
                data class Thumbnail(
                    val url: String,
                    val width: Int?,
                    val height: String?
                )
            }
        }
    }

    data class RequestQuery(
        val part: List<Part>,
        val filter: Filter,
        val forChannelId: List<String> = listOf(),
        @IntRange(from = 0, to = 50)
        val maxResults: Int = 5,
        val order: Order = Order.RELEVANCE,
        val pageToken: String? = null
    ) {
        enum class Part(val value: String) {
            CONTENT_DETAILS("contentDetails"),
            ID("id"),
            SNIPPET("snippet"),
            SUBSCRIBER_SNIPPET("subscriberSnippet");
        }

        sealed class Filter {
            data class ChannelId(val channelId: String): Filter()
            data class Id(val id: String): Filter()
            data object Mine: Filter()
            data object MyRecentSubscribers: Filter()
            data object MySubscribers: Filter()
        }

        enum class Order(val value: String) {
            ALPHABETICAL("alphabetical"),
            RELEVANCE("relevance"),
            UNREAD("unread");
        }

        fun toQueryMap(): Map<String, String> {
            val params = mutableMapOf<String, String>()

            params["part"] = part.joinToString(",") { it.value }

            when (filter) {
                is Filter.ChannelId -> params["channelId"] = filter.channelId
                is Filter.Id -> params["id"] = filter.id
                Filter.Mine -> params["mine"] = true.toString()
                Filter.MyRecentSubscribers -> params["myRecentSubscribers"] = true.toString()
                Filter.MySubscribers -> params["mySubscribers"] = true.toString()
            }

            if (forChannelId.isNotEmpty()) {
                params["forChannelId"] = forChannelId.joinToString(",")
            }

            params["maxResults"] = maxResults.toString()

            params["order"] = order.value

            pageToken?.let { params["pageToken"] = it }

            return params
        }
    }
}