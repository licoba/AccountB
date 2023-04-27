package com.example.dibage.accountb.commonView

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.example.dibage.accountb.R
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.utils.UIUtils

/**
 * PopupWindow显示需要的参数：activity,pop的标题以及内容
 */
abstract class PopWindowTip {
    var mPopTip: PopupWindow
    private val context = MyApplication.context
    private var activity: Activity? = null
    private var title: String? = null
    private var content: String? = null
    private var contentView: View? = null

    constructor() {
        mPopTip = PopupWindow()
    }

    fun dismiss() {
        mPopTip.dismiss()
    }

    fun setOutside(state: Boolean) {
        mPopTip.isOutsideTouchable = state
    }

    fun setFocus(state: Boolean) {
        mPopTip.isFocusable = state
    }

    fun setBackGround() {
        mPopTip.setBackgroundDrawable(BitmapDrawable())
    }

    fun update() {
        mPopTip.update()
    }

    fun setTitleAndContent(title: String?, content: String?) {
        this.title = title
        this.content = content
        UIUtils.darkenBackgroud(activity, 0.5f)
        mPopTip.showAtLocation(contentView, Gravity.CENTER, 0, 0)
        val pop_title = contentView!!.findViewById<TextView>(R.id.pop_title)
        val pop_message = contentView!!.findViewById<TextView>(R.id.pop_message)
        val btn_cancel = contentView!!.findViewById<Button>(R.id.btn_cancel)
        val btn_submit = contentView!!.findViewById<Button>(R.id.btn_submit)
        pop_title.text = title
        pop_message.text = content
        btn_cancel.setOnClickListener { v: View? ->
            mPopTip.dismiss()
            clickCancel()
        }
        btn_submit.setOnClickListener { v: View? ->
            clickConfirm()
            mPopTip.dismiss()
        }
    }

    fun setTitleAndContent(title: String?, content: String?, confirmText: String?) {
        UIUtils.darkenBackgroud(activity, 0.5f)
        this.title = title
        this.content = content
        mPopTip.showAtLocation(contentView, Gravity.CENTER, 0, 0)
        val pop_title = contentView!!.findViewById<TextView>(R.id.pop_title)
        val pop_message = contentView!!.findViewById<TextView>(R.id.pop_message)
        val btn_cancel = contentView!!.findViewById<Button>(R.id.btn_cancel)
        val btn_submit = contentView!!.findViewById<Button>(R.id.btn_submit)
        pop_title.text = title
        pop_message.text = content
        btn_submit.text = confirmText
        btn_cancel.setOnClickListener {
            mPopTip.setOnDismissListener(null)
            mPopTip.dismiss()
            clickCancel()
        }
        btn_submit.setOnClickListener {
            mPopTip.setOnDismissListener(null)
            mPopTip.dismiss() //这句话一定要放在前面，不然会造成PopWindow不及时变色
            clickConfirm()
        }
    }

    protected abstract fun clickCancel()

    constructor(activity: Activity) {
        this.activity = activity
        contentView = LayoutInflater.from(activity).inflate(R.layout.pop_tip, null)
        mPopTip = PopupWindow(
            contentView,
            activity.windowManager.defaultDisplay.width - 200,
            WindowManager.LayoutParams.WRAP_CONTENT, true
        )
        mPopTip.setOnDismissListener {
            UIUtils.darkenBackgroud(activity, 1.0f)
            dismissTodo()
        }
    }

    protected abstract fun dismissTodo()
    abstract fun clickConfirm()
}