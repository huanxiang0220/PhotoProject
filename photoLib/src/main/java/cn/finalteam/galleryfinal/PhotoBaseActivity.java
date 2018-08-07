/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.galleryfinal;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import cn.finalteam.galleryfinal.utils.ImageUtils;
import cn.finalteam.galleryfinal.utils.MediaScanner;
import cn.finalteam.galleryfinal.utils.SystemStatusManager;
import cn.finalteam.toolsfinal.ActivityManager;

import com.ifcnt.photolib.BuildConfig;
import com.ifcnt.photolib.R;

/**
 * Desction: Author:pengjianbo Date:15/10/10 下午5:46
 */
@SuppressLint("Override")
public abstract class PhotoBaseActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    protected static String mPhotoTargetFolder;

    private Uri mTakePhotoUri;
    private MediaScanner mMediaScanner;

    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;

    protected boolean mTakePhotoAction;// 打开相机动作

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("takePhotoUri", mTakePhotoUri);
        outState.putString("photoTargetFolder", mPhotoTargetFolder);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhotoUri = savedInstanceState.getParcelable("takePhotoUri");
        mPhotoTargetFolder = savedInstanceState.getString("photoTargetFolder");
    }

    protected Handler mFinishHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finishGalleryFinalPage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);
        mMediaScanner = new MediaScanner(this);
        // DisplayMetrics dm = DeviceUtils.getScreenPix(this);
        // mScreenWidth = dm.widthPixels;
        // mScreenHeight = dm.heightPixels;

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

//        setStatus();
    }

    private void setStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.photo_status_blue));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(this);
            // 打开系统状态栏控制
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.photo_status_blue));// 设置背景
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaScanner != null) {
            mMediaScanner.unScanFile();
        }
        ActivityManager.getActivityManager().finishActivity(this);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    // 判断文件是否存在
    public boolean fileIsExists(File file) {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    // 把日期转为字符串
    private String ConverToString(Date date, String type) {
        DateFormat df = new SimpleDateFormat(type);

        return df.format(date);
    }

    /**
     * 拍照
     */
    protected void takePhotoAction() {
        if (!existSDCard()) {
            String errormsg = getString(R.string.sd_card_does_not_exist);
            toast(errormsg);
            if (mTakePhotoAction) {
                resultFailure(errormsg, true);
            }
            return;
        }

        File takePhotoFolder = null;
        if (TextUtils.isEmpty(mPhotoTargetFolder)) {
            takePhotoFolder = GalleryFinal.getCoreConfig().getTakePhotoFolder();
        } else {
            takePhotoFolder = new File(mPhotoTargetFolder);
        }
        // boolean suc = FileUtils.mkdirs(takePhotoFolder);
        boolean suc = fileIsExists(takePhotoFolder);
        File toFile = new File(takePhotoFolder, "IMG" + ConverToString(new Date(), "yyyyMMddHHmmss") + ".jpg");

        if (suc) {
            if (Build.VERSION.SDK_INT >= 24) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                    Log.d("Tab", BuildConfig.APPLICATION_ID);
                    //TODO:修改拍照的配置（后期改成动态获取authority，build.gradle动态设置authority）
                    mTakePhotoUri = FileProvider.getUriForFile(this, "com.zhitongcaijin.ztc.fileprovider", createTakePhotoFile());
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePhotoIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, mTakePhotoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
                    startActivityForResult(takePhotoIntent, GalleryFinal.TAKE_REQUEST_CODE);
                }
            } else {
                mTakePhotoUri = Uri.fromFile(toFile);
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
                startActivityForResult(captureIntent, GalleryFinal.TAKE_REQUEST_CODE);
            }
        } else {
            takePhotoFailure();
        }
    }

    private File createTakePhotoFile() {
        File imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Image");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }

        File file = new File(imagePath, ConverToString(new Date(), "yyyyMMddHHmmss") + ".jpg");
        tempPath = file.getPath();
        return file;
    }

    private String tempPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GalleryFinal.TAKE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && mTakePhotoUri != null) {
                String path;
                if (Build.VERSION.SDK_INT >= 24) {
                    path = tempPath;
                } else {
                    //Android/data/com.zhitongcaijing/files/Pictures
                    path = mTakePhotoUri.getPath();
                }
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    final PhotoInfo info = new PhotoInfo();
                    info.setPhotoId(ImageUtils.getRandom(10000, 99999));
                    info.setPhotoPath(path);
                    updateGallery(path);
                    takeResult(info);
                } else {
                    takePhotoFailure();
                }
            } else {
                takePhotoFailure();
            }
        }
    }

    private void takePhotoFailure() {
        String errormsg = getString(R.string.take_photo_fail);
        if (mTakePhotoAction) {
            resultFailure(errormsg, true);
        } else {
            toast(errormsg);
        }
    }

    /**
     * 更新相册
     */
    private void updateGallery(String filePath) {
        if (mMediaScanner != null) {
            mMediaScanner.scanFile(filePath, "image/jpeg");
        }
    }

    protected void resultData(ArrayList<PhotoInfo> photoList) {
        GalleryFinal.OnHanlderResultCallback callback = GalleryFinal
                .getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            if (photoList != null && photoList.size() > 0) {
                callback.onHanlderSuccess(requestCode, photoList);
            } else {
                callback.onHanlderFailure(requestCode,
                        getString(R.string.photo_list_empty));
            }
        }
        finishGalleryFinalPage();
    }

    protected void resultFailureDelayed(String errormsg, boolean delayFinish) {
        GalleryFinal.OnHanlderResultCallback callback = GalleryFinal
                .getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            callback.onHanlderFailure(requestCode, errormsg);
        }
        if (delayFinish) {
            mFinishHanlder.sendEmptyMessageDelayed(0, 500);
        } else {
            finishGalleryFinalPage();
        }
    }

    protected void resultFailure(String errormsg, boolean delayFinish) {
        GalleryFinal.OnHanlderResultCallback callback = GalleryFinal
                .getCallback();
        int requestCode = GalleryFinal.getRequestCode();
        if (callback != null) {
            callback.onHanlderFailure(requestCode, errormsg);
        }
        if (delayFinish) {
            finishGalleryFinalPage();
        } else {
            finishGalleryFinalPage();
        }
    }

    protected void finishGalleryFinalPage() {
        ActivityManager.getActivityManager().finishActivity(
                PhotoEditActivity.class);
        ActivityManager.getActivityManager().finishActivity(
                PhotoSelectActivity.class);
        Global.mPhotoSelectActivity = null;
        System.gc();
    }

    protected abstract void takeResult(PhotoInfo info);

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, this);
    }

    @Override
    public void onPermissionsGranted(List<String> list) {
    }

    @Override
    public void onPermissionsDenied(List<String> list) {
    }
}
