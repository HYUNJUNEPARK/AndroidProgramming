package com.example.servicetest

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/*
onBind() : 스타티드 서비스에서는 사용되지 않고 바운드 서비스에서는 앞서 만든 바인더를 반환하고 액티비티에서 getService() 로 서비스에 접근할 수 있음
onDestroy() : 스타티드/바운드 서비스 종료 시 호출되며 서비스 중지 상태를 확인할 수 있음
*/
class MyService : Service() {

    override fun onDestroy() {
        Log.d("Service", "스타티드 or 바운드 서비스가 종료되었습니다.")
        super.onDestroy()
    }

    //[START 스타티드 서비스]
    //명령어(패키지명 + 명령어) : 메인액티비티에서 명령어를 intent 에 담아서 안드로이드 시스템에 전달
    companion object {
        val ACTION_START = "com.example.servicetest.START"
        val ACTION_RUN = "com.example.servicetest.RUN"
        val ACTION_STOP = "com.example.servicetest.STOP"
    }
    //MainActivity 의 serviceStart()가 보낸 intent 를 파라미터로 받음
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("Service", "StartedService : action=$action")
        return super.onStartCommand(intent, flags, startId)
    }
    //[END 스타티드 서비스]


    //[START 바운드 서비스]
    //바인더로부터 액티비티에서 getService() 를 호출해 MyService 컨텍스트를 반환하면 서비스와 액티비티가 데이터를 주고 받을 수 있음
    inner class MyBinder : Binder() {
        fun getService() : MyService {
            return this@MyService
        }
    }
    val binder = MyBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    fun serviceMessage() : String{
        return "Hello Activity! I am Service!"
    }
    //[END 바운드 서비스]
}
