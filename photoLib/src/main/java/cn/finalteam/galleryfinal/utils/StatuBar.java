package cn.finalteam.galleryfinal.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ifcnt.photolib.R;

/**
 * 设置系统标题栏跟应用的标题栏颜色保持一致
 */
public class StatuBar
{

	/**
	 * 设置系统状态栏 --- Translucent(半透明)
	 * */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void setTranslucentStatus(Activity activity, View view)
	{
		// 判断版本是4.4以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window win = activity.getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);

			view.setFitsSystemWindows(true);

			SystemStatusManager tintManager = new SystemStatusManager(activity);
			// 打开系统状态栏控制
			tintManager.setStatusBarTintEnabled(true);
			// tintManager.setStatusBarTintResource(R.drawable.title_bg_repeat);//设置背景
			tintManager.setStatusBarTintColor(Color.parseColor("#1588ee"));// 设置背景

			// 设置系统栏需要的内偏移
			// layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);
			view.setPadding(0, ScreenUtils.getStatusHeight(activity), 0, 0);
		}
	}

	/**
	 * 设置系统状态栏 --- Translucent(半透明)
	 * 
	 * @param activity
	 * @param view
	 * @param color
	 *            ：指定需要的颜色
	 */
	public static void setTranslucentStatus(Activity activity, View view,
			int color)
	{
		// 判断版本是4.4以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			/*
			 * Window win = activity.getWindow(); WindowManager.LayoutParams
			 * winParams = win.getAttributes(); final int bits =
			 * WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			 * winParams.flags |= bits; win.setAttributes(winParams);
			 */
			view.setFitsSystemWindows(true);
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemStatusManager tintManager = new SystemStatusManager(activity);
			// 打开系统状态栏控制
			tintManager.setStatusBarTintEnabled(true);
			// tintManager.setStatusBarTintResource(R.drawable.title_bg_repeat);//设置背景
			tintManager.setStatusBarTintColor(color);// 设置背景

			// 设置系统栏需要的内偏移
			view.setPadding(0, ScreenUtils.getStatusHeight(activity), 0, 0);
		}
	}
}
