package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import cn.mqclient.entity.Component;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class WebProcessor extends BaseProcessor<WebView>{
    public final static String TYPE_STR = "web";
    public final static int TYPE_INT = 4;

    private String path = "";

    public WebProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public WebView makeView(Context context, Component item) {
        WebView view = new WebView(context);
        view.setWebViewClient(new MQWebViewClient());
        return view;
    }

    @Override
    public void updateView(Context context, Component item) {
        if(item != null && item.getSrc()!= null &&
                item.getSrc().length() > 0 && mView != null){

            if(path.compareToIgnoreCase(item.getSrc()) != 0){
                mView.loadUrl(item.getSrc());
            }

            path = item.getSrc();

        }
    }

    class MQWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean isRet = false;

            return isRet;
        }
    }


}
