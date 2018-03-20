package com.example.sahilsa.mediapicker.pickMedia.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sahilsa.mediapicker.R;
import com.example.sahilsa.mediapicker.pickMedia.GalleryActivity;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectionTitle;
    public static String title;
    public static int maxSelection;
    public static int mode;
    GalleryActivity.PreviousPageFragmentListener previousPageFragmentListener;

    GalleryActivity.PreviousPageVideoFragmentListener previousPageVideoFragmentListener;

    public static GalleryFragment newInstance() {
        GalleryFragment imagesFragment = new GalleryFragment();
        return imagesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        maxSelection = 1;
        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE;
        mode = 1;
        selectionTitle = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        OneFragment.selected.clear();
        ImagesFragment.imagesSelected.clear();

        TwoFragment.selected.clear();
        VideosFragment.imagesSelected.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_gallery, container, false);
        viewPager = v.findViewById(R.id.viewpager);
        tabLayout = v.findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (mode == 1 || mode == 2) {
            adapter.addFragment(new OneFragment(), "Images");
        }
        if (mode == 1 || mode == 3)
            adapter.addFragment(new TwoFragment(), "Videos");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Fragment mFragmentAtPos0;
        private Fragment mFragmentAtPos1;


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mFragmentAtPos0 == null) {
                    mFragmentAtPos0 = OneFragment.newInstance(new GalleryActivity.FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getChildFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                            mFragmentAtPos0 = ImagesFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }
                previousPageFragmentListener = new GalleryActivity.PreviousPageFragmentListener() {
                    @Override
                    public void onSwitchToPreviousFragment() {
                        getChildFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                        mFragmentAtPos0 = OneFragment.newInstance();
                        notifyDataSetChanged();
                    }
                };

                return mFragmentAtPos0;
            } else if (position == 1) {
                if (mFragmentAtPos1 == null) {
                    mFragmentAtPos1 = TwoFragment.newInstance(new GalleryActivity.FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getChildFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
                            mFragmentAtPos1 = VideosFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }

                if (viewPager.getCurrentItem() == 1)
                    previousPageVideoFragmentListener = new GalleryActivity.PreviousPageVideoFragmentListener() {
                        @Override
                        public void onSwitchToPreviousVideoFragment() {
                            getChildFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
                            mFragmentAtPos1 = TwoFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    };

                return mFragmentAtPos1;
            }
            return null;

//            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof OneFragment && mFragmentAtPos0 instanceof ImagesFragment)
                return POSITION_NONE;
            else if (object instanceof ImagesFragment && mFragmentAtPos0 instanceof OneFragment)
                return POSITION_NONE;
            else if (object instanceof TwoFragment && mFragmentAtPos1 instanceof VideosFragment)
                return POSITION_NONE;
            else if (object instanceof VideosFragment && mFragmentAtPos1 instanceof TwoFragment)
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

    }

    public boolean onBackPressed(){
        boolean found = false;
        boolean foundVideo = false;
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fObj : fragments) {
            if (fObj instanceof ImagesFragment) {
                found = true;
            }

            if (fObj instanceof VideosFragment) {
                foundVideo = true;
            }
        }
        if (found && viewPager.getCurrentItem() == 0) {
            previousPageFragmentListener.onSwitchToPreviousFragment();
            return false;
        } else if (foundVideo && viewPager.getCurrentItem() == 1) {
            previousPageVideoFragmentListener.onSwitchToPreviousVideoFragment();
            return false;
        } else {
            return true;
        }
    }

}



