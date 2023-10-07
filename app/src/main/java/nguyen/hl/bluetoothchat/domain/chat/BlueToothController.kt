package nguyen.hl.bluetoothchat.domain.chat

import kotlinx.coroutines.flow.StateFlow

interface BlueToothController {
    val scannedDevices: StateFlow<List<BlueToothDevice>>
    val pairedDevices: StateFlow<List<BlueToothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun realease()
}
