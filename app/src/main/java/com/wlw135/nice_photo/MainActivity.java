package com.wlw135.nice_photo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button ShowBtn;
    private Button updateBtn;
    private ImageView ShowImg;
    private ArrayList<Bean> data;
    private int page = 1;
    private int curPos = 0;
    private PictureLoader loader;
    private NetworkApi networkApi;
    private BeanTask beanTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkApi = new NetworkApi();
        loader = new PictureLoader();
        initPicture();
        initControl();
    }

    private void initControl() {
        ShowBtn = (Button) findViewById(R.id.show_btn);
        ShowImg = (ImageView) findViewById(R.id.show_Img);
        ShowBtn.setOnClickListener(this);
        updateBtn = (Button) findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(this);
    }

    private void initPicture() {
        data = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_btn:
                if (data != null && !data.isEmpty()) {//每9页一轮回
                    if (curPos > 9) {
                        curPos = 0;
                    }
                    loader.load(ShowImg, data.get(curPos).getUrl());
                    curPos++;
                }
                break;
            case R.id.update_btn:
                beanTask = new BeanTask();
                beanTask.execute();
                curPos = 0;
                break;

        }
    }
    private class BeanTask extends AsyncTask<Void,Void,ArrayList<Bean>> {

        public BeanTask() { }

        @Override
        protected ArrayList<Bean> doInBackground(Void... params) {
            return networkApi.fetchBean(10,page);
        }

        @Override
        protected void onPostExecute(ArrayList<Bean> beans) {
            super.onPostExecute(beans);
            data.clear();
            data.addAll(beans);
            page++;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            beanTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beanTask.cancel(true);
    }
}
