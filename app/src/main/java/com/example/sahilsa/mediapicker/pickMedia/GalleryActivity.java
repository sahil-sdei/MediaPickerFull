package com.example.sahilsa.mediapicker.pickMedia;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sahilsa.mediapicker.R;
import com.example.sahilsa.mediapicker.pickMedia.fragments.ImagesFragment;
import com.example.sahilsa.mediapicker.pickMedia.fragments.OneFragment;
import com.example.sahilsa.mediapicker.pickMedia.fragments.TwoFragment;
import com.example.sahilsa.mediapicker.pickMedia.fragments.VideosFragment;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectionTitle;
    public static String title;
    public static int maxSelection;
    public static int mode;
    PreviousPageFragmentListener previousPageFragmentListener;

    PreviousPageVideoFragmentListener previousPageVideoFragmentListener;


    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                returnResult();
//            }
//        });

        // title = getIntent().getExtras().getString("title");
        maxSelection = 1;
        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE;
        mode = 1;
//        setTitle("Picker");
        selectionTitle = 0;

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.commit();

        } else {
            //You already have the permission, just go ahead.
            setUpViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                setUpViewPager();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(GalleryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                setUpViewPager();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(GalleryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                setUpViewPager();
            }
        }
    }

    private void setUpViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        OneFragment.selected.clear();
        ImagesFragment.imagesSelected.clear();

        TwoFragment.selected.clear();
        VideosFragment.imagesSelected.clear();
    }

    //This method set up the tab view for images and videos
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (mode == 1 || mode == 2) {
            adapter.addFragment(new OneFragment(), "Images");
        }
        if (mode == 1 || mode == 3)
            adapter.addFragment(new TwoFragment(), "Videos");
        viewPager.setAdapter(adapter);
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
                    mFragmentAtPos0 = OneFragment.newInstance(new FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                            mFragmentAtPos0 = ImagesFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }
                previousPageFragmentListener = new PreviousPageFragmentListener() {
                    @Override
                    public void onSwitchToPreviousFragment() {
                        getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                        mFragmentAtPos0 = OneFragment.newInstance();
                        notifyDataSetChanged();
                    }
                };

                return mFragmentAtPos0;
            } else if (position == 1) {
                if (mFragmentAtPos1 == null) {
                    mFragmentAtPos1 = TwoFragment.newInstance(new FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
                            mFragmentAtPos1 = VideosFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }

                if (viewPager.getCurrentItem() == 1)
                    previousPageVideoFragmentListener = new PreviousPageVideoFragmentListener() {
                        @Override
                        public void onSwitchToPreviousVideoFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
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


    public interface FirstPageFragmentListener {
        void onSwitchToNextFragment();
    }

    public interface PreviousPageFragmentListener {
        void onSwitchToPreviousFragment();
    }

    public interface PreviousPageVideoFragmentListener {
        void onSwitchToPreviousVideoFragment();
    }

    private void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", ImagesFragment.imagesSelected);
        returnIntent.putStringArrayListExtra("resultVideo", VideosFragment.imagesSelected);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        boolean found = false;
        boolean foundVideo = false;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
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
        } else if (foundVideo && viewPager.getCurrentItem() == 1) {
            previousPageVideoFragmentListener.onSwitchToPreviousVideoFragment();
        } else {
            super.onBackPressed();
        }
    }
}
