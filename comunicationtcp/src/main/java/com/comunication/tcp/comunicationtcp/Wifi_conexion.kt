package com.comunication.tcp.comunicationtcp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class Wifi_conexion(contexto: Context, mask:String): BroadcastReceiver() {

    private val context = contexto
    private val mascara = mask;
    private val wifi: WifiManager = contexto.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private fun escanear():Boolean = wifi.startScan()
    fun getListadosWifiConexiones():ArrayList<String>?{
        if(escanear()){
          return scanSuccess()
        }
        return scanFailure()
    }

    fun Connect_Wifi(ssid:String):Boolean{
        var wifiConf = WifiConfiguration()
        wifiConf.SSID = "\\$ssid\\"
        wifiConf.preSharedKey = "\\$PASS_NET\\"
        wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        wifi.addNetwork(wifiConf)
        val list = wifi.getConfiguredNetworks()
        list.forEachIndexed { index, it ->
            if(it.SSID!=null && it.SSID.toString().trim('"',' ').equals(ssid)){
                wifi.disconnect()
                wifi.enableNetwork(it.networkId,true)
                wifi.reconnect()
                return true
            }
        }

        return false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)?: false
        if (success) {
            scanSuccess()
        } else {
            scanFailure()
        }

    }

    private fun scanSuccess():ArrayList<String> {
        var wifiScanList = wifi.getScanResults()
        var lista = ArrayList<String>()
        wifiScanList.forEach {
            if (it.SSID.toString().contains(mascara))
                lista.add(it.SSID.toString())
        }
        return lista
    }

    private fun scanFailure():ArrayList<String>{
        return ArrayList<String>()
    }



    companion object{
        val HEAD_NET = "SN-"
        val PASS_NET = "152152153"
    }
}




