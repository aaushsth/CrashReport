#README file for Android App Crash Detection and Reporting System


Overview:
This system provides a customizable UI for detecting and reporting app crashes on Android devices. When an app crashes, the system will display a UI that allows the user to report the issue to the developer. This can help developers quickly identify and fix bugs in their app.

Installation:
1. Copy and paste the GlobalExceptionHandler file into your code.
2. Create an Application class that extends android.app.Application().
3. Create a CrashActivity with the desired UI design.
4. In the onCreate() method of your Application class, add the following line of code:

GlobalExceptionHandler.initialize(this, CrashActivity::class.java)

5. Add your Application class in the manifest as "android:name=".Application" in the application tag.

Usage:
To use the system, ensure that you have followed the installation steps above. When an app crash occurs, the system will display a customizable UI that allows the user to report the issue to the developer.

To report the crash log, you can use the example code provided in the CrashActivity::class.java file. You can also add recent API calls while reporting the crash issue, which can help the developer to debug the problem.

The following metrics can be reported(You can add more as required):
1. Crash reason
2. Line number and file name
3. Crash device and version
4. Latest API call request and response

Customization:
To customize the UI, modify the layout file for the CrashActivity class. This file can be found in the res/layout directory of your project.
You can add additional fields or information to the layout as needed.

To report api calls you need to store recent api calls.
To store api call you can add Custom Interceptor in OkHttpClient client builder
For example

```

class ApiLoggingInterceptor(private val prefs: Prefs) : Interceptor {

    @OptIn(DelicateCoroutinesApi::class)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)

        // Store the API call details in a database or log file
        val url = request.url.toString()
        val responseString = response.body!!.string()
        val logModel = LogModel(url, responseString)

        //you can store this request and response in db or shared prefs

        return response.newBuilder()
            .body(responseString.toResponseBody(response.body?.contentType()))
            .build()
    }
}


```

#Reporting recent api
Fetch stored data in CrashActivity and add api info on message body.
Example is shown in CrashActivity


