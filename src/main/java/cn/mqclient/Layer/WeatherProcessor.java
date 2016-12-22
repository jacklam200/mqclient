package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import cn.mqclient.entity.Component;

/**
 * Created by LinZaixiong on 2016/9/28.
 */

public class WeatherProcessor extends BaseProcessor<WebView> {
    public final static String TYPE_STR = "weather";
    public final static int TYPE_INT = 7;
    private String path = "";
    public WeatherProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public void updateView(Context context, Component item) {
        if(item != null && item.getSrc() != null && mView != null){

            if(path.compareToIgnoreCase(item.getSrc()) != 0){
                mView.loadUrl(item.getSrc());
            }

            path = item.getSrc();

        }
    }

    @Override
    public void execute(Context context, Component item) {
        super.execute(context, item);
    }

    @Override
    public WebView makeView(Context context, Component item) {
        WebView view = new WebView(context);
        view.setWebViewClient(new MQWebViewClient());
        return view;
    }

    class MQWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean isRet = false;

            return isRet;
        }
    }
}
