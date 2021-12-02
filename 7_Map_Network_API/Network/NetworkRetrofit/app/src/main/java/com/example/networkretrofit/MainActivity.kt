package com.example.networkretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networkretrofit.data.Repository
import com.example.networkretrofit.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*https://api.github.com/ 을 레트로핏 통신 라이브러리로 HTTP 프로토콜로 연결해 깃허브 사용자 정보를 JSON 데이터 형식으로 읽어와 UI에 띄워주는 앱
Glide 라이브러리
-https://github.com/bumptech/glide
-URL 주소만 알려주면 해당 이미지가 있는 서버에 접속하여 이미지를 다운로드해서 이미지뷰에 보내는 도구
-AndroidManifest - 인터넷 퍼미션 추가, 앱 수준 build.gradle - dependency / plugins(id 'kotlin-kapt') 추가
-dependencies 에 kapt 설정 추가
-Glide 라이브러리를 사용하기 위해서 MyGlideApp 에 AppGlideModule 를 상속받고 @GlideModule 추가

레트로핏 통신 라이브러리
-https://square.github.io/retrofit
-HttpURLConnection 클래스보다 더 간단하게 HTTP 로 데이터를 통신하는 라이브러리
-레트로핏 통신 라이브러리는 레트로핏 인터페이스를 해석해 HTTP 통신 처리

GSON 라이브러리
-레트로핏 통신으로 가져온 JSON 데이터를 코틀린 데이터 클래스로 변환해 주는 컨터버

JSON TO Kotlin Class 플러그인
-JSON 형식으로 된 텍스트 데이터를 코틀린 클래스로 간단하게 변환해주는 플러그인
-[File]-[Settings]->[Plugins] 선택 후 JASON To Kotlin Class 플러그인 검색 후 설치
-기본 패키지 우클릭 -> [New]-[Kotlin data class File from JSON] -> 샘플 JSON 형식 데이터를 복사 붙혀넣고 'Repository' 를 생성 -> 데이터 클래스가 생성됨
(ex. Repository, RepositoryItem, Owner, License)

MyGlideAPP
-AppGlideModule 를 상속
-@GlideModule 추가

RetrofitInterface
-레트로핏 통신 라이브러리가 RetrofitInterface 를 해석해 HTTP 통신 처리
-호출 방식(ex. @GET), 주소(도메인은 제외하고 작성 ex. users/Kotlin/repos), 데이터(ex. fun users(): Call<Repository>) 등 데이터가 담겨있음
*/

class MainActivity: AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //[START 리사이클러 뷰 세팅]
        val adapter = CustomAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        //[END 리사이클러 뷰 세팅]

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create()) //GsonConverterFactory : JSON 데이터를 코틀린 데이터 클래스로 변환해 주는 컨터버
                .build()

        binding.buttonRequest.setOnClickListener {
            val retrofitService = retrofit.create(RetrofitInterface::class.java) //RetrofitInterface 를 파라미터로 넘겨주면 실행가능한 서비스 객체가 됨
            retrofitService.users().enqueue(object: Callback<Repository> {
                /*enqueue() : RetrofitInterface 에 만든 users() 내부 메서드로 비동기 통신으로 데이터를 가져오며 콜백 메서드를 파라미터로 받음
                enqueue() 호출되면 통신이 시작되고 Github API 서버 응답이 오면 enqueue()의 파라미터로 전달된 콜백 메서드 동작 Callback<Repository> {...}*/
                override fun onFailure(call: Call<Repository>, t: Throwable) {
                }
                override fun onResponse(call: Call<Repository>, response: Response<Repository>) {
                    adapter.userList = response.body() as Repository //두번째 파라미터 response 의 body() 메서드로 전송된 서버 데이터 꺼낼 수 있음
                    adapter.notifyDataSetChanged()
                }
            })
        }

    }
}
