package edu.temple.bitcoindashboard;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ValueDashboard currentValueFragment = new ValueDashboard();
    ChartFragment chartFragment = new ChartFragment();
    BlockFragment blockFragment = new BlockFragment();
    BalanceFragment balanceFragment = new BalanceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.drawer_layout) == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
            }
            ListView lv = (ListView) findViewById(R.id.listView);

            lv.setAdapter(new CustomAdapter(this, getResources().getStringArray(R.array.navigation)));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0){
                            getPrice();
                        } else if(position == 1){
                            getChart();
                        } else if(position == 2){
                            getBlock();
                        } else if(position == 3){
                            getBalance();
                        }
                }
            });
        }else {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            assert drawer != null;
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        }
        loadFragment(R.id.Price_Frag, currentValueFragment);
        getPrice();

    }

    @Override
    public void onBackPressed() {
       if(findViewById(R.id.drawer_layout) !=null){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
      } else{
           super.onBackPressed();
       }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dash) {
            getPrice();
        } else if (id == R.id.nav_chart) {
            getChart();
        } else if (id == R.id.nav_block) {
            getBlock();
        } else{
            getBalance();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/*------------------------------------------------------------------------------------------------------------------------------*/
/* Getting the current value of bitcoins. Updates every 15 seconds. */
    private void getPrice() {

        loadFragment(R.id.Price_Frag, currentValueFragment);
        String temp = getResources().getString(R.string.current_price);
        setTitle(temp);

    }



/*------------------------------------------------------------------------------------------------------------------------------*/
/* Getting the chart data. Updates every 15 seconds. */
    private void getChart(){

        loadFragment(R.id.Price_Frag, chartFragment);
        String temp = getResources().getString(R.string.chart);
        setTitle(temp);

    }

/*------------------------------------------------------------------------------------------------------------------------------*/
/* Loading the block fragment to run */

    private void getBlock(){

        loadFragment(R.id.Price_Frag, blockFragment);
        String temp = getResources().getString(R.string.block);
        setTitle(temp);

    }
/*------------------------------------------------------------------------------------------------------------------------------*/
/* Loading the Address balance fragment to run */

    private void getBalance(){

        loadFragment(R.id.Price_Frag, balanceFragment);
        String temp = getResources().getString(R.string.balance);
        setTitle(temp);

    }

/*------------------------------------------------------------------------------------------------------------------------------*/
/* Loading the Fragment onto the activity*/
    private void loadFragment(int paneId, Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(paneId, fragment);
        ft.commit();
        fm.executePendingTransactions();

    }

}







