package com.dpc.dpc_loader.loader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dpc.dpc_loader.entry.AppEntry;
import com.dpc.dpc_loader.observer.InstalledAppsObserver;
import com.dpc.dpc_loader.observer.SystemLocaleObserver;
import com.dpc.dpc_loader.utils.LogUtil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

/**
 * 
 * @ClassName: AppListLoader
 * @Description: TODO(Loader主要实现，这是此demo最关键的地方)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-30
 * 
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>>
{
	private static final String TAG = "AppListLoader";

	public final PackageManager mPm;

	// 存放加载的数据reference
	private List<AppEntry> mApps;

	public AppListLoader(Context ctx)
	{
		// 注意：不要保存ctx对象的索引（一般我们这么使用：this.context =
		// ctx），这种操作会倒是泄漏的。如果需要上下文索引可以调用getContext()
		// 主要原因就是这个loader可能被不同的Activity使用
		super(ctx);
		mPm = getContext().getPackageManager();
	}

	/****************************************************/
	/************** (1) 异步加载数据 *************************/
	/****************************************************/

	// 后台加载安装的application数据
	@Override
	public List<AppEntry> loadInBackground()
	{
		LogUtil.i(TAG, "Loader inBackground");

		// 方法主要本体：加载数据
		List<ApplicationInfo> apps = mPm.getInstalledApplications(0);

		if (apps == null)
		{
			apps = new ArrayList<ApplicationInfo>();
		}

		// 加载Labels
		List<AppEntry> entries = new ArrayList<AppEntry>(apps.size());
		for (int i = 0; i < apps.size(); i++)
		{
			AppEntry entry = new AppEntry(this, apps.get(i));
			entry.loadLabel(getContext());
			entries.add(entry);
		}

		// 工具：排序
		Collections.sort(entries, ALPHA_COMPARATOR);

		return entries;
	}

	/*******************************************/
	/*********** (2) 传递数据 **********************/
	/*******************************************/

	// 传递数据（superclass会完成数据的传送）给onLoadFinished
	@Override
	public void deliverResult(List<AppEntry> apps)
	{
		if (isReset())
		{
			LogUtil.i(TAG, "Loader Reset ");

			// Loader被reset之后，需要释放资源
			// 这种情况一般出现在正在异步加载数据的时候,Loader被reset了。这样加载过来的数据本来是需要传递出去的，但是Loader就在此时被reset状态了
			// Loader就不需要传递数据了
			if (apps != null)
			{
				// 释放资源
				releaseResources(apps);
				return;
			}
		}

		List<AppEntry> oldApps = mApps;
		mApps = apps;

		if (isStarted())
		{
			LogUtil.i(TAG, "Loader 在started状态传递数据");
			// Loader处于started状态，数据正常传递（superclass可以完成这个任务）
			super.deliverResult(apps);
		}

		// 释放不需要的旧数据
		if (oldApps != null && oldApps != apps)
		{
			LogUtil.i(TAG, "Loader 释放就的数据");
			releaseResources(oldApps);
		}
	}

	/*********************************************************/
	/************* (3) Loader的三种状态 ***************************/
	/*********************************************************/

	@Override
	protected void onStartLoading()
	{
		LogUtil.i(TAG, "Loader onStartLoading");
		if (mApps != null)
		{
			LogUtil.i(TAG, "Loader 在onStartLoading状态即使传递数据");
			deliverResult(mApps);
		}

		// 注册监听器监测数据源的变化
		if (mAppsObserver == null)
		{
			mAppsObserver = new InstalledAppsObserver(this);
		}

		if (mLocaleObserver == null)
		{
			mLocaleObserver = new SystemLocaleObserver(this);
		}

		if (takeContentChanged())
		{
			LogUtil.i(TAG, "数据源的数据发生了变化需要强制加载新的数据");
			// 当检测器发现数据源的数据发生了变化，促发Loader重新获取新的数据
			forceLoad();
		} else if (mApps == null)
		{
			// 如果数据是空的时候，强制Loader重新加载数据
			LogUtil.i(TAG, "Loader当前的数据为空null,强制重新加载数据");
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading()
	{
		LogUtil.i(TAG, "Loader onStopLoading");
		// Loader处于stop状态的时候，需要关闭Loader的加载任务
		cancelLoad();
		// 注意：stop状态下检测器还是会去检测数据源的变化的,这样如果数据源发生变化的时候系统还是强制加载数据的
	}

	@Override
	protected void onReset()
	{
		LogUtil.i(TAG, "Loader onReset");
		// 确保Loader处于stopped状态
		onStopLoading();

		// Loader处于reset状态的时候既不需要加载数据也不需要监听数据
		// 释放资源
		if (mApps != null)
		{
			releaseResources(mApps);
			mApps = null;
		}
		// 关闭监听
		if (mAppsObserver != null)
		{
			getContext().unregisterReceiver(mAppsObserver);
			mAppsObserver = null;
		}

		if (mLocaleObserver != null)
		{
			getContext().unregisterReceiver(mLocaleObserver);
			mLocaleObserver = null;
		}
	}

	@Override
	public void onCanceled(List<AppEntry> apps)
	{
		LogUtil.i(TAG, "Loader onCanceled");
		// 关闭当前的asynchronous加载
		super.onCanceled(apps);

		// Load被关闭了，需要释放所有关联的资源
		releaseResources(apps);
	}

	@Override
	public void forceLoad()
	{
		LogUtil.i(TAG, "Loader forceLoad");
		super.forceLoad();
	}

	/**
	 * @Title: releaseResources
	 * @Description: TODO(释放资源)
	 * @param @param apps 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void releaseResources(List<AppEntry> apps)
	{
		// 在这个demo里面，释放资源没有什么内容，其实主要针对的释放资源就是数据库之类的cursor。
		// 在这个方法里面做的事情就是释放所有关联Loader的referenc
	}

	/*********************************************************************/
	/************************ (4) 监听数据源 **********************************/
	/*********************************************************************/

	// 监听apk的安装，卸载，更新
	private InstalledAppsObserver mAppsObserver;

	// 监听系统Locale的变化（这里主要是系统语言发生变化）
	private SystemLocaleObserver mLocaleObserver;

	/**
	 * @Fields ALPHA_COMPARATOR : TODO(主要是对application的排序)
	 */
	private static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>()
	{
		Collator sCollator = Collator.getInstance();

		@Override
		public int compare(AppEntry object1, AppEntry object2)
		{
			return sCollator.compare(object1.getLabel(), object2.getLabel());
		}
	};
}
