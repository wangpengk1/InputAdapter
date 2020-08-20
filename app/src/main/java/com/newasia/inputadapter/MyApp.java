package com.newasia.inputadapter;

import android.app.Application;

import com.newasia.baseinputadapter.Node;
import com.newasia.baseinputadapter.PopupSelectDlg;
import com.newasia.baseinputadapter.PopupTreeSelectDlg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PopupTreeSelectDlg.setOnLoadDataListener(((sql, listener) -> {
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

                            listener.onResult(listAdd);

                        }
                    }catch (JSONException e){e.printStackTrace();}

                }
            });
        }));


        PopupSelectDlg.setOnLoadDataListener(((sql, listener) -> {
            SqlHelper.getInstance().runQuery(sql,Ret -> {
                try {
                    if(Ret.getBoolean("result"))
                    {
                        Map<String, String> dataMap = new Hashtable<>();
                        for(int i=1;i<Ret.length()-1;++i)
                        {
                            JSONObject rowObj = Ret.getJSONObject("row"+i);
                            dataMap.put(rowObj.getString("0"),rowObj.getString("1"));
                        }
                        listener.onResult(dataMap);
                    }
                }catch (JSONException e){e.printStackTrace();}
            });
        }));
    }

}
