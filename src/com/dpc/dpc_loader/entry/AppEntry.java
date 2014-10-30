package com.dpc.dpc_loader.entry;

import java.io.File;

import com.dpc.dpc_loader.loader.AppListLoader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * 
 * @ClassName: AppEntry
 * @Description: TODO(安装应用的实体类)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-30
 * 
 */
public class AppEntry
{
	private final AppListLoader mLoader;
	private final ApplicationInfo mInfo;
	private final File mApkFile;
	private String mLabel;
	private Drawable mIcon;
	private boolean mMounted;

	public AppEntry(AppListLoader loader, ApplicationInfo info)
	{
		mLoader = loader;
		mInfo = info;
		mApkFile = new File(info.sourceDir);
	}

	public ApplicationInfo getApplicationInfo()
	{
		return mInfo;
	}

	public String getLabel()
	{
		return mLabel;
	}

	public Drawable getIcon()
	{
		if (mIcon == null)
		{
			if (mApkFile.exists())
			{
				mIcon = mInfo.loadIcon(mLoader.mPm);
				return mIcon;
			} else
			{
				mMounted = false;
			}
		} else if (!mMounted)
		{
			// 重新加载图标 一般用于安装的过程中
			if (mApkFile.exists())
			{
				mMounted = true;
				mIcon = mInfo.loadIcon(mLoader.mPm);
				return mIcon;
			}
		} else
		{
			return mIcon;
		}

		return mLoader.getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
	}

	@Override
	public String toString()
	{
		return mLabel;
	}

	public void loadLabel(Context context)
	{
		if (mLabel == null || !mMounted)
		{
			if (!mApkFile.exists())
			{
				mMounted = false;
				mLabel = mInfo.packageName;
			} else
			{
				mMounted = true;
				CharSequence label = mInfo.loadLabel(context.getPackageManager());
				mLabel = label != null ? label.toString() : mInfo.packageName;
			}
		}
	}
}