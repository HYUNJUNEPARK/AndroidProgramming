package com.example.fileio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fileio.databinding.ActivityMainBinding

//AndroidManifest 에 읽기/쓰기 퍼미션을 추가

class MainActivity : AppCompatActivity() {
    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(binding.root) 은 startProcess() 에서 작성

        checkPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 99){
            var check = true

            for(grant in grantResults) {
                if(grant != PackageManager.PERMISSION_GRANTED){
                    check = false
                    break
                }
            }
            if(!check){
                Toast.makeText(this, "권한요청을 모두 승인해야지만 앱을 실행할 수 있습니다.", Toast.LENGTH_LONG).show()
                finish()
            }else{
                startProcess()
            }
        }
    }

    /*
    내부 저장소에 파일을 저장해야하므로 filesDir.absolutePath 를 사용
    filesDir : 내부 저장소에 파일을 읽고, 쓸 때 사용
    absolutePath() : 시스템의 루트(/)부터 시작하는 경로인 절대경로를 반환
    ex) dirPath=/data/user/0/com.example.fileio/files
        fullPath=/data/user/0/com.example.fileio/files/NewFile.txt

    내부 저장소에 데이터를 저장하려면 1)절대경로, 2)파일명, 3)저장할 내용 이 필요함
    */
    fun startProcess() {
        setContentView(binding.root)

        val dirPath = filesDir.absolutePath
        val fileName = "NewFile.txt"
        val fileUtil = FileUtil()

        binding.btnWrite.setOnClickListener {
            val content = binding.editText.text.toString().trim()

            if(content.length > 0) {
                Log.d("FileUtil", "dirPath=$dirPath")
                fileUtil.writeTextFile(dirPath, fileName, content)
                binding.editText.setText("")
            }
        }
        binding.btnRead.setOnClickListener {
            val fullPath = "$dirPath/$fileName"

            Log.d("FileUtil", "fullPath=$fullPath")
            val content = fileUtil.readTextFile(fullPath)
            binding.editText.setText(content)
        }
    }

    fun checkPermission() {
        val readPermission = ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
        val writePermission = ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED

        if(readPermission || writePermission){
            requestPermission()
        }else{
            startProcess()
        }
    }

    //ActivityCompat.requestPermissions() 으로 권한 여가 알림창 띄움 -> onRequestPermissionsResult() 에서 처리
    fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 99)
    }

    fun getExternalDirectory() : String? {
        var directory:String? = null

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        } else {
            directory = "${Environment.getExternalStorageDirectory().absolutePath}/Documents"
        }

        Log.d("FileUtil", "external dir=${directory}")

        return directory
    }
}
