package com.wlw135.nice_photo;
/**
 * Created by Administrator on 2018/6/19.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 10716 on 2018/6/19.
 */

public class SisterApi {
    private static final String TAG = "Network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    /**
     * 查询妹子信息
     */
    /*public ArrayList<Sister> fetchSister(int count, int page) {
        String fetchUrl = BASE_URL + count + "/" + page;
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.v(TAG, "Server response：" + code);
            if (code == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data, "UTF-8");
                Log.d("result:",""+result);
                sisters = parseSister(result);
            } else {
                Log.e(TAG,"请求失败：" + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sisters;
    }*/


    /**
     * 解析返回Json数据的方法
     */
    public ArrayList<Sister> parseSister(String picStr) {
        ArrayList<Sister> sisters = new ArrayList<>();
        JSONObject object = null;
        try {
            object = new JSONObject(picStr);
            JSONArray array = object.getJSONArray("results");
            Log.d("长度：" , String.valueOf(+array.length()));
            for (int i = 0; i < array.length(); i++) {
                JSONObject results = (JSONObject) array.get(i);
                Sister sister = new Sister();
                sister.set_id(results.getString("_id"));
                sister.setCreatedAt(results.getString("createdAt"));
                sister.setDesc(results.getString("desc"));
                sister.setPublishedAt(results.getString("publishedAt"));
                sister.setSource(results.getString("source"));
                sister.setType(results.getString("type"));
                sister.setUrl(results.getString("url"));
                sister.setUsed(results.getBoolean("used"));
                sister.setWho(results.getString("who"));
                sisters.add(sister);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sisters;
    }
    String picStr;
    public ArrayList<Sister> fetchSister(int count, int page){
        ArrayList<Sister> sisters = new ArrayList<>();
        String fetchUrl = BASE_URL + count + "/" + page;
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(fetchUrl)//请求的url
                .get()
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        //请求错误回调方法
        try {
            Response response = call.execute();
            picStr=response.body().string().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  parseSister(picStr);

    }

    /**
     * 读取流中数据的方法
     */
    public byte[] readFromStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len ;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

}