package com.example.dibage.accountb.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.beans.OpenImageUrl
import com.flyjingfish.openimagelib.enums.MediaType
import com.flyjingfish.openimagelib.listener.SourceImageViewIdGet
import com.github.chrisbanes.photoview.PhotoView
//import com.luck.picture.lib.basic.IBridgeViewLifecycle
//import com.luck.picture.lib.basic.PictureSelector
//import com.luck.picture.lib.config.InjectResourceSource
//import com.luck.picture.lib.config.PictureConfig
//import com.luck.picture.lib.config.SelectMimeType
//import com.luck.picture.lib.entity.LocalMedia
//import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener
//import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener
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

        //在点击时调用（以下以RecyclerView为例介绍）
        OpenImage.with(this) //点击ImageView所在的RecyclerView（也支持设置setClickViewPager2，setClickViewPager，setClickGridView，setClickListView，setClickImageView，setNoneClickView）
            .setClickRecyclerView(recyclerView, object : SourceImageViewIdGet<OpenImageUrl> {
                override fun getImageViewId(data: OpenImageUrl, position: Int): Int {
                    return R.id.img_card //点击的ImageView的Id
                }
            }) //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
            .setSrcImageViewScaleType(ImageView.ScaleType.CENTER_CROP, true) //RecyclerView的数据
            .setImageUrlList(photoList.map { it.photo_path }, MediaType.IMAGE) //点击的ImageView所在数据的位置
            .setClickPosition(position) //开始展示大图
            .show()

//        val mediaList = photoList.map {
//            LocalMedia().apply { path = it.photo_path }
//        }

//        // 预览图片、视频、音频
//        PictureSelector.create(this)
//            .openPreview()
//            .startActivityPreview(
//                position,
//                false,
//                ArrayList(mediaList)
//            )

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

    //设置图片全屏显示，隐藏状态栏
    fun setAllWindow() {}
    override fun onBackPressed() {
        if (flag == 1) {
            ll_detail!!.visibility = View.VISIBLE
            fl_bigimage!!.visibility = View.GONE
            flag = 0
        } else {
            super.onBackPressed()
        }
    }
}