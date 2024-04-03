package com.example.shopapplication.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    clickDebounceMillis: Long = 300L,
    content: @Composable RowScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val debounceJob= remember() {
        mutableStateOf<Job?>(null)
    }

    Button(onClick = {
        if (debounceJob.value == null) {
            debounceJob.value = coroutineScope.launch {
                onClick()
                delay(clickDebounceMillis)
                debounceJob.value = null
            }
        }
    },
        modifier,
        enabled,
        interactionSource,
        elevation,
        shape,
        border,
        colors,
        contentPadding,
        content)
}

@Composable
fun ShopOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    clickDebounceMillis: Long = 300L,
    content: @Composable RowScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val debounceJob= remember {
        mutableStateOf<Job?>(null)
    }

    OutlinedButton(onClick = {
        if (debounceJob.value == null) {
            debounceJob.value = coroutineScope.launch {
                onClick()
                delay(clickDebounceMillis)
                debounceJob.value = null
            }
        }
    },
        modifier,
        enabled,
        interactionSource,
        elevation,
        shape,
        border,
        colors,
        contentPadding,
        content)
}


