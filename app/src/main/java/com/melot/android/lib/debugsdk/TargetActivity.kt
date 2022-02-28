package com.melot.android.lib.debugsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TargetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

//        supportFragmentManager
//            .beginTransaction()
//            .add(R.id.fragment_content, BlankFragment.newInstance("param1", "param2"))
//            .commit()
    }
}