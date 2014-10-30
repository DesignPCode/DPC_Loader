package com.dpc.dpc_loader;

import java.util.List;

import com.dpc.dpc_loader.adapter.AppListAdapter;
import com.dpc.dpc_loader.entry.AppEntry;
import com.dpc.dpc_loader.loader.AppListLoader;
import com.dpc.dpc_loader.utils.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @ClassName: MainActivity
 * @Description: TODO(放置AppListFragment主要显示安装的APK)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-30
 * 
 */
public class MainActivity extends FragmentActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 创建ListFragment 作为主要显示部分
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(android.R.id.content) == null)
		{
			AppListFragment list = new AppListFragment();
			fm.beginTransaction().add(android.R.id.content, list).commit();
		}
	}

	/**
	 * 
	 * @ClassName: AppListFragment
	 * @Description: TODO(显示安装的application:后台是Loader加载，管理由LoaderManager)
	 * @author N.Sun
	 * @email niesen918@gmail.com
	 * @date 2014-10-30
	 * 
	 */
	public static class AppListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<AppEntry>>
	{
		private static final String TAG = "AppListFragment";

		// 自定义Adapter
		private AppListAdapter mAdapter;

		// Loader的唯一ID 便于LoaderManger管理（后面会使用到这个ID）
		private static final int LOADER_ID = 1;

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
			super.onActivityCreated(savedInstanceState);
			setHasOptionsMenu(true);

			mAdapter = new AppListAdapter(getActivity());
			setEmptyText("No applications");
			setListAdapter(mAdapter);
			setListShown(false);
			LogUtil.i(TAG, "初始化Loader");
			if (getLoaderManager().getLoader(LOADER_ID) == null)
			{
				LogUtil.i(TAG, "重新创建了Loader");
			} else
			{
				LogUtil.i(TAG, "Loader复用");
			}
			// 这个方法是初始化Loader：如果Loader存在的话就重新复用，如果没有就重新创建
			getLoaderManager().initLoader(LOADER_ID, null, this);
		}

		/**********************/
		/** Loader 3个主要回调函数 **/
		/**********************/

		@Override
		public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args)
		{
			LogUtil.i(TAG, "onCreateLoader");
			return new AppListLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data)
		{
			LogUtil.i(TAG, "onLoadFinished");
			mAdapter.setData(data);

			// 控制界面
			if (isResumed())
			{
				setListShown(true);
			} else
			{
				setListShownNoAnimation(true);
			}
		}

		@Override
		public void onLoaderReset(Loader<List<AppEntry>> loader)
		{
			LogUtil.i(TAG, "onLoadReset");
			mAdapter.setData(null);
		}

		// 模拟系统locale数据源改变
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
			// actionbar-改变语言
			inflater.inflate(R.menu.activity_main, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId())
			{
			case R.id.menu_configure_locale:
				configureLocale();
				return true;
			}
			return false;
		}

		/**
		 * @Title: configureLocale
		 * @Description: TODO(模拟数据源数据改变：改变系统的语言)
		 * @param 设定文件
		 * @return void 返回类型
		 * @throws
		 */
		private void configureLocale()
		{
			Loader<AppEntry> loader = getLoaderManager().getLoader(LOADER_ID);
			if (loader != null)
			{
				startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
			}
		}
	}
}
