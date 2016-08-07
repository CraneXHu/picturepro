//
// Created by pkhope on 2016/8/6.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#include "me_pkhope_picturepro_utils_PicUtils.h"

#define RGBA_A(p) (((p) & 0xFF000000) >> 24)
#define RGBA_R(p) (((p) & 0x00FF0000) >> 16)
#define RGBA_G(p) (((p) & 0x0000FF00) >>  8)
#define RGBA_B(p)  ((p) & 0x000000FF)
#define MAKE_RGBA(r,g,b,a) (((a) << 24) | ((r) << 16) | ((g) << 8) | (b))

JNIEXPORT jstring JNICALL Java_me_pkhope_picturepro_utils_PicUtils_getOriginalPixels
  (JNIEnv *env, jobject obj, jobject srcBitmap, jobject mosaicBitmap) {

    // Get bitmap info
    AndroidBitmapInfo info;
    memset(&info, 0, sizeof(info));
    AndroidBitmap_getInfo(env, srcBitmap, &info);
    // Check format, only RGBA are supported
    if (info.width <= 0 || info.height <= 0 ||
        (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)) {
//        env->ThrowNew(env, env->FindClass(env, "java/io/IOException"), "invalid bitmap");
//        return env->NewStringUTF("");
    }

    void * pixelsSource = NULL;
    int res = AndroidBitmap_lockPixels(env, srcBitmap, &pixelsSource);
    if (pixelsSource == NULL) {
//        env->ThrowNew(env, env->FindClass(env, "java/io/IOException"), "fail to open bitmap");
//        return;
    }

    void * pixelsMosaic = NULL;
    int resMosaic = AndroidBitmap_lockPixels(env, mosaicBitmap, &pixelsMosaic);
    if (pixelsMosaic == NULL) {
//        env->ThrowNew(env, env->FindClass(env, "java/io/IOException"), "fail to open bitmap");
//        return;
    }

    // From top to bottom
    for (int y = 0; y < info.height; ++y) {
        // From left to right
        for (int x = 0; x < info.width; ++x) {
            int a = 0;
            void *pixelSource = NULL;
            void *pixelMosaic = NULL;
            // Get each pixel by format
            pixelSource = ((uint32_t *)pixelsSource) + y * info.width + x;
            pixelMosaic = ((uint32_t *)pixelsMosaic) + y * info.width + x;
            uint32_t v1 = *(uint32_t *)pixelSource;
            uint32_t v2 = *(uint32_t *)pixelMosaic;
            a = RGBA_A(v2);
            if (a == 254){
                // Write the pixel back
                *((uint32_t *)pixelSource) = MAKE_RGBA(RGBA_R(v1), RGBA_G(v1), RGBA_B(v1), a);
            }
        }
    }

    AndroidBitmap_unlockPixels(env, srcBitmap);
    AndroidBitmap_unlockPixels(env, mosaicBitmap);

}

