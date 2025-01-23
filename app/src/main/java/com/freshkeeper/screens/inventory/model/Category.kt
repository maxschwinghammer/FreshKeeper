package com.freshkeeper.screens.inventory.model

import com.freshkeeper.R

enum class Category(
    val displayName: String,
    val resId: Int,
) {
    DAIRY_GOODS("dairy_goods", R.string.dairy_goods),
    VEGETABLES("vegetables", R.string.vegetables),
    FRUITS("fruits", R.string.fruits),
    MEAT("meat", R.string.meat),
    FISH("fish", R.string.fish),
    FROZEN_GOODS("frozen_goods", R.string.frozen_goods),
    SPICES("spices", R.string.spices),
    BREAD("bread", R.string.bread),
    CONFECTIONERY("confectionery", R.string.confectionery),
    DRINKS("drinks", R.string.drinks),
    NOODLES("noodles", R.string.noodles),
    CANNED_GOODS("canned_goods", R.string.canned_goods),
    CANDY("candy", R.string.candy),
    OTHER("other", R.string.other),
    ;

    companion object {
        fun fromString(value: String): Category? = entries.find { it.displayName == value }
    }
}
