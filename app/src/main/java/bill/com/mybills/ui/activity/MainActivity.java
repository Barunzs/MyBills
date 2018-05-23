package bill.com.mybills.ui.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bill.com.mybills.LoginActivity;
import bill.com.mybills.R;
import bill.com.mybills.config.AppDAL;
import bill.com.mybills.model.MenuItemObject;
import bill.com.mybills.ui.adapter.CustomAdapter;
import bill.com.mybills.ui.fragment.BillFragment;
import bill.com.mybills.ui.fragment.DefaultFragment;
import bill.com.mybills.ui.fragment.EditProfileFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] titles = {"Nigeria", "Ghana", "Senegal", "Togo"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar topToolBar;
    private AppDAL appDAL = null;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDAL = new AppDAL(getApplicationContext());
        mTitle = mDrawerTitle = getTitle();
        topToolBar = findViewById(R.id.toolbar2);
        setSupportActionBar(topToolBar);
        //topToolBar.setLogo(R.drawable.background);
        //topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        List<MenuItemObject> listViewItems = new ArrayList<>();
        listViewItems.add(new MenuItemObject("My Profile", R.drawable.img_profile_picture_placeholder));
        listViewItems.add(new MenuItemObject("Generate Bill", android.R.drawable.ic_menu_agenda));
        listViewItems.add(new MenuItemObject("Scan Barcode", android.R.drawable.ic_popup_sync));
        listViewItems.add(new MenuItemObject("My Transaction", android.R.drawable.ic_menu_recent_history));
        listViewItems.add(new MenuItemObject("Settings", android.R.drawable.ic_menu_info_details));
        listViewItems.add(new MenuItemObject("Logout", android.R.drawable.ic_lock_power_off));

        mDrawerList.setAdapter(new CustomAdapter(this, listViewItems));

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                Toast.makeText(getApplicationContext(), "Position " + position, Toast.LENGTH_LONG).show();
                selectItemFragment(position);
            }
        });
    }

    private void selectItemFragment(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragment = new EditProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        EditProfileFragment.Companion.getTAG()).commit();
                break;
            case 1:
                fragment = new BillFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        BillFragment.Companion.getTAG()).commit();
                break;
            case 2:
                fragment = new DefaultFragment();
                break;
            case 3:
                fragment = new DefaultFragment();
                break;
            default:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                appDAL.setLoginSucess(false);
                finish();
                break;
        }
        if (fragment != null) {
            mDrawerList.setItemChecked(position, true);
            //setTitle(titles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        /*BillFragment billFragment = (BillFragment) getSupportFragmentManager().findFragmentByTag(BillFragment.Companion.getTAG());*/
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return !drawerOpen && super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }
        if(id==R.id.action_preview){
            Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
            startActivity(intent);
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
