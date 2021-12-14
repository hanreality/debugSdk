package com.melot.android.lib.debugsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        findViewById<View>(R.id.open).setOnClickListener {
            val intent = Intent(this, TargetActivity::class.java)
            intent.putExtra("hell0", 1233)
            startActivity(intent)
        }

        findViewById<View>(R.id.alert).setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("我是弹窗标题")
                .setMessage("我是弹窗内容")
                .create()
            alertDialog.show()
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_content, BlankFragment.newInstance("param1", "param2"))
            .commit()
    }
}