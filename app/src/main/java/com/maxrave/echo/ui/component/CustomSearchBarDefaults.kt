package com.maxrave.echo.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputFieldHeight
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val AnimationDelayMillis: Int = 100
private val SearchBarIconOffsetX: Dp = 4.dp
private val SearchBarMaxWidth: Dp = 720.dp
private val SearchBarMinWidth: Dp = 360.dp

object CustomSearchBarDefaults {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputField(
        query: TextFieldValue,
        onQueryChange: (TextFieldValue) -> Unit,
        onSearch: (String) -> Unit, // Changed to String to match KeyboardActions
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        placeholder: @Composable (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        colors: TextFieldColors = inputFieldColors(), // Use M3 default
        interactionSource: MutableInteractionSource? = null,
    ) {
        val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
        val focused = interactionSource.collectIsFocusedAsState().value
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        // Let TextFieldColors handle the color logic
        val textStyle = LocalTextStyle.current.merge(
            TextStyle(color = textColor(enabled))
        )
        val cursorBrush = SolidColor(cursorColor(false))

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier
                .sizeIn(
                    minWidth = SearchBarMinWidth,
                    maxWidth = SearchBarMaxWidth,
                    minHeight = InputFieldHeight,
                )
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onExpandedChange(true) },
            enabled = enabled,
            singleLine = true,
            textStyle = textStyle, // Apply the style
            cursorBrush = cursorBrush, // Apply the brush
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query.text) }), // Pass query.text
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = query.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon?.let { { Box(Modifier.offset(x = SearchBarIconOffsetX)) { it() } } },
                    trailingIcon = trailingIcon?.let { { Box(Modifier.offset(x = -SearchBarIconOffsetX)) { it() } } },
                    shape = SearchBarDefaults.inputFieldShape,
                    colors = colors, // Pass colors to the decoration box
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(),
                    container = {},
                )
            }
        )

        // LaunchedEffect for focus management remains the same
        val shouldClearFocus = !expanded && focused
        LaunchedEffect(expanded) {
            if (shouldClearFocus) {
                delay(AnimationDelayMillis.toLong())
                focusManager.clearFocus()
            }
        }
    }

    @Composable
    fun textColor(
        enabled: Boolean,
    ): Color =
        when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            // This can be simplified since the other conditions are the same
            else -> MaterialTheme.colorScheme.onSurface
        }

    @Composable
    @Stable
    fun cursorColor(isError: Boolean): Color =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
}