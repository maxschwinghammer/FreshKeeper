package com.freshkeeper.service

import com.freshkeeper.R
import com.freshkeeper.model.Category
import com.freshkeeper.model.EventType
import com.freshkeeper.model.HouseholdType
import com.freshkeeper.model.Language
import com.freshkeeper.model.NotificationSwitch
import com.freshkeeper.model.StorageLocation

val storageLocations =
    listOf(
        R.string.fridge,
        R.string.cupboard,
        R.string.freezer,
        R.string.counter_top,
        R.string.cellar,
        R.string.bread_box,
        R.string.spice_rack,
        R.string.pantry,
        R.string.fruit_basket,
        R.string.other,
    )
val categories =
    listOf(
        R.string.dairy_goods,
        R.string.vegetables,
        R.string.fruits,
        R.string.meat,
        R.string.fish,
        R.string.frozen_goods,
        R.string.spices,
        R.string.bread,
        R.string.confectionery,
        R.string.drinks,
        R.string.pasta,
        R.string.canned_goods,
        R.string.candy,
        R.string.groats,
        R.string.sauces,
        R.string.pet_food,
        R.string.child_food,
        R.string.other,
    )

val storageLocationMap =
    mapOf(
        StorageLocation.FRIDGE to R.string.fridge,
        StorageLocation.CUPBOARD to R.string.cupboard,
        StorageLocation.FREEZER to R.string.freezer,
        StorageLocation.COUNTER_TOP to R.string.counter_top,
        StorageLocation.CELLAR to R.string.cellar,
        StorageLocation.BREAD_BOX to R.string.bread_box,
        StorageLocation.SPICE_RACK to R.string.spice_rack,
        StorageLocation.PANTRY to R.string.pantry,
        StorageLocation.FRUIT_BASKET to R.string.fruit_basket,
        StorageLocation.OTHER to R.string.other,
    )

val categoryMap =
    mapOf(
        Category.DAIRY_GOODS to R.string.dairy_goods,
        Category.VEGETABLES to R.string.vegetables,
        Category.FRUITS to R.string.fruits,
        Category.MEAT to R.string.meat,
        Category.FISH to R.string.fish,
        Category.FROZEN_GOODS to R.string.frozen_goods,
        Category.SPICES to R.string.spices,
        Category.BREAD to R.string.bread,
        Category.CONFECTIONERY to R.string.confectionery,
        Category.DRINKS to R.string.drinks,
        Category.PASTA to R.string.pasta,
        Category.CANNED_GOODS to R.string.canned_goods,
        Category.CANDY to R.string.candy,
        Category.GROATS to R.string.groats,
        Category.SAUCES to R.string.sauces,
        Category.PET_FOOD to R.string.pet_food,
        Category.CHILD_FOOD to R.string.child_food,
        Category.OTHER to R.string.other,
    )

val categoryTips =
    mapOf(
        Category.SPICES to R.string.spice_tip,
        Category.PASTA to R.string.pasta_tip,
        Category.CANNED_GOODS to R.string.canned_goods_tip,
        Category.GROATS to R.string.groats_tip,
        Category.SAUCES to R.string.sauces_tip,
        Category.DRINKS to R.string.drinks_tip,
        Category.DAIRY_GOODS to R.string.fresh_food_warning,
        Category.MEAT to R.string.fresh_food_warning,
        Category.FISH to R.string.fresh_food_warning,
        Category.VEGETABLES to R.string.fresh_food_warning,
        Category.FRUITS to R.string.fresh_food_warning,
        Category.BREAD to R.string.fresh_food_warning,
        Category.CANDY to R.string.sweets_tip,
        Category.CONFECTIONERY to R.string.sweets_tip,
    )

val householdTypeMap =
    mapOf(
        R.string.family to HouseholdType.FAMILY,
        R.string.shared_apartment to HouseholdType.SHARED_APARTMENT,
        R.string.single to HouseholdType.SINGLE,
        R.string.pair to HouseholdType.PAIR,
    )

val storageLocationReverseMap =
    mapOf(
        R.string.fridge to "fridge",
        R.string.cupboard to "cupboard",
        R.string.freezer to "freezer",
        R.string.counter_top to "counter_top",
        R.string.cellar to "cellar",
        R.string.bread_box to "bread_box",
        R.string.spice_rack to "spice_rack",
        R.string.pantry to "pantry",
        R.string.fruit_basket to "fruit_basket",
        R.string.other to "other",
    )

val categoryReverseMap =
    mapOf(
        R.string.dairy_goods to "dairy_goods",
        R.string.vegetables to "vegetables",
        R.string.fruits to "fruits",
        R.string.meat to "meat",
        R.string.fish to "fish",
        R.string.frozen_goods to "frozen_goods",
        R.string.spices to "spices",
        R.string.bread to "bread",
        R.string.confectionery to "confectionery",
        R.string.drinks to "drinks",
        R.string.pasta to "pasta",
        R.string.canned_goods to "canned_goods",
        R.string.candy to "candy",
        R.string.groats to "groats",
        R.string.sauces to "sauces",
        R.string.pet_food to "pet_food",
        R.string.child_food to "child_food",
        R.string.other to "other",
    )

val householdTypeReverseMap = householdTypeMap.entries.associate { it.value to it.key }

val drawableMap =
    mapOf(
        EventType.USER_JOINED to R.drawable.user_joined,
        EventType.USER_ADDED to R.drawable.user_joined,
        EventType.USER_LEFT to R.drawable.leave,
        EventType.PRODUCT_ADDED to R.drawable.plus,
        EventType.EDIT to R.drawable.edit,
        EventType.NAME to R.drawable.edit,
        EventType.CONSUMED to R.drawable.remove,
        EventType.THROWN_AWAY to R.drawable.trash,
        EventType.QUANTITY_INCREASED to R.drawable.update,
        EventType.QUANTITY_DECREASED to R.drawable.update,
        EventType.EXPIRY to R.drawable.expiry,
        EventType.STORAGE to R.drawable.inventory_filled,
        EventType.CATEGORY to R.drawable.category,
        EventType.DEFAULT to R.drawable.edit,
    )

val activityTypeMap =
    mapOf(
        EventType.USER_JOINED to R.string.activity_user_joined,
        EventType.USER_ADDED to R.string.activity_user_added,
        EventType.USER_LEFT to R.string.activity_user_left,
        EventType.PRODUCT_ADDED to R.string.activity_added,
        EventType.EDIT to R.string.activity_edited,
        EventType.NAME to R.string.activity_name_changed,
        EventType.CONSUMED to R.string.activity_consumed,
        EventType.THROWN_AWAY to R.string.activity_thrown_away,
        EventType.QUANTITY_INCREASED to R.string.activity_quantity_increased,
        EventType.QUANTITY_DECREASED to R.string.activity_quantity_decreased,
        EventType.EXPIRY to R.string.activity_expiry_changed,
        EventType.STORAGE to R.string.activity_storage_changed,
        EventType.CATEGORY to R.string.activity_category_changed,
        EventType.DEFAULT to R.string.activity_default,
    )

val notificationSwitchMap =
    listOf(
        R.string.daily_reminders to NotificationSwitch.DAILY_REMINDERS,
        R.string.food_added to NotificationSwitch.FOOD_ADDED,
        R.string.household_changes to NotificationSwitch.HOUSEHOLD_CHANGES,
        R.string.food_expiring to NotificationSwitch.FOOD_EXPIRING,
        R.string.tips to NotificationSwitch.TIPS,
        R.string.statistics to NotificationSwitch.STATISTICS,
    )

val languages =
    mapOf(
        Language.DE to R.drawable.flag_germany,
        Language.EN to R.drawable.flag_usa,
        Language.ES to R.drawable.flag_spain,
        Language.FR to R.drawable.flag_france,
        Language.IT to R.drawable.flag_italy,
        Language.PT to R.drawable.flag_portugal,
    )
