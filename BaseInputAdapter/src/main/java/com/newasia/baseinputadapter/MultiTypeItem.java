package com.newasia.baseinputadapter;

import android.graphics.Color;
import android.view.View;
import android.widget.Switch;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.net.PortUnreachableException;

public class MultiTypeItem implements MultiItemEntity
{
    public static final int EDIT = 1;
    public static final int SELECT = 2;
    public static final int SHOW = 3;
    public static final int DATETIME = 4;
    public static final int DATA = 5;
    public static final int IMAGE = 6;
    public static final int REMARK = 7;
    public static final int SWITCH = 8;
    public static final int BUTTON = 9;
    public static final int AUTOCOMPLETE = 10;
    public static final int MULTI_SELECT = 11;
    public static final int TWOBUTTON = 12;
    public static final int TREE_SELECT = 13;
    public static final int DIVIDING = 14;


    public int mType = -1;
    public String mLabel = "";
    public StringBuffer mContent;
    public StringBuffer mID;
    public String mstrCommon = "";
    public int mNumCommon = -1;
    public boolean mIsNecessary = false;
    public View mContenView;

    public View.OnClickListener mClickedListener;
    public View.OnClickListener mClickedListener2;
    public String mLabel2 = "";

    public int mHeight = 0;
    public int mColor = Color.WHITE;


    public Switch.OnCheckedChangeListener mCheckChangeListener;


    public MultiTypeItem(int type)
    {
        mType = type;
    }

    public MultiTypeItem(int type,int height,int color)
    {
        mType = type;
        mHeight = height;
        mColor = color;
    }

    public MultiTypeItem(int type, String label, StringBuffer strContent)
    {
        this(type,label,strContent,false);
    }

    public MultiTypeItem(int type, String label, StringBuffer strContent,boolean isNecessary)
    {
        this(type,label,strContent,null,"",-1,isNecessary);
    }


    public MultiTypeItem(int type, String label, StringBuffer strContent, StringBuffer strID, String strCommon, int nCommon)
    {
        this(type,label,strContent,strID,strCommon,nCommon,false);
    }

    public MultiTypeItem(int type, String label, StringBuffer strContent, StringBuffer strID, String strCommon, int nCommon, boolean isNecessary)
    {
        mType = type;
        mLabel = label;
        mContent = strContent;
        mID = strID;
        mstrCommon = strCommon;
        mNumCommon = nCommon;
        mIsNecessary = isNecessary;
    }

    public MultiTypeItem(int type, String label, View.OnClickListener listener)
    {
        mType = type;
        mLabel = label;
        mClickedListener = listener;
    }


    public MultiTypeItem(int type, String label, String label2, View.OnClickListener listener, View.OnClickListener listener2)
    {
        mType = type;
        mLabel = label;
        mLabel2 = label2;
        mClickedListener = listener;
        mClickedListener2 = listener2;
    }


    public MultiTypeItem(int type, String label,boolean bCheck,Switch.OnCheckedChangeListener listener)
    {
        mType = type;
        mLabel = label;
        mCheckChangeListener = listener;
        if(bCheck) mNumCommon = 1;
    }


    @Override
    public int getItemType()
    {
        return mType;
    }

}