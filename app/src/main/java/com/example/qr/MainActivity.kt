package com.example.qr

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.qr.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private val option = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        ).build()
    private val requestImageCaptureCode = 1
    private var imageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.button.setOnClickListener{
            Log.d("Button","Clicked")
            takeImage()
            binding.textView.text = ""
            detectImage()
        }
    //setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
    }
    private fun setOnClickListeners() {
        Log.d("Buttonq","Clicked")
        binding.button.setOnClickListener{
            Log.d("Button","Clicked")
            takeImage()
            binding.textView.text = ""
            detectImage()
        }

    }

    private fun detectImage() {
       if(imageBitmap!=null){
           val mlImage = InputImage.fromBitmap(imageBitmap!!,0)
           val scanner = BarcodeScanning.getClient(option)
           scanner.process(mlImage).addOnSuccessListener { barCodes->
               if(barCodes.toString() == "[]"){
                   binding.textView.text = "No QR Code Found"
               }else{
                   for(barcode in barCodes){
                       when(barcode.valueType){
                           Barcode.TYPE_WIFI->{
                               val ssid = barcode.wifi?.ssid
                               val password = barcode.wifi?.password
                               val type = barcode.wifi?.encryptionType
                                 binding.textView.text = "SSID: $ssid\nPassword: $password\nType: $type"
                           }
                           Barcode.FORMAT_QR_CODE->{
                                val title = barcode.url?.title
                               val url = barcode.url?.url
                                 binding.textView.text = "Title: $title\nURL: $url"
                           }
                       }
                   }
               }
           }
       }else Toast.makeText(this, "Invalid image or select photo", Toast.LENGTH_SHORT).show()
    }

    private fun takeImage(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, requestImageCaptureCode)
        } catch (e: ActivityNotFoundException) {
            Log.e("Error", e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestImageCaptureCode && resultCode == RESULT_OK){
            imageBitmap = data?.extras?.get("data") as Bitmap
            if(imageBitmap!=null)binding.imageView.setImageBitmap(imageBitmap)

        }
    }
}