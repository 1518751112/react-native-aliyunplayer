package com.aliyunplay;


import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.downloader.AliDownloaderFactory;
import com.aliyun.downloader.AliMediaDownloader;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.private_service.PrivateService;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.IllegalViewOperationException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class SafeDownload extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private static final String REACT_CLASS = "RNSafeDow";
    private AliMediaDownloader mAliDownloaderArr[] =new AliMediaDownloader[5];
    private int length = 0;



    public SafeDownload(ReactApplicationContext context) {
        super(context);
        Log.i("路径",context.getPackageCodePath());

        try {
            AssetManager assets = context.getResources().getAssets();

            InputStream open = assets.open("encryptedApp.dat");
            Log.i("打开open",open.toString());
            byte[] data = toByteArray(open);
            open.close();

            PrivateService.initService(context,data);


        } catch (IOException e) {
            Log.i("打开open出错",e.getMessage());
            e.printStackTrace();
        }
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void show(String message, Promise promise) {
        try {
            WritableMap map = Arguments.createMap();
            map.putString("text","dd");
            promise.resolve(map);
        } catch (IllegalViewOperationException e) {
            promise.reject(e.getMessage());
        }
    }

    @ReactMethod
    public void init(ReadableMap config,Promise promise){
        int i = length+1;
        if(i==mAliDownloaderArr.length){
            promise.reject("下载数量过多");
        }
        DowConfig con = new DowConfig();

        VidAuth aliyunVidAuth = this.detect(con,config);


        //创建下载器
        AliMediaDownloader mAliDownloader = AliDownloaderFactory.create(this.reactContext);
        //配置下载保存的路径
        mAliDownloader.setSaveDir(con.path);

        int index=0;
        for (int ia=0;ia<mAliDownloaderArr.length;i++){
            if(mAliDownloaderArr[ia]==null){
                index = ia;
                break;
            }
        }

        //初始化事件监听
        this.initListener(mAliDownloader,index,aliyunVidAuth);
        mAliDownloaderArr[index] = mAliDownloader;
        length++;



        //准备下载源
        mAliDownloader.prepare(aliyunVidAuth);

        promise.resolve(index);

    }

    private void sendEvent(ReactContext reactContext,String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private void initListener(AliMediaDownloader mAliDownloader,int index,VidAuth vidAuth){

        mAliDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                //准备下载项成功
                List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                Log.i("hint","准备下载项成功");
                //比如：下载第一个TrackInfo
                mAliDownloader.selectItem(trackInfos.get(0).getIndex());

                //开始下载
                mAliDownloader.start();

                //监听
                WritableMap params = Arguments.createMap();
                params.putString("url",mediaInfo.getCoverUrl());
                params.putString("type",mediaInfo.getMediaType());
                params.putInt("code",200);
                params.putString("vid",vidAuth.getVid());
                sendEvent(reactContext, "onPrepared", params);
            }
        });
        mAliDownloader.setOnProgressListener(new AliMediaDownloader.OnProgressListener() {
            @Override
            public void onDownloadingProgress(int percent) {
                //下载进度百分比
                WritableMap params = Arguments.createMap();
                params.putString("percent",percent+"");
                params.putInt("code",200);
                params.putString("vid",vidAuth.getVid());
                sendEvent(reactContext, "onDowProgress", params);

//                Log.i("下载进度",percent+"");

            }
            @Override
            public void onProcessingProgress(int percent) {
                //处理进度百分比
                WritableMap params = Arguments.createMap();
                params.putString("percent",percent+"");
                params.putInt("code",200);
                params.putString("vid",vidAuth.getVid());
                sendEvent(reactContext, "onProcessing", params);
            }
        });
        mAliDownloader.setOnErrorListener(new AliMediaDownloader.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                //下载出错
                mAliDownloader.stop();
                mAliDownloader.release();
                //移除
                mAliDownloaderArr[index] = null;
                length--;

                WritableMap params = Arguments.createMap();
                params.putString("vid",vidAuth.getVid());
                params.putInt("code",400);
                params.putString("msg",errorInfo.getMsg());
                sendEvent(reactContext, "onError", params);

                Log.i("hint:下载出错"+errorInfo.getExtra(),errorInfo.getMsg());
            }
        });
        mAliDownloader.setOnCompletionListener(new AliMediaDownloader.OnCompletionListener() {
            @Override
            public void onCompletion() {



                WritableMap params = Arguments.createMap();
                params.putString("vid",vidAuth.getVid());
                params.putInt("code",200);
                params.putString("msg","下载完成");
                params.putString("filePath",mAliDownloader.getFilePath());
                sendEvent(reactContext, "onCompletion", params);

                Log.i("hint","下载成功");
                try {
                    Log.i("saveFilePath",mAliDownloader.getFilePath());
                }catch (Exception e){
                    Log.i("查看报错",e.getMessage());
                }
                //下载成功
                mAliDownloader.stop();
                mAliDownloader.release();
                //移除
                mAliDownloaderArr[index] = null;
                length--;
            }
        });

        AliMediaDownloader.setConvertURLCallback(new AliMediaDownloader.ConvertURLCallback() {
            @Override
            public String convertURL(String s, String s1) {
                Log.i("原始地址",s);
                Log.i("原始格式",s1);
                return null;
            }
        });
    }

    @ReactMethod
    public void start(int index,ReadableMap config,Promise promise){
        try {
            if(mAliDownloaderArr[index]==null){
                promise.reject("位置索引错误");
            }
            DowConfig con = new DowConfig();

            VidAuth aliyunVidAuth = this.detect(con,config);


            //开始下载
            mAliDownloaderArr[index].updateSource(aliyunVidAuth);
            mAliDownloaderArr[index].start();

        }catch (Exception e){
            promise.reject("位置索引错误");
        }

    }

    @ReactMethod
    public void stop(int index,Promise promise){
        try {
            if(mAliDownloaderArr[index]==null){
                promise.reject("位置索引错误");
            }
            //停止下载
            mAliDownloaderArr[index].stop();

        }catch (Exception e){
            promise.reject("位置索引错误");
        }

    }

    @ReactMethod
    public void release(int index,Promise promise){
        try {
            if(mAliDownloaderArr[index]==null){
                promise.reject("位置索引错误");
            }
            //释放下载
            mAliDownloaderArr[index].release();

        }catch (Exception e){
            promise.reject("位置索引错误");
        }

    }

    //辅助方法
    private byte[] InputStream2ByteArray(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();

        return data;
    }

    private byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    private VidAuth detect(DowConfig con,ReadableMap config){

        if(config.hasKey("path")){
            con.path =(String) config.getString("path");
        }

        if(config.hasKey("playAuth")){
            con.playAuth =(String) config.getString("playAuth");
        }
        if(config.hasKey("vid")){
            con.vid =(String) config.getString("vid");

        }
        if(config.hasKey("region")){
            con.region =(String) config.getString("region");

        }
        VidAuth aliyunVidAuth = new VidAuth();
        aliyunVidAuth.setVid(con.vid);
        aliyunVidAuth.setPlayAuth(con.playAuth);
        aliyunVidAuth.setRegion(con.region);
        return aliyunVidAuth;
    }
}

class DowConfig{
    String path;
    String vid;
    String playAuth;
    String region;
}
