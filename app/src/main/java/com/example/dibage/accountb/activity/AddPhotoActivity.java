package com.example.dibage.accountb.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.CardDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.dao.PhotoDao;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.entitys.Photo;
import com.example.dibage.accountb.utils.PhotoUtils;
import com.example.dibage.accountb.utils.SimpleUtils;
import com.example.dibage.accountb.utils.UIUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AddPhotoActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int IMAGE = 1; //调用系统相册-选择图片
    private static final int CAMERA = 2;//调用系统相机
    private final String TAG = "AddPhotoActivity";
    String fromAty = "";

    private Context context;
    private Toolbar toolbar;
    private PopupWindow mPopWindow;
    private Button btn_commit;
    private FrameLayout fl_camera;
    private Button btn_from_album;
    private Button btn_from_camera;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView img_photo;
    private TextView tv_photo;

    private EditText et_card_name;
    private EditText et_user_name;
    private EditText et_card_number;
    private EditText et_remark;


    DaoSession daoSession;
    private PhotoDao photoDao;
    private CardDao cardDao;
    private File cameraFile;

    int count_photo = 0;//计数、总照片数
    List<String> photoPathList = new ArrayList<>(4);//选择的照片的绝对路径
    List<String> privatePathList = new ArrayList<>();//私有路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        initFBI();
        initData();
        initView();
        initEvent();

    }

    private void initData() {
        daoSession = ((MyApplication) getApplication()).getDaoSession();
        photoDao = daoSession.getPhotoDao();
        cardDao = daoSession.getCardDao();
        fromAty = getIntent().getStringExtra("fromAty");
    }

    private void initEvent() {
        btn_commit.setOnClickListener(this);
        fl_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
    }

    private void initView() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("添加证件");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPhotoActivity.this.finish();
            }
        });
    }

    private void initFBI() {
        context = this;
        toolbar = findViewById(R.id.toolbar);
        fl_camera = findViewById(R.id.fl_camera);
        btn_commit = findViewById(R.id.btn_commit);
        imageView1 = findViewById(R.id.img_1);
        imageView2 = findViewById(R.id.img_2);
        imageView3 = findViewById(R.id.img_3);
        imageView4 = findViewById(R.id.img_4);
        tv_photo = findViewById(R.id.tv_photo);
        img_photo = findViewById(R.id.img_photo);
        et_card_name = findViewById(R.id.et_card_name);
        et_user_name = findViewById(R.id.et_user_name);
        et_card_number = findViewById(R.id.et_card_number);
        et_remark = findViewById(R.id.et_remark);

    }

    public void showPopupWindow() {
        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.from(AddPhotoActivity.this).inflate(R.layout.pop_camera_photo, null);
        mPopWindow = new PopupWindow(contentView,
                getWindowManager().getDefaultDisplay().getWidth() - 220, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //显示popupWindow
        View rootview = LayoutInflater.from(AddPhotoActivity.this).inflate(R.layout.activity_add_photo, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        //背景变暗
        UIUtils.darkenBackgroud(AddPhotoActivity.this, 0.5f);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                //在dismiss中恢复透明度
                UIUtils.darkenBackgroud(AddPhotoActivity.this, 1f);
            }
        });
        btn_from_album = contentView.findViewById(R.id.btn_from_album);
        btn_from_camera = contentView.findViewById(R.id.btn_from_camera);
        //myClickListener = new MyClickListener();
        btn_from_album.setOnClickListener(this);
        btn_from_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_from_album:
                mPopWindow.dismiss();
                checkPermission(IMAGE);
                break;
            case R.id.btn_from_camera:
                mPopWindow.dismiss();
                checkPermission(CAMERA);
                break;
            case R.id.btn_commit:

                if (validateData()) {//如果账号填写正确则向数据库添加数据
                    if (count_photo > 0)
                        savePhoto(photoPathList);
                    AddRecord();
                }
                Log.e("是否存在文件：", "文件名：" + context.fileList());
                break;
        }
    }

    private void AddRecord() {
        String cardname = et_card_name.getText().toString();
        String username = et_user_name.getText().toString();
        String cardnumber = et_card_number.getText().toString();
        String remark = et_remark.getText().toString();

        Card card = new Card(cardname, username, cardnumber, remark);
        cardDao.insert(card);
        if (count_photo > 0) {
            for (int i = 0; i < count_photo; i++) {
                Photo photo = new Photo();
                photo.setCardId(card.getId());
                photo.setPhoto_path(privatePathList.get(i));
                photoDao.insert(photo);
            }
        }

        if (fromAty.equals("MainActivity")) {
            Toasty.success(context, "保存成功，请前往证件夹查看").show();
        } else if (fromAty.equals("CardActivity")) {
            Toasty.success(context, "保存成功").show();
        }
        CardActivity.isUpdate = true;
        AddPhotoActivity.this.finish();
    }

    //验证填入的数据是否正确
    private boolean validateData() {
        if (et_card_name.getText().toString().trim().isEmpty()) {
            Toasty.info(context, "请填写证件名称").show();
            return false;
        } else if (et_user_name.getText().toString().trim().isEmpty()) {
            Toasty.info(context, "请填写证件持有人姓名").show();
            return false;
        } else if (et_card_number.getText().toString().trim().isEmpty()) {
            Toasty.info(context, "请填写证件号").show();
            return false;
        }
        return true;
    }

    //保存照片到程序的私有目录（根据绝对路径，然后保存到私有目录）
    private void savePhoto(List<String> photoPathList) {
        for (int i = 0; i < count_photo; i++) {
            try {
                //得到File对象后压缩File
                File file = new File(photoPathList.get(i));
                //file = CompressHelper.getDefault(this).compressToFile(file);
                byte[] bytes = PhotoUtils.File2Bytes(file);
                //文件被存储到/data/data/<package  name>/files/目录下，只允许本程序访问，其它程序没有访问权限
                //随机生成一个文件名
                String filename = SimpleUtils.getRandomFileName();
//                String ssss = getApplicationContext().getFilesDir().getAbsolutePath();
//                Log.e("私有文件存储的绝对路径：",ssss);
                privatePathList.add(getFilesDir() + "/" + filename + ".jpg");
                FileOutputStream fos = context.openFileOutput(filename + ".jpg", MODE_PRIVATE);
                //写入文件
                fos.write(bytes);
                //关闭输出流
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        动态申请权限
        condition：为1：申请相册权限；为2：申请拍照权限
     */
    private void checkPermission(int condition) {
        if (condition == IMAGE) {
            if (!(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) { //权限没有被授予
                ActivityCompat.requestPermissions(AddPhotoActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        IMAGE);

            } else {//权限被授予
                //Toasty.success(context,"已经拥有权限").show();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                //直接操作
            }
        } else if (condition == CAMERA) {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            XXPermissions.with(context).permission(permissions).request(new OnPermissionCallback() {
                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    if (allGranted == true) {
                        startCamera();
                    }
                }
                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (doNotAskAgain) {
                        Toasty.info(context,"被永久拒绝授权，请手动授予权限");
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(context, permissions);
                    } else {
                        Toasty.info(context,"获取权限失败");
                    }
                }
            });

        }
    }

    //拍照并且保存到程序的私有目录
    private void startCamera() {
        String filename = SimpleUtils.getRandomFileName();
        String filepath = getFilesDir()+"/" + filename + ".jpg";
        cameraFile = new File(filepath);
        Uri uri = FileProvider.getUriForFile(context, "com.example.dibage.accountb.fileprovider", cameraFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA);//开启相机
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
            } else {
                Toasty.info(context, "请授予软件需要的权限").show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //处理相册回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            count_photo++;
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            photoPathList.add(imagePath);
            showImage(imagePath);
            c.close();
        } else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK ) {
            count_photo++;
            photoPathList.add(cameraFile.getAbsolutePath());
            showImage(cameraFile.getAbsolutePath());

        }
    }

    //根据相册返回的路径 显示图片
    private void showImage(String imagePath) {
        List<ImageView> ivList = new ArrayList<>();
        ivList.add(imageView1);
        ivList.add(imageView2);
        ivList.add(imageView3);
        ivList.add(imageView4);

        if (count_photo > 0 && count_photo <= 3) {
            tv_photo.setVisibility(View.INVISIBLE);//隐藏文本提示框
            fl_camera.setOnClickListener(null);//移除监听
            img_photo.setVisibility(View.INVISIBLE);//隐藏照相机图片
            ivList.get(count_photo).setImageResource(R.mipmap.camera2);//设置后一位的图片
            ivList.get(count_photo).setVisibility(View.VISIBLE);//设置可见
            ivList.get(count_photo).setOnClickListener(new View.OnClickListener() {//注册监听
                @Override
                public void onClick(View view) {
                    showPopupWindow();
                }
            });
            ivList.get(count_photo - 1).setOnClickListener(null);//移除前一位的监听
        } else if (count_photo >= 3) {
            ivList.get(count_photo - 1).setOnClickListener(null);//移除本位的监听
        }

        //Bitmap bm = BitmapFactory.decodeFile(imagePath);
        //得到图片旋转角度
        //int degree = PhotoUtils.getBitmapDegree(imagePath);
        //将图片旋转过来
        //bm = PhotoUtils.rotateBitmapByDegree(bm, degree);
        for (int i = 0; i < count_photo; i++) {
            Bitmap bm = BitmapFactory.decodeFile(photoPathList.get(i));
            //得到图片旋转角度
            int degree = PhotoUtils.getBitmapDegree(photoPathList.get(i));
            //将图片旋转过来
            bm = PhotoUtils.rotateBitmapByDegree(bm, degree);
            ivList.get(i).setImageBitmap(bm);
            ivList.get(i).setVisibility(View.VISIBLE);
        }
    }


}
