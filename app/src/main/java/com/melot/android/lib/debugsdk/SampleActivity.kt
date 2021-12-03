package com.melot.android.lib.debugsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.melot.android.debug.sdk.DebugManager

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        DebugManager.INSTANCE.registerApplication(application)
        DebugManager.INSTANCE.registerDebugProxy(SampleDebugProxy())
        DebugManager.INSTANCE.enable = true

        findViewById<View>(R.id.open).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            intent.putExtra("hell0", 1233)
            startActivity(intent)
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_content, BlankFragment.newInstance("param1", "param2"))
            .commit()
    }
}