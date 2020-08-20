package com.newasia.baseinputadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class SimpleTreeRecyclerAdapter extends TreeRecyclerAdapter {

    public interface OnItemSelected
    {
        void onSelected(Node node);
    }


    private OnItemSelected mSelectedListener;

    public void setOnItemSelectedListener(OnItemSelected listener)
    {
        mSelectedListener = listener;
    }

    public SimpleTreeRecyclerAdapter(RecyclerView mTree, Context context, int defaultExpandLevel) {
        super(mTree, context, new ArrayList<Node>(), defaultExpandLevel,R.mipmap.tree_ex,R.mipmap.tree_ec);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHoder(View.inflate(mContext, R.layout.list_tree_item,null));
    }

    @Override
    public void onBindViewHolder(final Node node, RecyclerView.ViewHolder holder, int position) {

        final MyHoder viewHolder = (MyHoder) holder;
        //todo do something
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(node,viewHolder.cb.isChecked());
                if(mSelectedListener!=null && viewHolder.cb.isChecked())
                {
                    mSelectedListener.onSelected(node);
                }
            }
        });


        if (node.isChecked()){
            viewHolder.cb.setChecked(true);
        }else {
            viewHolder.cb.setChecked(false);
        }

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }

        viewHolder.label.setText(node.getName());


    }

    class MyHoder extends RecyclerView.ViewHolder{

        public CheckBox cb;

        public TextView label;

        public ImageView icon;
        public MyHoder(View itemView) {
            super(itemView);

            cb = (CheckBox) itemView
                    .findViewById(R.id.cb_select_tree);
            label = (TextView) itemView
                    .findViewById(R.id.id_treenode_label);
            icon = (ImageView) itemView.findViewById(R.id.icon);

        }

    }
}