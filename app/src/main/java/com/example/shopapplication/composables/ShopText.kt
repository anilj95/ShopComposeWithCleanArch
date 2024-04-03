package com.example.shopapplication.composables

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.shopapplication.R


@Composable
@SuppressWarnings("LongParameterList")
fun ShopText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    size: TextUnit = 14.sp,
    color: Color = Color.White,
    font: ShopFont = ShopFont.NotoSansRegular,
    maxLines: Int = Int.MAX_VALUE,
    shadow: Shadow? = null,
    textAlign: TextAlign = TextAlign.Start,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    inlineContent:Map<String, InlineTextContent> = mapOf(),
    fontWeight: FontWeight? = null,
    ) {
    Text(
        text = text,
        modifier = modifier,
        inlineContent = inlineContent,
        color = color,
        fontFamily = FontFamily(Font(resId = font.fontResourceId)),
        fontSize = size,
        maxLines = maxLines, overflow = TextOverflow.Ellipsis,
        style = TextStyle.Default.copy(shadow = shadow),
        textAlign = textAlign,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
    )
}

@Composable
@SuppressWarnings("LongParameterList")
fun ShopText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 14.sp,
    color: Color? = Color.White,
    font: ShopFont = ShopFont.NotoSansRegular,
    maxLines: Int = Int.MAX_VALUE,
    shadow: Shadow? = null,
    textAlign: TextAlign = TextAlign.Start,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    style: TextStyle = TextStyle.Default.copy(shadow = shadow),
    textDecoration: TextDecoration = TextDecoration.None,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        text = text,
        modifier = modifier,
        color = color ?: Color.Unspecified,
        fontFamily = FontFamily(Font(resId = font.fontResourceId)),
        fontSize = size,
        maxLines = maxLines,
        overflow = overflow,
        style = style,
        textAlign = textAlign,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        textDecoration = textDecoration
    )
}


sealed class ShopFont(val fontResourceId: Int) {
    object NotoSansBlack : ShopFont(R.font.shop_presentation_noto_sans_black)
    object NotoSansBold : ShopFont(R.font.shop_presentation_noto_sans_bold)
    object NotoSansSemiBold : ShopFont(R.font.shop_presentation_noto_sans_semi_bold)
    object NotoSansMedium : ShopFont(R.font.shop_presentation_noto_sans_medium)
    object NotoSansRegular : ShopFont(R.font.shop_presentation_noto_sans_regular)
}
