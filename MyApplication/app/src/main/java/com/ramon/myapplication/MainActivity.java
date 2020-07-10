package com.ramon.myapplication;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private WebView webView;
    private WebView webView2;
    ViewFlipper vf;
    ProgressBar pBar;
    RelativeLayout NoConnectedTxt;
    Button btClearDom;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        webView = findViewById(R.id.webViewLayout);
        webView2 = findViewById(R.id.webViewLayout2);
        pBar = findViewById(R.id.pBar);
        NoConnectedTxt = findViewById(R.id.NoConnected_layout);
        btClearDom = findViewById(R.id.btclear);
        vf = findViewById(R.id.flip_view);

        NoConnectedTxt.setVisibility(ImageView.GONE);
        btClearDom.setVisibility(ImageView.GONE);

        pBar.setMax(100);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView2.getSettings().setJavaScriptEnabled(true);

        //webView.loadUrl("http://192.168.4.1/");
        webView.loadUrl("https://www.google.com/");
        webView2.loadUrl("file:///android_asset/game-t-rex.html");
        vf.setDisplayedChild(0);
        //int count = 1;
        //Toast.makeText(MainActivity.this, String.valueOf(count)+"outro texto", Toast.LENGTH_SHORT).show();

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pBar.setVisibility(View.GONE);
                if( !isNetworkAvailable() ) {
                    NoConnectedTxt.setVisibility(ImageView.VISIBLE);
                    vf.setDisplayedChild(1);
                    count = count + 1;
                    if(count == 1){
                        webView2.reload();
                    }
                }else{
                    NoConnectedTxt.setVisibility(ImageView.GONE);
                    vf.setDisplayedChild(0);
                    count = 0;
                }
                refreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pBar.setProgress(newProgress);
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager myManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);
                Toast.makeText(MainActivity.this, "Seu Download começou...", Toast.LENGTH_SHORT).show();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        btClearDom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //webView.isPrivateBrowsingEnabled();
                //webView.getSettings().setBuiltInZoomControls(true);
                vf.setDisplayedChild(1);
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();    //Procurar por WebBackForwardList remove, para remover a url da off page do historico
        } else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}