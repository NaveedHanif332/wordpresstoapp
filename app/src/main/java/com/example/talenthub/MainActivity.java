package com.example.talenthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
     private  String url="https://fiverr.com/";
     ProgressDialog progressDialog;
     BottomNavigationView bottomNavigationView;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         progressBar=findViewById(R.id.progress_bar);
         swipeRefreshLayout=findViewById(R.id.refresh_layout);
         bottomNavigationView=findViewById(R.id.bottom);
         webView=findViewById(R.id.webview);

         bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home)
                {
                 webView.loadUrl(url);
                }
                else if(item.getItemId()==R.id.my_profile)
                {
                    webView.loadUrl("https://www.fiverr.com/zeeshanali51214/convert-website-into-pro-android-and-ios-app?context_referrer=search_gigs&source=main_banner&ref_ctx_" +
                            "id=7b8cd7f0-7e09-4b47-829f-0420d6805080&pckg_id=1&pos=2&context_type=auto&funnel=892dce52-de2d-431a-9ddb-6429429d7257");
                }
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s,final String s1, final String s2, final String s3, long l) {
                        Dexter.withContext(MainActivity.this)
                                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                         .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(s));
                                request.setMimeType(s3);
                                String cookies= CookieManager.getInstance().getCookie(s);
                                request.addRequestHeader("Cookies",cookies);
                                request.setDescription("Downloading files");
                                request.setTitle(URLUtil.guessFileName(s,s2,s3));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(s,s2,s3));
                                Toast.makeText(getApplicationContext(),"Downloading started",Toast.LENGTH_SHORT).show();
                                DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                            }
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                               Toast.makeText(getApplicationContext(),"permission is necessary",Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        progressBar.setProgress(20);
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        webView.getSettings().setJavaScriptEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        if(savedInstanceState==null)
        {
            check_connection();
        }
        else {
            webView.restoreState(savedInstanceState);
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.design_default_color_primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading ! Please wait");
        webView.setWebChromeClient(new WebChromeClient(){


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                progressDialog.show();
                setTitle("Loading...");
                if (progressBar.getProgress() == 100) {
                      progressDialog.cancel();
                      setTitle("Fiver");

                      progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reload,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.reload)
        {
            webView.reload();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
         if(webView.canGoBack())
         {
             webView.goBack();
         }else
         {
          final AlertDialog.Builder b=new AlertDialog.Builder(this);
          b.setMessage("Are you sure want to exit");
          b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  b.setCancelable(true);
              }
          }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  finishAffinity();
              }
          }).show();
         }
    }
    public  void check_connection()
    {
        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connected=intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
                if(connected)
                {
                    Toast.makeText(getApplicationContext(),"NO interent",Toast.LENGTH_SHORT).show();

                }
                else
                {    webView.loadUrl(url);
                }
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
         webView.saveState(outState);
    }
}
