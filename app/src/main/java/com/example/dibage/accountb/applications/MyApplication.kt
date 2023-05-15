package com.example.dibage.accountb.applications

import android.app.Application
import android.content.Context
import com.example.dibage.accountb.R
import com.example.dibage.accountb.dao.DaoMaster
import com.example.dibage.accountb.dao.DaoMaster.DevOpenHelper
import com.example.dibage.accountb.dao.DaoSession
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.MessageDialog
import com.tencent.mmkv.MMKV
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater


/**
 * Created by licoba on 2018/3/27.
 */
//初始化数据库和greenDAO核心类
class MyApplication : Application() {
    lateinit var daoSession: DaoSession

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        val helper = DevOpenHelper(this, "account-db")
        val db = helper.writableDb
        daoSession = DaoMaster(db).newSession()
        val rootDir = MMKV.initialize(this)
        println("mmkv root: $rootDir")
        DialogX.init(this)
        //设置全局 MessageDialog 入场动画
        MessageDialog.overrideEnterAnimRes = R.anim.anim_dialogx_default_enter
        //设置全局 MessageDialog 出场动画
        MessageDialog.overrideExitAnimRes = R.anim.anim_dialogx_default_exit
        DialogX.implIMPLMode = DialogX.IMPL_MODE.WINDOW

//        SkinCompatManager.withoutActivity(this)
//            .addInflater(SkinAppCompatViewInflater()) // 基础控件换肤初始化
//            .addInflater(SkinMaterialViewInflater()) // material design 控件换肤初始化[可选]
//            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout 控件换肤初始化[可选]
//            .addInflater(SkinCardViewInflater()) // CardView v7 控件换肤初始化[可选]
//            .setSkinWindowBackgroundEnable(false) // 关闭windowBackground换肤，默认打开[可选]
//            .loadSkin()
    }



    companion object {
        @JvmStatic
        var instance: MyApplication? = null
            private set
        var context: Context? = null
            private set
    }



}