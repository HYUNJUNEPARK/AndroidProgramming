package com.example.networkhttpurlconnection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.networkhttpurlconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/*HTTP 프로토콜을 사용해 입력받은 URL 로 서버연결 후 데이터를 읽어오는 앱
앱 수준의 build.gradle 에 dependency 추가, AndroidManifest 에 인터넷 퍼미션 추가

HttpURLConnection 클래스, HttpsURLConnection 클래스
-HTTP 프로토콜로 데이터 통신을 하도록 도와주는 클래스
-val urlConnection = url.openConnection() as HttpURLConnection
프로토콜 : 전송 방식을 표준화하여 어떤 컴퓨터와도 동일한 방식으로 데이터를 주고받을 수 있게 만들어진 통신 규약
패킷 : 데이터가 전송되는 실제 단위
HTTP : 서버와 브라우저의 데이터 통신이 가능하도록 설계된 표준 규약
쿼리 스트링 : 요청 주소의 뒤에 옵션 데이터를 붙여서 전달하는 방식. 요청 주소와 옵션은 ? 로 구분
JSON : 데이터 교환에 사용하는 표준 데이터 형식으로 이해하기 쉬우면서 데이터 용량이 적다는 장점이 있음
-> 인터넷은 전송할 데이터를 HTTP 라는 프로토콜로 만들어진 패킷(바구니)에 담은 후 전송 프로토콜인 TCP/IP를 사용하여 수신 측에 전달하는 구조
*/


class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonRequest.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //[START EditText 에 입력된 URL 정보로 url 객체 생성]
                    var urlText = binding.editUrl.text.toString()
                    if (!urlText.startsWith("https")) {
                        urlText = "https://${urlText}"
                    }
                    val url = URL(urlText) //import java.net
                    //[END EditText 에 입력된 URL 정보로 url 객체 생성]

                    //[START url 객체로 서버와 연결 : HttpURLConnection]
                    /*openConnection() : url 객체로부터 HTTP 프로토콜로 서버와 연결을 만듬
                    requestMethod : 데이터를 주고 받기 위한 요청 방식 설정*/
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    //[END url 객체로 서버와 연결 : HttpURLConnection]

                    if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                        //[START url 서버 데이터 로드]
                        val streamReader = InputStreamReader(urlConnection.inputStream) //입력 스트림(데이터를 읽어오는 스트림)을 연결
                        val buffered = BufferedReader(streamReader) //버퍼에 입력 스트림을 담아서 데이터를 읽을 준비
                        val content = StringBuilder()//버퍼에 담긴 데이터를 한줄씩 읽은 데이터를 저장할 변수
                        while (true) {
                            val line = buffered.readLine() ?: break
                            content.append(line)
                        }
                        buffered.close()
                        urlConnection.disconnect()
                        //[END url 서버 데이터 로드]

                        /*UI 배치
                        Dispatchers.IO -> Dispatchers.Main */
                        launch(Dispatchers.Main) {
                            binding.textContent.text = content.toString()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
