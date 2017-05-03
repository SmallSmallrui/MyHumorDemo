package com.hurui.myhumordemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import cn.fanrunqi.waveprogress.WaveProgressView;

/**
 * Created by lenovo on 2017/4/20.
 */

public class HumorImgAndVideoAdpter extends RecyclerView.Adapter {
    private Context mContext ;
    private List<HumorImgAndVideoBean> lists ;

    private OnMyItemClick onMyItemClick ;

    public void setOnMyItemClick(OnMyItemClick onMyItemClick) {
        this.onMyItemClick = onMyItemClick;
    }

    public interface OnMyItemClick{
        public void addData();
        public void ImageClick(int position) ;
        public void VideoClick() ;
        public void addDataNoVideo();
        public void deleteItem(int position);
    }

    public HumorImgAndVideoAdpter(Context mContext , List<HumorImgAndVideoBean> lists){
        this.mContext = mContext ;
        this.lists    = lists ;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.humor_img_video_item , parent , false) ;
        return new ImgAndVideoHodler(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ImgAndVideoHodler holder1 = (ImgAndVideoHodler) holder;
        holder1.icon_cancel.setTag(position);
        //当list没有数据时
        if(lists == null || lists.size() == 0){
            holder1.icon_cancel.setVisibility(View.GONE);
            holder1.waveProgressbar.setVisibility(View.GONE);
            Picasso.with(mContext).load(R.mipmap.btn_add).into(holder1.tu_pian);
            holder1.tu_pian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onMyItemClick != null){
                        onMyItemClick.addData();
                    }
                }
            });
        }else {
            //当list有数据的时候
            //1 .只有一条是视频的数据
            if(lists.get(0).isVideo == true){
                /*Picasso.with(mContext).load(new File(lists.get(position).path) ).resize(200 , 200).into(holder1.tu_pian);*/
                holder1.waveProgressbar.setVisibility(View.VISIBLE);
                holder1.icon_cancel.setVisibility(View.VISIBLE);
                if(lists.get(0).proess != 100) {
                    holder1.waveProgressbar.setCurrent(lists.get(0).proess, lists.get(0).proess + "%");
                }else{
                    holder1.waveProgressbar.setVisibility(View.GONE);
                }
                Glide.with(mContext).load(new File(lists.get(position).path)).into(holder1.tu_pian);
                holder1.tu_pian.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onMyItemClick != null){
                            onMyItemClick.VideoClick();
                        }
                    }
                });
            }else{
                //2.全是图片的时候，
                if(lists.size() >= 9){
                    holder1.waveProgressbar.setVisibility(View.VISIBLE);
                    holder1.icon_cancel.setVisibility(View.VISIBLE);
                    if(lists.get(position).proess != 100) {
                        holder1.waveProgressbar.setCurrent(lists.get(position).proess, lists.get(position).proess + "%");
                    }else{
                        holder1.waveProgressbar.setVisibility(View.GONE);
                    }

                    Picasso.with(mContext).load(new File(lists.get(position).path) ).resize(200 , 200).into(holder1.tu_pian);
                    holder1.tu_pian.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onMyItemClick != null){
                                onMyItemClick.ImageClick(position);
                            }
                        }
                    });
                }else{
                    if(position != lists.size() ){
                        holder1.waveProgressbar.setVisibility(View.VISIBLE);
                        holder1.icon_cancel.setVisibility(View.VISIBLE);
                        if(lists.get(position).proess != 100) {
                            holder1.waveProgressbar.setCurrent(lists.get(position).proess, lists.get(position).proess + "%");
                        }else{
                            holder1.waveProgressbar.setVisibility(View.GONE);
                        }

                        Picasso.with(mContext).load(new File(lists.get(position).path) ).resize(200 , 200).into(holder1.tu_pian);
                        holder1.tu_pian.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(onMyItemClick != null){
                                    onMyItemClick.ImageClick(position);
                                }
                            }
                        });
                    }else{
                        holder1.waveProgressbar.setVisibility(View.GONE);
                        holder1.icon_cancel.setVisibility(View.GONE);
                        Picasso.with(mContext).load(R.mipmap.btn_add).into(holder1.tu_pian);
                        holder1.tu_pian.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(onMyItemClick != null){
                                    onMyItemClick.addDataNoVideo();
                                }
                            }
                        });
                    }

                }
            }
        }


        holder1.icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMyItemClick != null){
                    onMyItemClick.deleteItem(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(lists == null || lists.size() == 0 || lists.get(0).isVideo == true){
            return 1 ;
        }
        if(lists.size() >= 9 ){
            return 9 ;
        }
        return lists.size() + 1;
    }

    public class ImgAndVideoHodler extends RecyclerView.ViewHolder{

        public ImageView tu_pian ;

        public ImageView icon_cancel ;

        public WaveProgressView waveProgressbar ;

        public ImgAndVideoHodler(View itemView) {
            super(itemView);
           // ButterKnife.bind(this , itemView);
            tu_pian = (ImageView) itemView.findViewById(R.id.tu_pian);
            icon_cancel = (ImageView) itemView.findViewById(R.id.delete_iv);
            waveProgressbar = (WaveProgressView)itemView.findViewById(R.id.waveProgressbar);
            waveProgressbar.setMaxProgress(100);
        }
    }

}
