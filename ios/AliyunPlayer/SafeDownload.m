//
//  SafeDownload.m
//  react-native-aliyunplayer
//
//  Created by yons on 2022/4/12.
//

#import "SafeDownload.h"

@implementation SafeDownload

//构造方法
-(id) init:(NSDictionary *) config index:(NSUInteger)index rnSafeDow:(RNSafeDow *)rnSafeDow{
    DowConfig * con = [[DowConfig alloc] init];
    AVPVidAuthSource * authSource = [self detect:con config:config];

    AliMediaDownloader *downloader = [[AliMediaDownloader alloc] init];
    [downloader setSaveDirectory:con.path];
    [downloader setDelegate:self];
    _downloader =downloader;
    _index = index;
    _rnSafeDow = rnSafeDow;
    _vidAuth =authSource;
    //准备下载源
    [downloader prepareWithPlayAuth:authSource];
    return self;
}
-(AVPVidAuthSource*) detect:(DowConfig*)con config:(NSDictionary*)config{
    if(config[@"path"]){
        con.path =config[@"path"];
    }
    if(config[@"vid"]){
        con.vid =config[@"vid"];
    }
    if(config[@"playAuth"]){
        con.playAuth =config[@"playAuth"];
    }
    if(config[@"region"]){
        con.region =config[@"region"];
    }
    //创建VidAuth
    AVPVidAuthSource* aliyunVidAuth = [[AVPVidAuthSource alloc] init];
    aliyunVidAuth.vid = con.vid;//视频vid
    aliyunVidAuth.region = con.region;//接入区域
    aliyunVidAuth.playAuth = con.playAuth;//接授码

    return aliyunVidAuth;
}

-(void)onPrepared:(AliMediaDownloader *)downloader mediaInfo:(AVPMediaInfo *)info {
    //准备下载项成功
    NSLog(@"准备下载项成功");
    NSArray<AVPTrackInfo*>* tracks = info.tracks;
    //比如：下载第一个TrackInfo
    [downloader selectTrack:[tracks objectAtIndex:0].trackIndex];

    //开始下载
      [downloader start];

    //监听
    [_rnSafeDow sendEventWithName:@"onPrepared" body:@{@"url":[info coverURL],@"type":[info mediaType],@"code":@200,@"vid":_vidAuth.vid}];
    NSLog(@"监听");

}
-(void)onError:(AliMediaDownloader *)downloader errorModel:(AVPErrorModel *)errorModel {
    //下载出错
    //监听
    [_rnSafeDow sendEventWithName:@"onError" body:@{@"msg":errorModel.message,@"code":@400,@"vid":_vidAuth.vid}];

    [self.downloader destroy];
    self.downloader = nil;
    [_rnSafeDow.mAliDownloaderArr removeObjectAtIndex:_index];
    _rnSafeDow.length--;
}
-(void)onDownloadingProgress:(AliMediaDownloader *)downloader percentage:(int)percent {
    //下载进度百分比
    NSLog(@"下载进度:%i",percent);
    //监听
    [_rnSafeDow sendEventWithName:@"onDowProgress" body:@{@"percent":[NSString stringWithFormat:@"%i",percent] ,@"code":@200,@"vid":_vidAuth.vid}];
}
-(void)onProcessingProgress:(AliMediaDownloader *)downloader percentage:(int)percent {
    //处理进度百分比
    NSLog(@"下载处理进度:%i",percent);
    //监听
    [_rnSafeDow sendEventWithName:@"onProcessing" body:@{@"percent":[NSString stringWithFormat:@"%i",percent],@"code":@200,@"vid":_vidAuth.vid}];
}
-(void)onCompletion:(AliMediaDownloader *)downloader {
    //下载成功
    NSLog(@"下载成功");
    //监听
    [_rnSafeDow sendEventWithName:@"onCompletion" body:@{@"msg":@"下载完成",@"code":@200,@"vid":_vidAuth.vid,@"filePath":downloader.downloadedFilePath}];

    [self.downloader destroy];
    self.downloader = nil;
    [_rnSafeDow.mAliDownloaderArr removeObjectAtIndex:_index];
    _rnSafeDow.length=_rnSafeDow.length-1;
}

@end
