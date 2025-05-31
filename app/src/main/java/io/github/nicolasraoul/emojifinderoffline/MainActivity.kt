package io.github.nicolasraoul.emojifinderoffline

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
// import android.widget.TextView // Commented out as it might become unused
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
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
        val emojiRecyclerView = findViewById<RecyclerView>(R.id.emoji_recycler_view)
        val generateButton = findViewById<Button>(R.id.generate_button)

        emojiRecyclerView.layoutManager = GridLayoutManager(this, 4) // 4 columns

        generateButton.setOnClickListener {
            val text = inputText.text.toString()
            val emojisList = mutableListOf<String>() // Initialize list for emojis

            if (text.isNotEmpty()) {
                emojisList.clear()
                emojiRecyclerView.adapter = null // Clear previous results by resetting adapter

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        model!!.generateContentStream("Output a dozen of emojis related to \"$text\". Do not output anything else than emojis.")
                            .collect {
                                val incomingText = it.text ?: ""
                                val filteredEmojisString = incomingText.filter { char ->
                                    !char.isLetterOrDigit() && !char.isWhitespace()
                                    // This is a simplified filter. A more robust emoji check might be needed
                                    // if this doesn't capture all emojis or includes unwanted symbols.
                                }.joinToString("")

                                // Add individual emojis to the list, avoiding duplicates
                                filteredEmojisString.forEach { char ->
                                    if (!emojisList.contains(char.toString())) {
                                         emojisList.add(char.toString())
                                    }
                                }
                            }
                        // After the collect loop (still in the coroutine scope on Dispatchers.Main)
                        val emojiAdapter = EmojiAdapter(ArrayList(emojisList)) { clickedEmoji ->
                            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("emoji", clickedEmoji)
                            clipboard.setPrimaryClip(clip)

                            Snackbar.make(emojiRecyclerView, "Copied to clipboard", Snackbar.LENGTH_SHORT).show()
                        }
                        emojiRecyclerView.adapter = emojiAdapter

                    } catch (e: GenerativeAIException) {
                        Log.e("EmojiSearch", "Error generating emojis", e)
                        // emojiDisplay.text = "Error generating emojis." // emojiDisplay is removed
                    }
                }
            } else {
                // emojiDisplay.text = "Please enter some text." // emojiDisplay is removed
            }
        }
    }
}