import React, { Component } from 'react'
import { View, requireNativeComponent, findNodeHandle, StyleProp, ViewStyle, UIManager, NativeModules, NativeEventEmitter } from 'react-native'
var AliyunPlayer = requireNativeComponent('RNAliplayer');

enum IScaleMode {
    SCALEASPECTFIT = 0,
    SCALEASPECTFILL = 1,
    SCALETOFILL = 2,
}

interface sourceConfig{
    type?:string //url , sts , auth
    url?:string
    vid?:string
    playAuth?:string
    region?:string
    accessKeyId?:string
    accessKeySecret?:string
    securityToken?:string
}

interface AliPlayerProps {
    style?: StyleProp<ViewStyle>;
    source: sourceConfig; // 播放配置
    setAutoPlay?: boolean; // 是否自动播放
    setLoop?: boolean; // 是否循环播放
    setMute?: boolean; //是否静音
    enableHardwareDecoder?: boolean; //是否开启硬件解码
    setVolume?: number; //设置播放器音量,范围0~1.
    setSpeed?: number; //播放速率，0.5-2.0之间，1为正常播放
    setReferer?: string; //设置请求referer
    setUserAgent?: string; // 设置UserAgent
    setMirrorMode?: number; // 0:无镜像;1:横向;2:竖向
    setRotateMode?: number; // 设置旋转 0:0度;1:90度;2:180度;3:270度;
    setScaleMode?: IScaleMode; // 设置画面缩放模式 0:宽高比适应;1:宽高比填充;2:拉伸填充;
    configHeader?: Array<any>; // 配置自定义header
    selectBitrateIndex?: number; // 切换清晰度  选择清晰度的index，-1代表自适应码率

    onAliCompletion?: (e: AliPlayerFuncParams<{ code: "onAliCompletion" }>) => void, // 播放完成事件
    onAliError?: (e: AliPlayerFuncParams<{ code: string; message: string }>) => void, // 出错事件
    onAliLoadingBegin?: (e: AliPlayerFuncParams<{ code: "onAliLoadingBegin"; duration: number; width: number; height: number }>) => void, // 缓冲开始。
    onAliLoadingProgress?: (e: AliPlayerFuncParams<{ percent: number }>) => void, // 缓冲进度
    onAliLoadingEnd?: (e: AliPlayerFuncParams<{ code: "onAliLoadingEnd"; duration: number; width: number; height: number }>) => void, // 缓冲结束
    onAliPrepared?: (e: AliPlayerFuncParams<{ duration: number; width: number; height: number }>) => void, // 准备成功事件
    onAliRenderingStart?: (e: AliPlayerFuncParams<{ code: "onRenderingStart"; duration: number; width: number; height: number }>) => void, // 首帧渲染显示事件
    onAliSeekComplete?: (e: AliPlayerFuncParams<{ code: "onAliSeekComplete" }>) => void, // 拖动结束
    onAliCurrentPositionUpdate?: (e: AliPlayerFuncParams<{ position: number }>) => void, // 播放进度
    onAliBufferedPositionUpdate?: (e: AliPlayerFuncParams<{ position: number }>) => void, // 缓冲进度
    onAliAutoPlayStart?: (e: AliPlayerFuncParams<{ code: "onAliAutoPlayStart"; duration: number; width: number; height: number }>) => void, // 自动播放开始
    onAliLoopingStart?: (e: AliPlayerFuncParams<{ code: "onAliLoopingStart" }>) => void, // 循环播放开始
    onAliBitrateChange?: (e: AliPlayerFuncParams<{ index: number; width: number; height: number }>) => void, // 切换清晰度
    onAliBitrateReady?: (e: AliPlayerFuncParams<{ index: number; width: number; height: number; bitrate: number }>) => void, // 获取清晰度回调
}

interface AliPlayerFuncParams<T> {
    nativeEvent: T
}

//播放器
export class AliPlayer extends Component<AliPlayerProps>{
    // constructor(props: AliPlayerProps) {
    //   super(props)

    // }

    componentWillUnmount() {
        this.stopPlay();
        this.destroyPlay();
    }

    _assignRoot = (component) => {
        this._root = component;
    };

    _dispatchCommand = (command, params = []) => {
        if (this._root) {
            UIManager.dispatchViewManagerCommand(findNodeHandle(this._root), command, params);
        }
    };

    setNativeProps = (props) => {
        if (this._root) {
            this._root.setNativeProps(props);
        }
    };

    // 开始播放。
    startPlay = () => {
        this._dispatchCommand('startPlay');
    };

    // 暂停播放
    pausePlay = () => {
        this._dispatchCommand('pausePlay');
    };

    // 停止播放
    stopPlay = () => {
        this._dispatchCommand('stopPlay');
    };

    // 重载播放
    reloadPlay = () => {
        this._dispatchCommand('reloadPlay');
    };

    // 重新播放
    restartPlay = () => {
        this._dispatchCommand('restartPlay');
    };

    // 释放。释放后播放器将不可再被使用
    destroyPlay = () => {
        this._dispatchCommand('destroyPlay');
    };

    // 跳转到指定位置,传入单位为秒
    seekTo = (position = 0) => {
        if (typeof position === 'number') {
            this._dispatchCommand('seekTo', [position]);
        }
    };

    render() {
        return (
            <AliyunPlayer ref={this._assignRoot} {...this.props} />
        )
    }
}

import RNFetchBlob from "rn-fetch-blob";

const path:string = RNFetchBlob.fs.dirs.DownloadDir;
const configName:string = "ali.config";
//下载器
export class AliDow{
    private vid:string;
    private config:Config;
    private index: number | undefined;
    private callback:((schedule: any) => void) | undefined
    private eventName = ["onPrepared","onDowProgress","onProcessing","onError","onCompletion"]
    //onPrepared:预处理;onDowProgress:下载进度;onProcessing:处理进度;onError:错误信息; onCompletion:完成回调


    public static readonly path:string = path;

    public readonly path:string = path;

    //配置文件名称
    public static readonly configName:string = configName;

    public readonly configName:string = configName;


    //--------辅助函数--------
    //文件配置读取
    public static async readJSON(){
        return JSON.parse(await RNFetchBlob.fs.readFile(`${this.path}/${configName}`,'utf8')); //读取json
    };

    public async readJSON(){
        return JSON.parse(await RNFetchBlob.fs.readFile(`${this.path}/${configName}`,'utf8')); //读取json
    };

    //文件配置保存
    public static async writeJSON(d:any){
        return await RNFetchBlob.fs.writeFile(`${this.path}/${configName}`, JSON.stringify(d),'utf8'); //保存json
    };

    public async writeJSON( d:any){
        return await RNFetchBlob.fs.writeFile(`${this.path}/${configName}`, JSON.stringify(d),'utf8'); //保存json
    };


    //--------构造函数--------

    constructor(config:Config) {
        this.vid = config.vid
        if(!config.region){
            config.region = "cn-shanghai" //默认区域
        }
        this.config = config

    }


    //开始下载
    public async dow(callback?:(res:any)=>void){
        this.index = await NativeModules.RNSafeDow.init(this.config);
        Count.size++
        //获取记录
        let logContent:AliConfig[] = []
        const logFileName = this.path+'/'+this.configName
        if (await RNFetchBlob.fs.exists(logFileName)){
            //读取数据
            logContent =await this.readJSON()
        }


        this.initEvent(logContent,callback||undefined)
    }

    //事件回调
    private initEvent(logContent:AliConfig[],callback?:(res:any)=>void){
        const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
        //读取单个记录
        let ins =-1;
        let log:AliConfig = {
            name:this.config.name||Date.now()+'',
            vid:this.vid,
            path:this.config.path+"/"+this.vid+"_0.mp4",
            status:0,
            dowSchedule:0
        };
        for (let i = 0; i < logContent.length; i++) {
            if (logContent[i].vid==this.vid){
                ins = i;
                log = logContent[i]
                break;
            }
        }

        if (ins==-1){
            ins = logContent.length
        }
        logContent[ins] = log
        //-----------
        for (let i=0;i<this.eventName.length;i++) {
            let key = this.eventName[i];
            eventEmitter.addListener(key, async (event) => {
                if(this.vid==event.vid){
                    if(callback){
                        callback({...event,type:key,index:Count.size})
                    }
                }
                // console.log(event) // "someValue"

                if(Count.size>0 && key=="onError"||key == "onCompletion"){
                    Count.size--
                }

                if(key=="onCompletion" && log.status==0){
                    log.status = 1;
                    await this.writeJSON(logContent)
                }

                if (key=="onDowProgress"){
                    //写入文件
                    log.dowSchedule = parseInt(event.percent)

                    await this.writeJSON(logContent)
                }
            })
        }



    }

}

interface Config{
    path: string
    vid:string
    region?: string
    name?: string
    playAuth:string
}

class Count{
    static size:number = 0
}
interface AliConfig{
    name:string;
    vid:string;
    dowSchedule:number;
    path:string;
    status:number;
}


export default {AliPlayer,AliDow}
