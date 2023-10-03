package com.kouta.data.vo.activities

import androidx.annotation.IntRange
import com.kouta.data.vo.PageInfo
import com.squareup.moshi.JsonClass

class ChannelActivities {
    @JsonClass(generateAdapter = true)
    data class Response(
        val nextPageToken: String,
        val prevPageToken: String,
        val pageInfo: PageInfo,
        val items: List<ActivityItem>
    ) {
        @JsonClass(generateAdapter = true)
        data class ActivityItem(
            val kind: String,
            val eTag: String,
            val id: String,
            val snippet: Snippet,
            val contentDetails: ContentDetails
        ) {
            @JsonClass(generateAdapter = true)
            data class Snippet(
                val publishedAt: String,
                val channelId: String,
                val title: String,
                val description: String,
                val thumbnails: Thumbnails,
                val channelTitle: String,
                val type: Type,
                val groupId: String
            ) {
                @JsonClass(generateAdapter = true)
                data class Thumbnails(
                    val default: Value,
                    val medium: Value,
                    val high: Value,
                    val standard: Value,
                    val maxres: Value

                ) {
                    @JsonClass(generateAdapter = true)
                    data class Value(
                        val url: String,
                        val width: Int,
                        val height: Int
                    )
                }

                enum class Type(val value: String) {
                    CHANNEL_ITEM("channelItem"),
                    FAVORITE("favorite"),
                    LIKE("like"),
                    PLAYLIST_ITEM("playlistItem"),
                    PROMOTED_ITEM("promotedItem"),
                    RECOMMENDATION("recommendation"),
                    SOCIAL("social"),
                    SUBSCRIPTION("subscription"),
                    UPLOAD("upload"),
                    UNKNOWN("");
                }
            }

            @JsonClass(generateAdapter = true)
            data class ContentDetails(
                val upload: Upload?,
                val like: Like?,
                val favorite: Favorite?,
                val comment: Comment?,
                val subscription: Subscription?,
                val playlistItem: PlaylistItem?,
                val recommendation: Recommendation?,
                val social: Social?,
                val channelItem: ChannelItem?
            ) {
                @JsonClass(generateAdapter = true)
                data class Upload(
                    val videoId: String
                )

                @JsonClass(generateAdapter = true)
                data class Like(
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Favorite(
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Comment(
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String?,
                        val channelId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Subscription(
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val channelId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class PlaylistItem(
                    val playlistId: String,
                    val playlistItemId: String,
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Recommendation(
                    val resourceId: ResourceId,
                    val reason: Reason,
                    val seedResourceId: SeedResourceId
                ) {
                    enum class Reason(val value: String) {
                        VIDEO_FAVORITED("videoFavorited"),
                        VIDEO_LIKED("videoLiked"),
                        VIDEO_WATCHED("videoWatched"),
                        UNKNOWN("");
                    }
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String,
                        val channelId: String
                    )

                    @JsonClass(generateAdapter = true)
                    data class SeedResourceId(
                        val kind: String,
                        val videoId: String?,
                        val channelId: String?,
                        val playlistId: String?
                    )
                }

                @JsonClass(generateAdapter = true)
                data class Social(
                    val author: String,
                    val imageUrl: String,
                    val referenceUrl: String,
                    val resourceId: ResourceId,
                    val type: Type
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(
                        val kind: String,
                        val videoId: String?,
                        val channelId: String?,
                        val playlistId: String?
                    )

                    enum class Type(val value: String) {
                        FACEBOOK("facebook"),
                        GOOGLE_PLUS("googlePlus"),
                        TWITTER("twitter"),
                        UNSPECIFIED("unspecified"),
                        UNKNOWN("");
                    }
                }

                @JsonClass(generateAdapter = true)
                data class ChannelItem(
                    val resourceId: ResourceId
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ResourceId(val hh: String)
                }
            }
        }
    }

    @JsonClass(generateAdapter = true)
    data class RequestQuery(
        val part: List<Part>,
        val filter: Filter,
        @IntRange(from = 0, to = 50)
        val maxResults: Int? = null,
        val pageToken: String? = null,
        val publishedAfter: DateTime? = null,
        val publishedBefore: DateTime? = null,
        val regionCode: String? = null
    ) {
        fun toQueryMap(): Map<String, String> {
            val params = mutableMapOf<String, String>()

            part.forEach { params[it.value] = it.value }

            when (filter) {
                is Filter.ChannelId -> params["channelId"] = filter.channelId
                is Filter.Mine -> params["mine"] = filter.isMine.toString()
            }

            maxResults?.let { params["maxResults"] = it.toString() }

            pageToken?.let { params["pageToken"] = it }

            publishedAfter?.let { params["publishedAfter"] = it.date }

            publishedBefore?.let { params["publishedBefore"] = it.date }

            regionCode?.let { params["regionCode"] = it }

            return params
        }

        enum class Part(val value: String) {
            CONTENT_DETAILS("contentDetails"),
            ID("id"),
            SNIPPET("snippet");
        }

        sealed class Filter {
            data class ChannelId(val channelId: String) : Filter()
            data class Mine(val isMine: Boolean) : Filter()
        }

        data class DateTime(val date: String)
    }
}