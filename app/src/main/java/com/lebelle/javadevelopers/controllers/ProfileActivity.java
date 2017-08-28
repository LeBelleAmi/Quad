package com.lebelle.javadevelopers.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lebelle.javadevelopers.R;
import com.lebelle.javadevelopers.api.Client;
import com.lebelle.javadevelopers.api.Service;
import com.lebelle.javadevelopers.model.DevelopersProfile;
import com.mikhaellopez.circularimageview.CircularImageView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lebelle.javadevelopers.R.id.avatar;


public class ProfileActivity extends AppCompatActivity {

    TextView fullName, userName, githubUrl, reposView, followersView, followingView, userBioView, locationView, blogView;
    ImageView imageView;
    CircularImageView circularImageView;
    Button shareProfile;
    public DevelopersProfile developersProfile;
    private ProgressBar loading;
    RelativeLayout userLoadingContainer;
    CoordinatorLayout userViewContainer;
    RelativeLayout networkContainer;
    Button refresh;

    //avatar animation
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int scrollRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //textviews declaration
        imageView = (ImageView) findViewById(R.id.backdrop);
        circularImageView = (CircularImageView) findViewById(avatar);
        userName = (TextView)findViewById(R.id.username);
        githubUrl = (TextView) findViewById(R.id.github_url);
        shareProfile = (Button) findViewById(R.id.share_button);
        reposView = (TextView) findViewById(R.id.repo);
        followersView = (TextView) findViewById(R.id.followers);
        followingView = (TextView) findViewById(R.id.following);
        userBioView = (TextView) findViewById(R.id.user_bio);
        fullName = (TextView) findViewById(R.id.real_name);
        locationView = (TextView) findViewById(R.id.location);
        blogView = (TextView) findViewById(R.id.blog);

        //progress bar for user profile
        loading = (ProgressBar) findViewById(R.id.loading_progress_bar);
        loading.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF0084"), PorterDuff.Mode.SRC_IN);
        userViewContainer = (CoordinatorLayout) findViewById(R.id.main_content);
        userLoadingContainer = (RelativeLayout) findViewById(R.id.loading_data);
        networkContainer = (RelativeLayout) findViewById(R.id.refresh_container);



        //get items from the adapter intent and set in views
        Bundle data = getIntent().getBundleExtra("items");
        final String profile_url = data.getString("profile_url");
        final String username = data.getString("username");
        String url = data.getString("url");
        String avatar = data.getString("avatar");


        userName.setText("@" + username);
        githubUrl.setText(profile_url);
        Glide.with(this)
                .load(avatar)
                //.asBitmap()
                .bitmapTransform(new BlurTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.navi_wallpaper)
                .into(imageView);
        Glide.with(this)
                .load(avatar)
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.avatar)
                .into(circularImageView);

        shareProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent sharingIntent = new Intent();
                sharingIntent.setAction(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String stringToShare = "Hello! Check out this awesome developer @"+  username +", " + profile_url + ".";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
                startActivity(Intent.createChooser(sharingIntent, "Share Profile via"));
            }
        });


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set collapsingToolbar title just like twitter

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        //implement offset listener for appbar and avatar
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) ;
                int percentage;
                {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    percentage = (Math.abs(verticalOffset)) * 100 / scrollRange;

                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(username);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }

                if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
                    mIsAvatarShown = false;

                    circularImageView.animate()
                            .scaleY(0).scaleX(0)
                            .setDuration(200)
                            .start();
                }

                if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
                    mIsAvatarShown = true;

                    circularImageView.animate()
                            .scaleY(1).scaleX(1)
                            .start();
                }
            }

        });

        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                networkContainer.setVisibility(View.GONE);
                userLoadingContainer.setVisibility(View.VISIBLE);
                fetchUserProfile(username);
            }
        });
        fetchUserProfile(username);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchUserProfile(String userName){
        try{

        Client client = new Client();
        Service apiService =
                client.getClient().create(Service.class);

        Call<DevelopersProfile> call = apiService.getDevelopersProfile(userName);

        call.enqueue(new Callback<DevelopersProfile>() {
            @Override
            public void onResponse(Call<DevelopersProfile> call, Response<DevelopersProfile> response) {
                developersProfile = response.body();
                showDeveloperInfo();
                userLoadingContainer.setVisibility(View.GONE);
                userViewContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<DevelopersProfile> call, Throwable t) {
                Log.e("Error", t.getMessage());
                networkContainer.setVisibility(View.VISIBLE);
            }
        });
        }catch (Exception e){
        }
    }
    /*
    *Display developer info in textviews
    *
    */
    public void showDeveloperInfo(){
        Log.e("DisplayError", "Error trying to display user info." );
        fullName.setText(developersProfile.getRealName());
        userBioView.setText(developersProfile.getBio());
        followersView.setText(developersProfile.getFollowers());
        followingView.setText(developersProfile.getFollowing());
        reposView.setText(developersProfile.getRepos());
        locationView.setText(developersProfile.getLocation());

        String userBlog = developersProfile.getBlog();
        if(userBlog.equals("")){
            blogView.setText(R.string.none_provided);
        }
        else{
            blogView.setText(userBlog);
        }
    }

}

