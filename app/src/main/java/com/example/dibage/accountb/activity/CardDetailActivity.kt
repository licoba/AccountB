package com.example.dibage.accountb.activity

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.example.dibage.accountb.R
import com.example.dibage.accountb.adapters.CardPhotoAdapter
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.dao.CardDao
import com.example.dibage.accountb.dao.DaoSession
import com.example.dibage.accountb.dao.PhotoDao
import com.example.dibage.accountb.entitys.Card
import com.example.dibage.accountb.entitys.Photo
import com.example.dibage.accountb.tools.GlideEngine
import com.github.chrisbanes.photoview.PhotoView
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.luck.picture.lib.basic.PictureMediaScannerConnection
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener
import com.luck.picture.lib.utils.DownloadFileUtils
import com.luck.picture.lib.utils.ToastUtils
import es.dmoral.toasty.Toasty
import org.greenrobot.greendao.query.QueryBuilder


/**
 * 证件详情
 */
class CardDetailActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var card: Card? = null
    private var btn_edit: ImageView? = null
    private var tv_cardname: TextView? = null
    private var tv_username: TextView? = null
    private var tv_cardnumber: TextView? = null
    private var tv_remark: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var daoSession: DaoSession? = null
    private val cardDao: CardDao? = null
    private var photoList: MutableList<Photo> = mutableListOf()
    private var mAdapter: CardPhotoAdapter? = null
    private var fl_bigimage: FrameLayout? = null
    private val iv_exit: ImageView? = null
    private var big_image: PhotoView? = null
    private var ll_detail: LinearLayout? = null
    var context: Context? = null
    var flag = 0 //用来判断大图是否显示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)
        initFBI()
        initData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        //点击查看大图
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
//                flag = 1
//                ll_detail!!.visibility = View.GONE
//                fl_bigimage!!.visibility = View.VISIBLE
//                Glide.with(context!!).load(photoList[position].photo_path).into(big_image!!)
//
                showBigPhoto(photoList[position], view, position)
            }
        })
        big_image!!.setOnPhotoTapListener { view, x, y ->
            flag = 0
            ll_detail!!.visibility = View.VISIBLE
            fl_bigimage!!.visibility = View.GONE
        }


    }

    private fun showBigPhoto(photo: Photo, view: View, position: Int) {
        val mediaList = photoList.map {
            LocalMedia().apply { path = it.photo_path }
        }
        // 预览图片、视频、音频
        PictureSelector.create(this)
            .openPreview()
            .setImageEngine(GlideEngine.createGlideEngine())
            .setExternalPreviewEventListener(MyExternalPreviewEventListener())
            .startActivityPreview(
                position,
                false,
                ArrayList(mediaList)
            )


    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.title = "证件详情"
        toolbar!!.setNavigationOnClickListener { finish() }
        tv_cardname!!.text = card!!.card_name
        tv_username!!.text = card!!.username
        tv_cardnumber!!.text = card!!.card_number
        tv_remark!!.text = card!!.remark
        mAdapter = CardPhotoAdapter(R.layout.item_cardphoto, photoList)
        mAdapter!!.setContext(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = mAdapter
    }

    private fun initData() {
        context = this
        intent = getIntent()
        daoSession = (application as MyApplication).daoSession
        val photoDao = daoSession?.getPhotoDao()
        card = intent.getSerializableExtra("card") as Card //获取传递过来的intent对象
        val qb: QueryBuilder<*> = photoDao!!.queryBuilder()
        photoList.clear()
        val queryResult =
            qb.where(PhotoDao.Properties.CardId.eq(card!!.id)).list() as MutableList<Photo>
        photoList.addAll(queryResult)
        //Log.e("图片张数：",photoList.size()+"");
    }

    private fun initFBI() {
        toolbar = findViewById(R.id.toolbar)
        btn_edit = findViewById(R.id.btn_edit)
        tv_cardname = findViewById(R.id.tv_cardname)
        tv_username = findViewById(R.id.tv_username)
        tv_cardnumber = findViewById(R.id.tv_cardnumber)
        tv_remark = findViewById(R.id.tv_remark)
        recyclerView = findViewById(R.id.recyclerView)
        fl_bigimage = findViewById(R.id.fl_bigimage)
        big_image = findViewById(R.id.big_image)
        ll_detail = findViewById(R.id.ll_detail)
    }


    inner class MyExternalPreviewEventListener : OnExternalPreviewEventListener {
        override fun onPreviewDelete(position: Int) {

        }

        override fun onLongPressDownload(context: Context, media: LocalMedia?): Boolean {
            
            MessageDialog.build()
                .setTitle("提示")
                .setMessage("是否保存图片到手机？")
                .setCancelButton("取消")
                .setOkButton("确定")
                .setOkButton { dialog, _ ->
                    dialog.dismiss()
                    val path = media!!.availablePath
                    DownloadFileUtils.saveLocalFile(context, path, media!!.mimeType) { realPath ->
                        if (TextUtils.isEmpty(realPath)) {
                            val errorMsg = getString(R.string.ps_save_image_error)
                            ToastUtils.showToast(context, errorMsg)
                        } else {
                            PictureMediaScannerConnection(this@CardDetailActivity, realPath)
                            PopTip.show(R.mipmap.common_success, "图片已保存")

                        }
                    }
                    false  // 点击后自动关闭对话框。
                }
                .show(context as Activity)

            return true  // 表示自己处理长按的逻辑
        }

    }


}