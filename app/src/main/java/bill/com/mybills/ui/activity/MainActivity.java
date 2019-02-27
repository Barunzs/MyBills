package bill.com.mybills.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bill.com.mybills.R;
import bill.com.mybills.config.AppDAL;
import bill.com.mybills.model.BusinessProfile;
import bill.com.mybills.model.Item;
import bill.com.mybills.model.MenuItemObject;
import bill.com.mybills.ui.adapter.CustomAdapter;
import bill.com.mybills.ui.fragment.BillFragment;
import bill.com.mybills.ui.fragment.CustomDateDialog;
import bill.com.mybills.ui.fragment.EditProfileFragment;
import bill.com.mybills.ui.fragment.MyBillTransactionFragment;
import bill.com.mybills.ui.fragment.MyProfileFragment;
import bill.com.mybills.ui.fragment.ScanFragment;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private AppDAL appDAL = null;
    private Fragment fragment = null;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDAL = new AppDAL(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        mTitle = mDrawerTitle = getTitle();
        Toolbar topToolBar = findViewById(R.id.toolbar2);
        setSupportActionBar(topToolBar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        auth = FirebaseAuth.getInstance();
        List<MenuItemObject> listViewItems = new ArrayList<>();
        listViewItems.add(new MenuItemObject("My Profile", android.R.drawable.ic_menu_day));
        listViewItems.add(new MenuItemObject("Edit Profile", R.drawable.img_profile_picture_placeholder));
        listViewItems.add(new MenuItemObject("Generate Bill", android.R.drawable.ic_menu_agenda));
        listViewItems.add(new MenuItemObject("Scan Barcode", android.R.drawable.ic_popup_sync));
        listViewItems.add(new MenuItemObject("My Transaction", android.R.drawable.ic_menu_recent_history));
        listViewItems.add(new MenuItemObject("Preview Bill", android.R.drawable.ic_menu_sort_by_size));
        listViewItems.add(new MenuItemObject("Reports", android.R.drawable.ic_menu_report_image));
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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fragment = new MyProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment,
                MyProfileFragment.Companion.getTAG()).commit();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                selectItemFragment(position);
            }
        });
    }



    private void selectItemFragment(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragment = new MyProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        MyProfileFragment.Companion.getTAG()).commit();
                break;
            case 1:
                fragment = new EditProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        EditProfileFragment.Companion.getTAG()).commit();
                break;
            case 2:
                fragment = new BillFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        BillFragment.Companion.getTAG()).commit();
                break;
            case 3:
                fragment = new ScanFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        ScanFragment.Companion.getTAG()).commit();
                break;

            case 4:
                fragment = new MyBillTransactionFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment,
                        MyBillTransactionFragment.Companion.getTAG()).commit();
                break;
            case 5:
                Intent billPreviewIntent = new Intent(getApplicationContext(), BillPreviewActivity.class);
                startActivity(billPreviewIntent);
            break;
            case 6:
                /*FragmentManager fm = getSupportFragmentManager();
                CustomDateDialog editNameDialogFragment = CustomDateDialog.Companion.newInstance("kkkk");
                editNameDialogFragment.show(fm, "fragment_edit_name");*/
                break;
            default:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                auth.signOut();
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
        Objects.requireNonNull( getSupportActionBar() ).setTitle(mTitle);
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
        if (id == R.id.action_preview) {
            //Bundle bundle = new Bundle();
            //ArrayList billItemListObj  = getBillItemList();
            //bundle.putParcelableArrayList("billItemList", billItemListObj);
            Intent intent = new Intent(getApplicationContext(), BillPreviewActivity.class);
            //intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewItem() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        fragment = new BillFragment();
        ft.add(R.id.main_fragment_container, fragment);
        ft.commit();
    }

    private ArrayList getBillItemList() {
        String itemListJsonDB = appDAL.getBillItemJson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<String> billItemListJson = gson.fromJson(itemListJsonDB, type);
        ArrayList billItemListObj = new ArrayList<Item>();
        if (billItemListJson != null && billItemListJson.size() > 0) {
            Type itemType = new TypeToken<Item>() {
            }.getType();
            Gson itemGson = new Gson();
            for (String billItemObj : billItemListJson) {
                Item billItem = itemGson.fromJson(billItemObj, itemType);
                billItemListObj.add(billItem);
            }
        }
        return billItemListObj;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docref = db.collection(user.getUid()).document("Business Profile");
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        BusinessProfile businessProfile = document.toObject(BusinessProfile.class);
                        showalert(businessProfile.isActive);
                    }
                }
            }
        });
    }

    private void showalert(Boolean isActive) {
        if (!isActive) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder.setTitle("Payment")
                    .setMessage("Please pay to use the full version of the app")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }
}
