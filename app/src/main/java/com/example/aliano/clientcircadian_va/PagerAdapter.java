package com.example.aliano.clientcircadian_va;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Aliano on 19/05/2017.
 */
// this is for the Home page tabs
class PagerAdapter extends FragmentPagerAdapter{
    Context context;
    private final String tabTitles[];
    private final static int PAGE_COUNT = 3;
   // private final List<Fragment> mFragments = new ArrayList<Fragment>();


    PagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.context = c;
        tabTitles = new String[]{c.getResources().getString(R.string.main_1),c.getResources().getString(R.string.main_2), c.getResources().getString(R.string.main_3)}; // a mettre dans string
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
        //final String o1 = this.getResources().getString(R.string.main_1);

        switch(pos) {
            case 0: return FragmentMain_tab1.newInstance(tabTitles[0]);
            case 1: return FragmentMain_tab2.newInstance(tabTitles[1]);
            case 2: return FragmentMain_tab3.newInstance(tabTitles[2]);
            default: return FragmentMain_tab1.newInstance(tabTitles[0]);
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
