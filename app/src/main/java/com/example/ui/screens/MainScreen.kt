package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.components.PropertyDetailSheet
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberOrangeWarning
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // BackHandler: Navigate back to Overview tab if user is on secondary tab
    BackHandler(enabled = selectedTab > 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCyanPrimary.copy(alpha = 0.15f))
                                .border(1.dp, CyberCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberCyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "BootCheck",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = CyberTextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Bootloader & Attestation Inspector",
                                fontSize = 11.sp,
                                color = CyberTextTertiary
                            )
                        }
                    }
                },
                actions = {
                    val isScanning = (uiState as? BootCheckUiState.Success)?.isScanning == true
                    val infiniteTransition = rememberInfiniteTransition(label = "spin")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing)
                        ),
                        label = "spinAngle"
                    )

                    IconButton(
                        onClick = { viewModel.rescan() },
                        enabled = !isScanning,
                        modifier = Modifier.testTag("rescan_app_bar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Re-scan Device",
                            tint = CyberCyanPrimary,
                            modifier = if (isScanning) Modifier.rotate(angle) else Modifier
                        )
                    }

                    IconButton(
                        onClick = {
                            val text = viewModel.getExportText()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Security Report"))
                        },
                        modifier = Modifier.testTag("share_app_bar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            tint = CyberTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberDarkBg,
                    titleContentColor = CyberTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .border(width = 0.5.dp, color = CyberBorder)
                    .testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    Triple(0, "Overview", Icons.Default.Shield),
                    Triple(1, "Bootloader", Icons.Default.Lock),
                    Triple(2, "Root", Icons.Default.Security),
                    Triple(3, "App Check", Icons.Default.CheckCircle),
                    Triple(4, "Risk Matrix", Icons.Default.Assessment)
                )

                navItems.forEach { (index, label, icon) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyanPrimary,
                            selectedTextColor = CyberCyanPrimary,
                            unselectedIconColor = CyberTextTertiary,
                            unselectedTextColor = CyberTextTertiary,
                            indicatorColor = CyberCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scanning progress indicator
            val isScanning = (uiState as? BootCheckUiState.Success)?.isScanning == true
            AnimatedVisibility(visible = isScanning) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Auditing bootloader partitions and attestation tokens...",
                        fontSize = 11.sp,
                        color = CyberCyanPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = CyberCyanPrimary,
                        trackColor = CyberBorder
                    )
                }
            }

            // Main Content Area
            when (val state = uiState) {
                is BootCheckUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = CyberCyanPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Inspecting Android Boot Chain...",
                                color = CyberTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is BootCheckUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberRedAlert,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Scan Failed",
                                color = CyberTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.message,
                                color = CyberTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                is BootCheckUiState.Success -> {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                        label = "tabTransition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> OverviewTab(
                                report = state.report,
                                onShowDetail = { viewModel.showDetail(it) },
                                onNavigateTab = { viewModel.selectTab(it) },
                                exportText = viewModel.getExportText()
                            )
                            1 -> BootloaderTab(
                                bootloader = state.report.bootloader,
                                onShowDetail = { viewModel.showDetail(it) }
                            )
                            2 -> RootIntegrityTab(
                                root = state.report.root,
                                onShowDetail = { viewModel.showDetail(it) }
                            )
                            3 -> AppCheckTab(
                                appCheck = state.report.appCheck,
                                onShowDetail = { viewModel.showDetail(it) }
                            )
                            4 -> ImpactMatrixTab(
                                matrix = state.impactMatrix,
                                onShowDetail = { viewModel.showDetail(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Property Details Modal Bottom Sheet
    if (activeDetail != null) {
        PropertyDetailSheet(
            detail = activeDetail,
            sheetState = sheetState,
            onDismiss = { viewModel.showDetail(null) }
        )
    }
}
