package com.vijayanix.iot.ui.device;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class VijPagerAdapter extends android.support.v4.view.PagerAdapter
{
    
    private List<View> mViewList;
    
    public VijPagerAdapter(List<View> viewList)
    {
        mViewList = viewList;
    }
    
    @Override
    public int getCount()
    {
        return mViewList.size();
    }
    
    @Override
    public boolean isViewFromObject(View arg0, Object arg1)
    {
        return arg0 == arg1;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView(mViewList.get(position));
    }
}
