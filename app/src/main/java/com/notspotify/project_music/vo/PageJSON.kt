package com.notspotify.project_music.vo

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PageJSON(
    val content: List<ArtistJSON>,
    val pageable: Pageable,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)


@JsonClass(generateAdapter = true)
data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean,
    val paged: Boolean
)