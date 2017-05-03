package com.hurui.myhumordemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class PublishHumorActivity extends HBaseActivity {
    private String token ; //七牛的token

    public final static int CHECK_IMAGE_REQUEST = 23;
    public final static int CHECK_VIDEO_REQUEST = 24;


    public  final static int YANSUO_SUCCESS = 11;
    private final static int SHANG_SUCCESS = 12 ;
    private final static int SHANG_PORESS = 13 ;

    //popwindow
    private View view ;

    @BindView(R.id.precleView)
    RecyclerView precleView ;

    @BindView(R.id.add_content)
    EditText add_content ;

    //关闭界面
    @OnClick(R.id.close_iv)
    void clickClose(){
        finish();
    }


    public ArrayList<HumorImgAndVideoBean> lists ;

    private HumorImgAndVideoAdpter adapter ;

    private MyHandler myHandler = new MyHandler() ;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                switch (msg.what) {
                    case SHANG_SUCCESS:
                        adapter.notifyItemChanged(msg.arg1);
                        break;
                    case SHANG_PORESS:
                        adapter.notifyItemChanged(msg.arg1);
                        break;
                    case YANSUO_SUCCESS :

                        //进行图片的上传
                        getUpimg(lists.get(msg.arg1));
                        break;
                }
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_humor);

        token = getIntent().getStringExtra("token") ;

        lists = new ArrayList<HumorImgAndVideoBean>() ;

        precleView.setLayoutManager(new GridLayoutManager(this , 3));

        adapter = new HumorImgAndVideoAdpter(this , lists);

        precleView.setAdapter(adapter);

        adapter.setOnMyItemClick(new HumorImgAndVideoAdpter.OnMyItemClick() {
            @Override
            public void addData() {
               /* Toast.makeText(mActivity, "没有数据加载数据", Toast.LENGTH_SHORT).show();
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(mActivity);
                intentPhoto.setPhotoCount(9 - lists.size());
                intentPhoto.setShowCamera(true);
                startActivityForResult(intentPhoto,CHECK_IMAGE_REQUEST );*/
                /*Intent intent = new Intent();
                intent.setType("video*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHECK_VIDEO_REQUEST);*/

                if (view == null) {
                    view = LayoutInflater.from(mActivity).inflate(R.layout.layout_popupwindow , null);
                }
                TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
                TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
                TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
                final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                popupWindow.setOutsideTouchable(true);
                View parent = LayoutInflater.from(mActivity).inflate(R.layout.activity_publish_humor
                        , null);
                popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
                //popupWindow在弹窗的时候背景半透明
                final WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.5f;
                getWindow().setAttributes(params);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        params.alpha = 1.0f;
                        getWindow().setAttributes(params);
                    }
                });

                btnCarema.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到调用系统相机
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, CHECK_VIDEO_REQUEST);
                        popupWindow.dismiss();
                    }
                });
                btnPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到调用系统图库
                        PhotoPickerIntent intentPhoto = new PhotoPickerIntent(mActivity);
                        intentPhoto.setPhotoCount(9 - lists.size());
                        intentPhoto.setShowCamera(true);
                        startActivityForResult(intentPhoto,CHECK_IMAGE_REQUEST );
                        popupWindow.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

            }

            @Override
            public void ImageClick(int position) {
                Toast.makeText(mActivity, "查看大图", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mActivity , PhotoActivity.class);
                PhotoActivity.bitmap = lists ;
                intent.putExtra("ID" , position) ;
                startActivity(intent);
            }

            @Override
            public void VideoClick() {
               //用来播放视频
            }

            @Override
            public void addDataNoVideo() {
                Toast.makeText(mActivity, "添加过了图像，不能添加视频了", Toast.LENGTH_SHORT).show();
                PhotoPickerIntent intentPhoto = new PhotoPickerIntent(mActivity);
                intentPhoto.setPhotoCount(9 - lists.size());
                intentPhoto.setShowCamera(true);
                startActivityForResult(intentPhoto,CHECK_IMAGE_REQUEST );
            }

            @Override
            public void deleteItem(int position) {
                lists.remove(position) ;
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ){
            switch (requestCode){
                case CHECK_IMAGE_REQUEST :
                    //选择图片后返回的路径集合
                    List<String> mResultsPath = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);

                    for(int i = 0 ; i < mResultsPath.size() ; i++){
                        final int postion = i ;
                        final HumorImgAndVideoBean bean = new HumorImgAndVideoBean();
                        bean.path = mResultsPath.get(i);

                        //命名文件的名字(按照时间进行)
                        bean.fileName = System.currentTimeMillis()+""+i;
                        lists.add(bean);

                        //压缩图片
                        Luban.get(this)
                                .setFilename(bean.fileName)  //设置压缩完成后的文件名
                                .load(new File(bean.path))                     //传人要压缩的图片
                                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                                .setCompressListener(new OnCompressListener() { //设置回调
                                    @Override
                                    public void onStart() {
                                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                    }
                                    @Override
                                        public void onSuccess(File file) {
                                            // TODO 压缩成功后调用，返回压缩后的图片文件
                                        //由于压缩的异步的,所以在此通过文件名进行比对 , 当图片已经过小时则不进行压缩.
                                        String name2 = file.getName().split("\\.")[0];
                                            for (int i = 0 ; i< lists.size() ; i++){
                                                String name1 = lists.get(i).fileName;
                                                String pa = lists.get(i).path;
                                                if(name1.equals(name2) || file.getAbsolutePath().equals(pa)){
                                                    lists.get(i).compresPath = file.getPath() ;
                                                    Message message = myHandler.obtainMessage() ;
                                                    message.what = YANSUO_SUCCESS ;
                                                    message.arg1 = i ;
                                                    myHandler.sendMessage(message) ;
                                                }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO 当压缩过去出现问题时调用
                                        Log.d("出现了问题" , e.getMessage()) ;
                                    }
                                }).launch();    //启动压缩
                        adapter.notifyDataSetChanged();
                    }


                    break;
                case CHECK_VIDEO_REQUEST :
                    HumorImgAndVideoBean bean = new HumorImgAndVideoBean();
                    bean.isVideo = true ;
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null,
                            null, null);
                    cursor.moveToFirst();
                    // String imgNo = cursor.getString(0); // 图片编号
                    String v_path = cursor.getString(1); // 图片文件路径
                    bean.path = v_path ;
                    lists.add(bean);
                    adapter.notifyDataSetChanged();
                    lists.get(0).compresPath = lists.get(0).path ;
                    getUpimg(lists.get(0));
                    break;
            }
        }
    }


    public void getUpimg(HumorImgAndVideoBean bean) {
        new MyThread(bean).start();
    }

    public class MyThread extends Thread{
        HumorImgAndVideoBean bean ;
        public MyThread(HumorImgAndVideoBean bean){
            this.bean = bean ;
        }

        @Override
        public void run() {
            // 图片上传到七牛 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
            UploadManager uploadManager = new UploadManager();
            uploadManager.put(bean.compresPath, bean.fileName, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info,
                                             JSONObject res) {
                            try {
                                // 七牛返回的文件名
                                if(res != null) {
                                    String upimg = res.getString("key");
                                    for (int i = 0; i < lists.size(); i++) {
                                        if (lists.get(i).fileName.equals(key)) {
                                            lists.get(i).urlPath = "http://ofplk6att.bkt.clouddn.com/" + upimg;
                                            lists.get(i).proess = 100;
                                            Message message = myHandler.obtainMessage();
                                            message.what = SHANG_SUCCESS;
                                            message.arg1 = i;
                                            myHandler.sendMessage(message);
                                        }
                                    }
                                }
                                //list集合中图片上传完成后，发送handler消息回主线程进行其他操作
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },  new UploadOptions(null, null, false,             //上传进度
                            new UpProgressHandler() {
                                public void progress(String key, double percent) {
                                    double progress = percent * 100;
                                    for(int i = 0 ; i < lists.size() ; i++){
                                        if(lists.get(i).compresPath != null && lists.get(i).fileName.equals(key)){
                                            lists.get(i).proess = (int) progress;
                                            Message message = myHandler.obtainMessage() ;
                                            message.what = SHANG_PORESS ;
                                            message.arg1 = i ;
                                            myHandler.sendMessage(message);
                                        }
                                    }
                                }
                            }, null));
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
