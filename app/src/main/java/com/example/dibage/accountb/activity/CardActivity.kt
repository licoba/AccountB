package com.example.dibage.accountb.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.example.dibage.accountb.R
import com.example.dibage.accountb.adapters.RecycleAdapter
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.dao.CardDao
import com.example.dibage.accountb.dao.DaoSession
import com.example.dibage.accountb.entitys.Card
import com.example.dibage.accountb.utils.SPUtils
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import es.dmoral.toasty.Toasty
import org.greenrobot.greendao.query.QueryBuilder

/**
 * 证件夹页面
 */
class CardActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var ll_empty: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: RecycleAdapter? = null
    private var btn_add: ImageView? = null
    lateinit var daoSession: DaoSession
    lateinit var cardDao: CardDao
    lateinit var qb: QueryBuilder<Card>
    var cardList: MutableList<Card> = ArrayList()
    private var context: Context? = null
    private var dialog: Dialog? = null
    private var tv_tip: TextView? = null
    private var mFingerprintIdentify: FingerprintIdentify? = null
    private var finger_state = false
    private var is_setting_pwd = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        initFBI()
        initData()
        initView()
        initEvent()
    }

    private fun initData() {
        context = this
        daoSession = (application as MyApplication).daoSession
        cardDao = daoSession.getCardDao()
        qb = cardDao.queryBuilder().orderDesc(CardDao.Properties.Id)
        cardList.clear()
        cardList.addAll(qb.list())
        finger_state = SPUtils.get(context, "finger_state", false) as Boolean
        is_setting_pwd = SPUtils.get(context, "is_setting_pwd", false) as Boolean
    }

    private fun initEvent() {
        btn_add!!.setOnClickListener {
            val intent = Intent(this@CardActivity, AddPhotoActivity::class.java)
            intent.putExtra("fromAty", "CardActivity")
            startActivity(intent)
        }
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                if (finger_state && is_setting_pwd) {
                    //因为证件照更为隐秘，所以需要再次验证指纹。
                    showFingerDialo(position)
                } else {
                    val intent = Intent(this@CardActivity, CardDetailActivity::class.java)
                    intent.putExtra("card", cardList[position])
                    startActivity(intent)
                }
            }
        })
    }

    private fun showFingerDialo(position: Int) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_finger_tip, null)
        tv_tip = view.findViewById(R.id.finger_tip)
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setView(view)
        if (dialog == null)
            dialog = builder.create()
        dialog!!.show()
        dialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowManager = windowManager
        val display = windowManager.defaultDisplay
        val lp = dialog!!.window.attributes
        lp.width = (display.width * 0.75).toInt() //设置宽度
        dialog!!.window.attributes = lp
        startValidate(position)
    }

    //开始指纹验证
    private fun startValidate(position: Int) {
        mFingerprintIdentify = FingerprintIdentify(this) // 构造对象
        val listener = mFingerprintIdentifyListener()
        listener.position = position
        mFingerprintIdentify!!.startIdentify(3, mFingerprintIdentifyListener()) // 开始验证指纹识别
    }

    inner class mFingerprintIdentifyListener : BaseFingerprint.IdentifyListener {
        var position = 0

        override fun onSucceed() {
            tv_tip!!.text = "验证成功 √"
            tv_tip!!.visibility = View.VISIBLE
            Handler().postDelayed({
                val intent = Intent(this@CardActivity, CardDetailActivity::class.java)
                intent.putExtra("card", cardList[position])
                startActivity(intent)
                dialog!!.dismiss()
            }, 500)
        }

        // 指纹不匹配，并返回可用剩余次数并自动继续验证
        override fun onNotMatch(availableTimes: Int) {
            tv_tip!!.text = "指纹不匹配，还有" + availableTimes + "次机会"
            tv_tip!!.visibility = View.VISIBLE
        }

        // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
        // isDeviceLocked 表示指纹硬件是否被暂时锁定
        override fun onFailed(isDeviceLocked: Boolean) {
            Toasty.error(context!!, "指纹不匹配，请确保为本人操作！").show()
            if (isDeviceLocked) {
                Toasty.error(context!!, "失败次数过多，指纹识别被暂时锁定（30秒）").show()
                mFingerprintIdentify!!.cancelIdentify()
            }
            dialog!!.dismiss()
        }

        override fun onStartFailedByDeviceLocked() {
            Toasty.error(context!!, "硬件被锁定，30秒无法使用指纹识别").show()
        }
    } //end mFingerprintIdentifyListener

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.title = "证件夹"
        toolbar!!.setNavigationOnClickListener { finish() }

        //创建布局管理（这个是为什么？？）
        val layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        mAdapter = RecycleAdapter(R.layout.item_card, cardList)
        recyclerView!!.adapter = mAdapter
        if (!cardList.isEmpty()) {
            ll_empty!!.visibility = View.INVISIBLE
        }
    }

    private fun initFBI() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        ll_empty = findViewById(R.id.ll_empty)
        btn_add = findViewById(R.id.btn_add)
    }

    override fun onResume() {
        super.onResume()
        if (isUpdate) {
            initData()
            mAdapter!!.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmField
        var isUpdate = false
    }
}