package com.freshkeeper.sheets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntrySheet(
    sheetState: SheetState,
    barcodeSheetState: SheetState,
    manualInputSheetState: SheetState,
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
        sheetState = sheetState,
        containerColor = ComponentBackgroundColor,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.add_an_entry),
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))

            AddEntryButton(
                text = stringResource(R.string.barcode_scanner),
                iconId = R.drawable.barcode,
                onClick = {
                    coroutineScope.launch {
                        barcodeSheetState.show()
                        sheetState.hide()
                    }
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            AddEntryButton(
                text = stringResource(R.string.manual_input),
                iconId = R.drawable.pencil,
                onClick = {
                    coroutineScope.launch {
                        manualInputSheetState.show()
                        sheetState.hide()
                    }
                },
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AddEntryButton(
    text: String,
    iconId: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(250.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, ComponentStrokeColor),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text, color = TextColor)
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(46.dp),
            )
        }
    }
}
