package com.example.dibage.accountb.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.example.dibage.accountb.R
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.commonView.PopWindowTip
import com.example.dibage.accountb.dao.AccountDao
import com.example.dibage.accountb.dao.DaoSession
import com.example.dibage.accountb.databinding.ActivityMainBinding
import com.example.dibage.accountb.databinding.ActivityMoreBinding
import com.example.dibage.accountb.entitys.Account
import com.example.dibage.accountb.utils.AccountUtils
import com.example.dibage.accountb.utils.EncryUtils
import com.example.dibage.accountb.utils.FileUtils
import com.example.dibage.accountb.utils.SPUtils
import com.example.dibage.accountb.utils.SimpleUtils
import com.example.dibage.accountb.utils.UIUtils
import com.google.gson.Gson
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import es.dmoral.toasty.Toasty
import org.greenrobot.greendao.query.QueryBuilder
import skin.support.SkinCompatManager

class MoreActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMoreBinding

    private var toolbar: Toolbar? = null
    private var switch_finger: Switch? = null
    private var mFingerprintIdentify: FingerprintIdentify? = null
    private var ll_settingpassword: LinearLayout? = null
    private var ll_developing: LinearLayout? = null
    private var ll_create_backup: LinearLayout? = null
    private val SETTING_PASSWORD = 1 //设置密码
    private val MODIFY_PASSWORD = 2 //修改密码
    private var tv_set_pwd: TextView? = null
    private var is_setting_pwd = false
    private var is_setting_finger = false
    private var context: Context? = null
    private var btnShare: Button? = null
    private var permission_state = false
    var daoSession: DaoSession? = null
    lateinit var mAccountDao: AccountDao
    var accountsList: List<Account> = ArrayList()
    var qb: QueryBuilder<Account>? = null
    var popWindowTip: PopWindowTip? = null
    var popWindowTip2: PopWindowTip? = null
    private var ll_recovery: LinearLayout? = null
    private var ll_course: LinearLayout? = null
    private var ll_support: LinearLayout? = null
    private var ll_openource: LinearLayout? = null
    private var ll_project_location: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFBI()
        iniData()
        initView()
        initEvent()
    }

    inner class MyCheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) { //从关到开
//                Toasty.success(context,"开").show();
                if (!mFingerprintIdentify!!.isHardwareEnable) {
                    Toasty.info(this@MoreActivity, "您的设备不支持指纹识别，无法使用此功能").show()
                } else if (!mFingerprintIdentify!!.isRegisteredFingerprint) {
                    Toasty.info(this@MoreActivity, "请在您的设备中录入指纹后再使用此功能").show()
                } else {
                    Toasty.success(this@MoreActivity, "指纹解锁已启用", Toast.LENGTH_SHORT, false)
                        .show()
                    SPUtils.put(this@MoreActivity, "finger_state", true)
                }
            } else if (!isChecked) { //从开到关
                //Toasty.success(context,"关").show();
                popWindowTip = object : PopWindowTip(this@MoreActivity) {
                    override fun clickCancel() {
                        switch_finger!!.setOnCheckedChangeListener(null)
                        switch_finger!!.isChecked = true
                        switch_finger!!.setOnCheckedChangeListener(MyCheckListener())
                    }

                    override fun dismissTodo() {
                        if (SPUtils.get(this@MoreActivity, "finger_state", false) as Boolean) {
                            switch_finger!!.setOnCheckedChangeListener(null)
                            switch_finger!!.isChecked = true
                            switch_finger!!.setOnCheckedChangeListener(MyCheckListener())
                        }
                    }

                    override fun clickConfirm() {
                        if (SPUtils.get(this@MoreActivity, "finger_state", false) as Boolean) {
                            Toasty.success(
                                this@MoreActivity,
                                "指纹解锁已关闭",
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                            SPUtils.put(this@MoreActivity, "finger_state", false)
                            //                            switch_finger.setOnCheckedChangeListener(null);
//                            switch_finger.setChecked(false);
//                            switch_finger.setOnCheckedChangeListener(new MyCheckListener());
                        }
                    }
                }
                popWindowTip!!.setTitleAndContent("指纹解锁", "确定关闭指纹解锁功能？")
                popWindowTip!!.setOutside(false)
                popWindowTip!!.update()
            }
        }
    }

    private fun initView() {
        UIUtils.setToolbar(this@MoreActivity, toolbar, "功能与设置")
        if (is_setting_pwd) {
            tv_set_pwd!!.text = "修改保护密码"
        }
        //在这里还没给switch注册监听
        if (is_setting_finger) switch_finger!!.isChecked = true else switch_finger!!.isChecked =
            false
    }

    private fun iniData() {
        context = applicationContext
        is_setting_pwd = SPUtils.get(context, "is_setting_pwd", false) as Boolean
        is_setting_finger = SPUtils.get(context, "finger_state", false) as Boolean
        daoSession = (application as MyApplication).daoSession
        mAccountDao = daoSession!!.accountDao
        qb = mAccountDao.queryBuilder()
            .orderAsc(AccountDao.Properties.Firstchar, AccountDao.Properties.Username)
        accountsList = qb!!.list()
        accountsList = AccountUtils.orderListAccount(accountsList)
    }

    private fun initEvent() {
        ll_settingpassword!!.setOnClickListener(this)
        btnShare!!.setOnClickListener(this)
        ll_developing!!.setOnClickListener(this)
        ll_create_backup!!.setOnClickListener(this)
        ll_recovery!!.setOnClickListener(this)
        mFingerprintIdentify = FingerprintIdentify(this)
        switch_finger!!.setOnCheckedChangeListener(MyCheckListener())
        ll_course!!.setOnClickListener(this)
        ll_support!!.setOnClickListener(this)
        ll_openource!!.setOnClickListener(this)
        ll_project_location!!.setOnClickListener(this)
        binding.llSkin.setOnClickListener {
            Toasty.info(this, "功能未完善").show()
            return@setOnClickListener

            Toasty.info(this, "替换皮肤").show()
            SkinCompatManager.getInstance()
                .loadSkin("night", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
//            SkinCompatManager.getInstance().loadSkin("", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
        }
        binding.toolbar.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    this,
                    R.color.WhiteText
                ), BlendModeCompat.SRC_ATOP
            )

    }

    private fun initFBI() {
        toolbar = findViewById(R.id.toolbar)
        switch_finger = findViewById(R.id.switch_finger)
        ll_settingpassword = findViewById(R.id.ll_settingpassword)
        tv_set_pwd = findViewById(R.id.tv_set_pwd)
        btnShare = findViewById(R.id.btnShare)
        ll_developing = findViewById(R.id.ll_developing)
        ll_create_backup = findViewById(R.id.ll_create_backup)
        ll_recovery = findViewById(R.id.ll_recovery)
        ll_course = findViewById(R.id.ll_course)
        ll_support = findViewById(R.id.ll_support)
        ll_openource = findViewById(R.id.ll_opensource)
        ll_project_location = findViewById(R.id.ll_project_location)
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.ll_settingpassword -> {
                is_setting_pwd = SPUtils.get(context, "is_setting_pwd", false) as Boolean
                if (is_setting_pwd) { //设置过密码则跳转到修改密码界面
                    intent = Intent(this@MoreActivity, ModifyPasswordActivity::class.java)
                    startActivity(intent)
                } else { //没有设置过密码则跳转到设置密码页面
                    intent = Intent(this@MoreActivity, SettingPasswordActivity::class.java)
                    startActivityForResult(intent, SETTING_PASSWORD)
                }
            }

            R.id.btnShare -> {
                intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "账号册子：一个在本地记录、保存账号密码的App，增加了添加证件照的功能，保护个人隐私。  " +
                            "https://github.com/SilenceStar/AccountB"
                )
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "分享账号册子给朋友"))
            }

            R.id.ll_developing -> //                intent = new Intent(MoreActivity.this, ModifyPasswordActivity.class);
//                startActivity(intent);
                Toasty.success(context!!, "导出txt、excel功能正在开发中。", Toast.LENGTH_SHORT, false)
                    .show()

            R.id.ll_course -> Toasty.success(context!!, "教程还没写。", Toast.LENGTH_SHORT, false)
                .show()

            R.id.ll_support -> Toasty.success(context!!, "谢谢你的支持。", Toast.LENGTH_SHORT, false)
                .show()

            R.id.ll_create_backup -> {
                //首先，检查权限，然后检查是否 设置保护密码和是否有数据
                //全部通过的话再弹出PopWindow
                checkPermissions()
                if (permission_state) {
                    if (checkPwdData()) {
                        popWindowTip = object : PopWindowTip(this@MoreActivity) {
                            override fun clickCancel() {}
                            override fun dismissTodo() {}
                            override fun clickConfirm() {
                                generateBackup()
                            }
                        }
                        popWindowTip!!.setTitleAndContent(
                            getString(R.string.generate_backup),
                            getString(R.string.generate_backup_tip),
                            "开始备份"
                        )
                    }
                }
            }

            R.id.ll_recovery -> {
                checkPermissions()
                if (permission_state) {
                    intent = Intent(this@MoreActivity, RecorveyActivity::class.java)
                    startActivityForResult(intent, RECORVRY_DATA)
                }
            }

            R.id.ll_opensource -> {
                intent = Intent(this@MoreActivity, OpenSourceActivity::class.java)
                startActivityForResult(intent, RECORVRY_DATA)
            }

            R.id.ll_project_location -> {
                val webSite = "https://github.com/SilenceStar/AccountB"
                val uri = Uri.parse(webSite) //指定网址
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
            }


        }
    }

    //从备份文件中恢复数据
    private fun recorveyData() {}

    //检查是否 设置保护密码 和 是否有数据
    fun checkPwdData(): Boolean {
        return if (!is_setting_pwd) {
            Toasty.warning(context!!, "请先设置保护密码").show()
            false
        } else if (accountsList.size <= 0) {
            Toasty.warning(context!!, "没有数据可以导出哦").show()
            false
        } else true
    }

    //检查是否拥有文件读写权限
    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MoreActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            permission_state = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission_state = true
            } else { //用户点击拒绝
                permission_state = false
                Toasty.warning(context!!, "需要访问文件权限才能进行操作").show()
            }
        }
    }

    //生成备份文件
    private fun generateBackup() {
        Log.e(TAG, accountsList.toString())
        val gson = Gson()
        val jsonString = gson.toJson(accountsList)
        //        Log.e(TAG,"json格式的List："+jsonString);
        val pwd = SPUtils.get(context, "pwd_encrypt", "diabge") as String
        val key = EncryUtils.getInstance().decryptString(pwd, SimpleUtils.DEFAULT_KEY)
        //        String key = AESUtil.aes(pwd, SimpleUtils.DEFAULT_KEY, Cipher.DECRYPT_MODE);
//        Log.e(TAG,"加密用的key："+key);
//        String backupString = AESUtil.aes(jsonString, key, Cipher.ENCRYPT_MODE);
        val backupString =
            EncryUtils.getInstance().decryptString(jsonString, SimpleUtils.DEFAULT_KEY)
        val dir = Environment.getExternalStorageDirectory().absolutePath + "/账号册子/"
        val path = dir + SimpleUtils.getNameByDate() + ".bkp"
        Log.e(TAG, "目录：$dir")
        if (FileUtils.saveData(path, backupString)) {
            //Toasty.success(MoreActivity.this, "存入成功").show();
            popWindowTip2 = object : PopWindowTip(this@MoreActivity) {
                override fun clickCancel() {}
                override fun dismissTodo() {}
                override fun clickConfirm() {
                }
            }
            popWindowTip2!!.setTitleAndContent("备份成功", "文件存放目录为：\n$path")
        } else Toasty.warning(this@MoreActivity, "存入失败").show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SETTING_PASSWORD -> {
                    is_setting_pwd = true
                    tv_set_pwd!!.text = "修改保护密码"
                }

                RECORVRY_DATA -> finish()
            }
        }
    }


    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

    companion object {
        private const val TAG = "MoreActivity"
        const val RECORVRY_DATA = 3 //导入账号
    }
}