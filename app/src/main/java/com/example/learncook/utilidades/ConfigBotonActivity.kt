package com.example.learncook.utilidades

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.learncook.R

class ConfigBotonActivity : AppCompatActivity() {
    private lateinit var preview: Button
    private lateinit var seekBar: SeekBar
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_boton)

        preview = findViewById(R.id.btn_preview_boton)
        seekBar = findViewById(R.id.seek_boton)
        btnGuardar = findViewById(R.id.btn_guardar_boton)

        val prefs = getSharedPreferences("configuraciones", Context.MODE_PRIVATE)
        val currentSize = prefs.getFloat("boton_tamano", 25f)
        seekBar.progress = (currentSize - 12).toInt()
        preview.textSize = currentSize

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = 12 + progress
                preview.textSize = size.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnGuardar.setOnClickListener {
            val size = 12 + seekBar.progress
            prefs.edit().putFloat("boton_tamano", size.toFloat()).apply()
            finish()
        }
    }
}
