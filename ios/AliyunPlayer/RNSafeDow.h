//
//  SafeDownload.h
//  react-native-aliyunplayer
//
//  Created by yons on 2022/4/11.
//
#import <Foundation/Foundation.h>
#import <AliyunMediaDownloader/AliMediaDownloader.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "SafeDownload.h"
@class SafeDownload;

NS_ASSUME_NONNULL_BEGIN



@interface RNSafeDow : RCTEventEmitter<RCTBridgeModule>

@property NSInteger * length;
@property SafeDownload * safeDownload;
@property NSMutableArray * mAliDownloaderArr;

-(id) init;
- (NSArray<NSString *> *)supportedEvents;
- (void)test;
@end


NS_ASSUME_NONNULL_END
