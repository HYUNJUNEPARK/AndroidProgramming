package kr.co.hanbit.seoulpubliclibraries

import kr.co.hanbit.seoulpubliclibraries.data.Library
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//레트로핏 라이브러리는 레트로핏 인터페이스를 해석해 HTTP 통신 처리
//호출 방식 @GET, 주소 {api_key}/json/SeoulPublicLibraryInfo/1/200, 데이터(fun getLibrary(): Call<Library>) 등 지정
interface RetrofitInterface {
    //API 를 받아올 사이트의 도메인은 제외하고 작성. 레트로핏 객체 생성에서 baseUrl 로 생략된 앞부분 도메인을 갖고 있음
    //{매핑할 이름} : 메서드가 호출되는 순간 '매핑할 이름'이 정의된 파라미터의 값으로 대체된 후 사용됨 - API_KEY 를 직접 입력해도 되지만 사용하는 API 가 여러개일 때 수정해야하는 코드가 많아짐
    @GET("{api_key}/json/SeoulPublicLibraryInfo/1/200")
    //@Path 를 사용하면 getLibrary 메서드에 넘어온 파라미터의 값(api_key)을 @GET 에 정의된 주소({api_key})에 동적으로 삽입할 수 있음
    //@Path("매핑할 이름") -> @GET("{매핑할 이름}/...")
    fun getLibrary(@Path("api_key") key:String) : Call<Library>
}