package com.dominando.android.apppiscina

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.comunication.tcp.comunicationtcp.ComunicacionTCP
import com.comunication.tcp.comunicationtcp.Wifi_conexion
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity() : AppCompatActivity() {
    private var tcpClient: ComunicacionTCP? =null
    var yaarranco = false
    var estado_conexion:Boolean? = false
    var wifi: Wifi_conexion? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            wifi = Wifi_conexion(this,"SN-")
            var listado = wifi?.getListadosWifiConexiones()
            if (listado != null && listado.size != 0) {
                var adapter = ArrayAdapter<String>(
                    applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    listado
                )
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        return
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        var ssid = listado.get(position)
                        estado_conexion = wifi?.Connect_Wifi(ssid)

                    }
                }

            }
        }
        catch (ex:Exception){
            Toast.makeText(MainActivity@this,"Error conectando a la wifi",Toast.LENGTH_LONG).show()
        }

        button.setOnClickListener {
            if (tcpClient != null && estado_conexion==true) {
                tcpClient?.enviar_pelo_socket("#CONNECT")

                if(!yaarranco) {
                    tcpClient?.comenzar_listener() { mensaje ->
                        if (mensaje != "")
                            leer_porta(mensaje)
                        if(mensaje.contains("Timeout"))
                            yaarranco = false
                    }
                    yaarranco = true
                }

            }

        }

        button2.setOnClickListener{
            try {
                tcpClient = ComunicacionTCP("192.168.4.1", 5005){mensaje->
                    yaarranco = false
                    Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show() //siempre vem aqui es por error
                }

            }
            catch (ex:Exception){
                Toast.makeText(MainActivity@this,"Error abrindo socket",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        var msg:String=""
        tcpClient?.stopCLiente()

    }

    private fun leer_porta(mensaje:String){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show()
    }


}
