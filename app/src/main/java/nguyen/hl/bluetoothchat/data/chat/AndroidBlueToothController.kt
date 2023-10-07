package nguyen.hl.bluetoothchat.data.chat

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nguyen.hl.bluetoothchat.domain.chat.BlueToothController
import nguyen.hl.bluetoothchat.domain.chat.BlueToothDeviceDomain

class AndroidBlueToothController(
    private val context: Context
) : BlueToothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BlueToothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BlueToothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BlueToothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BlueToothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    override fun startDiscovery() {
        TODO("Not yet implemented")
    }

    override fun stopDiscovery() {
        TODO("Not yet implemented")
    }

    override fun realease() {
        TODO("Not yet implemented")
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}