package com.example.contadordeono99

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat



class MainCalculator : AppCompatActivity() {
    // Referencias globales
    private lateinit var mainNumber: TextView
    private lateinit var buttons: List<Button>

    private var mainNumberValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_calculator)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Referencia del TextView
        mainNumber = findViewById(R.id.MainNumber)

        // Lista de botones “+”
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

        // Botón especial 0 (va aparte)
        findViewById<Button>(R.id.btnplus0).setOnClickListener {
            pressNumber(0, it)
        }

        // Botón -10
        findViewById<Button>(R.id.btnminus10).setOnClickListener {
            pressNumber(-10, it)
        }

        // Asignar listeners automáticos a botones +1…+10
        val values = listOf(1,2,3,4,5,6,7,8,9,10)
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                pressNumber(values[index], it)
            }
        }

        // Botón Reset
        findViewById<Button>(R.id.btnreset).setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            mainNumberValue = 0
            updateNumber()
            enableAllButtons()
        }
    }

    // ------------------------------
    // Funciones compactas
    // ------------------------------

    private fun pressNumber(value: Int, view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        mainNumberValue += value
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

        // No permitir que supere 99
        buttons.forEachIndexed { index, button ->
            val btnValue = index + 1
            if (btnValue > remaining) {
                button.isEnabled = false
            }
        }
    }
}