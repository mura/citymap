package jp.stoic.android.citymap.repository

import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import jp.stoic.android.citymap.room.History
import jp.stoic.android.citymap.room.HistoryDatabase
import jp.stoic.android.citymap.vo.SelectedShape
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val db: HistoryDatabase
) {

    @WorkerThread
    fun allHistory(pageSize: Int): Flow<PagingData<History>> {
        return Pager(config = PagingConfig(pageSize)) {
            db.historyDao().allHistoryByCreated()
        }.flow
    }

    @WorkerThread
    fun insert(selectedShape: SelectedShape) {
        val history = History(
            code = selectedShape.code,
            name = selectedShape.name,
            created = System.currentTimeMillis()
        )
        db.historyDao().create(history)
    }
}