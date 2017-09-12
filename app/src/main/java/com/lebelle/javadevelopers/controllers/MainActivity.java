package com.lebelle.javadevelopers.controllers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lebelle.javadevelopers.R;
import com.lebelle.javadevelopers.api.Client;
import com.lebelle.javadevelopers.api.Service;
import com.lebelle.javadevelopers.model.JavaDevelopers;
import com.lebelle.javadevelopers.model.JavaDevelopersAdapter;
import com.lebelle.javadevelopers.model.JavaDevelopersResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    //navigation drawer
    private DrawerLayout mDrawerLayout;
    //app toolbar
    private Toolbar toolbar;
    //drawer toogle
    private ActionBarDrawerToggle drawerToggle;

    public JavaDevelopersResponse javaDevelopersResponse;

    private RecyclerView recyclerView;
    TextView Disconnected;
    TextView EmptyState;
    private List<JavaDevelopers> myJavaDevelopers;
    ProgressDialog pd;
    LinearLayout load_more;
    private SwipeRefreshLayout swipeRefreshLayout;

    //toggle switch for night mode
    private SwitchCompat toggle;

    //app theme preference
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    //pagination constants
    // The total number of items in the data set after the last load
    private int previousTotal = 0;
    // boolean for awaiting data loading
    private boolean loading = true;
    // The minimum amount of items to have below your current scroll position before loading more
    private int visibleThreshold = 0;

    private int firstVisibleItem, visibleItemCount, totalItemCount;

    public int current_page = 1;

    /**
     * Adapter and layout manager for the list of javaDevelopers
     */
    private JavaDevelopersAdapter mAdapter;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        myJavaDevelopers = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        Disconnected = (TextView) findViewById(R.id.disconnected);
        EmptyState = (TextView) findViewById(R.id.empty_state);
        load_more = (LinearLayout) findViewById(R.id.progress_bar);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                current_page = 1;
                reloadData();
                Toast.makeText(MainActivity.this, "Quad Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        //toolbar layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //navigation layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setUpDrawerToggle();
        mDrawerLayout.addDrawerListener(drawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        //set switch access in navigationView
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.night_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        //set switch to change theme settings
        toggle = (SwitchCompat) actionView.findViewById(R.id.drawer_switch);
        //toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          //  SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            //@Override
            //public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                //toggleTheme
              //  if (isChecked){
                    //change theme
                //    setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                  //  editor.putBoolean(PREF_DARK_THEME,true);
                   // editor.apply();
                   // Intent intent = getIntent();
                   // finish();
                    //startActivity(intent);
                //}else {
                  //  setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //editor.putBoolean(PREF_DARK_THEME,false);
                    //editor.apply();
                //}
            //}
        //});
    }

    private void initViews(){
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Java Developers...");
        pd.setCancelable(false);
        pd.show();
        recyclerView=(RecyclerView) findViewById(R.id.list_view);
        llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) //check for scroll down
            {
                //Get the integer values of the no of items in the screen, the total item available and the
                //items already seen and save them in the corresponding variables.
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = llm.getItemCount();
                firstVisibleItem = llm.findFirstVisibleItemPosition();

                //If loading is true as you scroll down, and you haven't gotten to the end
                //of the list, assign loading to false.
                //if (loading && (totalItemCount > previousTotal)) {
                  //  loading = false;
                    //previousTotal = totalItemCount;
                //}

                //if loading is false and you are at the end of the list increment current_page
                //by 1 and then check if you are not at the last page using the value in current_page
                //if it is true, then pass the current page number to the Url and fetch the data from the
                //internet and then update the info displayed in the RV if successful.
                if (loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    current_page ++;
                    if (current_page < 8) {
                        load_more.setVisibility(View.VISIBLE);//show the progress bar for reloading.
                        loadDATA();
                        loading = false;
                    } else {
                        Snackbar snackbar =
                        Snackbar.make(findViewById(R.id.main_activity), "No more Java Developers to disp" +
                                "lay, Probably end of the List!", Snackbar.LENGTH_INDEFINITE).
                                setAction("Dismiss", new View.OnClickListener(){
                                    @Override
                                public void onClick(View view){}});
                                snackbar.show();
                        loading = true;
                    }
                }
            }
        }
    });

    loadDATA();
    }

    private void loadDATA(){
        try{
            Client client = new Client();
            Service apiService =
                    client.getClient().create(Service.class);
            Call<JavaDevelopersResponse> call = apiService.getJavaDevelopers(current_page);
            call.enqueue(new Callback<JavaDevelopersResponse>() {
                @Override
                public void onResponse(Call<JavaDevelopersResponse> call, Response<JavaDevelopersResponse> response) {
                    if (response != null) {
                        javaDevelopersResponse = response.body();
                        swipeRefreshLayout.setRefreshing(false);
                        pd.hide();
                        load_more.setVisibility(View.GONE);
                        if (javaDevelopersResponse != null) {
                            myJavaDevelopers.addAll(javaDevelopersResponse.getJavaDevelopers());
                            loading = true;
                        }
                        //Make a check of the page number. If it is the first page create an instance of the adapter
                        //and set it before notifying it for changes else just notify the already created adapter for changes
                        if (current_page == 1) {
                            mAdapter = new JavaDevelopersAdapter(getApplicationContext(), myJavaDevelopers);
                            recyclerView.smoothScrollToPosition(0);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JavaDevelopersResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    EmptyState.setVisibility(View.VISIBLE);
                    pd.hide();

                }
            });

        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //swipe refresh recyclerview
    private void reloadData() {
        try {
            Client client = new Client();
            Service service =
                    client.getClient().create(Service.class);
            Call<JavaDevelopersResponse> call = service.getJavaDevelopers(current_page);
            call.enqueue(new Callback<JavaDevelopersResponse>() {
                @Override
                public void onResponse(Call<JavaDevelopersResponse> call, Response<JavaDevelopersResponse> response) {
                    if (response != null) {
                        javaDevelopersResponse = response.body();
                        myJavaDevelopers.clear();
                        if (javaDevelopersResponse != null) {
                            myJavaDevelopers.addAll(javaDevelopersResponse.getJavaDevelopers());
                        }
                        mAdapter = new JavaDevelopersAdapter(getApplicationContext(), myJavaDevelopers);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        EmptyState.setVisibility(View.GONE);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JavaDevelopersResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Quad failed to refresh", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }

    private ActionBarDrawerToggle setUpDrawerToggle(){
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.app_name2);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        //return true;
                        switch (menuItem.getItemId()) {
                            case R.id.share:
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String stringToShare = "Hello! please check out my quiz app: https://goo.gl/0o2FoO";
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
                                startActivity(Intent.createChooser(sharingIntent, "Share Quad via"));
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.feedback:
                                Intent feedbackEmail = new Intent(Intent.ACTION_SENDTO);
                                feedbackEmail.setData(Uri.parse("mailto:omawumieyekpimi@gmail.com")); // only email apps should handle this
                                feedbackEmail.putExtra(Intent.EXTRA_EMAIL, "");
                                feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Quad Feedback");
                                if (feedbackEmail.resolveActivity(getPackageManager()) != null) {
                                    startActivity(Intent.createChooser(feedbackEmail, "Send Quad Feedback via"));
                                }
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.about:
                                final Dialog d = new Dialog(MainActivity.this);
                                d.setContentView(R.layout.about_quad);
                                d.setTitle("About");
                                Button connect = (Button) d.findViewById(R.id.dialogButtonOK);
                                connect.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        d.dismiss();                                     }
                                });
                                d.show();
                                mDrawerLayout.closeDrawers();
                                break;
                             case android.R.id.home:
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }
                        return true;
                    }
                });
    }

    //onCreate boolean for search icon on appbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;

    }

    //recycler view search filter for users
    private void search(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    //set up app night mode
    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }
}