package com.example.contentresolver

import android.net.Uri
import android.provider.MediaStore

/*
getMusicUri() : 음원의 uri 를 생성하는 메서드
-음원 uri = '기본 MediaStore 의 주소' + '음원 id'

getAlbumUri() : 앨범 아트 uri 를 생성하는 메서드
-The method 'Uri.parse()' creates a new Uri object from a properly formatted String (String -> Uri)
*/

class Music (id: String, title: String?, artist: String?, albumId: String?, duration: Long?) {
    var id: String = ""
    var title: String?
    var artist: String?
    var albumId: String? //앰범을 구분하는 ID
    var duration: Long?

    init {
        this.id = id
        this.title = title
        this.artist = artist
        this.albumId = albumId
        this.duration = duration
    }

    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
        )
    }

    fun getAlbumUri(): Uri {
        return Uri.parse(
            "content://media/external/audio/albumart/" + albumId
        )
    }
}
