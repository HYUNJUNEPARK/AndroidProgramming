package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/*SQLiteOpenHelper 클래스

데이터베이스를 파일로 생성하고 코틀린 코드에서 사용할 수 있도록 데이터베이스와 연결하는 역할
ex) val helper = SQLiteHelper(context, "테이블명", 버전정보)

onCreate() : 테이블 생성 쿼리를 정의하고 실행할 수 있으며 테이블이 생성되어 있다면 실행되지 않음
- 특별한 이유가 없다면 SQLite 에서 INTEGER 로 선언한 컬럼은 데이터 클래스에서 Long 으로 사용함
onUpgrade() : SQLiteHelper 에 전달되는 버전 정보가 변경되었을 때 현재 생성되어 있는 DB 버전보다 높으면 호출됨. 버전 변경 사항이 없으면 호출되지 않음

ContentValues() : put("컬럼명", 값) 메서드로 데이터 저장
writableDatabase : 삽입(쓰기) 전용 DB. 사용 후 close 호출
-insert("테이블명", null, 저장할 값)
-update("테이블명", 수정할 값, 수정조건(PRIMARY KEY), 3번째 파라미터에 매핑할 값)
readableDatabase : 읽기(조회) 전용 DB. 사용 후 close 호출
-rawQuery()

커서(cursor) : 쿼리를 통해 반환된 데이터셋을 반복문으로 하나씩 처리 할 수 있음.

insertMemo(), updateMemo() 는 RecyclerAdapter 를 한번 비우고 전체 내용을 다시 갱신해 추가 수정된 내용을 UI 에 업로드
-helper?.updateMemo(memo)
-listData.clear()
-listData.addAll(helper!!.selectMemo())
-notifyDataSetChanged()

deleteMemo() 는 데이터 삭제 후 바로 UI 수정 가능
-helper?.deleteMemo(mMemo!!)
-listData.remove(mMemo)
-notifyDataSetChanged()
*/

class SQLiteHelper(context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val create = "create table memo (" +
                "no INTEGER PRIMARY KEY, " +
                "content TEXT, " +
                "datetime INTEGER" +
                ")"
        db?.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    //CREATE
    fun insertMemo(memo:Memo) {
        val values = ContentValues()
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)
        val wd = writableDatabase
        wd.insert("memo", null, values)
        wd.close()
    }

    //READ
    fun selectMemo(): MutableList<Memo> {
        val list = mutableListOf<Memo>()
        val readQuery = "select * from memo"
        val rd = readableDatabase
        val cursor = rd.rawQuery(readQuery, null)

        while (cursor.moveToNext()) {
            val no = cursor.getLong(cursor.getColumnIndex("no"))
            val content = cursor.getString(cursor.getColumnIndex("content"))
            val datetime = cursor.getLong(cursor.getColumnIndex("datetime"))
            list.add(Memo(no, content, datetime))
        }
        cursor.close()
        rd.close()
        return list
    }

    //UPDATE
    fun updateMemo(memo:Memo) {
        val values = ContentValues()
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)
        val wd = writableDatabase
        wd.update("memo", values, "no = ${memo.no}", null)
        //== wd.update("memo", values, "no = ?", arrayOf("${memo.no}"))
        wd.close()
    }

    //DELETE
    fun deleteMemo(memo:Memo) {
        val deleteQuery = "delete from memo where no = ${memo.no}"
        val db = writableDatabase
        db.execSQL(deleteQuery)
        db.close()
    }
}

