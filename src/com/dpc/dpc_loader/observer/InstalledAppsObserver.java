package com.dpc.dpc_loader.observer;

import com.dpc.dpc_loader.loader.AppListLoader;
import com.dpc.dpc_loader.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 
 * @ClassName: InstalledAppsObserver
 * @Description: TODO(监听器：监测数据源apk的安装，卸载，更新)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-30
 * 
 */
public class InstalledAppsObserver extends BroadcastReceiver
{
	private static final String TAG = "InstalledAppsObserver";

	private AppListLoader mLoader;

	public InstalledAppsObserver(AppListLoader loader)
	{
		mLoader = loader;

		// 注册监听器（apk的安装，卸载，更行）
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		mLoader.getContext().registerReceiver(this, filter);

		// 注册监听器（Locale的变化）
		IntentFilter sdFilter = new IntentFilter();
		sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		mLoader.getContext().registerReceiver(this, sdFilter);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.i(TAG, "监测到数据源的数据发生了变化");
		// 通知Loader数据发生了变化
		mLoader.onContentChanged();
	}
}