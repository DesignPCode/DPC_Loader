package com.dpc.dpc_loader.adapter;

import java.util.List;

import com.dpc.dpc_loader.R;
import com.dpc.dpc_loader.entry.AppEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName: AppListAdapter
 * @Description: TODO(自定义Adapter 显示安装的application信息)
 * @author N.Sun
 * @email niesen918@gmail.com
 * @date 2014-9-30
 * 
 */
public class AppListAdapter extends ArrayAdapter<AppEntry>
{
	private LayoutInflater mInflater;

	public AppListAdapter(Context ctx)
	{
		super(ctx, android.R.layout.simple_list_item_2);
		mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view;

		if (convertView == null)
		{
			view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
		} else
		{
			view = convertView;
		}

		AppEntry item = getItem(position);
		((ImageView) view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
		((TextView) view.findViewById(R.id.text)).setText(item.getLabel());

		return view;
	}

	public void setData(List<AppEntry> data)
	{
		clear();
		if (data != null)
		{
			for (int i = 0; i < data.size(); i++)
			{
				add(data.get(i));
			}
		}
	}
}
