package jp.stoic.android.citymap.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(history: History)

    @Query("SELECT * FROM History ORDER BY created DESC")
    fun selectAll(): LiveData<List<History>>

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)
}