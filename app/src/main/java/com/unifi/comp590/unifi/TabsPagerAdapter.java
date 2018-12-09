package com.unifi.comp590.unifi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TabsPagerAdapter extends FragmentPagerAdapter {


    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case 2: RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int i) {
        switch (i) {
            case 0:return "Chats";

            case 1:return "Friends";

            case 2:return "Requests";

            default:
                return null;
        }

    }

}
