package com.example.appsname;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar Section
        toolbar = (Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Drawer Layout
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        Menu navMenu = nvDrawer.getMenu();
        if(PreferencesController.getID(MainActivity.this) != null) { //login state
            navMenu.findItem(R.id.nav_login).setVisible(false);
        } else { //logout state
            navMenu.findItem(R.id.nav_profile).setVisible(false);
            navMenu.findItem(R.id.nav_feedback).setVisible(false);
            navMenu.findItem(R.id.nav_logout).setVisible(false);
        }
        setupDrawerContent(nvDrawer);

        //Tab Layout
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPager(viewPager);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                Intent intentHome = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intentHome);
                break;
            case R.id.nav_profile:
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_feedback:
                Intent intentFeedback = new Intent(MainActivity.this, FeedbackHistoryActivity.class);
                startActivity(intentFeedback);
                break;
            case R.id.nav_login:
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.nav_logout:
                ProductActivity.countItem = 0;
                Intent intentLogout = new Intent(MainActivity.this, MainActivity.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogout);
                PreferencesController.removeUser(getApplicationContext()); // remove user id from shared preference
                break;
            default:
                Intent intentDefault = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intentDefault);
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }


    private void setupTabIcons() {
        TabLayout.Tab tabHome = tabLayout.getTabAt(0);
        tabHome.setIcon(R.drawable.tab_home);

        TabLayout.Tab tabOrderStatus = tabLayout.getTabAt(1);
        tabOrderStatus.setIcon(R.drawable.tab_order_status);

    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getFragmentManager());
        adapter.addFragment(new HomeFragment(), "Beranda");
        adapter.addFragment(new OrderStatusFragment(), "Status Pesanan");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem itemCart = menu.findItem(R.id.action_basket);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, String.valueOf(ProductActivity.countItem));
        invalidateOptionsMenu();
        return true;
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_basket:
                Intent intentBasket = new Intent(MainActivity.this, ShoppingListActivity.class);
                startActivity(intentBasket);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
}
