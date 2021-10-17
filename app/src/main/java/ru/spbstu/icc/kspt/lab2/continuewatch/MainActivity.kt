package ru.spbstu.icc.kspt.lab2.continuewatch

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private var flag = true
    private val sharedPref by lazy { getPreferences(Context.MODE_PRIVATE) }
    // (4) Сделал приватным
    // (5) Добавил строковые ресурсы для activity
    private var backgroundThread = Thread {
        while (true) {
            // (2) Поменял порядок обновления строки и сна потока, поскольку счетчик опаздывал на 1 секунду
            if (flag) {
                textSecondsElapsed.post {
                    // (1) Лучше использовать строковые ресурсы, они поддерживают форматирование строк

                    textSecondsElapsed.text = getString(R.string.SecondsLabel, ++secondsElapsed)
                }
            }
            Thread.sleep(1000)
        }
    }
    // (3) Приложение продолжает считать время в фоне
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        // При повороте экрана (или любой другой смены activity мы должны восстановить содержимое)
        if (savedInstanceState != null){
            with (savedInstanceState) {
                secondsElapsed = getInt(SECONDS)
            }
        } else {
            secondsElapsed = sharedPref.getInt(SECONDS, 0)
        }

        backgroundThread.start()
    }

    override fun onStop() {
        super.onStop()
        flag = false
        with(sharedPref.edit()) {
            putInt(SECONDS, secondsElapsed)
            apply()
        }
    }

    override fun onStart() {
        super.onStart()
        flag = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(SECONDS, secondsElapsed)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SECONDS = "seconds"
    }
}