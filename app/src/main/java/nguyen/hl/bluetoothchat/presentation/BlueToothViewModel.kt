package nguyen.hl.bluetoothchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import nguyen.hl.bluetoothchat.domain.chat.BlueToothController
import javax.inject.Inject

@HiltViewModel
class BlueToothViewModel @Inject constructor(
    private val blueToothController: BlueToothController
): ViewModel() {
    private val _state = MutableStateFlow(BlueToothUIState())
    val state = combine(
        blueToothController.scannedDevices,
        blueToothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        blueToothController.startDiscovery()
    }

    fun stopScan() {
        blueToothController.stopDiscovery()
    }
}
