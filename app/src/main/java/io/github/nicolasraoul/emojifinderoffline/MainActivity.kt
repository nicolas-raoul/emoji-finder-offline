package io.github.nicolasraoul.emojifinderoffline

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.edge.aicore.GenerativeAIException
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig
import io.github.nicolasraoul.emojifinderoffline.util.EmojiUtils // Added import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var model: GenerativeModel? = null
    private lateinit var generateButton: Button
    private lateinit var loadingIndicator: android.widget.ProgressBar

    private fun resetUiState() {
        generateButton.isEnabled = true
        loadingIndicator.visibility = android.view.View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainView = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
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
        val emojiDisplay = findViewById<FlexboxLayout>(R.id.emoji_display_container)
        generateButton = findViewById(R.id.generate_button)
        loadingIndicator = findViewById(R.id.loading_indicator)

        generateButton.setOnClickListener {
            val text = inputText.text.toString()
            if (text.isNotEmpty()) {
                generateButton.isEnabled = false
                loadingIndicator.visibility = android.view.View.VISIBLE
                emojiDisplay.removeAllViews() // Clear previous results
                CoroutineScope(Dispatchers.Main).launch {
                    val displayedEmojis = mutableSetOf<String>() // Keep track of displayed emojis
                    try {
                        model!!.generateContentStream("Output a dozen of emojis related to \"$text\". Do not output anything else than emojis.")
                            .collect { chunk ->
                                val chunkText = chunk.text ?: ""
                                val emojisFromChunk = EmojiUtils.filterUniqueEmojis(chunkText)

                                // Iterate through the string of emojis returned by filterUniqueEmojis
                                var k = 0
                                while (k < emojisFromChunk.length) {
                                    val codePoint = emojisFromChunk.codePointAt(k)
                                    val charCount = Character.charCount(codePoint)
                                    val singleEmoji = emojisFromChunk.substring(k, k + charCount)

                                    if (!displayedEmojis.contains(singleEmoji)) {
                                        displayedEmojis.add(singleEmoji)
                                        val emojiTextView = TextView(this@MainActivity).apply {
                                            text = singleEmoji
                                            textSize = 48f // SP value
                                            val paddingDp = 8
                                            val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
                                            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                                            setOnClickListener {
                                                val clickedEmoji = (it as TextView).text.toString()
                                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Copied Emoji", clickedEmoji)
                                                clipboard.setPrimaryClip(clip)
                                                Snackbar.make(mainView, "Copied $clickedEmoji to clipboard", Snackbar.LENGTH_SHORT).show()
                                            }
                                        }
                                        emojiDisplay.addView(emojiTextView)
                                    }
                                    k += charCount
                                }
                            }
                    } catch (e: GenerativeAIException) {
                        Log.e("EmojiSearch", "Error generating emojis", e)
                        emojiDisplay.removeAllViews()
                        val errorTextView = TextView(this@MainActivity).apply {
                            this.text = "Error generating emojis."
                            textSize = 18f
                            val paddingDp = 8
                            val paddingPx = (paddingDp * resources.displayMetrics.density).toInt()
                            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                        }
                        emojiDisplay.addView(errorTextView)
                    } finally {
                        resetUiState()
                    }
                }
            } else {
                emojiDisplay.removeAllViews()
                Snackbar.make(mainView, "Please enter some text.", Snackbar.LENGTH_SHORT).show()
                resetUiState()
            }
        }
    }
}