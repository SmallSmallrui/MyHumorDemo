package com.hurui.myhumordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private final static int GOTO_PUBLISH_HUMOR_REQUEST = 1 ;

    UploadManager uploadManager;
    OkHttpClient client;
    Request request;
    //服务端集成了七牛后提供的接口
    String url = "http://112.74.28.179:8080/Chislim/Travel_notes_Servlet?dowhat=getUpLoadToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initConfig();
        //实例化client
        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建OkHttpClient
                client = new OkHttpClient();
                // 创建请求
                request = new Request.Builder()//
                        .url(url)//
                        .get()//
                        .build();
            }
        }).start();
    }

    //对七牛的初始化设置
    private void initConfig() {
        Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                //       .recorder(recorder)  // recorder分片上传时，已上传片记录器。默认null
                //       .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
    }

    //获取七牛的token后进入到发说说的页面
    public void gotoHumor(View view){
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                String token = response.body().string();
                Log.i("七牛", token);

                //第三步跳转至 发布说说页面
                Intent intent = new Intent(MainActivity.this , PublishHumorActivity.class);
                intent.putExtra("token" , token) ;
                startActivityForResult(intent , GOTO_PUBLISH_HUMOR_REQUEST);
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i("七牛", e.getMessage());
            }
        });
    }

}
