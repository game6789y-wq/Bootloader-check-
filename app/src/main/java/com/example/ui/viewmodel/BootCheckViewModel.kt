package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.detector.SecurityAuditEngine
import com.example.data.model.ImpactMatrixItem
import com.example.data.model.SecurityReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PropertyDetail(
    val title: String,
    val value: String,
    val subtitle: String,
    val explanation: String,
    val impact: String,
    val recommendation: String,
    val isSafe: Boolean
)

sealed interface BootCheckUiState {
    data object Loading : BootCheckUiState
    data class Success(
        val report: SecurityReport,
        val impactMatrix: List<ImpactMatrixItem>,
        val isScanning: Boolean = false
    ) : BootCheckUiState
    data class Error(val message: String) : BootCheckUiState
}

class BootCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<BootCheckUiState>(BootCheckUiState.Loading)
    val uiState: StateFlow<BootCheckUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _activeDetail = MutableStateFlow<PropertyDetail?>(null)
    val activeDetail: StateFlow<PropertyDetail?> = _activeDetail.asStateFlow()

    private val _propertySearch = MutableStateFlow("")
    val propertySearch: StateFlow<String> = _propertySearch.asStateFlow()

    init {
        performScan(isInitial = true)
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setPropertySearch(query: String) {
        _propertySearch.value = query
    }

    fun showDetail(detail: PropertyDetail?) {
        _activeDetail.value = detail
    }

    fun rescan() {
        performScan(isInitial = false)
    }

    private fun performScan(isInitial: Boolean) {
        viewModelScope.launch {
            if (!isInitial) {
                val current = _uiState.value
                if (current is BootCheckUiState.Success) {
                    _uiState.update { current.copy(isScanning = true) }
                }
                delay(600) // realistic scan effect
            }
            try {
                val report = SecurityAuditEngine.runAudit(getApplication())
                val matrix = SecurityAuditEngine.getImpactMatrix()
                _uiState.value = BootCheckUiState.Success(
                    report = report,
                    impactMatrix = matrix,
                    isScanning = false
                )
            } catch (e: Exception) {
                _uiState.value = BootCheckUiState.Error(e.localizedMessage ?: "Scan failed")
            }
        }
    }

    fun getExportText(): String {
        val state = _uiState.value
        return if (state is BootCheckUiState.Success) {
            SecurityAuditEngine.formatReportMarkdown(state.report)
        } else {
            "BootCheck report unavailable"
        }
    }
}
