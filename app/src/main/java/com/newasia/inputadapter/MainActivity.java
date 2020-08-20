package com.newasia.inputadapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.newasia.baseinputadapter.CommonMultiTypeAdapter;
import com.newasia.baseinputadapter.MultiTypeItem;
import com.newasia.baseinputadapter.Node;
import com.newasia.baseinputadapter.SimpleTreeRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CommonMultiTypeAdapter mAdapter;
    private SimpleTreeRecyclerAdapter treeAdapter;

    private StringBuffer test1 = new StringBuffer("11123");
    private StringBuffer test2 = new StringBuffer("asjdiwja");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        //treeAdapter = new SimpleTreeRecyclerAdapter(mRecyclerView,this,0);
        //mRecyclerView.setAdapter(treeAdapter);

        //getDatasFromServer("select mjxl,mjms from sysmjb where mjbh = 1313");



        mAdapter = new CommonMultiTypeAdapter(this);
        mAdapter.addData(new MultiTypeItem(MultiTypeItem.EDIT,"编辑框",test1,true));
        mAdapter.addData(new MultiTypeItem(MultiTypeItem.SHOW,"SHOW",test2));
        mAdapter.addData(new MultiTypeItem(MultiTypeItem.TREE_SELECT,"部门",test1,test2,"select mjxl,mjms from sysmjb where mjbh = 1313",-1,false));
        mRecyclerView.setAdapter(mAdapter);
    }


//    public void onTextViewClicked(View view) {
//        PopupSelectDlg.Popup(this, "select mjxl,mjms from sysmjb where mjbh = 1313",(name,id)->{
//            Log.e("test",name+" -- "+id);
//        },"");
//    }


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
}