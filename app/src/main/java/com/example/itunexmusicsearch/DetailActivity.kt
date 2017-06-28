package com.example.itunexmusicsearch

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.MediaController
import android.widget.VideoView


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // ActionBarのタイトルを設定する
        val trackName = intent.extras.getString("track_name")
        supportActionBar!!.title = trackName

        val previewUrl = intent.extras.getString("preview_url")
        if (!TextUtils.isEmpty(previewUrl)) {
            val videoView = findViewById(R.id.video_view) as VideoView
            videoView.setMediaController(MediaController(this)) // 再生ボタンとかをつける
            videoView.setVideoURI(Uri.parse(previewUrl)) // URLを設定する
            videoView.start() // 再生する
        }
    }
}
