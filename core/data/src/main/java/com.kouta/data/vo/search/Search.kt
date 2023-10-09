package com.kouta.data.vo.search

import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.PageInfo
import com.kouta.data.vo.Thumbnail
import com.squareup.moshi.JsonClass

class Search {
    @JsonClass(generateAdapter = true)
    data class Response(
        val nextPageToken: String?,
        val prevPageToken: String?,
        val pageInfo: PageInfo,
        val items: List<SearchItems>
    ) {
        @JsonClass(generateAdapter = true)
        data class SearchItems(
            val id: Id,
            val snippet: Snippet
        ) {
            @JsonClass(generateAdapter = true)
            data class Id(
                val videoId: String?,
                val channelId: String?,
                val playlistId: String?
            )

            @JsonClass(generateAdapter = true)
            data class Snippet(
                val publishedAt: String,
                val channelId: String,
                val title: String,
                val description: String,
                val thumbnails: Thumbnails,
                val channelTitle: String,
                val liveBroadcastContent: LiveBroadcastContent
            ) {
                @JsonClass(generateAdapter = true)
                data class Thumbnails(
                    val default: Thumbnail,
                    val medium: Thumbnail,
                    val high: Thumbnail,
                    val standard: Thumbnail,
                    val maxres: Thumbnail
                )
            }
        }
    }


    data class RequestQuery(
        val parts: List<Part>,
        val filter: Filter? = null,
        val channelId: String? = null,
        val channelType: ChannelType = ChannelType.ANY,
        val eventType: EventType? = null,
        val maxResults: Int = 5,
        val order: Order = Order.RELEVANCE,
        val pageToken: String? = null,
        val publishedAfter: String? = null,
        val publishedBefore: String? = null,
        val q: String? = null,
        val regionCode: String = "jp",
        val relevanceLanguage: String = "ja",
        val safeSearch: SafeSearch = SafeSearch.NONE,
        val types: List<Type> = listOf(),
        val videoCaption: VideoCaption = VideoCaption.ANY,
        val videoCategoryId: String? = null,
        val videoDefinition: VideoDefinition = VideoDefinition.ANY,
        val videoDuration: VideoDuration = VideoDuration.ANY
    ) {
        enum class Part(val value: String) {
            ID("id"),
            SNIPPET("snippet"),
        }

        enum class Filter(val value: String) {
            FOR_CONTENT_OWNER("forContentOwner"),
            FOR_DEVELOPER("forDeveloper"),
            FOR_MINE("forMine");
        }

        enum class ChannelType(val value: String) {
            ANY("any"),
            SHOW("show");
        }

        enum class EventType(val value: String) {
            COMPLETED("completed"),
            LIVE("live"),
            UPCOMING("upcoming");
        }

        enum class Order(val value: String) {
            DATE("date"),
            RATING("rating"),
            RELEVANCE("relevance"),
            TITLE("title"),
            VIDEO_COUNT("videoCount"),
            VIEW_COUNT("viewCount");
        }

        enum class SafeSearch(val value: String) {
            MODERATE("moderate"),
            NONE("none"),
            STRICT("strict");
        }

        enum class Type(val value: String) {
            CHANNEL("channel"),
            PLAYLIST("playlist"),
            VIDEO("video");
        }

        enum class VideoCaption(val value: String) {
            ANY("any"),
            CLOSED_CAPTION("closedCaption"),
            NONE("none");
        }

        enum class VideoDefinition(val value: String) {
            ANY("any"),
            HIGH("high"),
            STANDARD("standard");
        }

        enum class VideoDuration(val value: String) {
            ANY("any"),
            LONG("long"),
            MEDIUM("medium"),
            SHORT("short");
        }

        fun toQueryMap(): Map<String, String> {
            val params = mutableMapOf<String, String>()

            params["part"] = parts.joinToString(",") { it.value }

            filter?.let {
                params[filter.value] = false.toString()
            }

            channelId?.let {
                params["channelId"] = it
            }

            params["channelType"] = channelType.value

            if (types.contains(Type.VIDEO) && eventType != null) {
                params["eventType"] = eventType.value
            }

            params["maxResults"] = maxResults.toString()

            params["order"] = order.value

            pageToken?.let { params["pageToken"] = it }

            publishedAfter?.let {
                params["publishedAfter"] = publishedAfter
            }

            publishedBefore?.let {
                params["publishedBefore"] = publishedBefore
            }

            q?.let {
                params["q"] = q
            }

            params["regionCode"] = regionCode

            params["relevanceLanguage"] = relevanceLanguage

            params["safeSearch"] = safeSearch.value

            if (types.isNotEmpty()) {
                params["type"] = types.joinToString(",") { it.value }
            }

            params["videoCaption"] = videoCaption.value

            videoCategoryId?.let {
                params["videoCategoryId"] = it
            }

//            params["videoDefinition"] = videoDefinition.value
//
//            params["videoDuration"] = videoDuration.value

            return params
        }
    }
}