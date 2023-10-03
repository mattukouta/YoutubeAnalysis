package com.kouta.data.vo.channels

import androidx.annotation.IntRange

class Channels {
    data class Response(
        val kind: String
    )

    data class RequestQuery(
        val part: List<Part>,
        val filter: Filter,
        @IntRange(from = 0, to = 50)
        val maxResults: Int? = null,
        val pageToken: String? = null
    ) {
        enum class Part(val value: String) {
            AUDIT_DETAILS("auditDetails"),
            BRANDING_SETTINGS("brandingSettings"),
            CONTENT_DETAILS("contentDetails"),
            CONTENT_OWNER_DETAILS("contentOwnerDetails"),
            ID("id"),
            LOCALIZATIONS("localizations"),
            SNIPPET("snippet"),
            STATISTICS("statistics"),
            STATUS("status"),
            TOPIC_DETAILS("topicDetails");
        }

        sealed class Filter {
            data class ForUsername(val name: String) : Filter()
            data class Id(val ids: List<String>) : Filter()
            data class Mine(val isMine: Boolean) : Filter()
        }

        fun toQueryMap(): Map<String, String> {
            val params = mutableMapOf<String, String>()

            part.forEach { params["part"] = it.value }

            when (filter) {
                is Filter.ForUsername -> params["forUsername"] = filter.name
                is Filter.Id -> params["id"] = filter.ids.joinToString(",")
                is Filter.Mine -> params["mine"] = filter.isMine.toString()
            }

            maxResults?.let { params["maxResults"] = it.toString() }

            pageToken?.let { params["pageToken"] = it }

            return params
        }
    }
}