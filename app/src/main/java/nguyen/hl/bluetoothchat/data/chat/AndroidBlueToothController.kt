package nguyen.hl.bluetoothchat.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nguyen.hl.bluetoothchat.domain.chat.BlueToothController
import nguyen.hl.bluetoothchat.domain.chat.BlueToothDeviceDomain

@SuppressLint("MissingPermission")
class AndroidBlueToothController(
    private val context: Context
) : BlueToothController {
    private val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN

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

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBlueToothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        if (!hasPermission(bluetoothScanPermission)) {
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(bluetoothScanPermission)) return

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun realease() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        if (!hasPermission(bluetoothScanPermission)) {
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBlueToothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}