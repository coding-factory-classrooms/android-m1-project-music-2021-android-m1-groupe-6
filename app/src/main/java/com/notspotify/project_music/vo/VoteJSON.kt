package com.notspotify.project_music.vo

import com.notspotify.project_music.enums.VoteType
import com.notspotify.project_music.model.Account

data class VoteJSON (
    val idAccount: Account,
    val idArtistJSON: ArtistJSON,
    val choice : VoteType
)