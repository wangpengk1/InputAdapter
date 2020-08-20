package com.newasia.baseinputadapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PopupSelectDlg {

    public static void Popup(final Context contex, String strSql, OnItemSelected listener, String addition) {
        final AlertDialog dialog = new AlertDialog.Builder(contex).create();

        ListAdapter adapter = new ListAdapter(contex, strSql, addition);
        final RecyclerView listView = new RecyclerView(contex);
        listView.setLayoutManager(new LinearLayoutManager(contex, RecyclerView.VERTICAL, false));
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter2,view,position)->{
            ListItem item = adapter.getItem(position);
            listener.itemSelected(item.name,item.id);
            dialog.cancel();
        });


        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                int height = v.getHeight();     //此处的view 和v 其实是同一个控件
                int contentHeight = listView.getHeight();

                WindowManager wm = (WindowManager) contex.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int needHeight = dm.heightPixels / 2;

                if (contentHeight > needHeight) {
                    //注意：这里的 LayoutParams 必须是 FrameLayout的！！
//                    listView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                            needHeight));
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.height = needHeight;
                    dialog.getWindow().setAttributes(params);
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
        //params.flags = params.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        //params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.white);
    }




    public static interface OnItemSelected {
        void itemSelected(String name, String id);
    }

    private static class ListItem {
        public String name = "";
        public String id = "";
    }

    public static interface OnLoadListFromServer {
        void onEnumList(String sql, OnEnumListResult listener);
    }

    public static interface OnEnumListResult {
        void onResult(Map<String, String> resultList);
    }


    private static OnLoadListFromServer OnLoadListener;

    public static void  setOnLoadDataListener(OnLoadListFromServer listener)
    {
        OnLoadListener = listener;
    }

    private static class ListAdapter extends BaseQuickAdapter<ListItem, BaseViewHolder> {
        private String mSql = "";
        private String mAddition = "";
        private Context mContext;

        public ListAdapter(Context context, String sql, String addition) {
            super(R.layout.list_item, null);
            mContext = context;
            mSql = sql;
            mAddition = addition;
            if (OnLoadListener != null) {
                OnLoadListener.onEnumList(sql, resultList -> {
                    if(addition!=null && !addition.isEmpty())
                    {
                        ListItem item = new ListItem();
                        item.id = "-1";
                        item.name = addition;
                        addData(item);
                    }
                    for (String key : resultList.keySet()) {
                        ListItem item = new ListItem();
                        item.id = key;
                        item.name = resultList.get(key);
                        addData(item);
                    }
                    notifyDataSetChanged();
                });
            }
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ListItem item) {
            holder.setText(R.id.list_item_text, item.name);
        }


    }

}