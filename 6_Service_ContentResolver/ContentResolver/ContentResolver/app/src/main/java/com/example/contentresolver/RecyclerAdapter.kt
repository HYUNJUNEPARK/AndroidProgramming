package com.example.contentresolver

import android.media.MediaPlayer
import android.net.Uri
import java.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contentresolver.databinding.ItemRecyclerBinding

/*
MediaPlayer : 음원을 재생하는 클래스
mediaPlayer != null
-item 목록을 클릭할 때마다 음악 중복 실행 방지
-mediaPlayer != null : 이미 mediaPlayer 에 데이터가 있다 == 음악이 재생 중이다
-mediaPlayer = null 으로 재생 중인 음원을 비워주고 클릭 된 Uri 에 해당하는 음원으로 채움
*/

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    var musicList = mutableListOf<Music>() //음원 목록을 저장
    var mediaPlayer:MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val music = musicList.get(position)
        holder.setMusic(music)
    }

    inner class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        var musicUri: Uri? = null

        init {
            binding.root.setOnClickListener {
                if(mediaPlayer != null) {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                mediaPlayer = MediaPlayer.create(binding.root.context, musicUri)
                mediaPlayer?.start()
            }
        }

        fun setMusic(music:Music) {
            binding.run {
                imageAlbum.setImageURI(music.getAlbumUri())
                textArtist.text = music.artist
                textTitle.text = music.title
                val duration = SimpleDateFormat("mm:ss").format(music.duration)
                textDuration.text = duration
            }
            this.musicUri = music.getMusicUri()
        }
    }
}
