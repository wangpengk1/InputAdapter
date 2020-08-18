package com.newasia.baseinputadapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PopupSelectDlg
{
    public static void Popup(final Context contex, String strSql, OnSelectedRun runnable, String addition)
    {
        AlertDialog dialog = new AlertDialog.Builder(contex).create();

        PopupSelectListAdapter adapter = new PopupSelectListAdapter(contex,dialog, strSql,runnable,addition);
        final ListView listView = new ListView(contex);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.setAdapter(adapter);


        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                int height = v.getHeight();     //此处的view 和v 其实是同一个控件
                int contentHeight = listView.getHeight();

                WindowManager wm = (WindowManager) contex.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int needHeight = dm.heightPixels/2;

                if (contentHeight > needHeight) {
                    //注意：这里的 LayoutParams 必须是 FrameLayout的！！
                    listView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            needHeight));
                }
            }
        });//设置弹出对话框只有窗口高度的一半，以防列表长度太大遮盖住了整个窗口

        dialog.setView(listView);




        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle);  //添加动画\
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;//显示dialog的时候,就显示软键盘
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.white);
    }


    public static abstract class OnSelectedRun implements Runnable
    {
        private String strText;
        private Dialog dlg;
        private String id = "-1";
        public String getStrText() {
            return strText;
        }

        public void setStrText(String strText) {
            this.strText = strText;
        }

        public Dialog getDlg() {
            return dlg;
        }

        public void setDlg(Dialog dlg) {
            this.dlg = dlg;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}


class PopupSelectListAdapter extends BaseAdapter
{
    private Context mContex;
    private String strSql;
    private List<String> datas = new LinkedList<>();
    private List<String> datas2 = new LinkedList<>();
    private PopupSelectDlg.OnSelectedRun mRun;
    private AlertDialog mDlg;
    private float downX,downY;
    private String mAddition;


    PopupSelectListAdapter(Context context, AlertDialog dlg, String sql, PopupSelectDlg.OnSelectedRun run, String add)
    {
        mDlg = dlg;
        mRun = run;
        strSql = sql;
        mContex = context;

        if (add!=null && !add.isEmpty())
        {
            datas.add(add);
            datas2.add("-1");
            mAddition =add;
        }

        final JSONObject object = new JSONObject();
        try
        {
            object.put("statement", sql);
        }catch (JSONException e){}

        //根据传入的数据库查询语句，获取到相应的待选内容，填充对话框的ListView
        ClientNetty client = new ClientNetty(object, Common.Role.DrivingRecord.ordinal(), new Handler(), new ClientNetty.ResultRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (isOk)
                    {
                        if (obj.getBoolean("result"))
                        {

                            for (int i=0;i<obj.length()-2;++i)
                            {
                                JSONObject tmpObj = obj.getJSONObject("row"+i);
                                datas.add(tmpObj.getString("0"));
                                if (tmpObj.length()>1)//如果有查询字段的数目为两个，就准备两个TextView的数据
                                {
                                    datas2.add(tmpObj.getString("1"));
                                }

                            }

                            notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(mContex, obj.getString("error"), Toast.LENGTH_LONG);
                        }
                    }
                    else
                    {
                        Toast.makeText(mContex, strError, Toast.LENGTH_LONG);
                    }

                }catch (JSONException e){e.printStackTrace();}
            }
        });


        new Thread(client).start();
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        if (convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = ((Activity)mContex).getLayoutInflater().inflate(R.layout.list_item, null);
        }

        updateView(position, view);
        return  view;
    }


    void updateView(final int pos, View view)
    {
        ((TextView)view.findViewById(R.id.list_item_text)).setText(datas.get(pos));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mRun.setStrText(datas.get(pos));
                if (datas2.size() == datas.size())
                {
                    mRun.setId(datas2.get(pos));
                }

                mRun.setDlg(mDlg);
                new Handler().postDelayed(mRun, 500);
            }
        });

    }
}
