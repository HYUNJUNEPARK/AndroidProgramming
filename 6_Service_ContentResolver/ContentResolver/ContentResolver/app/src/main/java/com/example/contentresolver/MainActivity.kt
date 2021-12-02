package com.example.contentresolver

import android.Manifest
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentresolver.databinding.ActivityMainBinding

/*
AndroidManifest 에 외부 저장소 읽기 퍼미션 추가

콘텐트 프로바이더
-내가 만든 앱의 데이터를 다른 앱에서도 사용할 수 있게 제공할 때 필요한 도구
-안드로이드 OS에 이미 구현되어 있어(연락처, 갤러리, 음악 등) 앱 개발을 하면서 사용할 일은 거의 없음

콘텐트 리졸버
-다른 앱의 콘텐트 프로바이더로부터 데이터를 가져오는 도구
-미디어 정보를 저장하는 저장소 용도로 MediaStore 를 사용함
-contentResolver.query() 로 커서(cursor)를 만들어서 데이터를 가져옴

커서(cursor) : 컨텐트 리졸버가 요청한 쿼리를 통해 반환된 데이터셋을 반복문으로 하나씩 처리 할 수 있음
-val cursor = contentResolver.query(url 주소, 테이블에서 필요한 컬럼, null, null, null)

MediaStore
-각각의 미디어가 종류별로 DB의 테이블처럼 있고, 각 테이블당 주소가 하나씩 제공됨. 테이블 주소와 칼럼명은 상수로 제공
*이미지: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
*오디오: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
*비디오: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
-미디어의 종류마다 1개의 주소를 가진 콘텐트 프로바이더가 구현되어 있음
-외부 저장소에 있기 때문에 외부 저장소를 읽는 권한이 필요

startProcess() : 리사이클러뷰 세팅
getMusicList() : 컨텐트 리졸버가 외부 저장소(MediaStore)에 요청한 데이터를 커서(cursor)로 받아옴
-val listUrl : 음원 데이터 uri 를 담아 둔 변수
-val dataCol : 음원 데이터 컬럼(불러올 데이터 목록)
-val musicList : 커서로 전달받은 데이터를 저장할 변수
*/

class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requirePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 999)
    }

    override fun permissionGranted(requestCode: Int) {
        startProcess()
    }

    override fun permissionDenied(requestCode: Int) {
        Toast.makeText(this, "외부저장소 권한 승인이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_LONG).show()
        finish()
    }

    fun startProcess() {
        val adapter = RecyclerAdapter()
        adapter.musicList.addAll(getMusicList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getMusicList() : List<Music> {
        val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val dataCol = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = contentResolver.query(listUrl, dataCol, null, null, null)
        val musicList = mutableListOf<Music>()
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getLong(4)
            val music = Music(id, title, artist, albumId, duration)
            musicList.add(music)
        }
        return musicList
    }
}
