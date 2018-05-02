package com.example.aliano.clientcircadian_va;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Aliano on 19/05/2017.
 */
// this is for the Home page tabs
class PagerAdapterCollection extends FragmentPagerAdapter {
    private final String tabTitles[]; // a mettre dans string
    private final static int PAGE_COUNT = 2;
    private Context context;
    // private final List<Fragment> mFragments = new ArrayList<Fragment>();


    PagerAdapterCollection(FragmentManager fm, Context c) {
        super(fm);
        this.context = c;
        tabTitles = new String[]{c.getResources().getString(R.string.collection_1),c.getResources().getString(R.string.collection_2)};
    }
    /*public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }
*/
    @Override
    public int getCount() {
        // mFragments.size();
        return PAGE_COUNT;
    }

    /**@Override
    public Fragment getItem(int position) {
    return new Fragment();
    }**/
    @Override
    public Fragment getItem(int pos) {
        switch(pos) {
            case 0: return FragmentCollection_tab1.newInstance(tabTitles[0]);
            case 1: return FragmentCollection_tab2.newInstance(tabTitles[1]);
            default: return FragmentCollection_tab1.newInstance(tabTitles[0]);
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
