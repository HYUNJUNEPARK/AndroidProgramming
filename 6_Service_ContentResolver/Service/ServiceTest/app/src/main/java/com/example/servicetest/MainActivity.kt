package com.example.servicetest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast

/*서비스 : 백그라운드 스레드에서 동작하는 컴포넌트로 알려져있지만 서비스만으로는 백그라운드 스레드에서 동작하지 않고 동작 과정에서 메인 스레드도 사용함
[New]-[Service]-[Service] 서비스 생성

서비스 실행 방식으로 분류 : 스타티드 서비스(일반적으로 많이 사용)/바운드 서비스
실행 구조로 분류 : 포어그라운드 서비스/백그라운드 서비스(기본적으로 서비스는 백그라운드 서비스)

스타티드 서비스 :
-액티비티와 상관없이 독립적으로 동작하며 액티비티 종료와 무관하게 동작
-startService() 메서드로 호출
-stopService() 메서드로 서비스 종료
바운드 서비스
-액티비티와 값을 주고 받을 때 사용하며 값을 주고 받기 위한 인터페이스 제공
-연결된 액티비티가 종료되면 서비스도 종료되고 특별한 경우를 제외하고는 잘 사용안됨
-여러개의 액티비티가 같은 서비스를 사용할 수 있어 기존에 있는 서비스를 바인딩해 재사용 가능
-bindService() 메서드로 호출

포어그라운드 서비스 :
-알림을 통해 현재 작업이 진행중이라는 것을 알려줘야함
-오레오 버전 부터는 모든 알림은 채널 단위로 동작함
-가용 자원이 부족 이유로 종료되지 않음
-ContextCompat.startForegroundService()//액티비티 -> onStartCommand()//서비스 -> startForeground()//서비스
백그라운드 서비스 :
-안드로이드 가용 자원이 부족하면 시스템에 의해 제거될 수 있음
*/


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //[START 스타티드 서비스]
    /* 파라미터로 view: View 를 전달하면 클릭리스너 연결없이 레이아웃 파일에서 메서드에 직접 접근 가능
    -> 뷰바인더를 사용하지 않고 코드와 xml 컴포넌트를 연결함
    -> activity_main.xml 에 android:onClick="serviceStart" 속성으로 버튼과 메서드가 연결되어 있음*/
    fun serviceStart(view: View) {
        val intent = Intent(this, MyService::class.java)
        intent.action = MyService.ACTION_START
        startService(intent)
    }
    fun serviceStop(view: View) {
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }
    //[END 스타티드 서비스]


    //[START 바운드 서비스]
    /*
    val serviceConnection : 서비스 커넥션 정보가 담긴 변수
    -서비스 커넥션은 bindService() 메서드를 통해 안드로이드 시스템에 전달되고 액티비티와 서비스가 연결됨
    -onServiceConnected() : 서비스가 연결되면 호출
    -onServiceDisconnected() : 서비스가 비정상적으로 종료되었을 때 호출됨(정상적으로 해제되거나 unbindService() 로 흐름을 끊어도 호출되지 않음)

    serviceBind() : 내부에 bindService() 를 갖고 있어 액티비티와 서비스를 연결
    -Context.BIND_AUTO_CREATE : 1) 서비스가 생성되어 있지 않으면 생성 후 바인딩 2) 이미 생성되어 있으면 바로 바인딩
    serviceUnbind() : 내부에 unbindService() 를 갖고 있어 액티비티와 서비스를 연결을 끊음
    callServiceFunction() : MyService 에서 만든 serviceMessage() 를 호출(연결 확인 메서드)
    -서비스와 액티비티가 정상적으로 연결되었다면 서비스에서 만든 메세지를 액티비티로 가져올 수 있음
    */
    var myService:MyService? = null
    var isService = false
    //[START 서비스 커넥션]
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MyService.MyBinder
            myService = binder.getService()
            isService = true
            Log.d("Service","BoundService : 연결되었습니다.")
        }
        override fun onServiceDisconnected(name:ComponentName) {
            isService = false
        }
    }
    //[END 서비스 커넥션]

    fun serviceBind(view: View) {
        val intent = Intent(this, MyService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    fun serviceUnbind(view: View) {
        if (isService) {
            unbindService(serviceConnection)
            isService = false
        }
    }
    fun callServiceFunction(view: View) {
        if (isService) {
            val message = myService?.serviceMessage()
            Toast.makeText(this, "message=${message}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "서비스가 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
