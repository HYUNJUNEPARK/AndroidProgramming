package com.example.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RoomDao {
    @Query("select * from room_table")
    fun getAll(): List<RoomTable>

    @Insert(onConflict = REPLACE)
    fun insert(table: RoomTable)

    @Delete
    fun delete(table: RoomTable)
}