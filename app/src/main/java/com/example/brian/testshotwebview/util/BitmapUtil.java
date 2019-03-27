package com.example.brian.testshotwebview.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class BitmapUtil {
    public static Bitmap shotScrollView(ScrollView scrollView) {
        Bitmap bitmap = null;
        try {
            int h = 0;
            for (int i = 0; i < scrollView.getChildCount(); i++) {
                h += scrollView.getChildAt(i).getHeight();
                scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            }
            bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static Bitmap getBitmapFromWebView(WebView webView) {
        //1:发起测量
        webView.measure(0, 0);
        //2:获取测量后高度 == Webview的高度
        int contentHeight = webView.getMeasuredHeight();
        //3:获取Webview控件的高度
        int height = webView.getHeight();
        //4:计算滚动次数
        int totalScrollCount = contentHeight / height;
        //5: 剩余高度
        int surplusScrollHeight = contentHeight - (totalScrollCount * height);

        //存储图片容器
        List<Bitmap> cacheBitmaps = new ArrayList<>();
        for (int i = 0; i < totalScrollCount; i++) {
            if (i > 0) {
                //滚动WebView
                webView.setScrollY(i * height);
            }
            Bitmap bitmap = getScreenshot(webView);
            cacheBitmaps.add(bitmap);
        }

        //剩余的部分
        if (surplusScrollHeight > 0) {
            //滑动到底部 ，由于剩余的部分不够一屏，则只能滑动到底部来截取一屏的图片
            webView.setScrollY(contentHeight);
            //本次截取到的bitmap 是包含了上一屏的内容
            Bitmap bitmap = getScreenshot(webView);
            //对截取到的bitmap 再次截取后半部分，去掉重复的部分（上一屏的内容）
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - surplusScrollHeight, bitmap.getWidth(), surplusScrollHeight, null,
                    false);
            cacheBitmaps.add(bmp);
        }
        return drawMulti(cacheBitmaps);
    }


    /**
     * 将bitmap集合上下拼接
     *
     * @return
     */
    private static Bitmap drawMulti(List<Bitmap> bitmaps) {
        int width = bitmaps.get(0).getWidth();
        int height = bitmaps.get(0).getHeight();
        for (int i = 1; i < bitmaps.size(); i++) {
            if (width < bitmaps.get(i).getWidth()) {
                width = bitmaps.get(i).getWidth();
            }
            height = height + bitmaps.get(i).getHeight();
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        //3:创建画笔
        Paint paint = new Paint();
        canvas.drawBitmap(bitmaps.get(0), 0, 0, paint);
        int h = 0;
        for (int j = 0; j < bitmaps.size() - 1; j++) {
            h = bitmaps.get(j).getHeight() + h;
            canvas.drawBitmap(bitmaps.get(j + 1), 0, h, paint);
        }
        return result;
    }

    /**
     * 截取一屏的页面
     *
     * @param webView
     * @return
     */
    private static Bitmap getScreenshot(WebView webView) {
        //1:打开缓存开关
        webView.setDrawingCacheEnabled(true);
        //2:获取缓存
        Bitmap drawingCache = webView.getDrawingCache();
        //3:拷贝图片
        Bitmap newBitmap = Bitmap.createBitmap(drawingCache);
        //4:关闭缓存开关
        webView.setDrawingCacheEnabled(false);
        return newBitmap;
    }
}
