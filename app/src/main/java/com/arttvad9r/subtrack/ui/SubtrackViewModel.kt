package com.arttvad9r.subtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arttvad9r.subtrack.data.SubscriptionRepository
import com.arttvad9r.subtrack.domain.Subscription
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubtrackViewModel(
    private val repository: SubscriptionRepository,
) : ViewModel() {
    val subscriptions = repository.subscriptions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        viewModelScope.launch {
            repository.reconcileAndScheduleAll()
        }
    }

    fun save(subscription: Subscription, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.save(subscription)
            onSaved(id)
        }
    }

    fun delete(subscription: Subscription, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.delete(subscription)
            onDeleted()
        }
    }
}

class SubtrackViewModelFactory(
    private val repository: SubscriptionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SubtrackViewModel::class.java))
        return SubtrackViewModel(repository) as T
    }
}
