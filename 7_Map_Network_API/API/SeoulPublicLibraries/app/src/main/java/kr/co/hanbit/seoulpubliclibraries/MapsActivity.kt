package kr.co.hanbit.seoulpubliclibraries

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kr.co.hanbit.seoulpubliclibraries.data.Library
import kr.co.hanbit.seoulpubliclibraries.databinding.ActivityMapsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*구글 지도에 서울 도서관 공공 API 에서 가져온 데이터를 사용해 도서관 위치를 표시해주는 앱
-도서관 정보 API 가 HTTPS 가 아닌 HTTP 를 사용하기 때문에 AndroidManifest <application ... android:usesCleartextTraffic="true"> 추가
-공공 데이터 API 출처 : http://data.seoul.go.kr/dataList/OA-15480/S/1/datasetView.do
-OpenAPI 구조 : http://openapi.seoul.go.kr:8088/(인증키)/(문서형식)/SeoulPublicLibraryInfo/(페이지)/(요청개수)
-[data] 패키지 우클릭 - [New] - [Kotlin data class File from JSON] - Class Name 'Library' 로 입력하고 공공 데이터(인터넷 주소 + 인증키 + 데이터형식 등)를 갖고 있는 URL 주소를 붙혀 넣음

-앱 수준 build.gradle 에 레트로핏 통신 라이브러리, gson 라이브러리 dependecy 추가
-AndroidManifest 에 인터넷 퍼미션과 위치 정보 퍼미션 추가

API(Application Programming Interface)
-응용 프로그램(ex. 앱)에서 운영체제나 다른 프로그래밍 언어 등에서 제공하는 기능을 제어 할 수있는 인터페이스
-프로그램 간의 상호작용을 도와주는 역할. API 를 통해 데이터를 주고 받는다 ex)startActivity( ) 도 API 의 종류
-데이터 또는 서비스를 공개해 일반 개발자들이 사용할 수 있도록 제공하는 인터페이스. 인터넷 주소 형태로 제공됨
-API 를 통해 명령어를 전달하거나 데이터를 주고받을 수 있는데, 요청 방식(ex. GET, POST)과 데이터 형식(ex. JSON)은 대부분 표준으로 제공됨

OpenApi 클래스
-Open API 기본 정보(도메인, Api key 등) 담아두는 클래스
-companion object 블럭 안에 변수를 담아둠으로써 OpenApi.DOMAIN 처럼 변수를 쉽게 호출할 수 있음

loadLibraries()
-레트로핏 통신 라이브러리로 JSON 데이터를 불러옴

showLibraries()
-loadLibraries() 에서 서버로 부터 받은 JSON 데이터를 전달 받아 마커를 표시
*/

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadLibraries()
    }

    fun loadLibraries() {
        val retrofit = Retrofit.Builder()
                .baseUrl(OpenApi.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val retrofitService = retrofit.create(RetrofitInterface::class.java)
        retrofitService.getLibrary(OpenApi.API_KEY).enqueue(object : Callback<Library> {
                    override fun onFailure(call: Call<Library>, t: Throwable) {
                        Toast.makeText(baseContext, "서버에서 데이터를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<Library>, response: Response<Library>) {
                        showLibraries(response.body() as Library)
                    }
        })
    }

    fun showLibraries(libraries:Library) {
        val latLngBounds = LatLngBounds.Builder() //카메라 위치 조정 : 마커 전체 영역을 구하고 마커의 영역만큼 UI 에 띄움
        for (lib in libraries.SeoulPublicLibraryInfo.row) {
            //[START 마커 세팅]
            val position = LatLng(lib.XCNTS.toDouble(), lib.YDNTS.toDouble())
            val marker = MarkerOptions().position(position).title(lib.LBRRY_NAME)
            var obj = mMap.addMarker(marker) //mMap.addMarker()로 마커를 지도에 표시
            obj.tag = lib.HMPG_URL //tag 에 URL 데이터를 담음
            //[END 마커 세팅]

            //마커를 클릭하면 tag 에 담겨있는 URL 로 이동
            mMap.setOnMarkerClickListener {
                if (it.tag != null) {
                    var url = it.tag as String
                    if (!url.startsWith("http")) {
                        url = "http://${url}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                true
            }
            latLngBounds.include(marker.position) //카메라 위치 조정
        }
        //latLngBounds 에 저장된 마커의 영역을 구하고 카메라 업데이트
        val bounds = latLngBounds.build()
        val padding = 0
        val updated = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(updated)
    }
}
