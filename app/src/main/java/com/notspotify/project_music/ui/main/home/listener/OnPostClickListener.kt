package com.notspotify.project_music.ui.main.home.listener

import com.notspotify.project_music.enums.VoteType

interface OnPostClickListener {
    fun  invoke(voteType: VoteType, idPost: Long)
}