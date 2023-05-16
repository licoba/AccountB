package com.example.dibage.accountb.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.ConvertUtils
import com.example.dibage.accountb.R
import com.example.dibage.accountb.applications.MyApplication
import com.example.dibage.accountb.dao.CardDao
import com.example.dibage.accountb.dao.DaoSession
import com.example.dibage.accountb.dao.PhotoDao
import com.example.dibage.accountb.entitys.Card
import com.example.dibage.accountb.entitys.Photo
import com.example.dibage.accountb.utils.PhotoUtils
import com.example.dibage.accountb.utils.SimpleUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.IOException

class AddPhotoActivity() : AppCompatActivity(), View.OnClickListener {
    private val TAG = "AddPhotoActivity"
    var fromAty = ""
    private var context: Context? = null
    private var toolbar: Toolbar? = null
    private var mPopWindow: PopupWindow? = null
    private var btn_commit: Button? = null
    private var fl_camera: FrameLayout? = null
    private var btn_from_album: Button? = null
    private var btn_from_camera: Button? = null
    private var imageView1: ImageView? = null
    private var imageView2: ImageView? = null
    private var imageView3: ImageView? = null
    private var imageView4: ImageView? = null
    private var img_photo: ImageView? = null
    private var tv_photo: TextView? = null
    private var et_card_name: EditText? = null
    private var et_user_name: EditText? = null
    private var et_card_number: EditText? = null
    private var et_remark: EditText? = null
    lateinit var daoSession: DaoSession
    private var photoDao: PhotoDao? = null
    private var cardDao: CardDao? = null
    private var cameraFile: File? = null
    var count_photo = 0 //计数、总照片数
    var photoPathList: MutableList<String> = ArrayList(4) //选择的照片的绝对路径
    var privatePathList: MutableList<String> = ArrayList() //私有路径
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        initFBI()
        initData()
        initView()
        initEvent()
    }

    private fun initData() {
        daoSession = (application as MyApplication).daoSession
        photoDao = daoSession.photoDao
        cardDao = daoSession.cardDao
        fromAty = intent.getStringExtra("fromAty").toString()
    }

    private fun initEvent() {
        btn_commit!!.setOnClickListener(this)
        fl_camera!!.setOnClickListener { showChoosePopup() }
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.title = "添加证件"
        toolbar!!.setNavigationOnClickListener { finish() }
    }

    private fun initFBI() {
        context = this
        toolbar = findViewById(R.id.toolbar)
        fl_camera = findViewById(R.id.fl_camera)
        btn_commit = findViewById(R.id.btn_commit)
        imageView1 = findViewById(R.id.img_1)
        imageView2 = findViewById(R.id.img_2)
        imageView3 = findViewById(R.id.img_3)
        imageView4 = findViewById(R.id.img_4)
        tv_photo = findViewById(R.id.tv_photo)
        img_photo = findViewById(R.id.img_photo)
        et_card_name = findViewById(R.id.et_card_name)
        et_user_name = findViewById(R.id.et_user_name)
        et_card_number = findViewById(R.id.et_card_number)
        et_remark = findViewById(R.id.et_remark)
    }

    private fun showChoosePopup() {

        PopMenu.show(arrayOf("从相册选取照片", "拍摄照片")).apply {
            onIconChangeCallBack =
                object : OnIconChangeCallBack<PopMenu?>(true) {
                    override fun getIcon(dialog: PopMenu?, index: Int, menuText: String): Int {
                        return when (index) {
                            0 -> R.mipmap.comm_album
                            1 -> R.mipmap.comm_camera
                            else -> 0
                        }
                    }
                }

            onMenuItemClickListener =
                OnMenuItemClickListener { dialog, text, index ->
                    when (index) {
                        0 -> {
                            checkPermission(IMAGE)
                        }

                        1 -> {
                            checkPermission(CAMERA)
                        }

                    }
                    false
                }

            radius = ConvertUtils.dp2px(8f).toFloat()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {


            R.id.btn_commit -> {
                if (validateData()) { //如果账号填写正确则向数据库添加数据
                    if (count_photo > 0) savePhoto(photoPathList)
                    AddRecord()
                }
                Log.e("是否存在文件：", "文件名：" + context!!.fileList())
            }
        }
    }

    private fun AddRecord() {
        val cardname = et_card_name!!.text.toString()
        val username = et_user_name!!.text.toString()
        val cardnumber = et_card_number!!.text.toString()
        val remark = et_remark!!.text.toString()
        val card = Card(cardname, username, cardnumber, remark)
        cardDao!!.insert(card)
        if (count_photo > 0) {
            for (i in 0 until count_photo) {
                val photo = Photo()
                photo.cardId = card.id
                photo.photo_path = privatePathList.get(i)
                photoDao!!.insert(photo)
            }
        }
        if ((fromAty == "MainActivity")) {
            Toasty.success((context)!!, "保存成功，请前往证件夹查看").show()
        } else if ((fromAty == "CardActivity")) {
            Toasty.success((context)!!, "保存成功").show()
        }
        CardActivity.isUpdate = true
        finish()
    }

    //验证填入的数据是否正确
    private fun validateData(): Boolean {
        if (et_card_name!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toasty.info((context)!!, "请填写证件名称").show()
            return false
        } else if (et_user_name!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toasty.info((context)!!, "请填写证件持有人姓名").show()
            return false
        } else if (et_card_number!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toasty.info((context)!!, "请填写证件号").show()
            return false
        }
        return true
    }

    //保存照片到程序的私有目录（根据绝对路径，然后保存到私有目录）
    private fun savePhoto(photoPathList: List<String>) {
        for (i in 0 until count_photo) {
            try {
                //得到File对象后压缩File
                val file = File(photoPathList[i])
                //file = CompressHelper.getDefault(this).compressToFile(file);
                val bytes = PhotoUtils.File2Bytes(file)
                //文件被存储到/data/data/<package  name>/files/目录下，只允许本程序访问，其它程序没有访问权限
                //随机生成一个文件名
                val filename = SimpleUtils.getRandomFileName()
                //                String ssss = getApplicationContext().getFilesDir().getAbsolutePath();
//                Log.e("私有文件存储的绝对路径：",ssss);
                privatePathList.add(filesDir.toString() + "/" + filename + ".jpg")
                val fos = context!!.openFileOutput("$filename.jpg", MODE_PRIVATE)
                //写入文件
                fos.write(bytes)
                //关闭输出流
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /*
        动态申请权限
        condition：为1：申请相册权限；为2：申请拍照权限
     */
    private fun checkPermission(condition: Int) {
        if (condition == IMAGE) {
            val permissions = arrayOf(Permission.READ_MEDIA_IMAGES)
            XXPermissions.with((context)!!).permission(permissions)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, allGranted: Boolean) {
                        if (allGranted) openGallery()

                    }

                    override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                        Toasty.info((context)!!, "获取权限失败，请授予相册访问权限")
                        XXPermissions.startPermissionActivity((context)!!, permissions)
                    }
                })

        } else if (condition == CAMERA) {
            val permissions =
                arrayOf( Manifest.permission.CAMERA)
            XXPermissions.with((context)!!).permission(*permissions)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, allGranted: Boolean) {
                        if (allGranted) startCamera()

                    }

                    override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                        Toasty.info((context)!!, "获取权限失败，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity((context)!!, permissions)
                    }
                })
        }
    }

    //拍照并且保存到程序的私有目录
    private fun startCamera() {
        val filename = SimpleUtils.getRandomFileName()
        val filepath = filesDir.toString() + "/" + filename + ".jpg"
        cameraFile = File(filepath)
        val uri = FileProvider.getUriForFile(
            (context)!!,
            "com.example.dibage.accountb.fileprovider",
            cameraFile!!
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA) //开启相机
    }


    private fun openGallery() {
        Toasty.info(context!!, "打开相册").show()
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, IMAGE)
    }

    //权限申请回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, IMAGE)
            } else {
                Toasty.info((context)!!, "请授予软件需要的权限").show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //处理相册回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == IMAGE) && (resultCode == RESULT_OK) && (data != null)) {
            count_photo++
            val selectedImage = data.data!!
            val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
            val c = contentResolver.query(selectedImage, filePathColumns, null, null, null)!!
            c.moveToFirst()
            val columnIndex = c.getColumnIndex(filePathColumns[0])
            val imagePath = c.getString(columnIndex)
            photoPathList.add(imagePath)
            showImage(imagePath)
            c.close()
        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            count_photo++
            photoPathList.add(cameraFile!!.absolutePath)
            showImage(cameraFile!!.absolutePath)
        }
    }

    //根据相册返回的路径 显示图片
    private fun showImage(imagePath: String) {
        val ivList: MutableList<ImageView?> = ArrayList()
        ivList.add(imageView1)
        ivList.add(imageView2)
        ivList.add(imageView3)
        ivList.add(imageView4)
        if (count_photo > 0 && count_photo <= 3) {
            tv_photo!!.visibility = View.INVISIBLE //隐藏文本提示框
            fl_camera!!.setOnClickListener(null) //移除监听
            img_photo!!.visibility = View.INVISIBLE //隐藏照相机图片
            ivList[count_photo]!!.setImageResource(R.mipmap.camera2) //设置后一位的图片
            ivList.get(count_photo)!!.visibility = View.VISIBLE //设置可见
            ivList[count_photo]!!.setOnClickListener(
                View.OnClickListener
                //注册监听
                { showChoosePopup() })
            ivList[count_photo - 1]!!.setOnClickListener(null) //移除前一位的监听
        } else if (count_photo >= 3) {
            ivList[count_photo - 1]!!.setOnClickListener(null) //移除本位的监听
        }

        //Bitmap bm = BitmapFactory.decodeFile(imagePath);
        //得到图片旋转角度
        //int degree = PhotoUtils.getBitmapDegree(imagePath);
        //将图片旋转过来
        //bm = PhotoUtils.rotateBitmapByDegree(bm, degree);
        for (i in 0 until count_photo) {
            var bm = BitmapFactory.decodeFile(photoPathList[i])
            //得到图片旋转角度
            val degree = PhotoUtils.getBitmapDegree(photoPathList[i])
            //将图片旋转过来
            bm = PhotoUtils.rotateBitmapByDegree(bm, degree)
            ivList[i]!!.setImageBitmap(bm)
            ivList.get(i)!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private val IMAGE = 1 //调用系统相册-选择图片
        private val CAMERA = 2 //调用系统相机
    }
}