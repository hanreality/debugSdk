package com.melot.android.lib.debugsdk

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.melot.android.debug.sdk.DebugManager
import com.melot.android.debug.sdk.model.DebugLoginModel
import com.melot.android.debug.sdk.proxy.DebugConfig
import com.melot.android.debug.sdk.proxy.IDebugProxy

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        DebugManager.INSTANCE.registerApplication(application)
        DebugManager.INSTANCE.registerDebugProxy(object : IDebugProxy {
            override fun changeServer() {
                val dialog = AlertDialog.Builder(DebugManager.INSTANCE.currentActivity as Context)
                    .setTitle("changeServer")
                    .create()
                dialog.show()
            }

            override fun disable() {
                Toast.makeText(baseContext, "disable", Toast.LENGTH_LONG).show()
            }

            override fun debugConfig(): DebugConfig? {
                return null
            }

            override fun quickLogin(id: String, pwd: String) {

            }

            override fun getTestAccounts(): ArrayList<DebugLoginModel> {
                return ArrayList()
            }
        })
        DebugManager.INSTANCE.enable = true

        findViewById<View>(R.id.open).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            startActivity(intent)
        }
    }
}