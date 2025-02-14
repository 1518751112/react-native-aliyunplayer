# react-native-lewin-aliyunplayer
阿里云播放器 react native,播放暂停等1.0版本没有UI，需自定义


## Table of contents
- [Install](#install)
- [Usage](#usage)

## 支持版本
2025/2/14
支持rn 0.76.6
android.AliyunPlayer:6.12.0
ios.AliyunPlayer:6.12.0

## Install
### 1: yarn add 或者npm install
`yarn add @tg1518/react-native-lewin-aliyunplayer `

### 2: android需要配置build.gradle maven {url 'https://maven.aliyun.com/repository/releases'}
```xml
allprojects {
    repositories {
        mavenLocal()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url("$rootDir/../node_modules/react-native/android")
        }
        maven {
            // Android JSC is installed from npm
            url("$rootDir/../node_modules/jsc-android/dist")
        }
        maven {
            url 'https://maven.aliyun.com/repository/releases'
        }
        google()
        jcenter()
        maven { url 'https://www.jitpack.io' }
    }
}
```
### 3: android需要配置License
将证书文件拷贝到Android Studio项目中的assets目录下。
使用全球配置，不用国际的
具体操作：https://help.aliyun.com/zh/vod/developer-reference/quick-integration-1
若配置License后校验失败，您可以检查<meta-data>节点是否处于<application>元素下面，且<meta-data>的name是否正确。若未能解决问题，您可以参考License相关常见问题进行排查。

针对国际站用户，若需使用播放器SDK，且播放器SDK为6.14.0及以上版本，请务必配置国际站环境License；若不使用播放器SDK，可以仅配置全球环境License。

您可以同时接入2套License（1套全球环境License，1套国际站环境License），后续在App每次启动后，通过配置播放器SDK的服务环境，来指定播放器的运行环境。播放器运行过程中，不支持切换环境。
```xml
// 全球环境配置License（默认配置）
<meta-data
        android:name="com.aliyun.alivc_license.licensekey"
        android:value="foIVziMaUHaRqgDyhf6b6eb8fcf014af39535d0720a32****"/>  <!-- TODO:请设置您的 LicenseKey值-->
<meta-data
android:name="com.aliyun.alivc_license.licensefile"
android:value="assets/cert/release.crt"/>  <!-- TODO:请设置您的 LicenseFile文件路径-->


        // 国际站环境配置License，手动在末尾添加_SEA
<meta-data
android:name="com.aliyun.alivc_license.licensekey_SEA"
android:value="f6b6foIVziMaUHaRqgDyheb8fcf014af39535d0a32720****"/>  <!-- TODO:请设置您的 LicenseKey值-->
<meta-data
android:name="com.aliyun.alivc_license.licensefile_SEA"
android:value="assets/cert/release.crt"/>  <!-- TODO:请设置您的 LicenseFile文件路径-->
```

### 4: android需要配置License
在Xcode工程里，将获取到的证书文件AliVideoCert-********.crt拷贝到Xcode的项目中，建议放到AppSupportFiles目录下，也可以放到沙箱或者其他路径。并在Target Membership中选中当前项目。
使用全球配置，不用国际的
具体操作：https://help.aliyun.com/zh/vod/developer-reference/quick-integration
```xml
//全球环境License配置（默认配置）
<key>AlivcLicenseFile</key>
<string>XXX</string>
<key>AlivcLicenseKey</key>
<string>foIVziMaUHaRqgDyhf6b6eb8fcf014af39535d0720a32****</string>


        // 国际站环境License配置，手动在末尾添加_SEA
<key>AlivcLicenseFile_SEA</key>
<string>XXX</string>
<key>AlivcLicenseKey_SEA</key>
<string>f6b6efoIVziMaUHaRqgDyhb8fcf014af39535d0a32072****</string>
```

## 播放组件
```javascript
import AliModule from "@tg1518/react-native-lewin-aliyunplayer";
const {AliDow,AliPlayer} = AliModule


const authSource = {
    type:'auth',
    vid:"b0fbe08f.......65646b66988",
    playAuth:"eyJTZWN1cml0eVRva2VuIjoi....",
    region:"cn-shanghai"
}
const urlSource = {
    type:'url',
    url:'https://d-appimg.doctopia.com.cn/video/1626170384423967.mp4',
}
    
this.player?.startPlay()// 开始播放
this.player?.pausePlay()//暂停播放
this.player?.stopPlay() //停止播放 会销毁组件
this.player?.reloadPlay() //重载播放
this.player?.restartPlay() //重新播放
this.player?.destroyPlay() //释放。释放后播放器将不可再被使用
this.player?.seekTo() //跳转到指定位置,传入单位为秒
    
<AliPlayer
    ref={(e) => this.player = e}
    style={{ flex: 1 }}
    source={urlSource}
    setAutoPlay={true}
    setLoop={true}
    onAliCurrentPositionUpdate={(e) => {
        // console.log(e.nativeEvent)
    }}
    onAliPrepared={(e) => {
        console.log(e.nativeEvent)
    }}
/>
```

> 属性和方法回调，可以看index.tsx

```javascript

interface AliPlayerProps {
    style?: StyleProp<ViewStyle>;
    source?: string; // 播放地址
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
    setScaleMode?: number; // 设置画面缩放模式 0:宽高比适应;1:宽高比填充;2:拉伸填充;
    configHeader?: Array<any>; // 配置自定义header
    selectBitrateIndex?: number; // 切换清晰度  选择清晰度的index，-1代表自适应码率

    onAliCompletion?: (e: AliPlayerFuncParams<{ code: "onAliCompletion" }>) => void, // 播放完成事件
    onAliError?: (e: AliPlayerFuncParams<{ code: string; message: string }>) => void, // 出错事件
    onAliLoadingBegin?: (e: AliPlayerFuncParams<{ code: "onAliLoadingBegin" }>) => void, // 缓冲开始。
    onAliLoadingProgress?: (e: AliPlayerFuncParams<{ percent: number }>) => void, // 缓冲进度
    onAliLoadingEnd?: (e: AliPlayerFuncParams<{ code: "onAliLoadingEnd" }>) => void, // 缓冲结束
    onAliPrepared?: (e: AliPlayerFuncParams<{ duration: number }>) => void, // 准备成功事件
    onAliRenderingStart?: (e: AliPlayerFuncParams<{ code: "onRenderingStart" }>) => void, // 首帧渲染显示事件
    onAliSeekComplete?: (e: AliPlayerFuncParams<{ code: "onAliSeekComplete" }>) => void, // 拖动结束
    onAliCurrentPositionUpdate?: (e: AliPlayerFuncParams<{ position: number }>) => void, // 播放进度
    onAliBufferedPositionUpdate?: (e: AliPlayerFuncParams<{ position: number }>) => void, // 缓冲进度
    onAliAutoPlayStart?: (e: AliPlayerFuncParams<{ code: "onAliAutoPlayStart" }>) => void, // 自动播放开始
    onAliLoopingStart?: (e: AliPlayerFuncParams<{ code: "onAliLoopingStart" }>) => void, // 循环播放开始
    onAliBitrateChange?: (e: AliPlayerFuncParams<{ index: number; width: number; height: number }>) => void, // 切换清晰度
    onAliBitrateReady?: (e: AliPlayerFuncParams<{ index: number; width: number; height: number; bitrate: number }>) => void, // 获取清晰度回调
}

interface AliPlayerFuncParams<T> {
    nativeEvent: T
}

```
