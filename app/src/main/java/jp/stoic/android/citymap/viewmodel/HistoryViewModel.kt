package jp.stoic.android.citymap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import jp.stoic.android.citymap.room.History
import jp.stoic.android.citymap.room.HistoryDatabase
import jp.stoic.android.citymap.vo.SelectedShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, HistoryDatabase::class.java, "history").build()

    val history: LiveData<List<History>>
        get() = db.historyDao().selectAll()

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