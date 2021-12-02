package com.example.thread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.concurrent.thread

/* 스레드 사용법 정리
1. Thread 클래스의 상속
2. Runnable 인터페이스 구현
3. 람다식으로 Runnable 익명객체 구현 - 인터페이스 내부에 메서드가 하나만 있는 경우
4. thread() 안에 start=true 를 전달
*/
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1. Thread 클래스 상속
        var wThread = WorkerThread()
        wThread.start()

        //2. Runnable 인터페이스 구현
        var rThread = Thread(WorkerRunnable())
        rThread.start()

        //3. 람다식으로 Runnable 익명객체 구현
        Thread {
            var i = 0
            while (i < 10) {
                i += 1
                Log.i("LambdaThread", "$i")
            }
        }.start()

        //4. thread() 안에 start=true 를 전달
        thread(start=true) {
            var i = 0
            while (i < 10) {
                i += 1
                Log.i("KotlinThread", "$i")
            }
        }
    }
}
//1. Thread 클래스 상속
class WorkerThread : Thread() {
    override fun run() {
        var i = 0
        while (i < 10) {
            i += 1
            Log.i("WorkerThread", "$i")
        }
    }
}
//2. Runnable 인터페이스 구현
class WorkerRunnable : Runnable {
    override fun run() {
        var i = 0
        while (i < 10) {
            i += 1
            Log.i("WorkerRunnable", "$i")
        }
    }
}