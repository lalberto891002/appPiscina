package com.dominando.android.apppiscina


import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import kotlin.concurrent.thread
import android.os.Handler
import java.util.*

//maneja la comunicacao completa envio e recepcao recibe o metodo para os atualizacao da UI e para o erro
class ComunicacionTCP(ip:String,porta:Int,msgError:String ="Error opening Socket",msgTimeout:String ="Timeout comunication",callback: (String) -> Unit):Observable() {
    private var IP = ip
    private var PORTA = porta
    private var tcpCLient :Socket?= null
    private var bufferOut:PrintWriter? = null
    private var bufferIn:BufferedReader? = null
    private val errorMsg = msgError
    private val errorTimeot:String =msgTimeout
    private var detiene_lectura = false
    private var contador_timeout = 0

    init {
        initSocket(callback)
    }

    fun enviar_pelo_socket(mensaje:String):Unit{
        if(bufferOut!=null){
            thread(start = true){
                bufferOut?.println(mensaje)
                bufferOut?.flush()
            }
        }
    }

    fun stopCLiente(){
        detiene_lectura = true
        Thread.sleep(150)
        if(bufferOut!=null){
            bufferOut?.flush()
            bufferOut?.close()

        }
    }

    private fun initSocket(callback: (String) -> Unit){
        thread(start = true){
            try {
                tcpCLient = Socket(InetAddress.getByName(IP), PORTA)
                bufferOut = PrintWriter(tcpCLient?.getOutputStream())
                bufferIn = BufferedReader(InputStreamReader(tcpCLient?.getInputStream()))
            }
            catch (e:Exception){
              tcpCLient = null
                Handler(Looper.getMainLooper()).post{
                    callback(errorMsg)

                }
            }
        }
    }

    fun comenzar_listener(callback:(String)->Unit){
        detiene_lectura = false
        var mensaje = ""
        thread (start = true,isDaemon = true){
            while(!detiene_lectura) {
                try {
                    mensaje = bufferIn?.readLine() ?: ""
                    if (mensaje != "") {
                        contador_timeout = 0
                        Handler(Looper.getMainLooper()).post{
                            callback(mensaje)
                        }
                    }
                    Thread.sleep(100)
                }
                catch (ex:Exception){
                    mensaje = errorMsg
                }
            }
        }

        thread(start = true,isDaemon = true) {
            while (!detiene_lectura) {
                contador_timeout++
                if (contador_timeout >= MAX_TIMEOUT_COUNT)
                    time_out_function(callback)
                Thread.sleep(1000)
            }

        }
    }
    fun detiene_timer_lectura(){
        detiene_lectura = true
    }

    fun time_out_function(callback: (String) -> Unit){
        detiene_timer_lectura()
        Handler(Looper.getMainLooper()).post{
            callback(errorTimeot)

        }

    }

    companion object{
        val MAX_TIMEOUT_COUNT = 10
    }

}




