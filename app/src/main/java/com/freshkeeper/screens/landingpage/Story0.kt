package com.freshkeeper.screens.landingpage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story0() {
    StoryTemplate(
        headline = "Willkommen bei FreshKeeper!",
        content = {
            Text("Beginne noch heute damit, deine Lebensmittelabfälle zu reduzieren!")
        },
    )
}
