package com.freshkeeper.screens.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InventoryViewModel : ViewModel() {
    private val _fridgeItems = MutableLiveData<List<String>>()
    val fridgeItems: LiveData<List<String>> = _fridgeItems

    private val _cupboardItems = MutableLiveData<List<String>>()
    val cupboardItems: LiveData<List<String>> = _cupboardItems

    private val _freezerItems = MutableLiveData<List<String>>()
    val freezerItems: LiveData<List<String>> = _freezerItems

    private val _countertopItems = MutableLiveData<List<String>>()
    val countertopItems: LiveData<List<String>> = _countertopItems

    private val _cellarItems = MutableLiveData<List<String>>()
    val cellarItems: LiveData<List<String>> = _cellarItems

    private val _bakeryItems = MutableLiveData<List<String>>()
    val bakeryItems: LiveData<List<String>> = _bakeryItems

    private val _spicesItems = MutableLiveData<List<String>>()
    val spicesItems: LiveData<List<String>> = _spicesItems

    private val _pantryItems = MutableLiveData<List<String>>()
    val pantryItems: LiveData<List<String>> = _pantryItems

    init {
        _fridgeItems.value =
            listOf(
                "Mozzarella",
                "Chicken breast",
                "Low-Fat Cottage Cheese",
                "Baby Spinach",
                "Avocado",
                "Free-Range Chicken Breast",
                "Milk",
                "Minced meat",
            )

        _cupboardItems.value =
            listOf(
                "Cookies",
                "Canned Beans",
                "Tomato Sauce",
                "Chickpeas",
                "Peanut Butter",
                "Cereal",
                "Crackers",
                "Rice",
                "Spaghetti",
            )

        _freezerItems.value =
            listOf(
                "Frozen Peas",
                "Ice Cream",
                "Frozen Berries",
                "Chicken Nuggets",
                "Vegetable Stir Fry Mix",
                "Frozen Pizza",
                "Fish Fillets",
                "Diced Chicken",
            )

        _countertopItems.value =
            listOf(
                "Bananas",
                "Apples",
                "Lemons",
            )

        _cellarItems.value =
            listOf(
                "Potatoes",
                "Onions",
                "Garlic",
                "Carrots",
                "Pumpkin",
                "Squash",
                "Apples",
                "Canned Pickles",
            )

        _bakeryItems.value =
            listOf(
                "Bread",
                "Baguette",
                "Croissants",
                "Bagels",
                "Pita Bread",
            )

        _spicesItems.value =
            listOf(
                "Salt",
                "Black Pepper",
                "Cinnamon",
                "Paprika",
                "Oregano",
                "Cumin",
                "Chili Powder",
                "Thyme",
            )

        _pantryItems.value =
            listOf(
                "Quinoa",
                "Canned Tomatoes",
                "Olive Oil",
                "Vinegar",
                "Sugar",
                "Flour",
            )
    }
}
