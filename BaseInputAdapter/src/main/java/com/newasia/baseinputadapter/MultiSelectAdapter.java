package com.newasia.baseinputadapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;


import java.util.ArrayList;
import java.util.List;

public class MultiSelectAdapter extends BaseQuickAdapter<MultiSelectAdapter.CheckItem, BaseViewHolder>
{

    public static class CheckItem
    {
        public String name = "";
        public String id = "";
        boolean isChecked = false;
    }

    public interface OnLoadResult
    {
        void onResult(ArrayList<CheckItem> list);
    }

    public interface OnLoadItemsFromServer
    {
        void onLoadItems(String sql,OnLoadResult resultListener);
    }


    private static OnLoadItemsFromServer mLoadListener;

    public static void setLoadItemsListener(OnLoadItemsFromServer listener)
    {
        mLoadListener = listener;
    }

    private String mSql = "";
    private OnCheckedChange mChangeListener;

    private ArrayList<String> mIDList = new ArrayList<>();

    public interface OnCheckedChange
    {
        void change(String str);
    }


    public MultiSelectAdapter(String sql, String ids, OnCheckedChange listener)
    {
        super(R.layout.multi_checked_item,null);
        mSql = sql;
        mChangeListener = listener;
        if(ids != null && !ids.isEmpty())
        {
            String[] strList = ids.split(":");
            for (String str:strList)
            {
                mIDList.add(str);
            }
        }
    }

    private boolean isContainID(String id)
    {
        boolean ret =false;
        if(id ==null && id.isEmpty()) return ret;
        for(String str:mIDList)
        {
            if(str.compareToIgnoreCase(id)==0)
            {
                return true;
            }
        }
        return ret;
    }

    private String createCheckedString()
    {
        String ret = "";
        List<CheckItem> checkItems = getData();
        for(CheckItem item:checkItems)
        {
            if(item.isChecked)
            {
                ret = ret+item.id+":";
            }
        }
        return ret;
    }



    @Override
    protected void convert(BaseViewHolder helper, final CheckItem item)
    {
        helper.setText(R.id.multi_check_item_checkBox, item.name);
        CheckBox box = helper.getView(R.id.multi_check_item_checkBox);
        box.setChecked(item.isChecked);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                item.isChecked = isChecked;
                mChangeListener.change(createCheckedString());
            }
        });
    }


    public void enumData()
    {
        getData().clear();
        if(mLoadListener!=null)
        {
            mLoadListener.onLoadItems(mSql,list -> {
                for(CheckItem item:list)
                {
                    if(isContainID(item.id))
                    {
                        item.isChecked = true;
                    }

                    addData(item);
                }
                notifyDataSetChanged();
            });
        }
    }

}


