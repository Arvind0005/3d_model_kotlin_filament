package com.example.deepvision1
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Display : AppCompatActivity()  {
    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer
    private val images = arrayOf(R.drawable.wallpaper, R.drawable.image2, R.drawable.image3)
    companion object {
        init { Utils.init() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val paramValue = intent.getStringExtra("position")
        System.out.println(paramValue.toString());
        var namei ="Bee"
        if(paramValue?.toIntOrNull()==0)
            namei="DamagedHelmet"
        else
            namei="Bee"
        super.onCreate(savedInstanceState)
        surfaceView = SurfaceView(this).apply{
            setContentView(this)
        }
        // setContentView(R.layout.activity_main)
        choreographer = Choreographer.getInstance()
        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)

        //display
        loadGltf(namei)
        modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)
        loadEnvironment("venetian_crossroads_2k")


    }
    private val frameCallback = object : Choreographer.FrameCallback {
        // private val startTime = System.nanoTime()
        private var currentAnimationIndex = 0
        private val startTime = System.nanoTime()
        private var seconds = 0.0
        var count=0.0
        override fun doFrame(currentTime: Long) {
            val elapsedSeconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    // if(currentAnimationIndex==0)
                    System.out.println(seconds);
                    System.out.println(elapsedSeconds);
                    System.out.println(currentAnimationIndex)
                    applyAnimation(currentAnimationIndex, seconds.toFloat())
                    val currentAnimationDuration =  getAnimationDuration(currentAnimationIndex)

                    if ((seconds-count) >= currentAnimationDuration.toFloat()) {
                        count=count+ currentAnimationDuration.toFloat();
                        currentAnimationIndex = (currentAnimationIndex + 1) % animationCount
                        seconds = 0.0
                    }
                    seconds=elapsedSeconds
                }
                updateBoneMatrices()
            }
            modelViewer.render(currentTime)
        }
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
        //modelViewer.startAnimation()
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
        // modelViewer.stopAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameCallback)
        //modelViewer.release()
    }

    private fun loadGltf(name: String) {
        val buffer = readAsset("models/${name}.glb")
        modelViewer.loadModelGltf(buffer) { uri -> readAsset("models/$uri") }
        modelViewer.transformToUnitCube()
    }
    private fun readAsset(assetName: String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    private fun loadEnvironment(ibl: String) {
        // Create the indirect light source and add it to the scene.
        var buffer = readAsset("envs/$ibl/${ibl}_ibl.ktx")
        KtxLoader.createIndirectLight(modelViewer.engine, buffer).apply {
            intensity = 50_000f
            modelViewer.scene.indirectLight = this
        }

        // Create the sky box and add it to the scene.
        buffer = readAsset("envs/$ibl/${ibl}_skybox.ktx")
        KtxLoader.createSkybox(modelViewer.engine, buffer).apply{
            modelViewer.scene.skybox = this
        }
    }
}