package com.example.emojifilter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.emojifilter.ui.MainViewModel // Adjust import if package structure is different

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main) // Assuming a layout might exist

        val viewModel = MainViewModel()
        viewModel.processLlmResult() // Process the result

        // You could log or display viewModel.getProcessedLlmResult()
        println("Processed result in MainActivity: " + viewModel.getProcessedLlmResult())
    }
}
