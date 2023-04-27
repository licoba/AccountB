package com.example.dibage.accountb.commonView

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import com.example.dibage.accountb.R
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.databinding.PopTipBinding
import com.example.dibage.accountb.utils.UIUtils
import razerdp.basepopup.BasePopupWindow

/**
 * PopupWindow显示需要的参数：activity,pop的标题以及内容
 */
class CommPopTip(context: Context) : BasePopupWindow(context) {

    private val mBinding = PopTipBinding.inflate(LayoutInflater.from(context))
    private var mOkToDo: (()->Unit)? = null
    private var mCancelToDo: (()->Unit)? = null
    init {
        contentView = mBinding.root
        mBinding.btnOk.setOnClickListener {

            mOkToDo?.invoke()
            dismiss()
        }
        mBinding.btnCancel.setOnClickListener {
            mCancelToDo?.invoke()
            dismiss()
        }
    }

    fun setContent(text:String){
        mBinding.popMessage.text = text
    }

    fun  setTitle(text: String){
        mBinding.popTitle.text = text
    }


    fun setOkToDo(toDo:(()->Unit)?){
        mOkToDo = toDo
    }



    fun setCancelToDo(toDo:(()->Unit)?){
        mCancelToDo = toDo
    }
}