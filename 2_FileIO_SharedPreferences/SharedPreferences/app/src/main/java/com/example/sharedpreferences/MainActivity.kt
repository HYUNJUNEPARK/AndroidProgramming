package com.example.sharedpreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager

/*SharedPreferences

앱 설정 등 내부 저장소에 간단한 데이터를 저장. 데이터를 key: value 쌍으로 XML 파일에 저장하며 앱이 종료되어도 남아있음
preferences.xml 에서 값을 조절하면 설정값이 자동으로 지정된 SharedPreferences 파일에 저장됨
저장된 설정값은 PreferenceManager.getDefaultSharedPreferences() 으로 호출해서 사용할 수 있음

SharedPreferences 생성
앱 수준 build.gradle 에 dependency 추가
[res] 우클릭-[New]-[Android Resource File]-> Resource type : XML / Root element : PreferenceScreen / File name : preferences

SettingFragment.kt : PreferenceFragmentCompat() 를 상속 받은 클래스
onCreatePreferences() 를 오버라이드해 addPreferencesFromResource() 에 R.xml.preferences 를 파라미터로 전달하면 설정 항목에 대한 View 를 자동생성
activity_main.xml 에 <fragment> 속성을 드래그 한 후 SettingFragment 를 추가해서 사용

앱 설정 UI 샘플 preferences.xml 에서 사용하는 입력필드 종류
a. CheckBoxPreference : 체크박스 타입 입력 필드
b. SwitchPreference : 스위치(ON/OFF) 타입 입력 필드
c. EditTextPreference : 값을 입력하는 타입 입력 필드
d. ListPreference : 목록형 입력 필드 / 띄울 목록과 값은 아래처럼 사용
- array.xml : ListPreference 에서 사용할 아이템을 저장해둔 xml 파일
<ListPreference
...
android:entries="@array/action_list"
android:entryValues="@array/action_values"
.../>
*/

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shared = PreferenceManager.getDefaultSharedPreferences(this)

        val checkboxValue = shared.getBoolean("key_add_shortcut",false)
        val switchValue = shared.getBoolean("key_switch_on", false)
        val name = shared.getString("key_edit_name", "")
        val selected = shared.getString("key_set_item", "")

        //처음에는 값을 입력하지 않았기 때문에 값이 출력되지 않을 수도 있음
        Log.d("ShrdPref", "add_shortcut=${checkboxValue}")
        Log.d("ShrdPref", "switchValue=${switchValue}")
        Log.d("ShrdPref", "name=${name}")
        Log.d("ShrdPref", "selected=${selected}")
    }
}
