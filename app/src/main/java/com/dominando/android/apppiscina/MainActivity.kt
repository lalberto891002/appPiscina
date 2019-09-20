package com.dominando.android.apppiscina

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*

class MainActivity() : AppCompatActivity() {
    private var tcpClient:ComunicacionTCP? =null
    var yaarranco = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            tcpClient = ComunicacionTCP("172.16.2.232", 9002){mensaje->
                yaarranco = false
                Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show() //siempre vem aqui es por error
            }

        }
        catch (ex:Exception){
            Toast.makeText(MainActivity@this,"Error abrindo socket",Toast.LENGTH_LONG).show()
        }

        button.setOnClickListener {
            if (tcpClient != null) {
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
