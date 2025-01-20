package dev.kevalkanpariya.swipetakehomeassign.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily

@Composable
fun DynamicText(text: String) {
    val maxTextLength = 15
    val fontSize = if (text.length > maxTextLength) 12.sp else 15.sp
    
    Text(
        text = text,
        style = TextStyle(
            fontFamily = mierFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = Color.Black
        ),
        maxLines = if (text.length > maxTextLength) 2 else 1, // Set max lines based on text length
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(0.4f)
    )
}
