//
//  SafeDownload.h
//  react-native-aliyunplayer
//
//  Created by yons on 2022/4/12.
//

#import <UIKit/UIKit.h>
#import <AliyunMediaDownloader/AliMediaDownloader.h>
#import <React/RCTBridgeModule.h>
#import "DowConfig.h"
#import "RNSafeDow.h"
@class RNSafeDow;

NS_ASSUME_NONNULL_BEGIN


@interface SafeDownload : NSObject<AMDDelegate>

@property NSUInteger index;
@property AliMediaDownloader * downloader;
@property RNSafeDow * rnSafeDow;
@property AVPVidAuthSource * vidAuth;

-(id) init:(NSDictionary *) config index:(NSUInteger)index rnSafeDow:(RNSafeDow *)rnSafeDow;
-(AVPVidAuthSource*) detect:(DowConfig*)con config:(NSDictionary*)config;

//准备下载项成功
-(void)onPrepared:(AliMediaDownloader *)downloader mediaInfo:(AVPMediaInfo *)info;
//下载出错
-(void)onError:(AliMediaDownloader *)downloader errorModel:(AVPErrorModel *)errorModel;
//下载进度百分比
-(void)onDownloadingProgress:(AliMediaDownloader *)downloader percentage:(int)percent;
//处理进度百分比
-(void)onProcessingProgress:(AliMediaDownloader *)downloader percentage:(int)percent;
//下载成功
-(void)onCompletion:(AliMediaDownloader *)downloader;
@end

NS_ASSUME_NONNULL_END
