//
//  SafeDownload.m
//  react-native-aliyunplayer
//
//  Created by yons on 2022/4/11.
//

#import "RNSafeDow.h"

@implementation RNSafeDow
// To export a module named CalendarManager
RCT_EXPORT_MODULE();

//构造方法
-(id) init{
    NSLog(@"初始化 唯一id:%@",[[NSBundle mainBundle]bundleIdentifier]);
    NSString *encrptyFilePath = [[NSBundle mainBundle] pathForResource:@"encryptedApp" ofType:@"dat"];
    
    [AliPrivateService initKey:encrptyFilePath];
    _length=0;
    _mAliDownloaderArr = [NSMutableArray arrayWithCapacity:5];
    NSLog(@"初始化成功");
    return self;
}

RCT_EXPORT_METHOD(init:(NSDictionary *) config resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"Pretending to create an event %@ name %@", config[@"path"], config[@"name"]);
    NSInteger * i = _length+1;
    if(i==(NSInteger *)[_mAliDownloaderArr count]){
        reject(@"fail",@"下载数量过多",nil);
    }
    
    NSUInteger index = 0;
    for (NSUInteger ia = 0;ia< [_mAliDownloaderArr count]; ia++) {
        if([_mAliDownloaderArr objectAtIndex:ia]){
            index = ia;
            break;
        }
    }
    //保存实例
    SafeDownload * safe = [[SafeDownload alloc] init:config index:index rnSafeDow:self];
    [_mAliDownloaderArr insertObject:safe atIndex:index];
    _length=_length+1;
    
    NSLog(@"获取id:%ld",index);
    resolve([NSString stringWithFormat:@"%ld",(long)index]);
    
    
}

RCT_EXPORT_METHOD(getDir:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSString * iosFileDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                                  
                                                                  NSUserDomainMask, YES) lastObject];
        NSString * iosCacheDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask, YES) lastObject];
        NSString * iosTempDir = NSTemporaryDirectory();
        
        resolve(@{@"iosFileDir":iosFileDir,@"iosCacheDir":iosCacheDir,@"iosTempDir":iosTempDir});
    } @catch (NSException *exception) {
        reject(exception.name,exception.reason,nil);
    } @finally {
        
    }
    
}

RCT_EXPORT_METHOD(stop:(NSUInteger)index resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        
        SafeDownload * safe = [_mAliDownloaderArr objectAtIndex:index];
        [safe.downloader stop];
        resolve(@"成功");
    } @catch (NSException *exception) {
        reject(exception.name,exception.reason,nil);
    } @finally {
        
    }
    
}

RCT_EXPORT_METHOD(release:(NSUInteger)index resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        
        SafeDownload * safe = [_mAliDownloaderArr objectAtIndex:index];
        [safe.downloader destroy];
        safe.downloader = nil;
        resolve(@"成功");
    } @catch (NSException *exception) {
        reject(exception.name,exception.reason,nil);
    } @finally {
        
    }
    
}

RCT_EXPORT_METHOD(start:(NSUInteger)index resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        
        SafeDownload * safe = [_mAliDownloaderArr objectAtIndex:index];
        [safe.downloader start];
        resolve(@"成功");
    } @catch (NSException *exception) {
        reject(exception.name,exception.reason,nil);
    } @finally {
        
    }
    
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"onPrepared",@"onDowProgress",@"onProcessing",@"onError",@"onCompletion"];
}

@end
