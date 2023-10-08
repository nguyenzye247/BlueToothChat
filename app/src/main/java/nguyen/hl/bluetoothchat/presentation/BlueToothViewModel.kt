package nguyen.hl.bluetoothchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import nguyen.hl.bluetoothchat.domain.chat.BlueToothController
import nguyen.hl.bluetoothchat.domain.chat.BlueToothDeviceDomain
import nguyen.hl.bluetoothchat.domain.chat.ConnectionResult
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

    private var deviceConnectionJob: Job? = null

    init {
        blueToothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        blueToothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BlueToothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = blueToothController
            .connectoDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        blueToothController.closeConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = blueToothController
            .startBluetoothServer()
            .listen()
    }

    fun startScan() {
        blueToothController.startDiscovery()
    }

    fun stopScan() {
        blueToothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(
                        isConnected = true,
                        isConnecting = false,
                        errorMessage = null
                    ) }
                }
                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }
            .catch { throwable ->
                blueToothController.closeConnection()
                _state.update { it.copy(
                    isConnected = false,
                    isConnecting = false
                ) }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        blueToothController.release()
    }
}
