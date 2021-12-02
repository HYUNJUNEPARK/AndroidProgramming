package com.example.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/*포어그라운드 서비스
ContextCompat.startForegroundService()//액티비티 -> onStartCommand()//서비스 -> startForeground()//서비스

val CHANNEL_ID : 포어그라운드 서비스를 사용하면 화면 상단 상태 바에 뜨는 알림이 사용할 채널
onStartCommand() : 액티비티에서 호출 된 startForegroundService() 가 보낸 intent 가 넘어옴
-val notification : Notification 를 상속받아 알림이 사용할 채널, 알림 제목, 알림 아이콘 등을 담음
-startForeground() : 생성한 알림 실행
createNotificationChannel() : 오레오 버전부터는 모든 알림은 채널 단위로 동작
*/
class Foreground : Service() {
    val CHANNEL_ID = "ForegroundChannel"

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
    override fun onDestroy() {
        Log.d("Service","포어그라운드 서비스 종료")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID,"Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
