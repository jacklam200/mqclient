package cn.mqclient.service.autoInstall;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cn.kanwah.installservice.InstallInterface;
import cn.mqclient.App;
import cn.mqclient.Log;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Kanwah on 2016/12/12.
 */

public class InstallController {
    InstallInterface iservice;
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            // 从远程service中获得AIDL实例化对象
            iservice = InstallInterface.Stub.asInterface(service);
            Log.i("Client","Bind Success:" + iservice);
        }

        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            iservice = null;
            Log.i("Client","onServiceDisconnected");
        }
    };

    public void bind(Context context){
        Intent service = new Intent(InstallInterface.class.getName());
        context.bindService(service, connection, BIND_AUTO_CREATE);
    }

    public void unBind(Context context){

//        Intent service = new Intent(InstallInterface.class.getName());
//        context.unbindService(connection);
    }

    public void install(String apkPath) throws RemoteException{

        iservice.setApkPath(apkPath, getPackageName(apkPath), getPackageName(apkPath) + ".LaucherActivity");
        if(iservice.install()){
            Log.d("jacklam", "install success");
        }

        Log.d("jacklam", "install");
    }

    private String getPackageName(String apkPath){

        String packageName = "";
        PackageManager pm = App.getInstance().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            packageName = appInfo.packageName;
        }

        return packageName;
    }

    public static  boolean hasRootPerssion(){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }
    public static boolean isCanInstall(Context context, String packageName, int versionCode){
        boolean isRet = true;

        PackageManager packageManager = null;
        packageManager = context.getPackageManager();
        List<PackageInfo> mAllPackages= new ArrayList<PackageInfo>();
        mAllPackages = packageManager.getInstalledPackages(0);
        for(int i = 0; i < mAllPackages.size(); i ++)
        {
            PackageInfo packageInfo = mAllPackages.get(i);
            Log.i("package path", packageInfo.applicationInfo.sourceDir);
            if(!TextUtils.isEmpty(packageName) &&
                    packageName.compareToIgnoreCase(packageInfo.packageName) == 0){
                if(packageInfo.versionCode <= versionCode){
                    isRet = false;
                }
            }
        }
        return  isRet;
    }

    public static boolean isInstall(Context context, String packageName){
        boolean isRet = false;

        PackageManager packageManager = null;
        packageManager = context.getPackageManager();
        List<PackageInfo> mAllPackages= new ArrayList<PackageInfo>();
        mAllPackages = packageManager.getInstalledPackages(0);
        for(int i = 0; i < mAllPackages.size(); i ++)
        {
            PackageInfo packageInfo = mAllPackages.get(i);
            Log.i("package path", packageInfo.applicationInfo.sourceDir);
            if(!TextUtils.isEmpty(packageName) &&
                    packageName.compareToIgnoreCase(packageInfo.packageName) == 0){
                isRet = true;
            }
        }
        return  isRet;
    }

    private static boolean isInstall = false;
    public static boolean installService(Context context, String apkPath){
        boolean isRet = false;
        if(!isInstall){
            isInstall = true;
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);

            if(packageInfo != null){
                Log.d("name", packageInfo.packageName);
                Log.d("uid", packageInfo.sharedUserId);
                Log.d("vname", packageInfo.versionName);
                Log.d("code", packageInfo.versionCode+"");
                if(isCanInstall(context, packageInfo.packageName, packageInfo.versionCode)){
                    isRet = clientInstall(apkPath);
                    Log.d("jacklam", "installService CanInstall");
                    if(!isRet){
                        isInstall = false;
                    }
                }
                else{
                    Log.d("jacklam", "installService didn't Install");
                    isInstall = false;
                }
            }



        }


        return isRet;
    }

    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    public void uninstall(){
        //TODO:
    }


    private static boolean clientInstall(String apkPath){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            Log.d("jacklam", value +" install :" + apkPath);
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }



    private static boolean returnResult(int value){
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }

}
