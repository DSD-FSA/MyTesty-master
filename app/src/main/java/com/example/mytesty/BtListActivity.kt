package com.example.mytesty

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytesty.databinding.ActivityMainBinding


class BtListActivity : AppCompatActivity(), RcAdapter.Listener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerPermissionListener()
        checkBluetoothPermission()
        init()


    }

    private fun checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Bluetooth on", Toast.LENGTH_LONG).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pLauncher.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            }
        }
    }


    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var btAdapter: BluetoothAdapter

    private fun init() {
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
        adapter = RcAdapter(this)
        binding.RcView.layoutManager = LinearLayoutManager(this)
        binding.RcView.adapter = adapter
        registerPermissionListener()
        getPairedDevices()
    }

// Остальная часть кода остается неизменной


    private fun getPairedDevices() {
        try {
            val pairedDevices: Set<BluetoothDevice>? = btAdapter.bondedDevices
            val tempList = ArrayList<Listitem>()
            pairedDevices?.forEach {
                tempList.add(Listitem(it.name, it.address))
            }
            adapter.submitList(tempList)
        } catch (_: SecurityException) {
            // Обработка ошибок без выдачи сообщений
        }
    }

    private fun registerPermissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                getPairedDevices()
            } else {
                Toast.makeText(this, "Not all Bluetooth permissions granted", Toast.LENGTH_LONG)
                    .show()
            }
        }

        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            TODO("VERSION.SDK_INT < S")
        }

        pLauncher.launch(permissionsToRequest)
    }
    companion object{
        const val DEVICE_KEY = "device_key"
    }

    override fun onClick(item: Listitem) {
        val i =  Intent().apply {
            putExtra(DEVICE_KEY, item)
        }
        setResult(RESULT_OK, i)
        finish()

    }

    override fun putExtra(deviceKey: String, item: Listitem) {

    }
}