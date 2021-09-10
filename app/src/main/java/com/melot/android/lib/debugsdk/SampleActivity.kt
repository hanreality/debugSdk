package com.melot.android.lib.debugsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.melot.android.debug.sdk.DebugManager
import com.melot.android.debug.sdk.proxy.IDebugProxy

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        DebugManager.INSTANCE.registerApplication(application)
        DebugManager.INSTANCE.registerDebugProxy(object : IDebugProxy {
            override fun changeServer() {
                Toast.makeText(baseContext, "changeServer", Toast.LENGTH_LONG).show()
            }
        })
        DebugManager.INSTANCE.enable = true

        findViewById<View>(R.id.open).setOnClickListener {
            startActivity(Intent(this, TargetActivity::class.java))
        }
    }
}