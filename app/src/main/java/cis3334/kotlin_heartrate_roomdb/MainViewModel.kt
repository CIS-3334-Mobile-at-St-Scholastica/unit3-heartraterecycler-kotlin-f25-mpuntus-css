package cis3334.kotlin_heartrate_roomdb

// =============================================
// MainViewModel.kt — state + logic
// =============================================

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class MainViewModel(private val repository: HeartrateRepository) : ViewModel() {
    // Text field states (strings so the user can type freely)
    val pulseText = MutableStateFlow("")
    val ageText = MutableStateFlow("")


    // Database list
    val items: StateFlow<List<Heartrate>> = repository.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    // Derived validation state
    val isInsertEnabled: StateFlow<Boolean> = combine(pulseText, ageText) { p, a ->
        p.toIntOrNull() != null && a.toIntOrNull() != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)


    fun insert() {
        val pulse = pulseText.value.toIntOrNull()
        val age = ageText.value.toIntOrNull()
        if (pulse != null && age != null) {
            viewModelScope.launch {
                repository.add(pulse, age)
// clear inputs after insert
                pulseText.value = ""
                // ageText.value = "" // Removed this line to keep age after insert
            }
        }
    }


    fun clearAll() {
        viewModelScope.launch { repository.clear() }
    }
}


// Factory for ViewModel since it has a non-empty constructor
//


class MainViewModelFactory(private val repository: HeartrateRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}