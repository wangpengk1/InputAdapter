package com.newasia.inputadapter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.newasia.baseinputadapter.PopupSelectDlg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopupSelectDlg.setOnLoadDataListener(((sql, listener) -> {
            SqlHelper.getInstance().runQuery(sql,Ret -> {
                try {
                    if(Ret.getBoolean("result"))
                    {
                        Map<String, String>  dataMap = new Hashtable<>();
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


    public void onTextViewClicked(View view) {
        PopupSelectDlg.Popup(this, "select mjxl,mjms from sysmjb where mjbh = 1313",(name,id)->{
            Log.e("test",name+" -- "+id);
        },"");
    }
}