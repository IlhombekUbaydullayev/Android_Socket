package com.example.android_softic.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.android_softic.model.BitCoin
import com.example.android_softic.R
import com.example.android_softic.manager.SocketListener
import com.example.android_softic.manager.WebSocketBtc
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import okhttp3.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

     private lateinit var webSocketBtc: WebSocketBtc
     private var lineValues = ArrayList<Entry>()
    private var count = 0
    lateinit var line_chart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        configureLineChart()
    }

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "BTC-USD"
        desc.textSize = 20F


        line_chart.description = desc
        val xAxis: XAxis = line_chart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            @RequiresApi(Build.VERSION_CODES.N)
            private val mFormat: SimpleDateFormat = SimpleDateFormat("HH mm", Locale.getDefault())

            @RequiresApi(Build.VERSION_CODES.N)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(System.currentTimeMillis()))
            }
        }
    }

    private fun initViews() {
        line_chart = findViewById(R.id.line_chart)
        webSocketBtc = WebSocketBtc()
        webSocketBtc.connectToSocket("live_trades_btcusd")
        webSocketBtc.socketListener(object : SocketListener {
            override fun onSuccess(bitCoin: BitCoin) {
                count++
                runOnUiThread {
                    if (bitCoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(this@MainActivity,"Successfull Connected",Toast.LENGTH_SHORT).show()
                    }else {
                        lineValues.add(Entry(count.toFloat(),bitCoin.data.price.toFloat()))
                        setLineChartData(lineValues)
                    }
                }
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@MainActivity,message,Toast.LENGTH_SHORT).show()
            }

        })


    }

    private fun setLineChartData(pricesHigh: ArrayList<Entry>) {
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val highLineDataSet = LineDataSet(pricesHigh,"Live")
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        highLineDataSet.setCircleColor(Color.YELLOW)
        dataSets.add(highLineDataSet)

        val lineData = LineData(dataSets)
        line_chart.data = lineData
        line_chart.invalidate()

    }
}