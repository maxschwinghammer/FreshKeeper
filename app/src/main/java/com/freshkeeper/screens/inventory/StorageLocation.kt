package com.freshkeeper.screens.inventory

import com.freshkeeper.R

enum class StorageLocation(
    val displayName: String,
    val resId: Int,
) {
    FRIDGE("fridge", R.string.fridge),
    CUPBOARD("cupboard", R.string.cupboard),
    FREEZER("freezer", R.string.freezer),
    COUNTER_TOP("counter_top", R.string.counter_top),
    CELLAR("cellar", R.string.cellar),
    BREAD_BOX("bread_box", R.string.bread_box),
    SPICE_RACK("spice_rack", R.string.spice_rack),
    PANTRY("pantry", R.string.pantry),
    FRUIT_BASKET("fruit_basket", R.string.fruit_basket),
    OTHER("other", R.string.other),
    ;

    companion object {
        fun fromString(value: String): StorageLocation? = entries.find { it.displayName == value }
    }
}
