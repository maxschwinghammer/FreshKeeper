package com.freshkeeper.screens.home.tips.viewmodel

import androidx.lifecycle.viewModelScope
import com.freshkeeper.R
import com.freshkeeper.model.Tip
import com.freshkeeper.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipsViewModel
    @Inject
    constructor() : AppViewModel() {
        private val _tips =
            MutableStateFlow(
                listOf(
                    Tip(
                        titleId = R.string.tip_title_herbs,
                        descriptionId = R.string.tip_desc_herbs,
                        imageResId = R.drawable.herbs,
                    ),
                    Tip(
                        titleId = R.string.tip_title_temperature,
                        descriptionId = R.string.tip_desc_temperature,
                        imageResId = R.drawable.temperature,
                    ),
                    Tip(
                        titleId = R.string.tip_title_leftovers,
                        descriptionId = R.string.tip_desc_leftovers,
                        imageResId = R.drawable.leftovers,
                    ),
                    Tip(
                        titleId = R.string.tip_title_bread,
                        descriptionId = R.string.tip_desc_bread,
                        imageResId = R.drawable.bread,
                    ),
                    Tip(
                        titleId = R.string.tip_title_freeze,
                        descriptionId = R.string.tip_desc_freeze,
                        imageResId = R.drawable.freeze,
                    ),
                    Tip(
                        titleId = R.string.tip_title_fruit_vegetable,
                        descriptionId = R.string.tip_desc_fruit_vegetable,
                        imageResId = R.drawable.fruit_vegetable,
                    ),
                    Tip(
                        titleId = R.string.tip_title_smoothie,
                        descriptionId = R.string.tip_desc_smoothie,
                        imageResId = R.drawable.smoothie,
                    ),
                    Tip(
                        titleId = R.string.tip_title_cheese,
                        descriptionId = R.string.tip_desc_cheese,
                        imageResId = R.drawable.cheese,
                    ),
                    Tip(
                        titleId = R.string.tip_title_eggs,
                        descriptionId = R.string.tip_desc_eggs,
                        imageResId = R.drawable.eggs,
                    ),
                    Tip(
                        titleId = R.string.tip_title_potatoes,
                        descriptionId = R.string.tip_desc_potatoes,
                        imageResId = R.drawable.potatoes,
                    ),
                    Tip(
                        titleId = R.string.tip_title_avocado,
                        descriptionId = R.string.tip_desc_avocado,
                        imageResId = R.drawable.avocado,
                    ),
                    Tip(
                        titleId = R.string.tip_title_bananas,
                        descriptionId = R.string.tip_desc_bananas,
                        imageResId = R.drawable.banana,
                    ),
                    Tip(
                        titleId = R.string.tip_title_milk,
                        descriptionId = R.string.tip_desc_milk,
                        imageResId = R.drawable.milk,
                    ),
                    Tip(
                        titleId = R.string.tip_title_pasta,
                        descriptionId = R.string.tip_desc_pasta,
                        imageResId = R.drawable.noodles,
                    ),
                    Tip(
                        titleId = R.string.tip_title_rice,
                        descriptionId = R.string.tip_desc_rice,
                        imageResId = R.drawable.rice,
                    ),
                    Tip(
                        titleId = R.string.tip_title_lemons,
                        descriptionId = R.string.tip_desc_lemons,
                        imageResId = R.drawable.lemon,
                    ),
                    Tip(
                        titleId = R.string.tip_title_nuts,
                        descriptionId = R.string.tip_desc_nuts,
                        imageResId = R.drawable.nuts,
                    ),
                    Tip(
                        titleId = R.string.tip_title_meat,
                        descriptionId = R.string.tip_desc_meat,
                        imageResId = R.drawable.meat,
                    ),
                    Tip(
                        titleId = R.string.tip_title_honey,
                        descriptionId = R.string.tip_desc_honey,
                        imageResId = R.drawable.honey,
                    ),
                    Tip(
                        titleId = R.string.tip_title_fish,
                        descriptionId = R.string.tip_desc_fish,
                        imageResId = R.drawable.fish,
                    ),
                ),
            )

        val tips: StateFlow<List<Tip>> = _tips

        fun removeTip(tip: Tip) {
            viewModelScope.launch {
                _tips.value =
                    _tips.value.toMutableList().apply {
                        remove(tip)
                    }
            }
        }
    }
