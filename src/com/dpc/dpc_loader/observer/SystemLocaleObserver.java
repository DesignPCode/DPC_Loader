package com.dpc.dpc_loader.observer;

import com.dpc.dpc_loader.loader.AppListLoader;
import com.dpc.dpc_loader.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 
 * @ClassName: SystemLocaleObserver
 * @Description: TODO(监听器：监测系统Locale数据变化)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-10-30
 * 
 */
public class SystemLocaleObserver extends BroadcastReceiver
{
	private static final String TAG = "SystemLocaleObserver";

	private AppListLoader mLoader;

	public SystemLocaleObserver(AppListLoader loader)
	{
		mLoader = loader;
		IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
		mLoader.getContext().registerReceiver(this, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.i(TAG, "数据源数据发生了变化-locale");
		// 通知Loader数据发生了变化.
		mLoader.onContentChanged();
	}
}