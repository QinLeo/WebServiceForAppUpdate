package com.example.administrator.updateutils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * 检验apk是否有新版本，是否升级
 * 
 * @author qinli 2016.05.10
 */
public class UpdateManager {

    private static Context mContext;

    /* 下载包安装路径 */
    private static final String savePath = Environment
            .getExternalStorageDirectory() + "/APP_DOWNED_PATH/";

    private static final int DOWN_UPDATE = 0X11;

    private static final int DOWN_OVER = 0X12;


    private static int versionCode;//服务器版本号
    private static int versionCodeClient;//本地版本号
    private AlertDialog alg;
    /* 刷新的进度条 */
    private ProgressBar mProgress;

    /* 新版本apk的远程路径 */
    private static String apkUrl;

    /* 保存下载的apk的名称为 */
    private static String saveFileName;

    /* 下载进度 */
    private int progress;

    private boolean interceptFlag = false;

    /*
     * Handler静态化，防止内存泄露
     */
    private static class MHandler extends Handler {
        /* 引用外部类 */
        private WeakReference<UpdateManager> updatemanager;

        public MHandler(UpdateManager activity) {
            updatemanager = new WeakReference<UpdateManager>(activity);
        }

        /* 处理线程结果 */
        @Override
        public void handleMessage(android.os.Message msg) {
            UpdateManager theClass = updatemanager.get();
            switch (msg.what) {
                // 正在下载，更新进度条
                case DOWN_UPDATE:
                    theClass.showProgress();
                    break;
                //检测版本返回结果
                case HttpConst.MSG_WHAT_VERSION_UPDTAE:
                    if (msg.obj == null) {
                        // toastUtils.showToast("查询新版本失败！", mContext);
                    }
                    if (msg.obj.equals("NoNet")) {
                        Toast.makeText(mContext, "无网络连接", Toast.LENGTH_SHORT).show();
                    }
                    if (msg.obj != null && !msg.obj.equals("NoNet")) {
                        versionBean ver = (versionBean) msg.obj;
                        versionCode = Integer.parseInt(ver.getVersionNumber());
                        String versionName = ver.getVersionName();
                        saveFileName = "BPXY" + versionName + ".apk";
                        apkUrl = ver.getVersionPath();
                        versionCodeClient = VersionUtils.getversionCode(mContext);
                        theClass.showUpdateDialog((versionBean) msg.obj);
                    }
                    break;
                // 下载完成，进行安装
                case DOWN_OVER:
                    theClass.installApk();
            }
        }
    }

    /* 实例化Handler */
    MHandler mHandler = new MHandler(this);

    // 构造函数
    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 检查新版本
     */
    public void checkVersion() {
        android.util.Log.i("UpdateManager", "checkVersion");
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                UserHttp.newInstance().Update(mContext, mHandler);
            }
        }).start();
    }

    /**
     * 显示进度条 外部类进行封装以便MHandler进行内部调用
     */
    public void showProgress() {
        mProgress.setProgress(progress);
    }

    /*
     * 显示版本更新提示框
     */
    private void showUpdateDialog(versionBean newVersionInfo) {
        final AlertDialog alg = new AlertDialog.Builder(mContext).create();
        alg.show();
        Window win = alg.getWindow();
        win.setContentView(R.layout.common_dialog_update);
        Button positiveButton = (Button) win.findViewById(R.id.PositiveButton);
        Button negativeButton = (Button) win.findViewById(R.id.NegativeButton);
        if (versionCode == versionCodeClient || versionCode < versionCodeClient) {
            alg.dismiss();
            toastUtils.showToast("当前已是最新版本", mContext);
        } else if (versionCode > versionCodeClient) {
            // 显示新版本内容
            try {
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alg.dismiss();
                        showDownloadDialog();
                    }
                });
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alg.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 显示下载中对话框
     */
    private void showDownloadDialog() {
        alg = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.common_dialog_progress, null);
        alg.setView(view);
        mProgress = (ProgressBar) view.findViewById(R.id.update_progress);
        alg.setTitle("下载中......");
        alg.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface arg0, final int arg1) {
                arg0.dismiss();
                interceptFlag = true; // 中止下载
            }
        });
        alg.show();
        downloadApk();
    }

    /**
     * 下载apk
     *
     * @param
     */
    private void downloadApk() {
        Runnable mdownApkRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File ApkFile = new File(file, saveFileName);
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (numread <= 0) {
                            /**
                             * 完成提示信息
                             */
                            alg.dismiss();
                            // 下载完成通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!interceptFlag); // 点击取消就停止下载.
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    android.util.Log.i("MalformedURLException", e.toString());
                } catch (IOException e) {
                    android.util.Log.i("IOException", e.toString());
                }
            }
        };
        new Thread(mdownApkRunnable).start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(savePath + saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}