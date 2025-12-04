package com.example.contadordeono99

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainCalculator : AppCompatActivity() {

    private lateinit var mainNumber: TextView
    private lateinit var buttons: List<Button>

    private var mainNumberValue: Int = 0

    data class HistoryItem(
        val operation: String,
        val prevValue: Int,
        val newValue: Int
    )

    // HISTORIAL (cronológico: index 0 = primer cambio)
    private val history = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_calculator)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        mainNumber = findViewById(R.id.MainNumber)

        buttons = listOf(
            findViewById(R.id.btnplus1),
            findViewById(R.id.btnplus2),
            findViewById(R.id.btnplus3),
            findViewById(R.id.btnplus4),
            findViewById(R.id.btnplus5),
            findViewById(R.id.btnplus6),
            findViewById(R.id.btnplus7),
            findViewById(R.id.btnplus8),
            findViewById(R.id.btnplus9),
            findViewById(R.id.btnplus10)
        )

        // Botón +0
        findViewById<Button>(R.id.btnplus0).setOnClickListener {
            pressNumber(0, it)
        }

        // Botón -10
        findViewById<Button>(R.id.btnminus10).setOnClickListener {
            pressNumber(-10, it)
        }

        // Asignar listeners a +1…+10
        val values = listOf(1,2,3,4,5,6,7,8,9,10)
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                pressNumber(values[index], it)
            }
        }

        // RESET
        findViewById<Button>(R.id.btnreset).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            history.clear()
            mainNumberValue = 0
            updateNumber()
            enableAllButtons()
        }

        // BACK
        findViewById<Button>(R.id.btnback).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (history.isNotEmpty()) {
                // restaurar al valor previo al último cambio
                val last = history.removeAt(history.lastIndex)
                mainNumberValue = last.prevValue
                updateNumber()
                blockButtonsIfNeeded()
            }
        }
        val btnOno99 = findViewById<Button>(R.id.btnono)

        btnOno99.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showAboutPopup()
        }


        // MENU (historial) - botón tipo ImageButton con id btnMenuHistory
        val btnMenu = findViewById<ImageButton?>(R.id.btnMenuHistory)
        btnMenu?.setOnClickListener { view ->
            showHistoryDialog()
        }

        // Inicializar pantalla
        updateNumber()
        blockButtonsIfNeeded()
    }

    // ----------------------------------------------------
    // Funciones compactas
    // ----------------------------------------------------

    private fun pressNumber(value: Int, view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        val prev = mainNumberValue
        val new = prev + value

        // Guardar operación en historial (texto + prev + new)
        val opText = if (value >= 0) {
            "$prev + $value = $new"
        } else {
            "$prev - ${-value} = $new"
        }
        history.add(HistoryItem(operation = opText, prevValue = prev, newValue = new))

        mainNumberValue = new
        updateNumber()
        blockButtonsIfNeeded()
    }

    private fun updateNumber() {
        mainNumber.text = mainNumberValue.toString()
    }

    private fun enableAllButtons() {
        buttons.forEach { it.isEnabled = true }
    }

    private fun blockButtonsIfNeeded() {
        val remaining = 99 - mainNumberValue
        enableAllButtons()
        // Desactiva botones que pasarían de 99
        buttons.forEachIndexed { index, button ->
            val btnValue = index + 1
            if (btnValue > remaining) button.isEnabled = false
        }
    }

    // --------------------
    // DIALOGO DE HISTORIAL
    // --------------------
    private fun showHistoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Historial")

        if (history.isEmpty()) {
            builder.setMessage("Historial vacío")
            builder.setNegativeButton("Cerrar", null)
            builder.setNeutralButton("Limpiar") { _, _ ->
                // ya está vacío, solo asegurar estado
                history.clear()
                mainNumberValue = 0
                updateNumber()
                enableAllButtons()
            }
            builder.show()
            return
        }

        // Lista de strings para mostrar (cronológica)
        val items = history.mapIndexed { idx, item -> "${idx+1}. ${item.operation}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        // Al pulsar una entrada, actualizamos la calculadora a ese momento
        builder.setAdapter(adapter) { dialog, which ->
            // 'which' es el índice en history
            val selected = history[which]
            mainNumberValue = selected.newValue
            /* eliminar todo el historial posterior a la selección */
            while (history.size > which + 1) history.removeAt(history.lastIndex)
            updateNumber()
            blockButtonsIfNeeded()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cerrar", null)

        builder.setNeutralButton("Limpiar") { _, _ ->
            history.clear()
            mainNumberValue = 0
            updateNumber()
            enableAllButtons()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showAboutPopup() {
        val message = """
        Ono 99 Calculator
        Una herramienta diseñada para llevar un control rápido y eficiente de conteos incrementales. Pensada para el juego de MATTEL ONO99 para hacer que el juego sea mas facil de jugar.
        Desarrollado por: ZFausto
        Ingeniero en software.
        
        Objetivo del proyecto:
        Ofrecer una calculadora ligera y optimizada para conteos tipo “+1, +5, +10, -10”, con historial visual.
        
        Características principales:
        Contador dinámico hasta 99
        Historial completo
        Haptics de respuesta táctil
        Interfaz simple
        
        Contacto y redes:
        • GitHub — https://github.com/Fausto-B
        • YouTube — https://www.youtube.com/@ZFausto
        
        Versión: 1.0.0
        Licencia: Uso personal y libre distribución.
        © 2025 ZFausto. Todos los derechos reservados.
    """.trimIndent()

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Ono 99 Calculator\n")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .setNeutralButton("Visitar GitHub") { _, _ ->
                openUrl("https://github.com/Fausto-B")
            }
            .create()

        dialog.show()
    }
    private fun openUrl(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(url)
        startActivity(intent)
    }


}
