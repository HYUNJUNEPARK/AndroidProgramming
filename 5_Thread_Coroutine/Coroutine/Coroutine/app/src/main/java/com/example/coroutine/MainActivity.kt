package com.example.coroutine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.URL

/*코루틴(Coroutine)
앱수준 build.gradle 에 dependency, AndroidManifest 에 인터넷 퍼미션 추가

동시성 프로그래밍 개념을 코틀린에 도입한 것으로 스레드는 코루틴이 실행되는 공간을 제공하는 역할
하나의 스레드에는 여러개의 코루틴이 존재할 수 있고 같은 스레드에 있는 코루틴간 작업을 넘겨받는 중에도 공간을 제공한 스레드는 계속 움직임
스레드 간 작업을 주고 받는 것(컨텍스트 스위칭)보다 하나의 스레드 안에 있는 코루틴 간 작업을 주고 받는 것이 성능 저하를 방지하고 자원을 더 적게 씀
launch 와 async 로 시작

코루틴 실행 스코프
1) 글로벌 스코프(GlobalScope) :
-앱의 시작부터 종료까지 장시간 실행되어야하는 코루틴이 있다면 여기에 작성
-앱의 생명 주기와 함께 동작하고 별도의 생명 주기 관리가 필요하지 않음.
2) 코루틴 스코프(CoroutineScope) :
-필요할 때만 열고 완료 되면 닫는 코루틴을 담는 스코프 ex)버튼을 클릭해서 서버의 정보를 갖고오거나 파일 오픈
-디스패처를 코루틴 스코프의 괄호 안에 넣어 코루틴이 실행될 스레드를 지정
buttonDownload.setOnClickListener {
    CoroutineScope(Dispatchers.IO).launch {
    ...
    }
}

디스패처 : 코루틴 스코프 내부에서 실행할 코드의 성격에 맞게 사용해야하는 도구
1) Dispatchers.IO : 이미지 다운, 파일 입출력 등 입출력에 최적화 되어있는 디스패처.
2) Dispatchers.Main : UI 와 상호작용에 최적화되어 있는 디스패처. 텍스트뷰에 글자를 입력해야할 경우 Main 컨텍스트 사용
3) Dispatchers.Default : CPU 를 많이 사용하는 작업을 백그라운드 스레드에서 실행하도록 최적화되어있는 디스패처.
4) Dispatchers.Unconfined
-> 모두 사용할 필요는 없고 1), 2) 만 잘 조합해서 사용

launch()
-호출하는 것만으로도 코루틴 생성. 반환 값을 변수에 저장해두는 상태 관리용 cancel(), join() 메서드와 조합해 사용
-코루틴 스코프 안에 선언된 여러개의 launch 블록은 모두 새로운 코루틴으로 분기 되면서 동시에 처리되기 때문에 순서를 정할 수 없음
cancel()
-코루틴의 동작을 멈춤
join()
-launch 블록 뒤에 join()을 사용하면 코루틴이 순차적으로 실행됨

async
-상태 관리와 연산 결과까지 반환 받을 수 있음
await()
-코루틴을 async 로 선언하고 결괏값을 처리하는 곳에 await() 함수를 사용하면 결과 처리가 완료된 후에 await() 를 호출한 줄의 코드가 실행됨

suspend
-일반 함수를 코루틴으로 만드는 키워드 ex)suspend fun readFile() {...}
-코루틴 안에서 suspend 키워드로 선언된 함수가 호출되면 이전까지의 코드 실행이 멈추고 suspend 함수의 처리가 완료된 후에 멈춰 있던 원래 스코프의 다음 코드가 실행됨
CoroutineScope().launch{
    //코드1 실행 -> suspend 함수 작업이 모두 끝난 후 -> 코드2 실행
    코드1
    suspend 함수
    코드2
}
suspend 키워드가 있기 떄문에 코루틴 스코프 안에서 자동으로 백그라운드 스레드처럼 동작
suspend 함수 코드가 실행 -> 코드1 동작 스레드가 잠시 멈춤 / 코드1의 상태값을 저장 -> suspend 함수 종류 -> 코드1의 상태값 복구
코드1의 실행은 잠시 멈추지만 스레드의 중단은 없음

withContext
-디스패처를 분리시키는 키워드로 suspend 함수와 함께 사용될 수 있음
-suspend 함수를 코루틴 스코프에서 호출할 때 호출한 스코프와 다른 디스패처를 사용할 경우 사용
ex) Main 디스패처에서 UI 를 제어해야하는 데 호출된 suspend 함수는 파일을 읽어와야하는 경우
*/
class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            buttonDownload.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    progress.visibility = View.VISIBLE
                    val url = editUrl.text.toString()
                    //Dispatchers.Main -> withContext -> Dispatchers.IO
                    val bitmap = withContext(Dispatchers.IO) {
                        loadImage(url)
                    }
                    imagePreview.setImageBitmap(bitmap)
                    progress.visibility = View.GONE
                }
            }
        }
    }
}

suspend fun loadImage(imageUrl:String) : Bitmap {
    val url = URL(imageUrl)
    val stream = url.openStream()
    return BitmapFactory.decodeStream(stream)
}
