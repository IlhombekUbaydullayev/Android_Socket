package com.example.android_softic.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_softic.model.BitCoin
import com.example.android_softic.R
import com.example.android_softic.manager.SocketListener
import com.example.android_softic.manager.WebSocketBtc
import okhttp3.*


class MainActivity : AppCompatActivity() {

     lateinit var tv_socket: TextView
     private lateinit var webSocketBtc: WebSocketBtc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        webSocketBtc = WebSocketBtc()
        tv_socket = findViewById(R.id.tv_socket)

        webSocketBtc.connectToSocket("live_trades_btcusd")
        webSocketBtc.socketListener(object : SocketListener {
            override fun onSuccess(bitCoin: BitCoin) {
                runOnUiThread {
                    if (bitCoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(this@MainActivity,"Successfull Connected",Toast.LENGTH_SHORT).show()
                    }else {
                        tv_socket.text = bitCoin.data.amount.toString()
                    }
                }
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@MainActivity,message,Toast.LENGTH_SHORT).show()
            }

        })
    }
}