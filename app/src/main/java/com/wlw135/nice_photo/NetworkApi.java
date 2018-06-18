package com.wlw135.nice_photo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 10716 on 2018/6/17.
 */

public class NetworkApi {
    private static final String TAG = "Network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    public ArrayList<Bean> fetchBean(int count, int page) {
        String fetchUrl = BASE_URL + count + "/" + page;
        ArrayList<Bean> beans = new ArrayList<>();
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.v(TAG, "Server response" + code);
            if (code == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data, "UTF-8");
                beans = parseBean(result);
            } else {
                Log.e(TAG, "失败" + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beans;
    }

    public ArrayList<Bean> parseBean(String content) throws Exception {
        ArrayList<Bean> beans = new ArrayList<>();
        JSONObject jsonArray = new JSONObject(content);
        JSONArray array = jsonArray.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject results = (JSONObject) array.get(i);
            Bean bean = new Bean();
            bean.set_id(results.getString("_id"));
            bean.setCreateAt(results.getString("createdAt"));
            bean.setDesc(results.getString("desc"));
            bean.setPublishedAt(results.getString("publishedAt"));
            bean.setSource(results.getString("source"));
            bean.setType(results.getString("type"));
            bean.setUrl(results.getString("url"));
            bean.setUsed(results.getBoolean("used"));
            bean.setWho(results.getString("who"));
            beans.add(bean);
        }
        return beans;
    }


    public byte[] readFromStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }
}
