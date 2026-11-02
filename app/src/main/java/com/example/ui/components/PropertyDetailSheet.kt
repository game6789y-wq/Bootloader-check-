package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberOrangeWarning
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import com.example.ui.viewmodel.PropertyDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailSheet(
    detail: PropertyDetail?,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    if (detail == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberCyanPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberCyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = detail.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = detail.subtitle,
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CyberTextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Value card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceCard)
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "DETECTED VALUE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextTertiary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detail.value,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (detail.isSafe) CyberGreenSuccess else CyberOrangeWarning,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Section
            Text(
                text = "What is this?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyanPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail.explanation,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Impact Section
            Text(
                text = "Security & Integrity Impact",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyanPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail.impact,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendation Section
            Text(
                text = "Recommendation",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyanPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail.recommendation,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dismiss_sheet_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyanPrimary,
                    contentColor = Color(0xFF00363D)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Got it",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
