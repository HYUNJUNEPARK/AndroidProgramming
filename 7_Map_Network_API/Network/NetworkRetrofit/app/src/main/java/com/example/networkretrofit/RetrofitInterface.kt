package com.example.networkretrofit

import com.example.networkretrofit.data.Repository
import retrofit2.Call
import retrofit2.http.GET

/*
레트로핏 라이브러리는 레트로핏 인터페이스를 해석해 HTTP 통신 처리
호출 방식(ex. @GET), 주소(ex. users/Kotlin/repos), 데이터(ex. fun users(): Call<Repository>) 등 데이터가 담겨있음
주소는 도메인은 제외하고 작성
-https://api.github.com/users/Kotlin/repos -> users/Kotlin/repos
*/

interface RetrofitInterface {
    @GET("users/Kotlin/repos")
    fun users(): Call<Repository> //깃허브 사용자를 호출할 메서드: 반환값 Call 클래스
}