package com.example.dibage.accountb.commonView;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.activitys.MainActivity;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.utils.UIUtils;

/**
 * PopupWindow显示需要的参数：activity,pop的标题以及内容
 */

public abstract class PopWindowTip {
    PopupWindow mPopTip;
    private Context context = MyApplication.getContext();
    private Activity activity;
    private String title;
    private String content;
    private View contentView;

    public PopWindowTip() {
        mPopTip = new PopupWindow();
    }

    public void dismiss() {
        mPopTip.dismiss();
    }


    public void setOutside(boolean state){
        mPopTip.setOutsideTouchable(state);
    }

    public void setFocus(boolean state){
        mPopTip.setFocusable(state);
    }

    public void setBackGround(){
        mPopTip.setBackgroundDrawable(new BitmapDrawable());
    }

    public void update(){
        mPopTip.update();
    }


    public void setTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
        UIUtils.darkenBackgroud(activity, 0.5f);
        mPopTip.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        TextView pop_title = contentView.findViewById(R.id.pop_title);
        TextView pop_message = contentView.findViewById(R.id.pop_message);
        Button btn_cancel = contentView.findViewById(R.id.btn_cancel);
        Button btn_submit = contentView.findViewById(R.id.btn_submit);
        pop_title.setText(title);
        pop_message.setText(content);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopTip.dismiss();
                clickCancel();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickConfirm();
                mPopTip.dismiss();
            }
        });
    }

    public void setTitleAndContent(String title, String content,String confirmText) {
        UIUtils.darkenBackgroud(activity, 0.5f);
        this.title = title;
        this.content = content;
        mPopTip.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        TextView pop_title = contentView.findViewById(R.id.pop_title);
        TextView pop_message = contentView.findViewById(R.id.pop_message);
        Button btn_cancel = contentView.findViewById(R.id.btn_cancel);
        Button btn_submit = contentView.findViewById(R.id.btn_submit);
        pop_title.setText(title);
        pop_message.setText(content);
        btn_submit.setText(confirmText);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopTip.setOnDismissListener(null);
                mPopTip.dismiss();
                clickCancel();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopTip.setOnDismissListener(null);
                mPopTip.dismiss();//这句话一定要放在前面，不然会造成PopWindow不及时变色
                clickConfirm();

            }
        });
    }

    protected abstract void clickCancel();


    public PopWindowTip(final Activity activity) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        this.contentView = inflater.from(activity).inflate(R.layout.pop_tip, null);
        mPopTip = new PopupWindow(contentView,
                activity.getWindowManager().getDefaultDisplay().getWidth() - 200,
                WindowManager.LayoutParams.WRAP_CONTENT, true);

        mPopTip.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                UIUtils.darkenBackgroud(activity, 1.0f);
                dismissTodo();
            }
        });

    }

    protected abstract void dismissTodo();

    public abstract void clickConfirm();
    


}
