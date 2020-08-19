package com.newasia.inputadapter;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class SqlHelper {
    private String drive = "net.sourceforge.jtds.jdbc.Driver";
    private String connStr;
    private String server;
    private String dbName;
    private String userName;
    private String userPwd;
    private int port;
    private Connection con;
    private PreparedStatement pstm;
    private static SqlHelper s_instance;


    public static String s_IP = "222.141.67.173";
    public static String s_dbName = "RZERP_HNXY";

    public static SqlHelper getInstance()
    {
        if (s_instance==null)
        {
            s_instance = new SqlHelper(s_IP, 9033, s_dbName, "sjb", "newasia");
        }

        return s_instance;
    }

    private SqlHelper(String server, int nPort, String dbName, String userName, String userPwd) {
        this.server = server;
        this.dbName = dbName;
        this.userName = userName;
        this.userPwd = userPwd;
        this.port = nPort;
        this.connStr = "jdbc:jtds:sqlserver://" + this.server + ":" + port + "/" + this.dbName;
        try {
            Class.forName(drive);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int ExecuteNonQuery(String sql, List<Object> params) {
        try {
            con = DriverManager.getConnection(this.connStr, this.userName, this.userPwd);
            pstm = con.prepareStatement(sql);
            if (params != null && !params.equals("")) {
                for (int i = 0; i < params.size(); i++) {
                    pstm.setObject(i + 1, params.get(i));
                }
            }
            return pstm.executeUpdate();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return -1;
        } finally {
            try {
                pstm.close();
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private String ExecuteQuery(String sql, List<Object> params) {
        // TODO Auto-generated method stub
        JSONArray jsonArray = new JSONArray();
        try {
            con = DriverManager.getConnection(this.connStr, this.userName, this.userPwd);
            pstm = con.prepareStatement(sql);
            if (params != null && !params.equals("")) {
                for (int i = 0; i < params.size(); i++) {
                    pstm.setObject(i + 1, params.get(i));
                }
            }
            ResultSet rs = pstm.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();

            JSONObject headObject = new JSONObject();
            for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
                String columnName = rsMetaData.getColumnLabel(i + 1);
                try {
                    headObject.put(new String("" + i), columnName.trim());
                } catch (Exception e) {
                    //return new String();
                }

            }
            jsonArray.put(headObject);


            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
                    String columnName = rsMetaData.getColumnLabel(i + 1);
                    String value = rs.getString(columnName);
                    try {
                        jsonObject.put(new String("" + i), value);
                    } catch (Exception e) {
                        // TODO: handle exception
                        //return new String();
                    }

                }
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new String();
    }


    public  interface onQueryResult
    {
        void doSomeing(JSONObject Ret);
    }

    public  void runQuery(final String strSql,onQueryResult listener)
    {
        SqlTask task = new SqlTask(listener);
        task.execute(strSql);

    }


    class SqlTask extends AsyncTask<String, Void, JSONObject>
    {
        private onQueryResult listener;
        public SqlTask(onQueryResult onResult)
        {
            listener = onResult;
        }
        @Override
        protected JSONObject doInBackground(String... strings)
        {
            JSONObject retObj = new JSONObject();
            String strSql = strings[0];
            boolean result = true;
            String ret = null;
            if(strSql.substring(0, 6).compareToIgnoreCase("insert")==0 || strSql.substring(0, 6).compareToIgnoreCase("update")==0
             || strSql.substring(0, 6).compareToIgnoreCase("delete")==0)
            {
                int count = ExecuteNonQuery(strSql, null);
                if(count != 1)  result = false;
                try
                {
                    retObj.put("result", result);
                }catch (JSONException e){e.printStackTrace();}
            }
            else
            {
                ret = ExecuteQuery(strSql, null);
                try
                {
                    if(ret.isEmpty())
                    {
                        result = false;
                    }
                    else
                    {

                        JSONArray jsonArray = new JSONArray(ret);
                        if (jsonArray.length()>1)
                        {
                            for (int i=1;i<jsonArray.length();++i)
                            {
                                retObj.put("row"+i, jsonArray.getJSONObject(i));
                            }
                            retObj.put("field", jsonArray.getJSONObject(0));

                        }
                        else
                        {
                            result = false;
                        }
                    }

                    retObj.put("result", result);

                }catch (JSONException e){e.printStackTrace();}

            }

            return retObj;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            listener.doSomeing(s);
        }
    }






}