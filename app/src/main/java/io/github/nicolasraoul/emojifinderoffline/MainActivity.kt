package io.github.nicolasraoul.emojifinderoffline

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.edge.aicore.GenerativeAIException
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var model: GenerativeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        model =
            GenerativeModel(
                generationConfig {
                    context = applicationContext
                    temperature = 0.2f
                    topK = 16
                    maxOutputTokens = 256
                }
            )

        val inputText = findViewById<EditText>(R.id.input_text)
        val emojiDisplay = findViewById<TextView>(R.id.emoji_display)
        val generateButton = findViewById<Button>(R.id.generate_button)

        generateButton.setOnClickListener {
            val text = inputText.text.toString()
            if (text.isNotEmpty()) {
                emojiDisplay.text = "" // Clear previous results
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        model!!.generateContentStream("Output a dozen of emojis related to $text")
                            .collect {
                                emojiDisplay.append(it.text)
                            }
                    } catch (e: GenerativeAIException) {
                        Log.e("EmojiSearch", "Error generating emojis", e)
                        emojiDisplay.text = "Error generating emojis."
                    }
                }
            } else {
                emojiDisplay.text = "Please enter some text."
            }
        }
    }
}