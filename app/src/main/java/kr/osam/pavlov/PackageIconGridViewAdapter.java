package kr.osam.pavlov;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class PackageIconGridViewAdapter extends BaseAdapter
{
    Context context;
    int layout;
    ArrayList<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();

    public PackageIconGridViewAdapter(Context context, int layout, ArrayList<ApplicationInfo> applicationInfos)
    {
        this.context = context;
        this.layout = layout;
        this.applicationInfos = applicationInfos;

    }

    @Override
    public int getCount()
    {
        return applicationInfos.size();
    }
    @Override
    public Object getItem(int position)
    {
        return applicationInfos.get(position);
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
            convertView = new GridItem()
        convertView.findViewById(R.id.iv_pkgicon);

        return convertView;
    }

}
