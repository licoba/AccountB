package com.example.dibage.accountb.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.dibage.accountb.R
import com.example.dibage.accountb.adapters.AccountAdapter
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.base.BaseActivity
import com.example.dibage.accountb.commonView.PopWindowTip
import com.example.dibage.accountb.dao.AccountDao
import com.example.dibage.accountb.dao.DaoSession
import com.example.dibage.accountb.databinding.ActivityMainBinding
import com.example.dibage.accountb.entitys.Account
import com.example.dibage.accountb.utils.AccountUtils
import com.example.dibage.accountb.utils.UIUtils
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.gjiazhe.wavesidebar.WaveSideBar
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import es.dmoral.toasty.Toasty
import org.greenrobot.greendao.query.QueryBuilder


class MainActivity : BaseActivity() {
    var context: Context = this@MainActivity
    private lateinit var binding: ActivityMainBinding

    private var floatingActionMenu: FloatingActionMenu? = null
    private var fabAddAccount: FloatingActionButton? = null
    private var fabAddIdCard: FloatingActionButton? = null
    private var sideBar: WaveSideBar? = null
    private var listView: ListView? = null
    private var toolbar: Toolbar? = null
    private var mPopWindow: PopupWindow? = null
    private val mPopTip: PopupWindow? = null
    private var llEmpty: LinearLayout? = null
    var accountsList: MutableList<Account> = mutableListOf()
    lateinit var qb: QueryBuilder<Account>
    var accountAdapter: AccountAdapter? = null
    lateinit var daoSession: DaoSession
    lateinit var mAccountDao: AccountDao
    private var alpha = 1.0f //初始值设为1，为不变暗
    private var mHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        //监听Item的点击事件
        listView!!.onItemClickListener = myItemClickListener()
        //监听ListView的触摸事件
        listView!!.setOnTouchListener { view, motionEvent ->
            if (floatingActionMenu!!.isOpened) {
                floatingActionMenu!!.close(true)
                true
            } else false
        }
        listView!!.onItemLongClickListener = myItemLongClickListener()
        fabAddAccount!!.setOnClickListener(FablickListener)
        fabAddIdCard!!.setOnClickListener(FablickListener)

        //背景变暗的处理
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    1 -> UIUtils.darkenBackgroud(this@MainActivity, msg.obj as Float)
                }
            }
        }
    }

    override fun initData() {
        daoSession = (application as MyApplication).daoSession
        mAccountDao = daoSession.getAccountDao()
        //获取queryBuilder，通过queryBuilder来实现查询功能
        //mAccountDao.queryBuilder()表示查询所有，
        // orderAsc(AccountDao.Properties.Firstchar)表示按照首字母升序排序（#比A大，所以需要用函数重新排序）
        qb = mAccountDao.queryBuilder()
            .orderAsc(AccountDao.Properties.Firstchar, AccountDao.Properties.Username)
        accountsList = ArrayList()
        accountsList.clear()
        accountsList.addAll(AccountUtils.orderListAccount(qb.list()))
        accountAdapter = AccountAdapter(context, R.layout.item_listview, accountsList)
        listView!!.adapter = accountAdapter
        accountAdapter!!.notifyDataSetChanged()
    }

    override fun initFBI() {
        floatingActionMenu = findViewById(R.id.fabMenu)
        fabAddAccount = findViewById(R.id.fabAddAcount)
        fabAddIdCard = findViewById(R.id.fabAddIdCard)
        sideBar = binding.sideBar
        listView = findViewById(R.id.listview)
        llEmpty = binding.llEmpty
        toolbar = findViewById(R.id.toolbar)
    }

    override fun initView() {
        if (accountsList!!.size > 0) {
            llEmpty!!.visibility = View.GONE
            sideBar!!.visibility = View.VISIBLE
        } else {
            sideBar!!.visibility = View.INVISIBLE
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.title = "账号册子"
        toolbar!!.setOnMenuItemClickListener(onMenuItemClick)
        //自定义侧边索引
        sideBar!!.setIndexItems(
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "#"
        )
        sideBar!!.setOnSelectIndexItemListener { index ->
            Log.d("WaveSideBar", index)
            var location = 0
            var flag = false //如果没有匹配到值，那么界面就不动
            //
            for (i in accountsList!!.indices) {
                if (accountsList!![i].firstchar == index) {
                    location = i
                    flag = true
                    break
                }
            }
            Log.d("Location:", location.toString() + "")
            if (flag) listView!!.setSelection(location)
        }
    }

    override fun onPause() {
        super.onPause()
        isPause = true //记录页面已经被暂停
        if (floatingActionMenu!!.isOpened) floatingActionMenu!!.close(true)
    }

    override fun onResume() { //每次onResume都会刷新一次数据
        super.onResume()
        if (isPause) { //判断是否暂停
            isPause = false
            initData()
            if (accountsList!!.size > 0) {
                llEmpty!!.visibility = View.GONE
                sideBar!!.visibility = View.VISIBLE
            } else {
                sideBar!!.visibility = View.INVISIBLE
            }
        }
    }

    inner class myItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            val account = adapterView.getItemAtPosition(i) as Account
            showPopupWindow(account)
        }
    }

    inner class myItemLongClickListener : AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(
            adapterView: AdapterView<*>?,
            view: View,
            i: Int,
            l: Long
        ): Boolean {
            showPopupMenu(accountsList!![i])
            return true
        }
    }

    private fun showPopupWindow(account: Account) {


        PopMenu.show(arrayOf("添加", "编辑", "删除", "分享"))


        val tv1: TextView
        val tv2: TextView
        val tv3: TextView
        val tv4: TextView
        val inflater = layoutInflater
        val contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.pop_detail, null)
        tv1 = contentView.findViewById(R.id.tv_description)
        tv2 = contentView.findViewById(R.id.tv_username)
        tv3 = contentView.findViewById(R.id.tv_password)
        tv4 = contentView.findViewById(R.id.tv_remarks)
        tv1.text = account.description
        tv2.text = account.username
        tv3.text = account.password
        tv4.text = account.remark
        mPopWindow = PopupWindow(
            contentView,
            windowManager.defaultDisplay.width - 200, WindowManager.LayoutParams.WRAP_CONTENT, true
        )
        mPopWindow!!.contentView = contentView

        //显示popupWindow
        val rootview = LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_main, null)
        mPopWindow!!.animationStyle = R.style.Popupwindow
        mPopWindow!!.showAtLocation(rootview, Gravity.CENTER, 0, 0)
        //UIUtils.darkenBackgroud(MainActivity.this, 0.5f);
        darkWindow()
        val layout1 = contentView.findViewById<LinearLayout>(R.id.layout1)
        val layout2 = contentView.findViewById<LinearLayout>(R.id.layout2)
        val layout3 = contentView.findViewById<LinearLayout>(R.id.layout3)
        layout1.setOnClickListener {
            val cmb = context
                .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = account.username
            Toasty.success(context, "账号已复制", Toast.LENGTH_SHORT, true).show()
        }
        layout2.setOnClickListener {
            val cmb = context
                .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = account.password
            Toasty.success(context, "密码已复制", Toast.LENGTH_SHORT, true).show()
        }
        mPopWindow!!.setOnDismissListener { brightWindow() }
    }

    private fun showPopupMenu(account: Account) {
        PopMenu.show(arrayOf("修改", "删除", "复制并新建")).apply {
            onIconChangeCallBack =
                object : OnIconChangeCallBack<PopMenu?>(true) {
                    override fun getIcon(dialog: PopMenu?, index: Int, menuText: String): Int {
                        return when (index) {
                            0 -> R.mipmap.img_dialogx_demo_edit
                            1 -> R.mipmap.img_dialogx_demo_delete
                            2 -> R.mipmap.img_dialogx_demo_add
                            else -> 0
                        }
                    }
                }

            onMenuItemClickListener =
                OnMenuItemClickListener<PopMenu> { dialog, text, index ->
                    when (index) {
                        0 -> {
                            val intent = Intent(this@MainActivity, EditAccountActivity::class.java)
                            intent.putExtra("account_data", account)
                            startActivity(intent)
                        }

                        1->{
                            showDeletePop(account)
                        }

                        2->{
                            Toasty.info(context, "还没做")
                        }
                    }
                    false
                }
        }

    }

    //删除提示框
    private fun showDeletePop(account: Account) {
        val popTip: PopWindowTip = object : PopWindowTip(this@MainActivity) {
            override fun clickCancel() {}
            override fun dismissTodo() {}
            override fun clickConfirm() {
                mAccountDao!!.delete(account)
                accountsList!!.clear()
                accountsList!!.addAll(qb!!.list())
                accountsList = AccountUtils.orderListAccount(accountsList)
                accountAdapter!!.notifyDataSetChanged()
                if (accountsList.size > 0) {
                    llEmpty!!.visibility = View.INVISIBLE
                    sideBar!!.visibility = View.VISIBLE
                } else {
                    llEmpty!!.visibility = View.VISIBLE
                    sideBar!!.visibility = View.INVISIBLE
                }
                Toasty.success(context, "删除成功", Toast.LENGTH_SHORT, false).show()
            }
        }
        popTip.setTitleAndContent("删除警告", "账号被删除之后将无法被找回，确定删除该账号？")
    }

    //浮动添加按钮的监听器
    private val FablickListener = View.OnClickListener { v ->
        val intent: Intent
        when (v.id) {
            R.id.fabAddAcount -> {
                intent = Intent(context, AddAccountActivity::class.java)
                startActivityForResult(intent, ADD_ACCOUNT)
                floatingActionMenu!!.close(true)
            }

            R.id.fabAddIdCard -> {
                intent = Intent(context, AddPhotoActivity::class.java)
                intent.putExtra("fromAty", "MainActivity")
                startActivity(intent)
                floatingActionMenu!!.close(true)
            }
        }
    }
    private val onMenuItemClick = Toolbar.OnMenuItemClickListener { menuItem ->
        val msg = ""
        val intent: Intent
        when (menuItem.itemId) {
            R.id.action_search -> {
                intent = Intent(context, SearchActivity::class.java)
                startActivity(intent)
            }

            R.id.action_setting -> {
                intent = Intent(context, MoreActivity::class.java)
                startActivityForResult(intent, MoreActivity.RECORVRY_DATA)
            }

            R.id.action_card -> {
                intent = Intent(context, CardActivity::class.java)
                startActivity(intent)
            }
        }
        if (msg != "") {
            Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show()
        }
        true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    private fun brightWindow() {
        Thread {
            while (alpha < 1.0f) {
                try {
                    Thread.sleep(3) //每0.004s变暗0.01
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                val msg = mHandler!!.obtainMessage()
                msg.what = 1
                alpha += 0.01f
                msg.obj = alpha
                mHandler!!.sendMessage(msg)
            }
        }.start()
    }

    private fun darkWindow() {
        Thread {
            while (alpha > 0.5f) {
                try {
                    Thread.sleep(3) //每0.004s变暗0.01
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                val msg = mHandler!!.obtainMessage()
                msg.what = 1
                alpha -= 0.01f
                msg.obj = alpha
                mHandler!!.sendMessage(msg)
            }
        }.start()
    } //    @Override

    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        if (resultCode == RESULT_OK) {
    //            switch (requestCode) {
    //                case RECORVRY_DATA://备份文件导入完成，刷新数据
    //
    //                    break;
    //                case ADD_ACCOUNT://添加账号完成
    //                    initData();
    //                    break;
    //            }
    //        }
    //    }
    companion object {
        const val ADD_ACCOUNT = 1
        var isPause = false
    }
}