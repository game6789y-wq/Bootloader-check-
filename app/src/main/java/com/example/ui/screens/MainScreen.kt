package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.components.PropertyDetailSheet
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import com.example.ui.viewmodel.BootCheckUiState
import com.example.ui.viewmodel.BootCheckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BootCheckViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val activeDetail by viewModel.activeDetail.collectAsState()
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = selectedTab > 0) { viewModel.selectTab(0) }

    val isScanning = (uiState as? BootCheckUiState.Success)?.isScanning == true
    val transition = rememberInfiniteTransition(label = "scanIcon")
    val scanAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "scanRotation"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberCyanPrimary.copy(alpha = 0.12f))
                                .border(1.dp, CyberCyanPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(23.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text("BootCheck", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = CyberTextPrimary, letterSpacing = (-0.3).sp)
                            Text("DEVICE SECURITY CENTER", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = CyberTextTertiary, letterSpacing = 1.25.sp)
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.rescan() },
                        enabled = !isScanning,
                        modifier = Modifier.testTag("rescan_app_bar_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Re-scan device", tint = CyberCyanPrimary, modifier = if (isScanning) Modifier.rotate(scanAngle) else Modifier)
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, viewModel.getExportText())
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share security report"))
                        },
                        modifier = Modifier.testTag("share_app_bar_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share report", tint = CyberTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .border(width = 1.dp, color = CyberBorder.copy(alpha = 0.75f))
                    .testTag("bottom_nav_bar")
            ) {
                val items = listOf(
                    Triple(0, "Home", Icons.Default.Shield),
                    Triple(1, "Boot", Icons.Default.Lock),
                    Triple(2, "Root", Icons.Default.Security),
                    Triple(3, "Apps", Icons.Default.CheckCircle),
                    Triple(4, "Risks", Icons.Default.Assessment),
                    Triple(5, "Specs", Icons.Default.PhoneAndroid)
                )
                items.forEach { (index, label, icon) ->
                    val selected = selectedTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(icon, contentDescription = label, modifier = Modifier.size(21.dp))
                        },
                        label = {
                            Text(label, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyanPrimary,
                            selectedTextColor = CyberCyanPrimary,
                            unselectedIconColor = CyberTextTertiary,
                            unselectedTextColor = CyberTextTertiary,
                            indicatorColor = CyberCyanPrimary.copy(alpha = 0.13f)
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            AnimatedVisibility(visible = isScanning) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(CyberSurfaceVariant).padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(CyberCyanPrimary))
                        Spacer(Modifier.width(8.dp))
                        Text("SECURITY SCAN IN PROGRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, color = CyberCyanPrimary)
                    }
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                        color = CyberCyanPrimary,
                        trackColor = CyberBorder
                    )
                }
            }

            when (val state = uiState) {
                is BootCheckUiState.Loading -> {
                    Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                Modifier.size(76.dp).clip(RoundedCornerShape(26.dp)).background(CyberCyanPrimary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = CyberCyanPrimary, strokeWidth = 3.dp, modifier = Modifier.size(38.dp))
                            }
                            Text("Checking your device", style = MaterialTheme.typography.titleLarge, color = CyberTextPrimary)
                            Text("Reading boot state and system integrity…", style = MaterialTheme.typography.bodyMedium, color = CyberTextSecondary)
                        }
                    }
                }
                is BootCheckUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberRedAlert, modifier = Modifier.size(44.dp))
                            Text("Scan could not be completed", style = MaterialTheme.typography.titleLarge, color = CyberTextPrimary)
                            Text(state.message, style = MaterialTheme.typography.bodyMedium, color = CyberTextSecondary)
                            Button(
                                onClick = { viewModel.rescan() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = CyberDarkBg)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Try again", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                is BootCheckUiState.Success -> {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                        label = "screenTransition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> OverviewTab(state.report, { viewModel.showDetail(it) }, { viewModel.selectTab(it) }, viewModel.getExportText())
                            1 -> BootloaderTab(state.report.bootloader) { viewModel.showDetail(it) }
                            2 -> RootIntegrityTab(state.report.root) { viewModel.showDetail(it) }
                            3 -> AppCheckTab(state.report.appCheck) { viewModel.showDetail(it) }
                            4 -> ImpactMatrixTab(state.impactMatrix) { viewModel.showDetail(it) }
                            5 -> DeviceSpecsTab()
                        }
                    }
                }
            }
        }
    }

    if (activeDetail != null) {
        PropertyDetailSheet(
            detail = activeDetail,
            sheetState = sheetState,
            onDismiss = { viewModel.showDetail(null) }
        )
    }
}
