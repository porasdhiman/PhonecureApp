package com.worksdelight.phonecure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by worksdelight on 28/02/17.
 */

public class DashBoradFragment extends Fragment {
    WebView webView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.privecy_policy_layout, container, false);
        MainActivity parentActivity = (MainActivity) getActivity();
        parentActivity.visibilityMethod();


        webView=(WebView)v.findViewById(R.id.webView);
        webView.setWebViewClient(new AppWebViewClients());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("file:///android_asset/terms.html");
        return v;
    }
    public class AppWebViewClients extends WebViewClient {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }
}

