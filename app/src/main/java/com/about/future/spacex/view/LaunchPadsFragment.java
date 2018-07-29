package com.about.future.spacex.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.LaunchPadsAdapter;
import com.about.future.spacex.data.LaunchPadsLoader;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchPadsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<LaunchPad>>, LaunchPadsAdapter.ListItemClickListener {

    private static final int LAUNCH_PADS_LOADER_ID = 917;
    public static final String LAUNCH_PAD_ID_KEY = "launch_pad_id";
    public static final String TOTAL_LAUNCH_PADS_KEY = "total_launch_pads";

    private AppDatabase mDb;
    private List<LaunchPad> mLaunchPads;
    private LaunchPadsAdapter mLaunchPadsAdapter;
    private int mTotalLaunchPads;

    @BindView(R.id.swipe_refresh_launch_pads_list_layout)
    SwipeRefreshLayout mSwipeRefreshLaunchPadsLayout;
    @BindView(R.id.launch_pads_rv)
    RecyclerView mLaunchPadsRecyclerView;
    @BindView(R.id.launch_pads_no_connection_cloud)
    ImageView mNoConnectionImageView;
    @BindView(R.id.launch_pads_no_connection_message)
    TextView mNoConnectionMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launch_pads_list, container, false);
        ButterKnife.bind(this, view);

        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mLaunchPadsRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    mLaunchPadsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mLaunchPadsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mLaunchPadsRecyclerView.setLayoutManager(sglm);
        }

        mLaunchPadsRecyclerView.setHasFixedSize(false);
        mLaunchPadsAdapter = new LaunchPadsAdapter(getContext(), this);
        mLaunchPadsRecyclerView.setAdapter(mLaunchPadsAdapter);

        mDb = AppDatabase.getInstance(getContext());

        // If launch pads were already loaded once, just query the DB and display them, otherwise init the loader
        if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
            // Load data
            setupViewModel();
        } else {
            Log.v("GET LAUNCH PAD DATA", "CALLED");
            // Get data
            getData();
        }

        mSwipeRefreshLaunchPadsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v("GET ROCKET DATA", "CALLED2");
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLaunchPadsLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return view;
    }

    private void setupViewModel() {
        LaunchPadsViewModel launchPadsViewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);
        launchPadsViewModel.getLaunchPads().observe(this, new Observer<List<LaunchPad>>() {
            @Override
            public void onChanged(@Nullable List<LaunchPad> launchPads) {
                if (launchPads != null) {
                    mLaunchPads = launchPads;
                    mLaunchPadsAdapter.setLaunchPads(launchPads);
                    mTotalLaunchPads = launchPads.size();
                }
            }
        });
    }

    private void getData() {
        // Get of refresh data, if there is a network connection
        if (NetworkUtils.isConnected(getActivityCast())) {
            mLaunchPadsRecyclerView.setVisibility(View.VISIBLE);
            mNoConnectionImageView.setVisibility(View.INVISIBLE);
            mNoConnectionMessage.setVisibility(View.INVISIBLE);

            //Init or restart launch pads loader
            getLoaderManager().restartLoader(LAUNCH_PADS_LOADER_ID, null, this);
        } else {
            // Otherwise, if LaunchPads were loaded before, just display a toast
            if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, display a connection error message and a no connection icon
                mLaunchPadsRecyclerView.setVisibility(View.INVISIBLE);
                mNoConnectionImageView.setVisibility(View.VISIBLE);
                mNoConnectionMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onItemClickListener(int launchPadId) {
        Intent launchPadDetailsIntent = new Intent(getActivity(), LaunchPadDetailsActivity.class);
        launchPadDetailsIntent.putExtra(LAUNCH_PAD_ID_KEY, launchPadId);
        launchPadDetailsIntent.putExtra(TOTAL_LAUNCH_PADS_KEY, mTotalLaunchPads);
        startActivity(launchPadDetailsIntent);
    }

    @NonNull
    @Override
    public Loader<List<LaunchPad>> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case LAUNCH_PADS_LOADER_ID:
                // If the loaded id matches launch pad loader, return a new launch pad loader
                return new LaunchPadsLoader(getActivityCast());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<LaunchPad>> loader, final List<LaunchPad> data) {
        switch (loader.getId()) {
            case LAUNCH_PADS_LOADER_ID:
                Log.v("LAUNCH PADS LOADER", "LOADED");

                String launchPadsAsString = new Gson().toJson(mLaunchPads);
                String dataAsString = new Gson().toJson(data);

                if (!TextUtils.equals(launchPadsAsString, dataAsString)) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.launchPadDao().insertLaunchPads(data);
                            SpaceXPreferences.setLaunchPadsStatus(getContext(), true);
                        }
                    });
                    ScreenUtils.snakBarThis(getView(), getString(R.string.launch_pads_updated));
                } else {
                    ScreenUtils.snakBarThis(getView(), getString(R.string.launch_pads_up_to_date));
                }

                setupViewModel();

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<LaunchPad>> loader) {
        mLaunchPadsAdapter.setLaunchPads(null);
    }
}
