package cn.mqclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import cn.mqclient.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import cn.mqclient.entity.http.BaseEntity;
import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.entity.http.TokenEntity;
import cn.mqclient.model.LoginRequest;
import cn.mqclient.model.MQConfigRequest;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.RequestListener;
import cn.mqclient.service.SubscriberService;
import cn.mqclient.service.TimerService;
import cn.mqclient.service.autoTake.CameraService;
import cn.mqclient.third.location.THLocationManager;
import cn.mqclient.utils.QRCodeUtil;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;
import okhttp3.Response;

/**
 * Created by LinZaixiong on 2016/10/6.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, RequestListener {
    private static final String TAG = "LoginActivity";
    private EditText et_name;
    private EditText et_psw;
    private Button bt_login;
    private LoginRequest requeat;
    private MQConfigRequest mqRequst;
    //private ImageView iv_qrcode;
    private ProgressDialog progressDialog;
    private EditText et_alias;

    public static void start(Context context) {

        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        THLocationManager.getInstance().register();
        THLocationManager.getInstance().start();
        initView();
        initData();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        initView();
        initData();
    }

    protected void initView(){
        et_name = (EditText) this.findViewById(R.id.et_name);
        et_psw = (EditText)findViewById(R.id.et_psw);
        et_alias  = (EditText)findViewById(R.id.et_alias);
        //iv_qrcode  = (ImageView)findViewById(R.id.iv_qrcode);
        bt_login = (Button)findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        requeat = new LoginRequest(this);
        et_name.setText("");
        et_psw.setText("");
        et_alias.setText("");


    }

    protected void initData(){
        //generateCodebar(iv_qrcode, "http://www.baidu.com");

//        if(SharePref.getInstance().getBoolean(SpConstants.BIND, false)){
//            MQConfigEntity.MQConfig config = new MQConfigEntity.MQConfig();
//            config.getInfo();
//            SubscriberService.start(this, config);
//            FullscreenActivity.start(this);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        THLocationManager.getInstance().stop();
        THLocationManager.getInstance().deregister();
        if(requeat != null){
            requeat.cancel();
        }
        if(mqRequst != null){
            mqRequst.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_login){

            String name = et_name.getText().toString();
            String psw = et_psw.getText().toString();
            String alias = et_alias.getText().toString();
            requeat.setParams(this, name, psw, alias);
            progressDialog = ProgressDialog.show(this,"Loading...", "Please wait...", true, false);
            requeat.start();
        }
    }

    @Override
    public void success(BaseRequest request, Response response, BaseEntity result) {
        Log.d(TAG, "success result:" + result);
//        AsyncActivity.start(this);
        if(request == this.requeat){
            processLogin( response, result);
        }
        else if(request == this.mqRequst){
            progressDialog.dismiss();
            processMQConfig(response, result);
        }


    }

    @Override
    public void failed(BaseRequest request, String err) {
        progressDialog.dismiss();
        if(request == this.requeat) {

            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R.string.mq_server_failed, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "failed result:" + err);
    }

    private void processLogin( Response response, BaseEntity entity){

        TokenEntity result = (TokenEntity)entity;
        if( response != null && result != null &&
                response.code() == 200 && result.getRet() == 0 && result.getData() != null){

            SharePref.getInstance().clearInfo();
            SharePref.getInstance().setValue(SpConstants.WARRANTNO, result.getData().getWarrantNo());
            SharePref.getInstance().setValue(SpConstants.TOKEN, result.getData().getToken());
            SharePref.getInstance().setValue(SpConstants.TOKENEXPIRES, result.getData().getTokenExpires());
            SharePref.getInstance().setValue(SpConstants.STARTTIME, result.getData().getStartTime());
            SharePref.getInstance().setValue(SpConstants.ENDTIME, result.getData().getEndTime());
            SharePref.getInstance().setValue(SpConstants.BIND, true);
            SharePref.getInstance().setValue("counting", result.getData().getCounting());
            TimerService.startWithText(this, result.getData().getCounting());
            sendMQConfig(result.getData().getToken());

        }
        else{
            if(progressDialog != null)
                progressDialog.dismiss();
            if(entity != null)
                Toast.makeText(this, entity.getMsg(), Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMQConfig(String token){

        mqRequst = new MQConfigRequest(this, token);
        mqRequst.start();
    }

    private void processMQConfig(Response response, BaseEntity entity){
        MQConfigEntity result = (MQConfigEntity)entity;

        if( response != null && result != null &&
                response.code() == 200 && result.getRet() == 0 && result.getData() != null){

            MQConfigEntity.MQConfig config = result.getData();
            if(!TextUtils.isEmpty(config.getHost()) && !TextUtils.isEmpty(config.getPassword()) &&
                    !TextUtils.isEmpty(config.getUserName())){
                config.saveInfo();
                SubscriberService.start(App.getInstance(), config);
                FullscreenActivity.start(this);
                finish();
            }
            else{
                Toast.makeText(this, R.string.mq_server_failed, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(progressDialog != null)
                progressDialog.dismiss();
        }

    }

    private void generateCodebar(ImageView view, String url){

        Bitmap codeBitMap = null;
        try {
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200.0f, this.getResources().getDisplayMetrics());
            int mid_size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18.0f, this.getResources().getDisplayMetrics());
            codeBitMap = new QRCodeUtil().Create2DCode(this, url, size, size, R.mipmap.ic_launcher, mid_size, mid_size);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if(codeBitMap !=null){
            view.setImageBitmap(codeBitMap);
        }
    }

    protected void onResume(){
        super.onResume();

    }
}
