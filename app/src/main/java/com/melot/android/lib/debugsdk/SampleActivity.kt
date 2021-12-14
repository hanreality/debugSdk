package com.melot.android.lib.debugsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import com.melot.android.debug.sdk.util.DevelopUtil

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

        findViewById<View>(R.id.popup).setOnClickListener {

            val popupWindow = PopupWindow(
                LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null),
                DevelopUtil.dp2px(200f),
                DevelopUtil.dp2px(60f)
            )
            popupWindow.isOutsideTouchable = true
            popupWindow.showAtLocation(findViewById<View>(R.id.alert), Gravity.CENTER_HORIZONTAL, 0, 0)
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_content, BlankFragment.newInstance("param1", "param2"))
            .commit()
    }
}