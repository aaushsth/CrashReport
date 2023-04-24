package com.ayush.crashreport

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ayush.crashreport.databinding.ActivityCrashBinding
import com.google.gson.Gson
import org.json.JSONObject

@SuppressLint("SetTextI18n")

class CrashActivity : AppCompatActivity() {
  //  private val prefs: Prefs by inject()

    private lateinit var binding: ActivityCrashBinding
    private var errorData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashBinding.inflate(layoutInflater)
        GlobalExceptionHandler.getThrowableFromIntent(intent).let {
            errorData = Gson().toJson(it)
        }
        setOnClickListeners()
        setContentView(binding.root)
    }

    private fun setOnClickListeners() {
        binding.bReport.setOnClickListener {
            generateEmailTemplate()
        }
        binding.bRestartApp.setOnClickListener {
          //  prefs.clearPrefs()
            finishAffinity()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun generateEmailTemplate() {
        try {
            val subject = "Application Crash Report"
            val recipient = "developerEmail@gmail.com"
            val message =
                "Dear Developer,\n\nI would like to report a crash that occurred in the application. Below is the stack trace for your reference:\n\n"

            val formattedStackTrace = StringBuilder()
            val stackTraceObj = JSONObject(errorData)

            if (stackTraceObj.has("cause")) {
                val cause = stackTraceObj.getJSONObject("cause")
                val causeDetailMessage = cause.getString("detailMessage")
                formattedStackTrace.append("Cause Detail: $causeDetailMessage\n\n")
                val stackTraceArray = cause.getJSONArray("stackTrace")
                formattedStackTrace.append("Stack Trace:\n")
                for (i in 0 until stackTraceArray.length()) {
                    val stackTraceItem = stackTraceArray.getJSONObject(i)
                    val declaringClass =  if(stackTraceItem.has("declaringClass")) stackTraceItem.getString("declaringClass") else ""
                    val fileName = if(stackTraceItem.has("fileName")) stackTraceItem.getString("fileName") else ""
                    val lineNumber =if(stackTraceItem.has("lineNumber")) stackTraceItem.getInt("lineNumber") else 1
                    val methodName = if(stackTraceItem.has("methodName")) stackTraceItem.getString("methodName") else ""
                    formattedStackTrace.append("$declaringClass.$methodName($fileName:$lineNumber)\n")
                }
            } else {
                Log.e("CrashActivity","No value for cause")
            }

            formattedStackTrace.append("\n")
            if (stackTraceObj.has("detailMessage")) {
                val detailMessage = stackTraceObj.getString("detailMessage")
                formattedStackTrace.append("$detailMessage\n")
            }
            if (stackTraceObj.has("stackTrace")) {
                val stackTraceArray2 = stackTraceObj.getJSONArray("stackTrace")
                for (i in 0 until stackTraceArray2.length()) {
                    val stackTraceItem = stackTraceArray2.getJSONObject(i)
                    val declaringClass =  if(stackTraceItem.has("declaringClass")) stackTraceItem.getString("declaringClass") else ""
                    val fileName = if(stackTraceItem.has("fileName")) stackTraceItem.getString("fileName") else ""
                    val lineNumber =if(stackTraceItem.has("lineNumber")) stackTraceItem.getInt("lineNumber") else 1
                    val methodName = if(stackTraceItem.has("methodName")) stackTraceItem.getString("methodName") else ""
                    formattedStackTrace.append("$declaringClass.$methodName($fileName:$lineNumber)\n")
                }
            }


            val regardsMessage =
                "\n\nPlease let me know if you require any further information to resolve this issue.\n\nThank you."
            val deviceName = "\n\nDevice: ${android.os.Build.MODEL}"
            val androidVersion = "\n\nVersion: ${android.os.Build.VERSION.RELEASE}"
           // val logData = prefs.logData
            //You can store latest 5 api calls and add this here
          /*  val logDatString =  if (logData.isNotEmpty()){
                "\n\n latest 5 login Data:\n\n ${logData.toJson()}"
            }else{
                 "\n\n latest 5 login Data:\n\n "
            }*/
            val emailBody =
                message + formattedStackTrace.toString() + "logDatString" + deviceName + androidVersion + regardsMessage


            val uri = Uri.parse("mailto:")
                .buildUpon()
                .appendQueryParameter("subject", "$subject [${getString(R.string.app_name)}]")
                .appendQueryParameter("to", recipient)
                .appendQueryParameter("body", emailBody)
                .build()

            val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(Intent.createChooser(emailIntent, "mail using:"))
        } catch (e: Exception) {
            Log.e("CrashActivity","exception:${e.localizedMessage}")
        }
    }

    companion object {
        private const val TAG = "CrashActivity"
    }

}