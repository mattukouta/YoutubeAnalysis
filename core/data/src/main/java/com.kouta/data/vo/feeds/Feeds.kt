package com.kouta.data.vo.feeds

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

class Feeds {
    @Xml(name = "feed")
    data class Response(
        @Element(name = "entry")
        val entry: List<Entry>
    ) {
        @Xml(name = "entry")
         data class Entry(
             @PropertyElement(name="yt:videoId")
             val videoId: String
         )
    }
}