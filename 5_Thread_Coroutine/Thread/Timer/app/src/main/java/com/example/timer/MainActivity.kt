package com.example.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.timer.databinding.ActivityMainBinding
import kotlin.concurrent.thread

/*
프로세스 : 시스템상 실행 중인 프로그램. 독립된 메모리 공간을 할당받음
스레드 : 하나의 프로세스상에 독립적인 실행 흐름
안드로이드는 메인 스레드와 백그라운드 스레드의 통신을 위해 '핸들러'와 '루퍼'를 제공
안드로이드 시스템은 메모리 외의 다른 곳에서 데이터를 가져오는 작업은 백그라운드 스레드에서 처리하는 것을 권장함 -> 메인스레드와 백그라운드 스레드로 나눠서 작업 처리
_______________________________________________
메인 스레드(UI 스레드) :
-하나의 앱에 하나만 존재
백그라운드 스레드(서브 스레드) :
-하나의 앱에 여러개 존재 가능
-원칙적으로 백그라운드 스레드는 UI 구성요소에 접근하면 안됨. 단 UI에 접근할 때는 runOnUiThread 블럭으로 감싸줘야 함
thread(start=true) {
...
    runOnUiThread {
        binding.textView.text = "$i"
    }
...
}
_______________________________________________
루퍼(Looper) :
-메인액티비티가 실행됨과 동시에 for 문 하나가 무한루프를 돌고 있는 서브 스레드.
-대기하고 있다가 자신의 큐(Message Queue)에 쌓인 메시지와 Runnable 객체를 차례로 '핸들러' 에 전달
Message Queue :
-다른 스레드 또는 자신으로 부터 전달 받은 메시지를 보관하는 Queue
핸들러 :
-루퍼로 부터 받은 메시지 또는 Runnable 객체를 처리하거나 메시지를 받아 Message Queue 에 넣는 스레드간 통신 장치
-루퍼는 앱이 실행되면 자동으로 하나 생성되어 무한루프를 돌지만 핸들러는 개발자가 직접 작성해야함
_______________________________________________
스레드는 아래 3가지 방법으로 사용할 수 있음
1)Thread 클래스의 상속
2)Runnable 인터페이스를 구현
3)thread() 안에 start=true 를 전달

1)Thread 클래스의 상속
-Thread 클래스를 상속받아 스레드를 생성
-run() 메서드를 오버라이딩해 스레드가 처리할 로직을 정의하며 run() 메서드의 실행이 끝나면 스레드는 종료됨
-Thread 객체를 만들어 별도의 스레드를 생성하고 start()를 호출하면 run() 메서드에서 정의된 로직이 실행됨
class WorkerThread : Thread() {
    override fun run() {
    ...
    }
}
var thread = WorkerThread()
thread.start()
_______________________________________________
2)Runnable 인터페이스 구현
-Thread 를 상속받은 객체와 달리 Thead 클래스의 생성자로 전달하고 Thread 클래스의 start() 메서드를 호출해야 스레드가 생성됨
class WorkerRunnable : Runnable {
    override fun run() {
    ...
    }
}
var thread = Thread(WorkerRunnable())
thread.start()
_______________________________________________
3)thread()
thread(start=true){
    ...
}
*/
class MainActivity : AppCompatActivity() {
    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    var total = 0 //전체 시간을 저장
    var started = false //시작됨을 체크. '시작 버튼'이 눌리면 true 로 변경

    //[START 핸들러 세팅]
    val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val minute = String.format("%02d", total/60)
            val second = String.format("%02d", total%60)
            binding.textTimer.text = "$minute:$second"
        }
    }
    //[END 핸들러 세팅]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonStart.setOnClickListener {
            started = true
            thread(start = true) {
                while (started) {
                    Thread.sleep(1000)
                    if (started) {
                        total = total + 1
                        //핸들러에 메시지를 전달. 핸들러를 호출한 곳이 하나밖에 없으므로 메시지에 0을 담아서 호출
                        handler?.sendEmptyMessage(0)
                    }
                }
            }
        }
        binding.buttonStop.setOnClickListener {
            if (started) {
                started = false
                total = 0
                binding.textTimer.text = "00:00"
            }
        }
    }
}
