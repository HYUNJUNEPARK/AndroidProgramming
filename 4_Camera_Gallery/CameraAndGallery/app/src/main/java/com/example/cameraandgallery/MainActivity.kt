package com.example.cameraandgallery

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.cameraandgallery.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat

/*
AndroidManifest 에 카메라 접근 권한과 외부 저장소 읽기, 쓰기 권한 추가

URI(Uniform Resource Identifier) : 특정 리소스 자원을 고유하게 식별할 수 있는 식별자
MediaStore : 안드로이드 외부 저장소를 관리하는 일종의 DB

setViews()
-앱 시작 시 외부 저장소 권한을(val PERM_STORAGE = 99) 요청하고 승인 되었을 때 호출되며 '카메라', '갤러리' 버튼을 UI에 배치
-카메라 버튼 : 카메라 권한이(val PERM_CAMERA = 100) 승인 되었을 때 fun openCamera() 호출
-갤러리 버튼 : fun openGallery() 호출

openCamera()
-카메라 권한이 (val PERM_CAMERA = 100) 승인 되었을 때 카메라앱을 실행
-카메라 촬영이 있다면 이미지 Uri 를 생성해 외부 저장소에 저장하고 onActivityResult() 로 requestCode(REQ_CAMERA = 101) 와 사진 데이터를 보냄
-파일의 Uri 를 생성하는 createImageUri() 와 파일 이름을 생성하는 newFileName() 이 실행해 이미지 Uri 를 생성하고 문제가 없다면 uri 데이터를 realUri 에 담는다

createImageUri()
-파라미터로 파일이름과 데이터 형식(MIME_TYPE)을 받아 Uri 를 생성

openGallery()
-Intent.ACTION_PICK 을 사용해 intent.type 에서 지정한 데이터 형식과 같은 데이터를 MediaStore 에서 불러와 갤러리 앱 실행
-갤러리에서 이미지를 선택하면 인텐트와 requestCode(REQ_STORAGE) 를 보내고 onActivityResult 에서 처리

loadBitmap()
-카메라로 이미지를 촬영하고 UI 를 세팅하면 사진의 프리뷰가 넘어와 이미지가 깨질 수 있음
-파라미터로 넘어온 uri 에 해당되는 이미지를 비트맵 형식으로 바꿔줌
*/


class MainActivity : BaseActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //[START requestCodes]
    val PERM_STORAGE = 99 //외부 저장소 권한
    val PERM_CAMERA = 100 //카메라 권한
    val REQ_CAPTURED_PHOTO = 101 //카메라 촬영 요청
    val REQ_SELECTED_GALLERY_PHOTO = 102 //갤러리 데이터 처리
    //[END requestCodes]

    /*realUri
    openCamera() 에서 사진 촬영이 있을 때 이미지 uri 정보를 저장하고 onActivityResult() 에서 촬영한 사진을 @+id/imagePreview 에 바인딩하고 null 로 비워 재활용*/
    var readUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STORAGE)
    }

    override fun permissionGranted(requestCode: Int) {
        when(requestCode) {
            PERM_STORAGE -> setViews()
            PERM_CAMERA -> openCamera()
        }
    }

    override fun permissionDenied(requestCode: Int) {
        when(requestCode) {
            PERM_STORAGE -> {
                Toast.makeText(baseContext, "외부저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
            PERM_CAMERA -> Toast.makeText(baseContext, "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
        }
    }

    /*
    REQ_CAPTURED_PHOTO : 사진 촬영하면 넘어온 프리뷰 이미지를 비트맵 이미지로 바꿔주고 UI 에 이미지 세팅 후 readUri 을 비워줌
    REQ_SELECTED_GALLERY_PHOTO : 갤러리에서 이미지가 선택되면 UI 에 이미지를 세팅해줌
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode){
                REQ_CAPTURED_PHOTO -> {
                    readUri?.let { uri ->
                        val bitmap = loadBitmap(uri)
                        binding.imagePreview.setImageBitmap(bitmap)
                        readUri = null
                    }
                }
                REQ_SELECTED_GALLERY_PHOTO -> {
                    data?.data?.let { uri ->
                        binding.imagePreview.setImageURI(uri)
                    }
                }
            }
        }
    }

    fun setViews() {
        binding.buttonCamera.setOnClickListener {
            requirePermissions(arrayOf(Manifest.permission.CAMERA), PERM_CAMERA)
        }
        binding.buttonGallery.setOnClickListener {
            openGallery()
        }
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageUri(newFileName(), "image/jpg")?.let { uri ->
            readUri = uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, readUri)
            startActivityForResult(intent, REQ_CAPTURED_PHOTO)
        }
    }

    fun createImageUri(filename: String, mimeType: String) : Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun newFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_SELECTED_GALLERY_PHOTO)
    }

    fun loadBitmap(photoUri: Uri): Bitmap? {
        var imageBitmap: Bitmap? = null
        try {
            imageBitmap = if (Build.VERSION.SDK_INT > 27) {
                val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageBitmap
    }
}
