package com.example.myapplication

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    lateinit var bmiButton:MaterialButton
    lateinit var et_height:TextInputLayout
    lateinit var et_weight:TextInputLayout
    lateinit var result: TextView
    lateinit var btnDial:MaterialButton
    lateinit var btnCall:MaterialButton
    lateinit var icCancel:ImageView
    lateinit var pgBar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bmiButton = findViewById(R.id.cal_bmi)
        et_height = findViewById(R.id.height)
        et_weight = findViewById(R.id.weight)
        result = findViewById(R.id.result)

        bmiButton.setOnClickListener{
            calculateBMI()
        }
        et_height.editText?.addTextChangedListener {
            et_height.error=null
        }
       et_weight.editText?.addTextChangedListener {
           et_weight.error=null
       }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
          when(item.itemId){
              R.id.op_aboutBmi->{
                  setContentView(R.layout.activity_about_bmi)
                  var webView: WebView = findViewById(R.id.webView)
                  pgBar=findViewById(R.id.pgBar)
                  webView.loadUrl("https://en.wikipedia.org/wiki/Body_mass_index")
                  webView.webViewClient = object : WebViewClient() {
                      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                          pgBar.visibility = View.VISIBLE
                          pgBar.progress = 0
                      }

                      override fun onPageFinished(view: WebView?, url: String?) {
                          pgBar.visibility = View.GONE
                      }

                      fun onProgressChanged(view: WebView?, newProgress: Int) {
                          pgBar.progress = newProgress
                      }
                  }

              }

              R.id.op_bmiChart->{
                  val intent=Intent(this@MainActivity,BmiChartActivity::class.java)
                  startActivity(intent)
              }

              R.id.op_aboutDev->{
                  val intent=Intent(this@MainActivity,AboutDeveloperActivity::class.java)
                  startActivity(intent)
              }

              R.id.op_contactUs->{
                  val dialog=Dialog(this)
                  dialog.setContentView(R.layout.activity_contact_us)

                  btnDial=dialog.findViewById(R.id.btn_dial)
                  btnCall=dialog.findViewById(R.id.btn_call)
                  icCancel=dialog.findViewById(R.id.ic_cancel)

                  btnDial.setOnClickListener {
                      val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9568853442"))
                      startActivity(intent)
                      dialog.dismiss()
                  }

                  btnCall.setOnClickListener {
                      val phoneNumber = "9568853442"
                      makePhoneCall(phoneNumber)
                      dialog.dismiss()
                  }

                  icCancel.setOnClickListener {
                      dialog.dismiss()
                  }

                  dialog.setCancelable(false)
                  dialog.show()
              }
          }

        return super.onOptionsItemSelected(item)
    }


//// Bmi calculator logic function
fun calculateBMI() {
    if (bmiButton.text == "Calculate BMI") {
        val weight = et_weight.editText?.text.toString()
        val height = et_height.editText?.text.toString()

        if (weight.isEmpty()) {
            et_weight.error = "*required field"
        } else {
            et_weight.error = null
        }

        if (height.isEmpty()) {
            et_height.error = "*required field"
        } else {
            et_height.error = null
        }

        if (weight.isNotEmpty() && height.isNotEmpty()) {
            val myHeight = height.toDouble()
            val myWeight = weight.toDouble()
            val bmi = myWeight / ((myHeight / 100) * (myHeight / 100))
            val status = BMICategory(bmi)
            bmiButton.text = "Reset"
            result.text = "Your BMI: %.2f\nStatus: %s".format(bmi, status)
            result.visibility = TextView.VISIBLE
        }
    } else if (bmiButton.text == "Reset") {
        et_height.editText?.text?.clear()
        et_weight.editText?.text?.clear()
        result.visibility = TextView.INVISIBLE
        bmiButton.text = "Calculate BMI"
    }
}


    //// status according to bmi value
    fun BMICategory(BMI: Double): String {
        return when {
            BMI < 18.5 -> "Underweight"
            BMI >= 18.5 && BMI < 24.9 -> "Normal Weight"
            BMI >= 25.0 && BMI < 29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    ////call  implicite permission code for phone call

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CALL_PHONE),100)
        } else {

            val callIntent = Intent(Intent.ACTION_CALL,Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = "9568853442"
                makePhoneCall(phoneNumber)
            } else {
                Toast.makeText(this@MainActivity,"Access Denied", Toast.LENGTH_LONG).show()

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.webView)
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setTitle("Exit")
            dialog.setCancelable(false)
            dialog.setMessage("Do you want to close this app?")
            dialog.setPositiveButton("Yes") { _, _ ->
                finish()
            }
            dialog.setNegativeButton("No") { _, _ ->

            }
            dialog.show()
        }
    }


}

