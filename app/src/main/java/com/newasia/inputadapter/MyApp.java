package com.newasia.inputadapter;

import android.app.Application;
import android.util.Log;

import com.newasia.baseinputadapter.CommonMultiTypeAdapter;
import com.newasia.baseinputadapter.Node;
import com.newasia.baseinputadapter.PopupSelectDlg;
import com.newasia.baseinputadapter.PopupTreeSelectDlg;
import com.xuexiang.xaop.XAOP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XAOP.init(this);
        CommonMultiTypeAdapter.setUploadImageListener((imgFile, listener) -> {
            byte[] data = loadImage(imgFile);
            Log.e("test",data.length+"");
        });
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


    public static byte[] loadImage(File file){
        //用于返回的字节数组
        byte[] data=null;
        //打开文件输入流
        FileInputStream fin=null;
        //打开字节输出流
        ByteArrayOutputStream bout=null;
        try{
            //文件输入流获取对应文件
            fin=new FileInputStream(file);
            //输出流定义缓冲区大小
            bout=new ByteArrayOutputStream((int)file.length());
            //定义字节数组，用于读取文件流
            byte[] buffer=new byte[1024];
            //用于表示读取的位置
            int len=-1;
            //开始读取文件
            while((len=fin.read(buffer))>0){
                //从buffer的第0位置开始，读取至第len位置，结果写入bout
                bout.write(buffer,0,len);
            }
            //将输出流转为字节数组
            data=bout.toByteArray();
            //关闭输入输出流
            fin.close();
            bout.close();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return data;
    }

}
