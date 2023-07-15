package com.example.deepvision1
import android.content.Intent
import com.google.android.filament.utils.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.filament.Skybox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.ByteBuffer
import android.content.Context
import java.io.IOException

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//import com.google.android.filament.animation.AnimationController
class MainActivity : AppCompatActivity() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer
    private lateinit var images: Array<Int>
    companion object {
        init { Utils.init() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        surfaceView = SurfaceView(this).apply{
            setContentView(R.layout.activity_main)
        }
        val glbFileNames = getAllGLBFileNames(this)
        images=Array(glbFileNames.size){R.drawable.deepvisiontech_final}

        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ImageAdapter(images) { position ->
            navigate(position)
        }

    }

    fun getAllGLBFileNames(context: Context): List<String> {
        val assetManager = context.assets
        val glbFileNames = mutableListOf<String>()

        try {
            val fileList = assetManager.list("models") // Assuming "models" is the directory name

            if (fileList != null) {
                for (file in fileList) {
                    if (file.endsWith(".glb")) {
                        glbFileNames.add(file)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return glbFileNames
    }

    fun navigate(view: Int) {
        val intent = Intent(this, Display::class.java)
        intent.putExtra("position",view)
        startActivity(intent)
    }

    private inner class ImageAdapter(private val images: Array<Int>,
                                     private val onItemClick: (position: Int) -> Unit) :
        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageResId = images[position]
            holder.imageView.setImageResource(imageResId)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)

            init {
                imageView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(position)
                    }
                }
            }
        }
    }

}