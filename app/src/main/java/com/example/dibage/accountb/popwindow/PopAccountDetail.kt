package com.example.dibage.accountb.popwindow

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.dibage.accountb.R
import com.example.dibage.accountb.base.BaseActivity
import com.example.dibage.accountb.databinding.PopDetailBinding
import com.example.dibage.accountb.entitys.Account
import es.dmoral.toasty.Toasty
import razerdp.basepopup.BasePopupFlag
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.ScaleConfig


/**
 * 账号详情弹窗
 */
class PopAccountDetail(context: Context, account: Account) : BasePopupWindow(context) {

    private val mBinding = PopDetailBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = mBinding.root
        popupGravity = Gravity.BOTTOM
        setOverlayNavigationBar(true)
        setOverlayNavigationBarMode(BasePopupFlag.OVERLAY_MASK)
        setOverlayNavigationBarMode(BasePopupFlag.OVERLAY_CONTENT)

        val tv1: TextView = contentView.findViewById(R.id.tv_description)
        val tv2: TextView = contentView.findViewById(R.id.tv_username)
        val tv3: TextView = contentView.findViewById(R.id.tv_password)
        val tv4: TextView = contentView.findViewById(R.id.tv_remarks)

        tv1.text = account.description
        tv2.text = account.username
        tv3.text = account.password
        tv4.text = account.remark.ifEmpty { "无" }
        val layout1 = contentView.findViewById<ViewGroup>(R.id.layout1)
        val layout2 = contentView.findViewById<ViewGroup>(R.id.layout2)
        val layout3 = contentView.findViewById<ViewGroup>(R.id.layout3)
        layout1.setOnClickListener {
            val cmb = context
                .getSystemService(BaseActivity.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = account.username
            Toasty.success(context, "账号已复制", Toast.LENGTH_SHORT, true).show()
        }
        layout2.setOnClickListener {
            val cmb = context
                .getSystemService(BaseActivity.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = account.password
            Toasty.success(context, "密码已复制", Toast.LENGTH_SHORT, true).show()
        }
        layout3.setOnClickListener {
            val cmb = context
                .getSystemService(BaseActivity.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = account.remark
            Toasty.success(context, "备注已复制", Toast.LENGTH_SHORT, true).show()
        }
    }


    override fun onCreateShowAnimator(): Animator {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 200 // 设置动画时间为 100 毫秒
        // 设置差值器，使得动画变化速率先慢后快
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            displayAnimateView.translationY = displayAnimateView.height * value
        }
        return animator
    }


    override fun onCreateDismissAnimator(): Animator {
        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.duration = 200 // 设置动画时间为 100 毫秒
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            displayAnimateView.translationY = displayAnimateView.height * value
        }
        return animator
    }

}