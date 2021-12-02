package com.example.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemaps.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition

/*
Google Map API 세팅 순서
1. 프로젝트 설정 화면에서 'Google Maps Activity' 로 프로젝트 생성
2. [Tools]-[SDK Manager]-[Android SDK]-[SDK Tools]-> Google Play service 'Installed' //설치되어 있지 않다면 onMapReady() 가 호출되지 않음
3. API 발급
[app]-[res]-[values]-google_maps_api.xml 상단 링크에서 API 발급 받은 후 설정 변경 후 사용
-API 를 생성한 후 Credentials - Actions - Edit API key(펜 아이콘) - Application restrictions - None 설정 후 API 복사
4. FusedLocationProviderClient API : 사용자의 현재위치를 검색하는 API. GPS, 와아파이, 통신사 네트워크 위치를 결합해서 위치 검색
-앱 수준 build.gradle 에 dependency 추가
-AndroidManifest 에 위치 접근 권한 추가

** The minCompileSdk(31) specified in a dependency's ... 메세지가 뜨며 정상 작동되지 않을 때
-> 앱 수준 build.gralde - defaultConfig - resolutionStrategy{ force 'androidx.core:core-ktx:16.0' } 추가
*/

class MapsActivity : BaseActivity(), OnMapReadyCallback {
    val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        requirePermissions(permissions, 999)
    }

    override fun permissionGranted(requestCode: Int) {
        startProcess()
    }

    override fun permissionDenied(requestCode: Int) {
        Toast.makeText(this, "권한 승인이 필요합니다.", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) //위치 검색 클라이언트를 생성
        updateLocation()
    }

    /*구글 지도가 표시될 xml 파일 지정(activity_maps.xml)
    getMapAsync() 로 안드로이드 시스템에게 지도 그리기 요청*/
    fun startProcess() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //@SuppressLint() : 마지막 줄에 사용된 fusedLocationClient.requestLocationUpdates() 는 퍼미션이 필요한데 현재 코드에서는 확인할 수 없어 퍼미션을 체크하지 않아도 된다는 어노테이션
    @SuppressLint("MissingPermission")
    fun updateLocation() {
        //[START 위치 정보 요청]
        /*-요청 정확도
          -요청 주기 */
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000 //5초
        }
        //[END 위치 정보 요청]

        //[START 위치 정보 요청에 대한 응답을(마지막으로 알려진 위치) 받음]
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for(location in it.locations) {
                        Log.d("Location", "${location.latitude} , ${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }
        //[END 위치 정보 요청에 대한 응답을(마지막으로 알려진 위치) 받음]

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    fun setLastLocation(lastLocation: Location) {
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude) //위도와 경도 좌표 정보

        //[START 마커 아이콘/크기 변경]
        var bitmapDrawable: BitmapDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(R.drawable.marker) as BitmapDrawable
        } else {
            bitmapDrawable = resources.getDrawable(R.drawable.marker) as BitmapDrawable
        }
        var originBitmap: Bitmap = bitmapDrawable.bitmap
        var scaledBitmap = Bitmap.createScaledBitmap(originBitmap, 100, 100, false)
        var discriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        //[END 마커 아이콘/크기 변경]

        val markerOptions = MarkerOptions().position(LATLNG).title("마커 타이틀 입력").snippet("snippet 내용 입력").icon(discriptor)
        val cameraPosition = CameraPosition.Builder().target(LATLNG).zoom(15.0f).build()

        mMap.clear() //updateLocation() 에서 설정한 주기마다 현재 위치 정보를 넣기 위해 이전 정보를 비워줌
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}