package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.stoic.android.citymap.room.History
import jp.stoic.android.citymap.room.HistoryDatabase
import jp.stoic.android.citymap.vo.SelectedShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val db: HistoryDatabase
) : ViewModel() {

    val allHistory: Flow<PagingData<History>> =
        Pager(config = PagingConfig(pageSize = 10)) {
            db.historyDao().allHistoryByCreated()
        }.flow.cachedIn(viewModelScope)

    fun insert(selectedShape: SelectedShape) {
        val history = History(
            code = selectedShape.code,
            name = selectedShape.name,
            created = System.currentTimeMillis()
        )
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                db.historyDao().create(history)
            }
        }
    }
}