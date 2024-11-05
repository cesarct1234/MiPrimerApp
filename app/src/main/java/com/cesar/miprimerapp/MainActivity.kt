
package com.cesar.miprimerapp

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var grabadora: MediaRecorder? = null
    private var ruta: String? = null
    private var imgGrabar: ImageView? = null
    private var imgPlay: ImageView? = null
    private var imgStop: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enlazar las vistas
        imgGrabar = findViewById(R.id.imgGrabar)
        imgPlay = findViewById(R.id.imgPlay)
        imgStop = findViewById(R.id.imgStop)

        // Verificar y solicitar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), 1000)
        }

        // Configurar los listeners de los botones
        imgGrabar?.setOnClickListener { grabar(it) }
        imgPlay?.setOnClickListener { reproducir(it) }
        imgStop?.setOnClickListener { detener(it) }
    }

    private fun grabar(view: View?) {
        if (grabadora == null) {
            // Configurar la ruta de almacenamiento
            ruta = getExternalFilesDir(null)?.absolutePath + "/grabacion.mp3"
            grabadora = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(ruta)
                try {
                    prepare()
                    start()
                    imgGrabar?.setBackgroundColor(Color.RED)
                    Toast.makeText(applicationContext, "Grabando...", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            detener(view)
        }
    }

    private fun detener(view: View?) {
        if (grabadora != null) {
            try {
                grabadora?.stop()
                grabadora?.release()
                grabadora = null
                imgGrabar?.setBackgroundColor(Color.BLACK)
                Toast.makeText(applicationContext, "Grabación detenida", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "No hay grabación en curso", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reproducir(view: View?) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(ruta)
            mediaPlayer.prepare()
            mediaPlayer.start()
            Toast.makeText(applicationContext, "Reproduciendo audio", Toast.LENGTH_SHORT).show()

            // Liberar MediaPlayer al finalizar
            mediaPlayer.setOnCompletionListener {
                it.release()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error al reproducir el audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



