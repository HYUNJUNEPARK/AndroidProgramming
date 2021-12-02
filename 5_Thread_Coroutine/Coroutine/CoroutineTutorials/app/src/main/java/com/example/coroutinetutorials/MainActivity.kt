package com.example.coroutinetutorials

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutinetutorials.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var job:Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //[START launch(), cancel() 예시]
        binding.btnJobStart.setOnClickListener {
            job = CoroutineScope(Dispatchers.Default).launch() {
                val job1 = launch {
                    for(i in 0..10) {
                        delay(500)
                        Log.d("코루틴", "결과 = $i")
                    }
                }
            }
        }
        binding.btnJobStop.setOnClickListener {
            job?.cancel()
        }
        //[END launch(), cancel() 예시]


        //[START launch(), join() 예시]
        binding.btnJobJoin.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch() {
                //join() : launch 블록 뒤에 join()을 사용하면 코루틴이 순차적으로 실행됨 launch1 -> launch2

                //launch1
                launch {
                    for(i in 0..5) {
                        delay(500)
                        Log.d("코루틴", "결과1 = $i")
                    }
                }.join()
                //launch2
                launch {
                    for(i in 0..5) {
                        delay(500)
                        Log.d("코루틴", "결과2 = $i")
                    }
                }
            }
        }
        //[END launch(), join() 예시]


        //[START async, await() 예시]
        binding.btnAsync.setOnClickListener {
            CoroutineScope(Dispatchers.Default).async {
                //코루틴을 async 로 선언하고 결괏값을 처리하는 곳에 await() 함수를 사용하면 결과 처리가 완료된 후에 await() 를 호출한 줄의 코드가 실행됨
                val deffered1 = async {
                    delay(500)
                    350
                }
                val deffered2 = async {
                    delay(1000)
                    200
                }
                Log.d("코루틴", "연산 결과 = ${deffered1.await() + deffered2.await()}")//연산 결과 = 550
            }
        }
        //[END async, await() 예시]


    //[START suspend 키워드, withContext() 예시]
        //Dispatchers.Main -> withContext -> Dispatchers.IO
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                readFile()
            }
            Log.d("코루틴", "파일결과=$result")
        }
    }
    suspend fun readFile() : String {
        return "파일내용"
    }
    //[END suspend 키워드, withContext() 예시]
}