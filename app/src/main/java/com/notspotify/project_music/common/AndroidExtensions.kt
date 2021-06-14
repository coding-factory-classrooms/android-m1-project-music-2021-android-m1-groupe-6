package com.notspotify.project_music.common

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

internal fun Fragment.makeToast(value: String) {
    Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()
}

internal fun AnimationDrawable.startWithFade(){
    this.setEnterFadeDuration(1000)
    this.setExitFadeDuration(1000)
    this.start()
}

@RequiresApi(Build.VERSION_CODES.N)
internal fun TextView.formatHtml(text : String){
    this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
}