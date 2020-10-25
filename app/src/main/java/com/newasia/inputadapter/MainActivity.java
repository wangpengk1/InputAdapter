package com.newasia.inputadapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.newasia.baseinputadapter.CommonMultiTypeAdapter;
import com.newasia.baseinputadapter.GetImageHelper;
import com.newasia.baseinputadapter.MultiTypeItem;
import com.newasia.baseinputadapter.Node;
import com.newasia.baseinputadapter.SimpleTreeRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CommonMultiTypeAdapter mAdapter;
    private SimpleTreeRecyclerAdapter treeAdapter;

    private StringBuffer test1 = new StringBuffer("11123");
    private StringBuffer test2 = new StringBuffer("asjdiwja");
    private StringBuffer mOutImage = new StringBuffer("");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        //treeAdapter = new SimpleTreeRecyclerAdapter(mRecyclerView,this,0);
        //mRecyclerView.setAdapter(treeAdapter);

        //getDatasFromServer("select mjxl,mjms from sysmjb where mjbh = 1313");


        Map<Integer,Integer> typeMap = new Hashtable<>();
        typeMap.put(14,R.layout.custom_item_layout);
        mAdapter = new CommonMultiTypeAdapter(this,mFilter,typeMap);

        mAdapter.addData(new MultiTypeItem(MultiTypeItem.EDIT,"编辑框",test1,true));
        mAdapter.addData(new MultiTypeItem(MultiTypeItem.SHOW,"SHOW",test2));
        mAdapter.addData(new MultiTypeItem(14));
        mAdapter.addData(new MultiTypeItem(MultiTypeItem.TREE_SELECT,"部门",test1,test2,"select mjxl,mjms from sysmjb where mjbh = 1313",-1,false));
        MultiTypeItem item = new MultiTypeItem(MultiTypeItem.IMAGE, "里程照", mOutImage,null,"",-1,false);
        mAdapter.addData(item);
        mRecyclerView.setAdapter(mAdapter);
    }


//    public void onTextViewClicked(View view) {
//        PopupSelectDlg.Popup(this, "select mjxl,mjms from sysmjb where mjbh = 1313",(name,id)->{
//            Log.e("test",name+" -- "+id);
//        },"");
//    }

    private CommonMultiTypeAdapter.ConvertFilter mFilter = new CommonMultiTypeAdapter.ConvertFilter() {
        @Override
        public void onConvert(BaseViewHolder helper, MultiTypeItem item) {
            if(item.getItemType()==14)
            {
                Log.e("test","11111");
            }
        }
    };

    public void getDatasFromServer(String sql)
    {
        SqlHelper.getInstance().runQuery(sql, new SqlHelper.onQueryResult() {
            @Override
            public void doSomeing(JSONObject Ret)
            {
                try
                {
                    if (Ret.getBoolean("result"))
                    {
                        ArrayList<Node> listAdd = new ArrayList<>();
                        for (int i=1;i<Ret.length()-1;++i)
                        {
                            JSONObject tmpObj = Ret.getJSONObject("row"+i);
                            String code = tmpObj.getString("0");
                            String name = tmpObj.getString("1");
                            if (code.length()<=2)
                            {
                                listAdd.add(new Node(code, "-1", name));
                            }
                            else
                            {
                                int len = code.length();
                                if(len%2==0)
                                {
                                    listAdd.add(new Node(code, code.substring(0,len-2), name));
                                }
                                else
                                {
                                    listAdd.add(new Node(code, code.substring(0,len-3), name));
                                }

                            }
                        }

                        if (listAdd.size()>0)
                        {
                            treeAdapter.addData(listAdd);
                            treeAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                    }


                }catch (JSONException e){e.printStackTrace();}

            }
        });
    }


    public static class TreeItem
    {
        public String name;
        public String id;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GetImageHelper.getInstance(true,this).onActivityResult(requestCode,resultCode,data);
    }
}