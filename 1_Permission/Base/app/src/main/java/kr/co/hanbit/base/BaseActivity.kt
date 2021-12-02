package kr.co.hanbit.base

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/*
권한 승인 작업이 필요한 액티비티에 상속 받아 사용
권한 승인 여부에 따라 permissionGranted(), permissionDenied() 를 오버라이드해 사용하면 됨

ContextCompat.checkSelfPermission(context, permission)
: 사용자가 이미 앱에 특정권한을 부여했는지 확인하며 권한 여부에 따라 PERMISSION_GRANTED 또는 PERMISSION_DENIED 를 반환

ActivityCompat.requestPermissions(context, permission, requestCode)
: 사용자에게 권한 요청하는 팝업을 띄우고 사용자 응답을 onRequestPermissionsResult() 에서 처리
*/

abstract class BaseActivity : AppCompatActivity() {
    abstract fun permissionGranted(requestCode: Int)
    abstract fun permissionDenied(requestCode: Int)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
        }
    }

    /*권한 요청 시 직접 호출하는 메서드
      버전에 맞게 권한 처리 진행하며 Marshmallow 버전부터는 권한 처리 방법이 달라짐
      마시멜로 버전 부터는 파라미터로 받은 permissions 에는 권한들이 배열에 들어 있고 all 을 사용해 배열 속에 들어 있는 모든 값을 체크함
      권한이 모두 승인되었다면 true / 아니라면 false 를 isAllPermissionsGranted 에 담은 후 permissionGranted() 또는 ActivityCompat.requestPermissions() 로 권한 처리
    */
    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            val isAllPermissionsGranted = permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

            if (isAllPermissionsGranted) {
                permissionGranted(requestCode)
            } else {
                ActivityCompat.requestPermissions(this, permissions, requestCode)
            }
        }
    }
}