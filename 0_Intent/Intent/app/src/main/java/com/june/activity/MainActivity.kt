package com.june.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.june.activity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}

    //startActivityForResult() 가 deprecated 되었기 때문에 ActivityResultLauncher 사용
    lateinit var resultListener: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //서브 액티비티로 보낼 데이터를 담은 intent
        val intent = Intent(this, SubActivity::class.java)
        intent.putExtra("key1", "value1FromMain")
        intent.putExtra("key2", 100)

        //startActivityForResult 의 대안
        binding.btnStart.setOnClickListener {
            resultListener.launch(intent)
        }

        //onActivityResult 의 대안
        resultListener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK) {
                val message = it.data?.getStringExtra("returnValue")
                binding.textView2.text = message
            }
        }
    }
}