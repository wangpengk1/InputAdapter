package com.newasia.baseinputadapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class TransparentProgressDialog {

    private static CustomProgressDialog dialog;
    private static Context context;

    public static void showLoadingMessage(Context context, String msg, boolean cancelable) {
        dismiss();
        setDialog(context, msg, R.drawable.transparent_spinner, cancelable);
        if(dialog!=null) dialog.show();

    }

    public static void showErrorMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.drawable.transparent_error, true);
        if(dialog!=null) {
            dialog.show();
            dismissAfter2s();
        }
    }

    public static void showSuccessMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.drawable.transparent_success, true);
        if(dialog!=null) {
            dialog.show();
            dismissAfter2s();
        }
    }

    public static void showInfoMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.drawable.transparent_info, true);
        if(dialog!=null) {
            dialog.show();
            dismissAfter2s();
        }
    }



    private static void setDialog(Context ctx, String msg, int resId, boolean cancelable) {
        context = ctx;

        if(!isContextValid()){
            return;
        }
        dialog = CustomProgressDialog.createDialog(ctx);
        dialog.setMessage(msg);
        dialog.setImage(ctx, resId);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);		// back键是否可dimiss对话框


    }

    /**
     * 关闭对话框
     */
    public static void dismiss() {
        if(isContextValid() && dialog!=null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }


    /**
     * 计时关闭对话框
     *
     */
    private static void dismissAfter2s() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==0)
                dismiss();
        };
    };


    /**
     * 判断parent view是否还存在
     * 若不存在不能调用dismis，或setDialog等方法
     * @return
     */
    private static boolean isContextValid() {
        if(context==null)
            return false;
        if(context instanceof Activity) {
            Activity act = (Activity)context;
            if(act.isFinishing())
                return false;
        }
        return true;
    }

}


class CustomProgressDialog extends Dialog
{
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
        CustomProgressDialog dialog = new CustomProgressDialog(context, R.style.Transparent);
        dialog.setContentView(R.layout.transparent);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return dialog;
    }

    public void setMessage(String message) {
        TextView msgView = (TextView)findViewById(R.id.transparent_message);
        msgView.setText(message);
    }

    public void setImage(Context ctx, int resId) {
        ImageView image = (ImageView)findViewById(R.id.transparent_image);
        image.setImageResource(resId);

        if(resId==R.drawable.transparent_spinner) {
            Animation anim = AnimationUtils.loadAnimation(ctx,R.anim.progressbar);
            anim.start();
            image.startAnimation(anim);
        }

    }
}