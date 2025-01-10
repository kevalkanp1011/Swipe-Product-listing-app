package dev.kevalkanpariya.swipetakehomeassign.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily

@Composable
fun StepIcon(stepNumber: Int, size: Dp = 48.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color = Color(0xff5f00d3), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stepNumber.toString(),
            color = Color.White,
            style = TextStyle(fontFamily = mierFontFamily, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        )
    }
}