package com.eflc.mintdrop.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntryData
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.service.record.EntryRecordService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val subcategoryRepository: SubcategoryRepository,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val entryRecordService: EntryRecordService
) : ViewModel() {
    private val _lastEntryData = MutableStateFlow(EntryData(0, "", "", 0.0))
    val lastEntryData = _lastEntryData.asStateFlow()

    fun getLastEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            entryHistoryRepository.findLastEntry().let {
                val subcategory = subcategoryRepository.findSubcategoryById(it.subcategoryId)
                _lastEntryData.tryEmit(
                    EntryData(
                        id = it.uid,
                        description = it.description,
                        categoryName = subcategory.name,
                        amount = it.amount
                    )
                )
            }
        }
    }

    fun deleteEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            _lastEntryData.let {
                entryHistoryRepository.findEntryHistoryById(it.value.id).let {
                    entryHistory -> entryRecordService.deleteRecord(entryHistory)
                }
            }
        }
    }
}
