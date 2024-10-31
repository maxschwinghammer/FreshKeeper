package com.freshkeeper.screens.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _expiringSoonItems = MutableLiveData<List<Pair<String, String>>>()
    val expiringSoonItems: LiveData<List<Pair<String, String>>> = _expiringSoonItems

    private val _expiredItems = MutableLiveData<List<Pair<String, String>>>()
    val expiredItems: LiveData<List<Pair<String, String>>> = _expiredItems

    init {
        _expiringSoonItems.value =
            listOf(
                "Chicken strips" to "Today",
                "Mozzarella" to "Tomorrow",
                "Avocado" to "Tomorrow",
                "Minced meat" to "In 3 days",
                "Toast" to "In 5 days",
                "Milk" to "In 6 days",
            )

        _expiredItems.value =
            listOf(
                "Bacon" to "Yesterday",
                "Feta cheese" to "2 days ago",
                "Cream" to "4 days ago",
                "Organic strawberries with a long name" to "5 days ago",
            )
    }
}
