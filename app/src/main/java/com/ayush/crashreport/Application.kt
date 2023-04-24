package com.ayush.crashreport


/**
 * Created by Ayush Shrestha$ on 2023/4/21$.
 */
class Application:android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, CrashActivity::class.java)
    }
}