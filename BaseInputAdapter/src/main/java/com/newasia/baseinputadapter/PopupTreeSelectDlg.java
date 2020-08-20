package com.newasia.baseinputadapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


public class PopupTreeSelectDlg {
    private static SimpleTreeRecyclerAdapter mAdapter;
    public static void Popup(final Context contex, String strSql, OnItemSelected listener, String addition) {
        final AlertDialog dialog = new AlertDialog.Builder(contex).create();



        View view = ((Activity)contex).getLayoutInflater().inflate(R.layout.popup_tree_content, null);
        RecyclerView listView = view.findViewById(R.id.popup_tree_listview);
        listView.setLayoutManager(new LinearLayoutManager(contex,RecyclerView.VERTICAL,false));
        ProgressBar bar = view.findViewById(R.id.popup_tree_progress);
        mAdapter = new SimpleTreeRecyclerAdapter(listView,contex,0);
        listView.setAdapter(mAdapter);
        dialog.setView(view);


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


        mAdapter.setOnItemSelectedListener(node -> {
            listener.itemSelected(node.getName(),node.getId()+"");
            dialog.cancel();
        });


        if(OnLoadListener!=null)
        {
            OnLoadListener.onEnumList(strSql,resultList -> {
                Log.e("test","load completed");
                mAdapter.addData(resultList);
                mAdapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
            });
        }
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
        void onResult(ArrayList<Node> resultList);
    }


    private static OnLoadListFromServer OnLoadListener;

    public static void  setOnLoadDataListener(OnLoadListFromServer listener)
    {
        OnLoadListener = listener;
    }
}

