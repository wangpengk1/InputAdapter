package com.newasia.baseinputadapter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonMultiTypeAdapter extends BaseMultiItemQuickAdapter<MultiTypeItem, BaseViewHolder>
{
    private GetImageHelper mImageHelper;
    private Activity mContext;

    public static interface OnAutoCompleteResult
    {
        void onResult(ArrayList<String> list);
    }

    public static interface OnEnumAutoComplete
    {
        void OnEnum(String sql,OnAutoCompleteResult resutListener);
    }

    public static interface OnDownImageResult
    {
        void onResult(boolean bok, Bitmap bitmap);
    }

    public static interface OnDownImageListener
    {
        void onDownImage(String name,OnDownImageResult result);
    }


    public static interface OnUploadImageResult
    {
        void onUploadResult(boolean bok,String name);
    }

    public static interface OnUploadImageListener
    {
        void onUploadImage(File imgFile, OnUploadImageResult listener);
    }

    private static OnEnumAutoComplete mAutoCompleteEnumListener;
    private static OnDownImageListener mDownImageListener;
    private static OnUploadImageListener mUploadImageListener;

    public static void setEnumAutoCompleteMethod(OnEnumAutoComplete method)
    {
        mAutoCompleteEnumListener = method;
    }

    public static void setDownImageListener(OnDownImageListener listener)
    {
        mDownImageListener = listener;
    }

    public static void setUploadImageListener(OnUploadImageListener listener)
    {
        mUploadImageListener = listener;
    }




    public CommonMultiTypeAdapter(Activity context)
    {
        super(null);
        mImageHelper = new GetImageHelper(true,context);
        mContext = context;
        addItemType(MultiTypeItem.EDIT, R.layout.multi_type_item_edit);
        addItemType(MultiTypeItem.BUTTON,R.layout.multi_type_item_button);
        addItemType(MultiTypeItem.DATETIME, R.layout.multi_type_item_show);
        addItemType(MultiTypeItem.SHOW, R.layout.multi_type_item_show);
        addItemType(MultiTypeItem.SELECT, R.layout.multi_type_item_select);
        addItemType(MultiTypeItem.DATA, R.layout.multi_type_item_show);
        addItemType(MultiTypeItem.IMAGE, R.layout.multi_type_item_image);
        addItemType(MultiTypeItem.REMARK, R.layout.multi_type_item_remark);
        addItemType(MultiTypeItem.SWITCH,R.layout.multi_type_item_switch);
        addItemType(MultiTypeItem.MULTI_SELECT,R.layout.multi_type_item_multi_select);

        addItemType(MultiTypeItem.AUTOCOMPLETE, R.layout.multi_type_item_auto_complete);

        addItemType(MultiTypeItem.TWOBUTTON, R.layout.multi_type_item_two_button);

        addItemType(MultiTypeItem.TREE_SELECT,R.layout.multi_type_item_select);

    }


    public boolean checkInputParam()
    {
        boolean ret = false;
        List<MultiTypeItem> items = getData();
        for(MultiTypeItem item:items)
        {
            if (item.mIsNecessary)
            {
                if(item.mContent.toString().isEmpty())
                {
                    CommonUtils.showToast(mContext, item.mLabel+"不能为空！");
                    if(item.mContenView != null)
                    {
                        item.mContenView.requestFocus();
                    }
                    return false;
                }
            }

            if((item.mType == MultiTypeItem.AUTOCOMPLETE || item.mType == MultiTypeItem.EDIT) && item.mID != null)
            {
                if(!item.mContent.toString().matches(item.mID.toString()))
                {
                    CommonUtils.showToast(mContext, item.mLabel+"格式不正确！");
                    if(item.mContenView != null)
                    {
                        item.mContenView.requestFocus();
                    }
                    return false;
                }
            }
        }

        ret = true;

        return ret;
    }


    @Override
    protected void convert(final BaseViewHolder helper,final MultiTypeItem item)
    {
        switch (helper.getItemViewType())
        {
            case MultiTypeItem.EDIT:
            {
                helper.setText(R.id.list_item_edit_label, item.mLabel);
                helper.setText(R.id.list_item_edit_eidt,item.mContent);
                EditText editText = helper.getView(R.id.list_item_edit_eidt);
                item.mContenView = editText;
                if(item.mIsNecessary)
                {
                    editText.setHint("请输入（必填）");
                }
                else
                {
                    editText.setHint("请输入");
                }

                if(item.mNumCommon != -1)
                editText.setInputType(item.mNumCommon);

                helper.setVisible(R.id.list_item_edit_mark, item.mIsNecessary);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.mContent.delete(0, item.mContent.length());
                        item.mContent.append(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
                break;
            case MultiTypeItem.BUTTON:
            {
                helper.setText(R.id.list_item_btn_button, item.mLabel);
                if(item.mClickedListener !=null)
                {
                    Button button = helper.getView(R.id.list_item_btn_button);
                    item.mContenView = button;
                    button.setOnClickListener(item.mClickedListener);
                }
            }
            break;
            case MultiTypeItem.TWOBUTTON:
            {
                helper.setText(R.id.multi_type_item_button1, item.mLabel);
                helper.setText(R.id.multi_type_item_button2, item.mLabel2);
                if(item.mClickedListener !=null)
                {
                    Button button = helper.getView(R.id.multi_type_item_button1);
                    button.setOnClickListener(item.mClickedListener);
                }

                if(item.mClickedListener2 !=null)
                {
                    Button button = helper.getView(R.id.multi_type_item_button2);
                    button.setOnClickListener(item.mClickedListener2);
                }
            }
            break;
            case MultiTypeItem.DATETIME:
            {
                helper.setVisible(R.id.list_item_show_mark, item.mIsNecessary);
                helper.setText(R.id.list_item_show_label, item.mLabel);
                helper.setText(R.id.list_item_show_content, item.mContent.toString());
                TextView textView = helper.getView(R.id.list_item_show_content);
                item.mContenView = textView;
                if(item.mIsNecessary)
                {
                    textView.setHint("选择（必填）");
                }
                else
                {
                    textView.setHint("请选择");
                }
                RelativeLayout layout = helper.getView(R.id.list_item_select_root);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v)
                            {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                helper.setText(R.id.list_item_show_content, format.format(date));
                                helper.setTextColor(R.id.list_item_show_content, Color.BLACK);
                                item.mContent.delete(0, item.mContent.length());
                                item.mContent.append(format.format(date));
                            }
                        }).setType(new boolean[]{true,true,true,true,true,false}).isCenterLabel(true)
                                .build();

                        pvTime.show();
                    }
                });
            }
            break;
            case MultiTypeItem.DATA:
            {
                helper.setVisible(R.id.list_item_show_mark, item.mIsNecessary);
                helper.setText(R.id.list_item_show_label, item.mLabel);
                helper.setText(R.id.list_item_show_content, item.mContent.toString());
                TextView textView = helper.getView(R.id.list_item_show_content);
                item.mContenView = textView;
                if(item.mIsNecessary)
                {
                    textView.setHint("请选择（必填）");
                }
                else
                {
                    textView.setHint("请选择");
                }
                RelativeLayout layout = helper.getView(R.id.list_item_select_root);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v)
                            {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                helper.setText(R.id.list_item_show_content, format.format(date));
                                helper.setTextColor(R.id.list_item_show_content, Color.BLACK);
                                item.mContent.delete(0, item.mContent.length());
                                item.mContent.append(format.format(date));
                            }
                        }).setType(new boolean[]{true,true,true,false,false,false}).isCenterLabel(true)
                                .build();

                        pvTime.show();
                    }
                });
            }
                break;
            case MultiTypeItem.SHOW:
                {
                    helper.setVisible(R.id.list_item_show_mark, item.mIsNecessary);
                    helper.setText(R.id.list_item_show_label, item.mLabel);
                    helper.setText(R.id.list_item_show_content, item.mContent.toString());
                    item.mContenView = helper.getView(R.id.list_item_show_content);
                }
                break;
            case MultiTypeItem.SELECT:
            {
                helper.setVisible(R.id.list_item_select_mark, item.mIsNecessary);
                helper.setText(R.id.list_item_select_label, item.mLabel);
                helper.setText(R.id.list_item_select_content, item.mContent.toString());
                item.mContenView = helper.getView(R.id.list_item_select_content);
                RelativeLayout layout = helper.getView(R.id.list_item_select_root);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        PopupSelectDlg.Popup(mContext,item.mstrCommon,(name,id)->{
                            if(item.mID != null)
                            {
                                item.mID.delete(0, item.mID.length());
                                item.mID.append(id);
                            }
                            item.mContent.delete(0, item.mContent.length());
                            item.mContent.append(name);
                            helper.setText(R.id.list_item_select_content, item.mContent.toString());
                        },"");
                    }
                });
            }
                break;
            case MultiTypeItem.TREE_SELECT:
            {
                helper.setVisible(R.id.list_item_select_mark, item.mIsNecessary);
                helper.setText(R.id.list_item_select_label, item.mLabel);
                helper.setText(R.id.list_item_select_content, item.mContent.toString());
                item.mContenView = helper.getView(R.id.list_item_select_content);
                RelativeLayout layout = helper.getView(R.id.list_item_select_root);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        PopupTreeSelectDlg.Popup(mContext,item.mstrCommon,(name,id)->{
                            if(item.mID != null)
                            {
                                item.mID.delete(0, item.mID.length());
                                item.mID.append(id);
                            }
                            item.mContent.delete(0, item.mContent.length());
                            item.mContent.append(name);
                            helper.setText(R.id.list_item_select_content, item.mContent.toString());
                        },"");
                    }
                });
            }
            break;
            case MultiTypeItem.AUTOCOMPLETE:
            {
                helper.setVisible(R.id.list_item_auto_complete_mark, item.mIsNecessary);
                helper.setText(R.id.list_item_auto_complete_label, item.mLabel);
                helper.setText(R.id.list_item_auto_complete_edit, item.mContent.toString());
                final AutoCompleteTextView textView = helper.getView(R.id.list_item_auto_complete_edit);
                item.mContenView = textView;

                if(item.mIsNecessary)
                {
                    textView.setHint("请输入（必填）");
                    helper.setVisible(R.id.list_item_auto_complete_mark,true);
                }
                else
                {
                    textView.setHint("请输入");
                    helper.setVisible(R.id.list_item_auto_complete_mark,false);
                }

                if(item.mNumCommon != -1)
                    textView.setInputType(item.mNumCommon);

                textView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.mContent.delete(0, item.mContent.length());
                        item.mContent.append(s.toString());
                    }


                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                if(!item.mstrCommon.isEmpty()) {
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.auto_complete_item, R.id.auto_complete_item_tv);
                    if(mAutoCompleteEnumListener!=null)
                    {
                        mAutoCompleteEnumListener.OnEnum(item.mstrCommon,list -> {
                            for(String str:list)
                            {
                                adapter.add(str);
                            }
                            if (adapter.getCount() > 0) {
                                textView.setAdapter(adapter);
                                textView.setThreshold(1);
                            }
                        });
                    }
                }
            }
            break;
            case MultiTypeItem.REMARK:
            {
                helper.setText(R.id.list_item_remark_title, item.mLabel);
                helper.setText(R.id.list_item_remark_edit, item.mContent.toString());
                if(item.mIsNecessary)
                {
                    helper.setVisible(R.id.list_item_remark_mark,true);
                }
                else
                {
                    helper.setVisible(R.id.list_item_remark_mark,false);
                }
                EditText editText = helper.getView(R.id.list_item_remark_edit);
                item.mContenView = editText;
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.mContent.delete(0, item.mContent.length());
                        item.mContent.append(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
            break;
            case MultiTypeItem.IMAGE:
            {
                helper.setText(R.id.list_image_title,item.mLabel);
                final ImageView img0 = helper.getView(R.id.list_image_img0);
                item.mContenView = img0;
                if(item.mContent !=null && !item.mContent.toString().isEmpty() && mDownImageListener!=null)
                {
                    mDownImageListener.onDownImage(item.mContent.toString(),(bok,bitmap)->{
                        img0.setImageBitmap(bitmap);
                    });
                }
                else
                {
                    helper.setText(R.id.list_image_title,item.mLabel+"(未上传)");
                }
//                img0.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v)
//                    {
//                        Intent intent = new Intent();
//                        intent.setClass(mContext, ImagePreviewActivity.class);
//                        intent.putExtra("image", item.mContent.toString());
//                        mContext.startActivity(intent);
//                        return true;
//                    }
//                });

                img0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageHelper.popupGetImageDlg((Activity) mContext, new GetImageHelper.onTakeImageResult() {
                            @Override
                            public void getImageResult(final File imgeFile)
                            {
                                final Bitmap tmpBitmap = BitmapFactory.decodeFile(imgeFile.getAbsolutePath());
                                TransparentProgressDialog.showLoadingMessage(mContext, "上传图片中....", false);
                                if(mUploadImageListener!=null)
                                {
                                    mUploadImageListener.onUploadImage(imgeFile,(bok,name)->{
                                        if(bok)
                                        {
                                            TransparentProgressDialog.showSuccessMessage(mContext, "上传完毕");
                                            item.mContent.delete(0, item.mContent.length());
                                            item.mContent.append(name);
                                            img0.setImageBitmap(tmpBitmap);
                                            helper.setText(R.id.list_image_title,item.mLabel);
                                        }
                                        else TransparentProgressDialog.showErrorMessage(mContext,"保存失败");
                                    });
                                }
                            }
                        });

                    }
                });
            }
                break;
            case MultiTypeItem.MULTI_SELECT:
            {
                helper.setVisible(R.id.multi_select__mark, item.mIsNecessary);
                helper.setText(R.id.multi_select_label, item.mLabel);
                RecyclerView list = helper.getView(R.id.multi_select_list);
                list.setLayoutManager(new GridLayoutManager(mContext, 4, RecyclerView.VERTICAL,false));
                final MultiSelectAdapter adapter = new MultiSelectAdapter(item.mstrCommon,item.mContent.toString(),new MultiSelectAdapter.OnCheckedChange() {
                    @Override
                    public void change(String str)
                    {
                        item.mContent.delete(0, item.mContent.length());
                        item.mContent.append(str);
                    }
                });
                list.setAdapter(adapter);
                adapter.enumData();
            }
            break;
                default:
                    break;
        }
    }

}


