package com.jacob.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;

public class ProfileActivity extends AppCompatActivity implements UtilsAwv.Listener {
    public UtilsAwv mwebView;
    public LinearLayout main;
    public UtilsManager manager;
    public RelativeLayout relativeLayout;
    public Button btnNoInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init_screen();

        main = findViewById(R.id.profile);
        main.setVisibility(View.INVISIBLE);

        mwebView = (UtilsAwv) findViewById(R.id.myWebView);
        mwebView.setListener(this, this);
        mwebView.setMixedContentAllowed(false);
        manager = new UtilsManager(this);
        mwebView.setManager(manager);
        mwebView.getSettings().setDomStorageEnabled(true);
        //mwebView.setVisibility(View.INVISIBLE);

        RelativeLayout bglayout = findViewById(R.id.drawer_layout);
        bglayout.setBackgroundResource(R.drawable.loading_screen);
        relativeLayout = findViewById(R.id.relativeLayout);

        btnNoInternetConnection = findViewById(R.id.btnNoConnection);

        btnNoInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection(null);
            }
        });
        checkConnection(savedInstanceState);
    }

    @SuppressWarnings( "deprecation" )
    private void init_screen(){
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        mwebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mwebView.restoreState(savedInstanceState);
    }

    public void checkConnection(Bundle savedInstanceState){
        boolean isConnected = isConnectionAvailable();

        if (isConnected){
            if (savedInstanceState == null) {
                manager.initDbx();
                manager.dbx.initData(mwebView);
            }
            mwebView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        else{
            manager.go_back();
        }
    }

    @SuppressWarnings( "deprecation" )
    public boolean isConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return ( cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting() );
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mwebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mwebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mwebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mwebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        //openQuitDialog();
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {
        main.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

    @Override
    public void onLowMemory() {
        Log.d("TAG_MEMORY", "Memory is Low");
        super.onLowMemory();
    }
}
