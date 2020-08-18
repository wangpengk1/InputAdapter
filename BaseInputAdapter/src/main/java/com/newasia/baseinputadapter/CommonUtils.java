package com.newasia.baseinputadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import static android.content.Context.WINDOW_SERVICE;


public class CommonUtils
{
    //private static  ArrayList<BitmapDrawable> mBackGroundList = new ArrayList<>();
    private static ArrayList<Integer> mBackGroundList = new ArrayList<>();



    public static String transfromDate(String strDate)
    {
        String strTime = "";
        if(strDate.length()>=16)
        {
            strTime = strDate.substring(10,16);
        }
        String strRet = strDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try
        {
            date = sdf.parse(strDate);
        }catch (ParseException e) {e.printStackTrace(); return strDate;}
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);

        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        target.set(Calendar.HOUR, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);


        long intervalMilli = target.getTimeInMillis() - now.getTimeInMillis();
        int xcts = (int) Math.ceil (intervalMilli /(24.00 * 60.00 * 60.00 * 1000.00));
        // -2:前天 -1：昨天 0：今天 1：明天 2：后天， out：显示日期
        if (xcts==0)
        {
            strRet = "今天"+strTime;
        }
        if (xcts==-2)
        {
            strRet = "前天"+strTime;
        }
        if (xcts==-1)
        {
            strRet = "昨天"+strTime;
        }
        if (xcts==1)
        {
            strRet = "明天"+strTime;
        }
        if (xcts==2)
        {
            strRet = "后天"+strTime;
        }
        if (xcts>2 || xcts<-2)
        {
            if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR))
            {
                strRet = (target.get(Calendar.MONTH)+1)+"月"+target.get(Calendar.DAY_OF_MONTH)+"日 "+strTime;
            }
            else
            {
                strRet = (target.get(Calendar.MONTH)+1)+"月"+target.get(Calendar.DAY_OF_MONTH)+"日 "+strTime;
            }
        }

        return strRet;
    }




    public static void FilteringData(JSONObject data)
    {
        try
        {
            for (int i=0;i<data.length();++i)
            {
                if (data.getString(""+i).matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.000"))
                {
                    String strTmp = data.getString(""+i).replace("T", " ").replace(".000", "");
                    strTmp = strTmp.substring(0,16);
                    data.put(""+i, strTmp);
                }
            }
        }catch (JSONException e){e.printStackTrace();}
    }

    public static int getRandomNum(int startNum,int endNum){
        if(endNum > startNum){
            Random random = new Random();
            return random.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }

    public static Point getScreenSize(Context context)
    {
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Point pt = new Point();
        manager.getDefaultDisplay().getSize(pt);
        return  pt;
    }


    public static void showToast(Context context, String strMsg)
    {
        Toast toast = Toast.makeText(context, strMsg, Toast.LENGTH_LONG);
        WindowManager manager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        Point pt = new Point();
        manager.getDefaultDisplay().getSize(pt);
        toast.setGravity(Gravity.TOP, 0, pt.y/5);
        toast.show();

    }




    public static void OnSoftKeyBoradAssistActivity (Activity activity) {
        new SoftHideKeyBoardUtil(activity);
    }


    public static class SoftHideKeyBoardUtil {
        private View mChildOfContent;
        private int usableHeightPrevious;
        private FrameLayout.LayoutParams frameLayoutParams;
        //为适应华为小米等手机键盘上方出现黑条或不适配
        private int contentHeight;//获取setContentView本来view的高度
        private boolean isfirst = true;//只用获取一次
        private  int statusBarHeight;//状态栏高度
        private SoftHideKeyBoardUtil(Activity activity) {
            //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
            FrameLayout content = activity.findViewById(android.R.id.content);
            //2､获取到setContentView放进去的View
            mChildOfContent = content.getChildAt(0);
            //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                //4､软键盘弹起会使GlobalLayout发生变化
                public void onGlobalLayout() {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                        isfirst = false;
                    }
                    //5､当前布局发生变化时，对Activity的xml布局进行重绘
                    possiblyResizeChildOfContent();
                }
            });
            //6､获取到Activity的xml布局的放置参数
            frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        }

        // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度
        private void possiblyResizeChildOfContent() {
            //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
            int usableHeightNow = computeUsableHeight();
            //2､如果当前可用高度和原始值不一样
            if (usableHeightNow != usableHeightPrevious) {
                //3､获取Activity中xml中布局在当前界面显示的高度
                int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                //4､Activity中xml布局的高度-当前可用高度
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                //5､高度差大于屏幕1/4时，说明键盘弹出
                if (heightDifference > (usableHeightSansKeyboard/4)) {
                    // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
                    } else {
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    }
                } else {
                    frameLayoutParams.height = contentHeight;
                }
                //7､ 重绘Activity的xml布局
                mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        }
        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
            return (r.bottom - r.top);
        }
    }



    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }


            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    public static  View.OnTouchListener onClickScale = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()== MotionEvent.ACTION_DOWN)
            {
                v.setScaleX(1.5f);
                v.setScaleY(1.5f);
                return true;
            }
            if(event.getAction()== MotionEvent.ACTION_UP)
            {
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
                v.callOnClick();
            }
            return false;
        }
    };
}
