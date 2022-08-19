package com.example.SOOJOOB

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
//import org.jetbrains.anko.alert
//import org.jetbrains.anko.noButton
//import org.jetbrains.anko.yesButton
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.location.Geocoder
import android.os.Handler
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.gun0912.tedpermission.provider.TedPermissionProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Math.*
import java.net.URL
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.pow

//import kotlinx.android.synthetic.main.activity_maps.*
//import org.jetbrains.anko.toast



class MapsActivity : AppCompatActivity(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener, TextToSpeech.OnInitListener {
    private var prelat:Double = 0.0
    private var prelon:Double = 0.0
    private var sumDistance:Double = 0.0
    private var dis:Double = 0.0
    private var sumTime = ""
    private var time = 0
    private var trashCount = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index :Int = 1
    private lateinit var secText: TextView
    private lateinit var milliText: TextView
    private lateinit var trashCountText: TextView
    private lateinit var startBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var trashBtn: Button
    private lateinit var end_button: Button
    // 캡쳐
    private lateinit var capture_button: Button
    // 클러스팅
    private lateinit var toilet_button: Button
    private lateinit var trashcan_button: Button

    // 현재 위치
    private lateinit var latLng : LatLng
    // 폴리라인 포지션 끝맞춤
    private var longClickFlag = false
    private lateinit var min_latLng : LatLng
    private lateinit var max_latLng : LatLng
    // 이동경로를 그릴 선 설정 (10f, MAGENTA)
    private var polyLineOptions=PolylineOptions().width(10f).color(Color.MAGENTA)
    private lateinit var mMap: GoogleMap // 마커, 카메라 지정을 위한 구글 맵 객체
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 위치 요청 메소드 담고 있는 객체
    private lateinit var locationRequest:LocationRequest // 위치 요청할 때 넘겨주는 데이터에 관한 객체
    private lateinit var locationCallback: MyLocationCallBack // 위치 확인되고 호출되는 객체
    private var locationCallback2 = StartLocationCallBack()
    // capture 이미지
    private lateinit var capture_imageView : ImageView
    // 지도 클러스터
    private lateinit var clusterManager: ClusterManager<MyItem>
    // tts
    private var tts: TextToSpeech? = null

    /** 지도 클러스터 시작*/
    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String

        override fun getPosition(): LatLng {
            return position
        }
        override fun getTitle(): String? {
            return title
        }
        override fun getSnippet(): String? {
            return snippet
        }
        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
        }
    }

    private fun setUpClusterer() {
        clusterManager = ClusterManager(this, mMap)

        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        // 마커 추가
//        addItems()
    }

    // 현재위치 클러스팅 Item 추가 (테스트용)
    private fun addItems() {
        val offsetItem = MyItem(latLng.latitude, latLng.longitude, "Trash", " ${trashCount}")
//        mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.boy_map)))
        clusterManager.addItem(offsetItem)
    }
    /** 클러스터 끝 */

    // 위치 정보를 얻기 위한 각종 초기화
    private fun locationInit(){
        // FusedLocationProviderClient 객체 생성
        // 이 객체의 메소드를 통해 위치 정보를 요청할 수 있음
        fusedLocationProviderClient=FusedLocationProviderClient(this)
        // 위치 갱신되면 호출되는 콜백
        // 내부 클래스 조작용 객체 생성
        locationCallback=MyLocationCallBack()
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체 생성
        locationRequest=LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY // 가장 정확한 위치를 요청한다,
//        locationRequest.interval=10000 // 위치를 갱신하는데 필요한 시간 <밀리초 기준>
//        locationRequest.fastestInterval=5000 // 다른 앱에서 위치를 갱신했을 때 그 정보를 가져오는 시간 <밀리초 기준>
    }
    // 위치 정보를 찾고 나서 인스턴스화되는 클래스
    @SuppressLint("MissingPermission")
    inner class MyLocationCallBack:LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            // lastLocation프로퍼티가 가리키는 객체 주소를 받는다.
            // 그 객체는 현재 경도와 위도를 프로퍼티로 갖는다.
            // 그러나 gps가 꺼져 있거나 위치를 찾을 수 없을 때는 lastLocation은 null을 가진다.
            val location = locationResult?.lastLocation
//            //  gps가 켜져 있고 위치 정보를 찾을 수 있을 때 다음 함수를 호출한다. <?. : 안전한 호출>
            location?.run{
                // 첫 위치 초기화
                prelat = latitude
                prelon = longitude
                // center 포지션 정의
                min_latLng = LatLng(prelat, prelon)
                max_latLng = LatLng(prelat, prelon)

//                // 현재 경도와 위도를 LatLng메소드로 설정한다.
//                val latLng=LatLng(latitude,longitude)
                latLng=LatLng(latitude,longitude)
//                // 카메라를 이동한다.(이동할 위치,줌 수치)
                if(!longClickFlag) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                }
                // **********************************한훈 참고용 시작*************************************
                // 확대/축소 : 1-세계, 5-대륙, 10-도시, 15-거리, 20-건물
//        // 영역 내에 카메라 중심 맞추기
//        val gumi_example = LatLngBounds(
//                iwc,
//                LatLng(36.1040515, 128.4202060)
//        )
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gumi_example.center, 10f))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(samsung_gumi))
                // 삼성 구미를 중심으로 거리수준의 배율로 카메라 이동
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(samsung_gumi, 15f))
                // **********************************한훈 참고용 완료*************************************
//                // 마커를 추가한다.
//                mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                // **********************************한훈 참고용 시작*************************************
                // 마커 추가 (이하 option 설명)
//                googleMap.addMarker(MarkerOptions().position(samsung_gumi).title("Marker in Samsung_Gumi"))
                // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_XXX)) -> 마커 색상 변경
//        googleMap.addMarker(MarkerOptions().position(iwc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                // .alpha(0.7f) -> 불투명도
//        googleMap.addMarker(MarkerOptions().position(iwc).alpha(0.7f))
                // 마커변경
//        googleMap.addMarker(MarkerOptions().position(iwc).icon(BitmapDescriptorFactory.fromBitmap(R.drawable.boy)))
                // **********************************한훈 참고용 완료*************************************
                mMap.isMyLocationEnabled = true

//                if(isRunning) {
//                    // polyLine에 좌표 추가
//                    polyLineOptions.add(latLng).add(LatLng(36.1068791, 128.4147517))
//                    // 선 그리기
//                    mMap.addPolyline(polyLineOptions)
//                    println("왜 안돼냐고~")
//                    println(latLng)
//                    println(polyLineOptions)
//                }
            }
        }
    }

    // 위치 정보를 얻기 위한 각종 초기화
    private fun locationstart(){
        // FusedLocationProviderClient 객체 생성
        // 이 객체의 메소드를 통해 위치 정보를 요청할 수 있음
        fusedLocationProviderClient=FusedLocationProviderClient(this)
        // 위치 갱신되면 호출되는 콜백 생성
        locationCallback2 = StartLocationCallBack()
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체 생성
        locationRequest=LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY // 가장 정확한 위치를 요청한다,
//        locationRequest.interval=10000 // 위치를 갱신하는데 필요한 시간 <밀리초 기준>
        // 위치를 갱신하는데 필요한 시간 <밀리초 기준> -> 5초
        locationRequest.interval=5000
        locationRequest.fastestInterval=5000 // 다른 앱에서 위치를 갱신했을 때 그 정보를 가져오는 시간 <밀리초 기준>

    }

    // 위치 정보를 찾고 나서 인스턴스화되는 클래스
    inner class StartLocationCallBack:LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            // lastLocation프로퍼티가 가리키는 객체 주소를 받는다.
            // 그 객체는 현재 경도와 위도를 프로퍼티로 갖는다.
            // 그러나 gps가 꺼져 있거나 위치를 찾을 수 없을 때는 lastLocation은 null을 가진다.

            val location = locationResult?.lastLocation
            //  gps가 켜져 있고 위치 정보를 찾을 수 있을 때 다음 함수를 호출한다. <?. : 안전한 호출>
            location?.run{
                // 현재 경도와 위도를 LatLng메소드로 설정한다.
                latLng=LatLng(latitude,longitude)

                // 폴리라인 끝지점 최신화
                // 폴리라인 위/경도 최소 지점
                var tmplat : Double = min_latLng.latitude
                var tmplng : Double = min_latLng.longitude
                if(tmplat > latitude){
                    tmplat = latitude
                }
                if(tmplng > longitude){
                    tmplng = longitude
                }
                min_latLng = LatLng(tmplat, tmplng)
                // 폴리라인 위/경도 최대 지점
                tmplat = max_latLng.latitude
                tmplng = max_latLng.longitude
                if(tmplat < latitude){
                    tmplat = latitude
                }
                if(tmplng < longitude){
                    tmplng = longitude
                }
                max_latLng = LatLng(tmplat, tmplng)

                dis = DistanceManager.getDistance(prelat,prelon , latitude,longitude).toDouble()
                println("좌표 사이 거리 : " + dis + "m")
                sumDistance += dis
                println("총 이동 거리 :" + sumDistance)


                // 마커를 추가한다.
//                mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                // 달리는 중일때만  polyLine에 좌표 추가
                if(isRunning) {
                    polyLineOptions.add(latLng)
                    // 선 그리기
                    mMap.addPolyline(polyLineOptions)
                    println("왜 안돼냐고~2")
                    println(latLng)
                    println(polyLineOptions)
                }

                prelat = latitude
                prelon = longitude

                // 카메라를 이동한다.(이동할 위치,줌 수치)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20f))
            }

        }

    }

    // 이 메소드부터 프로그램 시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 세로 모드로 화면 고정
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 위치 정보를 얻기 위한 각종 초기화
        locationInit()
//        locationstart()
        // 활동의 onCreate() 메서드에서 FragmentManager.findFragmentById()를 호출하여 지도 프래그먼트의 핸들을 가져옵니다.
        // 프래그먼트 매니저로부터 SupportMapFragment프래그먼트를 얻는다. 이 프래그먼트는 지도를 준비하는 기능이 있다.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // 그런 다음 getMapAsync()를 사용하여 지도 콜백에 등록합니다
        // 지도가 준비되면 알림을 받는다. (아마, get함수에서 다른 함수를 호출해서 처리하는 듯)
        mapFragment.getMapAsync(this)
        // 여기까지가 기본 지도 설정 - 한훈 참고용

        secText = findViewById(R.id.secText)
        milliText = findViewById(R.id.milliText)
        startBtn = findViewById(R.id.startBtn)
        trashBtn = findViewById(R.id.trashBtn)
        trashCountText = findViewById(R.id.trashCountText)
//        lap_Layout = findViewById(R.id.lap_Layout)
//        resetBtn = findViewById(R.id.resetBtn)
        tts = TextToSpeech(this, this)
        //버튼 클릭 리스너
        var startflag = false
        longClickFlag = false
        startBtn.setOnClickListener {
            if(!startflag) {
                //                mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.boy_map)))
                startflag = true

                /** 3번 터치 야매로 고침 (고치자...) */
                start()
                pause()
            }
            isRunning = !isRunning
            if (isRunning)
                start()
            else {
                // 사용자에게 종료하는 방법 알려주기
                Toast.makeText(this,"버튼을 꾹~ 눌러 종료하세요!", Toast.LENGTH_SHORT).show()
                longClickFlag = true
                pause()
            }
        }
        startBtn.setOnLongClickListener {
            if(!isRunning && longClickFlag){
                startBtn.text ="완료"
                startBtn.isEnabled = false
                trashBtn.isEnabled = false

                // 폴리라인 끝에 맞춰 영역 내에 카메라 중심 맞추기
//                val result_position = LatLngBounds(min_latLng, max_latLng)
                val result_position = LatLngBounds(min_latLng, max_latLng)
                println("min_latLng :" + min_latLng)
                println("max_latLng :" + max_latLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(result_position.center, 20f))
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(result_position, 30))

                Handler().postDelayed({
                    // 구글맵 스크린샷
                    mMap.snapshot{
                        it?.let{
                            capture_imageView.setImageBitmap(it)
                            println("googlemap screenshot: " + it)
                        }
                    }
                    // 현위치 삭제
                    mMap.isMyLocationEnabled = false
                    Handler().postDelayed({
                        // 모두 정지
                        end_button.isEnabled = true
                        onStop()
                    }, 500)
                }, 1000)
            }
            return@setOnLongClickListener(true)
        }
//        resetBtn.setOnClickListener {
//            reset()
//        }
//        recordBtn.setOnClickListener {
//            if(time!=0) lapTime()
//        }
        trashBtn.setOnClickListener {
            trashTTS()
            trashCount ++
            trashCountText.text = "$trashCount"
            println(trashCount)
            // 줍깅 마칭
            //            mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
            mMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.flower)))
//            addItems()
            trashBtn.isEnabled = false
            Handler().postDelayed({
                trashBtn.isEnabled = true
            }, 2000)
        }

        end_button = findViewById(R.id.end_button)
        end_button.isEnabled = false

        val endIntent = Intent(this, EndActivity::class.java) // 인텐트를 생성

        end_button.setOnClickListener { // 버튼 클릭시 할 행동
            if(longClickFlag) {
                endTTS()
                pause()
                endIntent.putExtra("timeRecord", time)
                endIntent.putExtra("sumDistance", sumDistance)
                endIntent.putExtra("trashCount", trashCount)

                // 구글맵 스크린샷 (BitMap 형식)
                mMap.snapshot {
                    it?.let {
                        endIntent.putExtra("captureImage", it)
                        println("googlemap screenshot: " + it)
                    }
                }
                endIntent.putExtra("captureImageTest", R.drawable.boy)

                startActivity(endIntent)  // 화면 전환하기
                finish()
//                onDestroy()
            }
        }
        capture_imageView = findViewById(R.id.capture_imageView)
        capture_button = findViewById(R.id.capture_button)

        capture_button.setOnClickListener { // 버튼 클릭시 할 행동
            // 구글맵 스크린샷
            mMap.snapshot{
                it?.let{
                    capture_imageView.setImageBitmap(it)
                    println("googlemap screenshot: " + it)
                }
            }
        }
        // 화장실
        toilet_button = findViewById(R.id.toilet_button)
        var toiletflag = false
        // 쓰레기통
        trashcan_button = findViewById(R.id.trashcan_button)
        var trashcanflag = false

        // 화장실
        toilet_button.setOnClickListener { // 버튼 클릭시 할 행동
            if (!toiletflag){
                // 클러스터 초기화
                clusterManager.clearItems()
                /** OPEN API GET */
                // 쓰레드 생성
                val thread = toiletNetworkThread()
                thread.start()
                thread.join()
                /** OPEN API GET */
                toiletflag = true
                toilet_button.isEnabled = false
                trashcanflag = false
                trashcan_button.isEnabled = true
            }
        }
        // 쓰레기통
        trashcan_button.setOnClickListener { // 버튼 클릭시 할 행동
            if(!trashcanflag) {
                // 클러스터 초기화
                clusterManager.clearItems()
                /** OPEN API GET */
                // 쓰레드 생성
                val thread = trashcanNetworkThread()
                thread.start()
                thread.join()
                /** OPEN API GET */
                trashcanflag = true
                trashcan_button.isEnabled = false
                toiletflag = false
                toilet_button.isEnabled = true
            }
        }
    }



    // 프로그램이 켜졌을 때만 위치 정보를 요청한다
    override fun onResume(){
        super.onResume()
        // 사용자에게 gps키라고 알리기
//        Toast.makeText(this,"이 앱은 GPS(위치)를 켜야 이용 가능합니다!", Toast.LENGTH_SHORT).show()
        // '앱이 gps사용'에 대한 권한이 있는지 체크
//        // 거부됐으면 showPermissionInfoDialog(알림)메소드를 호출, 승인됐으면 addLocationListener(위치 요청)메소드를 호출
        permissionCheck(cancel={showPermissionInfoDialog()},
            ok={addLocationListener()})
        permissionCheck(cancel={showPermissionInfoDialog()},
            ok={addLocationListener2()})
    }
    // 프로그램이 중단되면 위치 요청을 삭제한다
    override fun onPause(){
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback2)
        // 어플이 종료되면 지도 요청 해제
    }
    // 위치 요청 메소드
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        // 위치 정보 요청
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체, 위치 갱신되면 호출되는 콜백, 특정 스레드 지정(별 일 없으니 null)
        // 위치권한을 요청하고 액티비티가 잠깐 쉴 때, 자신의 위치를 확인하고 갱신된 정보를 요청함.
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)

    }
    @SuppressLint("MissingPermission")
    private fun addLocationListener2(){
        // 위치 정보 요청
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체, 위치 갱신되면 호출되는 콜백, 특정 스레드 지정(별 일 없으니 null)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback2,null)

    }

    // OnMapReadyCallback 인터페이스를 구현하고, GoogleMap 객체를 사용할 수 있을 때
    // 지도를 설정하도록 onMapReady() 메서드를 재정의
    override fun onMapReady(googleMap: GoogleMap) {
//         googleMap객체 생성
//         googleMap을 이용하여 마커나 카메라,선 등을 조정하는 것이다
        mMap = googleMap
//        // 서울을 기준으로 위도,경도 지정
//        val GUMI = LatLng(36.107060,128.418309)
////        // MarkerOptions에 위치 정보를 넣을 수 있음
//        val markerOptions = MarkerOptions()
//        markerOptions.position(GUMI)
//        markerOptions.title("구미")
//        markerOptions.snippet("SSAFY교육장")
////        // 마커 추가
////        mMap.addMarker(markerOptions)
////        // 카메라 구미로 이동
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(GUMI))
////        // 10만큼 줌인 하기
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(20f))
//        // P.s. onMapReady가 호출된 후에 위치 수정이 가능하다!!
//        polyLineOptions.add(GUMI)

        // **********************************한훈 참고용 시작*************************************
//                PolylineOptions()
//                    .clickable(true)
//                    .width(25f)
//                    .color(Color.RED)
//                    .add(좌표1, 좌표2, ...)

        // 다중선과 함께 데이터 객체를 저장한다.
//                polyline1.tag = "A"
//                polyline2.tag = "B"
        // **********************************한훈 참고용 완료*************************************
        // **********************************한훈 참고용 시작*************************************
        // 폴리라인을 클릭하면 실행. 사용자가 다중선을 클릭할 때마다 실선과 점선 간의 패턴을 변경함
        mMap.setOnPolylineClickListener(this)
        // 지도를 클릭하면 실행. 지도에 영역을 나타내는 다각형
        mMap.setOnPolygonClickListener(this)
        // **********************************한훈 참고용 완료*************************************

        // 지도 유형 설정하기
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
//        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
//        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
//        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
//        googleMap.mapType = GoogleMap.MAP_TYPE_NONE

        // ui 세팅
        val uisettings = googleMap.uiSettings
        // 확대/축소 컨트롤 바
        uisettings.setZoomControlsEnabled(true)
        // 나침반 표시
        uisettings.setCompassEnabled(true)
        // 지도 툴바 삭제 (마커 누르면 우측밑에 필요 없는거)
        uisettings.setMapToolbarEnabled(false)
        // 기울이기 동작 제거 (두 손가락 대고 위아래 움직이기)
        uisettings.setTiltGesturesEnabled(false)
        // 회전 동작 제거 (두 손가락 대고 회전 하기)
//        uisettings.setRotateGesturesEnabled(false)

        // **********************************한훈 참고용 시작*************************************
//        // 현재위치(내위치)로 이동 버튼 활성화
//        googleMap.isMyLocationEnabled = true
//        // 사용자가 내 위치 버튼을 클릭하면 앱이 GoogleMap.OnMyLocationButtonClickListener에서 onMyLocationButtonClick() 콜백을 수신
        googleMap.setOnMyLocationButtonClickListener(this)
//        // 사용자가 내 위치의 파란색 점을 클릭하면 앱이 GoogleMap.OnMyLocationClickListener에서 onMyLocationClick() 콜백을 수신
        googleMap.setOnMyLocationClickListener(this)
        // **********************************한훈 참고용 완료*************************************

        // 클러스터 설정
        setUpClusterer()
    }




    // 권환 관련 메서드
    private val gps_request_code=1000 // gps에 관한 권한 요청 코드(번호)
    private fun permissionCheck(cancel:()->Unit,ok:()->Unit){
        // 앱에 GPS이용하는 권한이 없을 때
        // <앱을 처음 실행하거나, 사용자가 이전에 권한 허용을 안 했을 때 성립>
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){  // <PERMISSION_DENIED가 반환됨>
            // 이전에 사용자가 앱 권한을 허용하지 않았을 때 -> 왜 허용해야되는지 알람을 띄움
            // shouldShowRequestPermissionRationale메소드는 이전에 사용자가 앱 권한을 허용하지 않았을 때 ture를 반환함
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                cancel()
            }
            // 앱 처음 실행했을 때
            else
            // 권한 요청 알림
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),gps_request_code)        }
        // 앱에 권한이 허용됨
        else
            ok()
    }
    // 사용자가 권한 요청<허용,비허용>한 후에 이 메소드가 호출됨
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            // (gps 사용에 대한 사용자의 요청)일 때
            gps_request_code->{
                // 요청이 허용일 때
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    addLocationListener()
                // 요청이 비허용일 때
//                else{
//                    toast("권한 거부 됨")
//                    finish() }
            }
        }
    }
    // 사용자가 이전에 권한을 거부했을 때 호출된다.
    private fun showPermissionInfoDialog(){
//        alert("지도 정보를 얻으려면 위치 권한이 필수로 필요합니다",""){
//            yesButton{
//                // 권한 요청
        ActivityCompat.requestPermissions(this@MapsActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),gps_request_code)
//            }
//            noButton { toast("권한 거부 됨")
//                finish() }
//        }.show()
    }

    private fun start() {
        startTTS()

        addLocationListener2()
        locationstart()
        startBtn.text ="중지"
        timerTask = timer(period = 10) { //반복주기는 peroid 프로퍼티로 설정, 단위는 1000분의 1초 (period = 1000, 1초)
            time++ // period=10으로 0.01초마다 time를 1씩 증가하게 됩니다
            var milli = time % 100 // time%100, 나눗셈의 나머지 (밀리초 부분)
            var sec = time / 100 // time/100, 나눗셈의 몫 (초 부분)
            var min = sec / 60
            sec %= 60

            sumTime = "$min" + " : " + "$sec" + "\"" + "$milli"
            // UI조작을 위한 메서드
            runOnUiThread {
                secText.text = "$min" + ":" + "$sec" + "\""
                milliText.text = "$milli"
            }
        }
    }

    private fun pause() {
        startBtn.text ="재실행"
        timerTask?.cancel();
    }
//
//    private fun reset() {
//        timerTask?.cancel() // timerTask가 null이 아니라면 cancel() 호출
//
//        time = 0 // 시간저장 변수 초기화
//        isRunning = false // 현재 진행중인지 판별하기 위한 Boolean변수 false 세팅
//        secText.text = "0" // 시간(초) 초기화
//        milliText.text = "00" // 시간(밀리초) 초기화
//
//        startBtn.text ="시작"
////        lap_Layout.removeAllViews() // Layout에 추가한 기록View 모두 삭제
//        index = 1
//    }
//
//    private fun lapTime() {
//        val lapTime = time // 함수 호출 시 시간(time) 저장
//
//        // apply() 스코프 함수로, TextView를 생성과 동시에 초기화
//        val textView = TextView(this).apply {
//            setTextSize(20f) // fontSize 20 설정
//        }
//        textView.text = "${lapTime / 100}.${lapTime % 100}" // 출력할 시간 설정
//
////        lap_Layout.addView(textView,0) // layout에 추가, (View, index) 추가할 위치(0 최상단 의미)
//        index++ // 추가된 View의 개수를 저장하는 index 변수
//    }

    object DistanceManager {

        private const val R = 6372.8 * 1000

        /**
         * 두 좌표의 거리를 계산한다.
         *
         * @param lat1 위도1
         * @param lon1 경도1
         * @param lat2 위도2
         * @param lon2 경도2
         * @return 두 좌표의 거리(m)
         */
        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            val c = 2 * asin(sqrt(a))
            return (R * c).toInt()
        }

    }
    // 사용자가 다중선을 클릭할 때마다 실선과 점선 간의 패턴을 변경함
    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)
    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if (polyline.pattern == null || !polyline.pattern!!.contains(DOT)) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED
            polyline.color = Color.GREEN
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null
            polyline.color = Color.BLUE
        }
        Toast.makeText(this, "Route type " + polyline.tag.toString(),
            Toast.LENGTH_SHORT).show()    }

    override fun onPolygonClick(p0: Polygon) {
        TODO("Not yet implemented")
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }
    // TextToSpeech override 함수
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.KOREA)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //doSomething
            } else {
                //doSomething
            }
        } else {
            //doSomething
        }

    }
    private fun startTTS() {
        tts!!.speak("플로깅을 시작합니다", TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun endTTS() {
        tts!!.speak("종료", TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun trashTTS() {
        tts!!.speak("수줍!", TextToSpeech.QUEUE_FLUSH, null, "")
    }
    override fun onDestroy() {

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }

    /** 주소변환!!! */
    //위도 경도로 주소 구하는 Reverse-GeoCoding
    private fun getAddress(position: LatLng): String {
        val geoCoder = Geocoder(TedPermissionProvider.context, Locale.KOREA)
        var addr = "주소 오류"

        //GRPC 오류? try catch 문으로 오류 대처
        try {
            addr = geoCoder.getFromLocation(position.latitude, position.longitude, 1).first().getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addr
    }
    //주소로 위도,경도 구하는 GeoCoding
    private fun getLatLng(address:String) : LatLng{
        val geoCoder = Geocoder(TedPermissionProvider.context, Locale.KOREA)   // Geocoder 로 자기 나라에 맞게 설정
        val list = geoCoder.getFromLocationName(address, 3)

        var location = LatLng(37.554891, 126.970814)     //임시 서울역

        if(list != null){
            if (list.size ==0){
                Log.d("GeoCoding", "해당 주소로 찾는 위도 경도가 없습니다. 올바른 주소를 입력해주세요.")
            }else{
                val addressLatLng = list[0]
                location = LatLng(addressLatLng.latitude, addressLatLng.longitude)
                return location
            }
        }

        return location
    }

    /** 대구광역시 서구 쓰레기통 설치 현황 API */
    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    inner class trashcanNetworkThread: Thread(){
        override fun run() {
            // API 정보를 가지고 있는 주소
//            val site = "https://api.odcloud.kr/api/15041577/v1/uddi:cb541318-d90b-4bb3-be27-db9c32fa47ec?" + page + perPage + returnType + key
//            val site = "https://api.odcloud.kr/api/15041577/v1/uddi:cb541318-d90b-4bb3-be27-db9c32fa47ec?page=1&perPage=20&returnType=JSON&serviceKey=Jz%2BuUFF4gp1POyXj63UYJAnHQF%2FeY0OjL9TVaZAeMgm3Bt1giHbDYGkgkatFrJc60gnz171syeDeMVhDCEBFSA%3D%3D"
            val site = "http://i7d210.p.ssafy.io:8080/trashcan"
            val url = URL(site)
            val conn = url.openConnection()
            val input = conn.getInputStream()
            val isr = InputStreamReader(input)
            // br: 라인 단위로 데이터를 읽어오기 위해서 만듦
            val br = BufferedReader(isr)

            // Json 문서는 일단 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
            var str: String? = null
            val buf = StringBuffer()

            do{
                str = br.readLine()

                if(str!=null){
                    buf.append(str)
                }
            }while (str!=null)

            // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            println("buf.toString(): " + buf.toString())
//            val root = JSONObject(buf.toString())
//            val response = root.getJSONObject("response").getJSONObject("body").getJSONObject("items")
//            val item = response.getJSONArray("item") // 객체 안에 있는 item이라는 이름의 리스트를 가져옴
            // 객체 안에 있는 data이라는 이름의 리스트를 가져옴
            val item = JSONArray(buf.toString())

            // 화면에 출력
            runOnUiThread {
                // 페이지 수만큼 반복하여 데이터를 불러온다.
//                for(i in 0 until item.length()){
                for(i in 0 until item.length()){
                    // 쪽수 별로 데이터를 읽는다.
                    val jObject = item.getJSONObject(i)
                    println("jObject: " + jObject)

                    // 주소를 통해 위도 경도 반환
                    val tclatitude = JSON_Parse(jObject,"tclatitude")
                    println("tclatitude: " + tclatitude)
                    val tclongitude = JSON_Parse(jObject,"tclongitude")
                    println("tclongitude: " + tclongitude)
                    if(tclatitude != "" && tclongitude != "") {
                        println("LatLng: " + LatLng(tclatitude.toDouble(), tclongitude.toDouble()))
                        val tclocation = "위치 : ${JSON_Parse(jObject, "tclocation")}"
                        println("tclocation: " + tclocation)
                        // 쓰레기통 마커
//                    mMap.addMarker(MarkerOptions().position(LatLng(tclatitude.toDouble(), tclongitude.toDouble())).title(tclocation))
                        // 쓰레기통 클러스터
                        val offsetItem = MyItem(
                            tclatitude.toDouble(),
                            tclongitude.toDouble(),
                            "대구광역시 쓰레기통",
                            "${tclocation}"
                        )
                        clusterManager.addItem(offsetItem)
                    }
                }
            }
        }
    }
    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    inner class toiletNetworkThread: Thread(){
        override fun run() {
            // API 정보를 가지고 있는 주소
//            val site = "https://api.odcloud.kr/api/15041577/v1/uddi:cb541318-d90b-4bb3-be27-db9c32fa47ec?" + page + perPage + returnType + key
//            val site = "https://api.odcloud.kr/api/15041577/v1/uddi:cb541318-d90b-4bb3-be27-db9c32fa47ec?page=1&perPage=20&returnType=JSON&serviceKey=Jz%2BuUFF4gp1POyXj63UYJAnHQF%2FeY0OjL9TVaZAeMgm3Bt1giHbDYGkgkatFrJc60gnz171syeDeMVhDCEBFSA%3D%3D"
            val site = "http://i7d210.p.ssafy.io:8080/toilet"
            val url = URL(site)
            val conn = url.openConnection()
            val input = conn.getInputStream()
            val isr = InputStreamReader(input)
            // br: 라인 단위로 데이터를 읽어오기 위해서 만듦
            val br = BufferedReader(isr)

            // Json 문서는 일단 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
            var str: String? = null
            val buf = StringBuffer()

            do{
                str = br.readLine()

                if(str!=null){
                    buf.append(str)
                }
            }while (str!=null)

            // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            println("buf.toString(): " + buf.toString())
//            val root = JSONObject(buf.toString())
//            val response = root.getJSONObject("response").getJSONObject("body").getJSONObject("items")
//            val item = response.getJSONArray("item") // 객체 안에 있는 item이라는 이름의 리스트를 가져옴
            // 객체 안에 있는 data이라는 이름의 리스트를 가져옴
            val item = JSONArray(buf.toString())

            // 화면에 출력
            runOnUiThread {
                // 페이지 수만큼 반복하여 데이터를 불러온다.
//                for(i in 0 until item.length()){
                for(i in 0 until item.length()){
                    // 쪽수 별로 데이터를 읽는다.
                    val jObject = item.getJSONObject(i)
                    println("jObject: " + jObject)

                    // 주소를 통해 위도 경도 반환
                    val ptlatitude = JSON_Parse(jObject,"ptlatitude")
                    println("ptlatitude: " + ptlatitude)
                    val ptlongitude = JSON_Parse(jObject,"ptlongitude")
                    println("ptlongitude: " + ptlongitude)
                    if(ptlatitude != "" && ptlongitude != "") {
                        println("LatLng: " + LatLng(ptlatitude.toDouble(), ptlongitude.toDouble()))
                        val ptlocation = "위치 : ${JSON_Parse(jObject, "ptlocation")}"
                        println("ptlocation: " + ptlocation)
                        val ptoperatingtime = "운영시간: ${JSON_Parse(jObject,"ptoperatingtime")}"
                        // 쓰레기통 마커
//                    mMap.addMarker(MarkerOptions().position(LatLng(tclatitude.toDouble(), tclongitude.toDouble())).title(tclocation))
                        // 쓰레기통 클러스터
                        val offsetItem = MyItem(
                            ptlatitude.toDouble(),
                            ptlongitude.toDouble(),
                            "대구광역시 화장실",
                            "${ptlocation}\n" +
                                    "${ptoperatingtime}"
                        )
                        clusterManager.addItem(offsetItem)
                    }
                }
            }
        }
    }
    // 함수를 통해 데이터를 불러온다.
    fun JSON_Parse(obj:JSONObject, data : String): String {

        // 원하는 정보를 불러와 리턴받고 없는 정보는 캐치하여 "없습니다."로 리턴받는다.
        return try {

            obj.getString(data)

        } catch (e: Exception) {
            "없습니다."
        }
    }
}