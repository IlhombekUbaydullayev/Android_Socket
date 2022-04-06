package com.example.android_softic.manager

import android.util.Log
import com.example.android_softic.model.BitCoin
import com.example.android_softic.model.Currency
import com.example.android_softic.model.DataSend
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

class WebSocketBtc {
    lateinit var mWebSocket : WebSocket
    private lateinit var socketlistener : SocketListener
    private var gson = Gson()

    fun connectToSocket(currency: String) {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request,object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send(gson.toJson(Currency("bts:subscribe", DataSend(currency))))
            }

            override fun onMessage(webSocket: WebSocket?, text: String) {
                Log.d("@@@","Receiving : $text")
                val bitCoin = gson.fromJson(text, BitCoin::class.java)
                socketlistener.onSuccess(bitCoin)

            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@","Receiving bytes : $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@","Closing : $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@","Error : " + t.message)
                socketlistener.onFailure(t.localizedMessage)
            }
        })
        client.dispatcher().executorService().shutdown()
    }

    fun socketListener(socketlistener: SocketListener) {
        this.socketlistener = socketlistener
    }
}
interface SocketListener{
    fun onSuccess(bitCoin: BitCoin)
    fun onFailure(message: String)
}