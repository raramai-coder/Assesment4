package com.example.mtnlessom1

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private val tag: String = "Tag1"

class MainActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG: String = "MainActivity"
    private val REQUEST_CODE = 100
    private lateinit var image: ImageView
    private var imageUri: Uri? = null
    private lateinit var currentPhotoPath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build()

        loadAd()


        val playAdbtn = findViewById<Button>(R.id.btnPlayAd)

        playAdbtn.setOnClickListener {

            showInterstitial()

        }

        val takePictureBtn = findViewById<Button>(R.id.btnTakePicture)

        takePictureBtn.setOnClickListener {
            this.capturePhoto()
        }

        image = findViewById<ImageView>(R.id.imageView)

        val loadPicturebtn = findViewById<Button>(R.id.btnLOADPicture)

        loadPicturebtn.setOnClickListener {
            this.loadPicture()
        }

        val sharePicturebtn = findViewById<Button>(R.id.sharePicture)

        sharePicturebtn.setOnClickListener {
            this.sharePicture()
        }


    }

    private fun sharePicture(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_ORIGINATING_URI, image.setImageURI(imageUri).toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent,null)
        startActivity(shareIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timestamp}_",
                ".jpg",
                storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun loadPicture(){
        val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery,REQUEST_CODE)
    }

    private fun capturePhoto(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent,REQUEST_CODE)

        /*Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            cameraIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                }catch (ex: IOException){
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                    startActivityForResult(cameraIntent,REQUEST_CODE)
                }
            }
        }*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data!=null){
            image.setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }

    private fun newActivity(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }




    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                    newActivity()

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                 override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                     mInterstitialAd = null;
                    // Called when ad is dismissed.
                }

            }
            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()

        }
    }
 }



