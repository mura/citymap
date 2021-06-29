package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.stoic.android.citymap.repository.HistoryRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    fun allHistory(pageSize: Int) =
        repository.allHistory(pageSize).cachedIn(viewModelScope)
}