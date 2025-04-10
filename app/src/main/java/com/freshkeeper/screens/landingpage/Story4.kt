package com.freshkeeper.screens.landingpage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story4() {
    StoryTemplate(
        headline = "Eigene Statistiken einsehen",
        content = {
            Text("Du wirst benachrichtigt, wenn Lebensmittel bald ablaufen.")
        },
    )
}
