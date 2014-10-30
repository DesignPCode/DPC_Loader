package com.dpc.dpc_loader.utils;

import android.util.Log;

/**
 * 
 * @ClassName: LogUtil
 * @Description: TODO(日志工具 ：flag = true打印 false取消打印)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-23
 * 
 */
public class LogUtil
{
	public static boolean flag = true;

	public final static void e(String tag, String msg, Throwable tr)
	{
		if (flag) 
			Log.e(tag, msg, tr);
	}

	public final static void e(String tag, String msg)
	{
		if (flag)
			Log.e(tag, msg);
	}

	public final static void e(String msg)
	{
		if (flag)
			Log.e("", msg);
	}

	public final static void e(Throwable tr)
	{
		if (flag)
			Log.e("", "", tr);
	}

	public final static void d(String tag, String msg)
	{
		if (flag)
			Log.d(tag, msg);
	}

	public final static void d(String msg)
	{
		if (flag)
			Log.d("", msg);
	}

	public final static void i(Throwable tr)
	{
		if (flag)
			Log.i("", "", tr);
	}

	public final static void i(String s, String s1, Throwable tr)
	{
		if (flag)
			Log.i(s, s1, tr);
	}

	public final static void i(String tag, String msg)
	{
		if (flag)
			Log.i(tag, msg);
	}

	public final static void i(String msg)
	{
		if (flag)
			Log.i("", msg);
	}

}
