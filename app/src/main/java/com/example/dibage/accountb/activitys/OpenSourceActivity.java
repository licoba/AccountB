package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.OpenSourceAdapter;
import com.example.dibage.accountb.entitys.OpenSource;
import com.example.dibage.accountb.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

public class OpenSourceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context context;
    private RecyclerView recyclerView;
    private List<OpenSource> dataList;
    private OpenSourceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);
        initFBI();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            String webSite = dataList.get(position).getWebsite();
            Uri uri= Uri.parse(webSite);   //指定网址
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        });
    }

    private void initData() {
        dataList = new ArrayList<>();
        context = this;
        mAdapter = new OpenSourceAdapter(R.layout.item_opencource,dataList);
        OpenSource[] data = new OpenSource[30];
        data[1] = new OpenSource("https://github.com/hss01248/Toasty","Toasty","基于GrenderG作者的Toasty进行了修改，实现了单例化（防止弹出无数的 toast）,支持弹窗位置设置。弹窗颜色多样化，支持原生样式，支持自定义图标");
        data[2] = new OpenSource("https://github.com/Clans/FloatingActionButton","FloatingActionButton","一个MD风格的悬浮按钮，自带很多弹窗伸开以及收拢的动画效果，支持自定义菜单，自定义图标");
        data[3]= new OpenSource("https://github.com/greenrobot/greenDAO","GreenDao","Android中最出名也是最推行的ORM框架，效率高于其他一些ORM框架，对原生SQL语言进行了封装，提供大量灵活的借接口，同时支持兼容原生SQL语句");
        data[4] = new OpenSource("https://github.com/bumptech/glide","Glide","一个被google所推荐的图片加载库，会根据ImageView的大小而自动压缩显示图片，自己写的ImageView展示图片，很卡，而这个用起来就很顺滑，很舒服！同时支持网络图片的加载，可以说是非常强大好用了");
        data[5] = new OpenSource("https://github.com/yanzhenjie/SwipeRecyclerView","SwipeRecyclerView","RecyclerView侧滑菜单，Item拖拽，滑动删除Item，自动加载更多，HeaderView，FooterView，Item分组黏贴。Item拖拽排序、侧滑删除也是支持的");
        data[6] = new OpenSource("https://github.com/google/gson","gson","Google官方用来解析json的开源框架，主要用途为序列化Java对象为JSON字符串，或反序列化JSON字符串成Java对象(包括直接解析成集合)");
        data[7] = new OpenSource("https://github.com/GcsSloop/encrypt","encrypt","Android加密解密工具包，作者同样是GcsSloop。支持AES、DES、RSA的对称以及非对称加密、解密，当然也支持Base64、MD5等加密方式");
        data[8] = new OpenSource("https://github.com/promeG/TinyPinyin","TinyPinyin","适用于Java和Android的快速、低内存占用的汉字转拼音库。生成的拼音不包含声调，均为大写；支持自定义词典，支持简体中文、繁体中文；很低的内存占用");
        data[9] = new OpenSource("https://github.com/uccmawei/FingerprintIdentify","FingerprintIdentify","一个可拓展的Android指纹识别API兼容库,目前集成了以下API：安卓API：最低支持安卓6.0系统；三星SDK：最低支持安卓4.2系统；三星SDK：最低支持安卓4.2系统");
        data[10] = new OpenSource("https://github.com/chrisbanes/PhotoView","PhotoView","一个常用的图片预览控件，主要用于Android中大图查看，例如结合ViePager完成朋友圈九宫格图片预览功能，PhotoView主要的功能有，图片手势缩放，旋转，相比ImageView，用户体验更好");
        for (int i = 1;i<=10;i++) {
            dataList.add(data[i]);
        }
    }

    private void initFBI() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initView() {
        UIUtils.setToolbar(OpenSourceActivity.this, toolbar, "使用开源");
        //列数为两列
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutManager.offsetChildrenHorizontal(3);
        layoutManager.offsetChildrenVertical(3);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}
