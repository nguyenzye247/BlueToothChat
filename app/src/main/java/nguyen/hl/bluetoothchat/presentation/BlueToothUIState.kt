package nguyen.hl.bluetoothchat.presentation

import nguyen.hl.bluetoothchat.domain.chat.BlueToothDevice

data class BlueToothUIState(
    val scannedDevices: List<BlueToothDevice> = emptyList(),
    val pairedDevices: List<BlueToothDevice> = emptyList()
)
