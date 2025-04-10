package com.freshkeeper.screens.landingpage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story2() {
    StoryTemplate(
        headline = "Prüfe deine aktuellen Bestände",
        content = {
            Text("Du wirst benachrichtigt, wenn Lebensmittel bald ablaufen.")
        },
    )
}
