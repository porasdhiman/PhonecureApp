package com.worksdelight.phonecure;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

/**
 * Created by worksdelight on 01/02/17.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[];
Context c;
    public SampleFragmentPagerAdapter(FragmentManager fm,Context c) {
        super(fm);
        this.c=c;
        tabTitles=new String[] {c.getResources().getString(R.string.pending), c.getResources().getString(R.string.completed)};
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment

            return UserPendingFragment.newInstance(c.getResources().getString(R.string.pending));
            case 1: // Fragment # 0 - This will show FirstFragment different title

                return UserCompletedFragment.newInstance(c.getResources().getString(R.string.completed));
            default:
                return UserPendingFragment.newInstance(c.getResources().getString(R.string.pending));
        }
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
       /* View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.imgView);
        img.setImageResource(imageResId[position]);*/
        return null;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
