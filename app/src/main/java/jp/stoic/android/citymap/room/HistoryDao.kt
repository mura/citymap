package jp.stoic.android.citymap.room

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(history: History)

    @Query("SELECT * FROM History ORDER BY created DESC")
    fun allHistoryByCreated(): PagingSource<Int, History>

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)
}