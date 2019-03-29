package com.example.brian.testshotwebview;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.brian.testshotwebview.util.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 0;
    @BindView(R.id.webview)
    WebView webview;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rqPermission();
        showWebView();


    }

    private void rqPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                return;
            }
        }
    }


    private void showWebView() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webview.getProgress() == 100) {
                    //加载完成
                    Log.d(TAG, "webview.getProgress() == 100 ");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        webview.enableSlowWholeDocumentDraw();
//                    }
                    Bitmap bitmap = BitmapUtil.getBitmapFromWebView(webview);
                    saveBitmap(bitmap);
                    Log.d(TAG, "bitmap --> width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
//        webview.loadUrl("https://www.jianshu.com/p/8b1bcbbae4e7");

        String url = "https://brianczy.github.io/MyBlogs/2019/03/11/Android%E9%97%AE%E9%A2%98%E8%AE%B0%E5%BD%95/";
//        String url = "http://www.jcodecraeer.com/";
        webview.loadUrl(url);
    }

    private void saveBitmap(Bitmap bitmap) {

//        File f = new File("/sdcard/namecard/", picName);
        File path = Environment.getExternalStorageDirectory();
        File file = new File("/sdcard", "webview.png");  // 路径为：  /sdcard/webview.png
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


}

