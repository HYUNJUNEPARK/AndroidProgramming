package com.example.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.room.databinding.ActivityMainBinding

/*Room: ORM 라이브러리 (Object Relational Mapping)
쿼리문 이해가 없어도 코드만으로 class 와 RDB 를 매핑하고 컨트롤 할 수 있음.
class -> ORM(데이터 변환을 도와줌) -> SQLite
앱수준 build.gradle 에 1) id 'kotlin-kapt' plugin 2) dependency 추가
See: https://developer.android.com/jetpack/androidx/releases/room

어노테이션 프로세싱(Annotation Processing API)
클래스명, 변수명 위에 '@명령어' 를 사용하는 것으로 명령어가 컴파일 시 코드로 생성되기 때문에 실행 시 발생할 수 있는 성능 문제가 많이 개선됨
plugin id 'kotlin-kapt' : 어노테이션 프로세싱을 코트린에서 사용 가능하게 해줌

RoomTable : DB 테이블의 정보를 담고 있는 클래스
-@Entity(tableName = "") : 해당 어노테이션이 적용된 클래스를 찾아 테이블로 변환
-@ColumnInfo(name = "") : 컬럼에 대한 정보를 담고 있는 변수위에 사용됨
-@Ignore : 해당 변수가 테이블과 관계없는 변수라는 것을 나타냄
-@PrimaryKey(autoGenerate = true)

RoomDao : DB 에 접근 해 쿼리를 실행하는 메서드를 모아둔 인터페이스
-@RoomDao : DAO 라는 것을 명시(Data Access Object)
-@Query : 쿼리를 직접 작성하고 실행
-@Insert, @Delete

RoomHelper : RoomDatabase() 를 상속받는 추상 클래스로 RoomDao 를 상속받는 추상 메서드가 내부에 있음
RoomTable, RoomDao 의 정보가 모여 있는 빈껍데기 클래스로 최종적으로 Room 라이브러리를 통해 미리 만들어져 있는 코드를 사용할 수 있게 됨
-@Database(entities= , version= , exportSchema=)
-entities : Room 라이브러리가 사용할 테이블 클래스 목록 ex. entities = arrayOf(RoomTable::class)
-version : DB 버전
-exportSchema : true 면 스키마 정보를 파일로 출력
*/

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var helper: RoomHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //[START 리사이클러뷰 세팅]
        helper = Room.databaseBuilder(this, RoomHelper::class.java, "room_table")
                .addMigrations(MigrateDatabase.MIGRATE_1_2)
                .allowMainThreadQueries()
                .build()
        val adapter = RecyclerAdapter()
        adapter.helper = helper
        adapter.listData.addAll(helper?.roomMemoDao()?.getAll()?: listOf())
        binding.recyclerMemo.adapter = adapter
        binding.recyclerMemo.layoutManager = LinearLayoutManager(this)
        //[END 리사이클러뷰 세팅]

        binding.buttonSave.setOnClickListener {
            if (binding.editMemo.text.toString().isNotEmpty()) {
                val memo = RoomTable(binding.editMemo.text.toString(), System.currentTimeMillis())
                helper?.roomMemoDao()?.insert(memo)
                adapter.listData.clear()
                adapter.listData.addAll(helper?.roomMemoDao()?.getAll() ?: listOf())
                adapter.notifyDataSetChanged()
                binding.editMemo.setText("")
            }
        }
    }
}

/*Migration 클래스 : DB 수정
앱 업데이트가 DB 테이블을 변경할 때 이미 기기 내 데이터베이스에 있는 사용자 데이터를 유지하는 것이 중요함
Room 라이브러리는 Migration 클래스를 지원해 유저의 데이터를 유지함
See : https://developer.android.com/training/data-storage/room/migrating-db-versions?hl=ko */
object MigrateDatabase {
    val MIGRATE_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val alter = "ALTER table room_table add column new_title text"
            database.execSQL(alter)
        }
    }
}
