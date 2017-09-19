package com.example.itunexmusicsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class ListActivity : AppCompatActivity() {

    lateinit private var mAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mAdapter = ListAdapter(this, R.layout.list_item)

        val listView = findViewById(R.id.list_view) as ListView
        listView.setAdapter(mAdapter)

        val editText = findViewById(R.id.edit_text) as EditText
        editText.setOnKeyListener(OnKeyListener())

        listView.onItemClickListener = OnItemClickListener()
    }

    private inner class ListAdapter(context: Context, resource: Int) : ArrayAdapter<Music>(context, resource) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                // 再利用可能なViewがない場合は作る
                convertView = layoutInflater.inflate(R.layout.list_item, null)
            }

            val imageView = convertView?.findViewById(R.id.image_view) as ImageView
            val trackTextView = convertView.findViewById(R.id.track_text_view) as TextView
            val artistTextView = convertView.findViewById(R.id.artist_text_view) as TextView

            imageView.setImageBitmap(null) // 残ってる画像を消す（再利用された時）

            // 表示する行番号のデータを取り出す
            val result = getItem(position)

            Picasso.with(context).load(result.artworkUrl100).into(imageView)
            trackTextView.text = result.trackName
            artistTextView.text = result.artistName

            return convertView
        }
    }

    private inner class OnKeyListener : View.OnKeyListener {

        override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
            if (keyEvent.action != KeyEvent.ACTION_UP || keyCode != KeyEvent.KEYCODE_ENTER) {
                return false
            }

            val editText = view as EditText
            // キーボードを閉じる
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)

            var text = editText.text.toString()
            try {
                // url encode　例. スピッツ > %83X%83s%83b%83c
                text = URLEncoder.encode(text, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                Log.e("", e.toString(), e)
                return true
            }

            if (!TextUtils.isEmpty(text)) {
                val request = iTunesService.create().search(text)
                 Log.d("", request.request().url().toString())
                val hoge = object :Callback<Musics> {

                    override fun onResponse(call: Call<Musics>?, response: Response<Musics>?) {
                        Log.d("", response.toString())
                        Log.d("", "success")

                        mAdapter.clear()
                        response?.body()?.results?.forEach { mAdapter.add(it) }
                    }

                    override fun onFailure(call: Call<Musics>?, t: Throwable?) {
                        Log.d("", call.toString())
                        Log.d("", "failed")
                    }
                }
                request.enqueue(hoge)
            }
            return true
        }
    }

    private inner class OnItemClickListener : AdapterView.OnItemClickListener {

        override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val intent = Intent(this@ListActivity, DetailActivity::class.java)
            // タップされた行番号のデータを取り出す
            val result = mAdapter.getItem(position)
            intent.putExtra("track_name", result.trackName)
            intent.putExtra("preview_url", result.previewUrl)

            startActivity(intent)
        }
    }
}

