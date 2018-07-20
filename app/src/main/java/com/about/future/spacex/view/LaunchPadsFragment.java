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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.LaunchPadsAdapter;
import com.about.future.spacex.data.LaunchPadsLoader;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchPadsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<LaunchPad>>, LaunchPadsAdapter.ListItemClickListener {

    private static final int LAUNCH_PADS_LOADER_ID = 917;
    public static final String LAUNCH_PAD_ID_KEY = "launch_pad_id";
    public static final String TOTAL_LAUNCH_PADS_KEY = "total_launch_pads";

    private AppDatabase mDb;
    private LaunchPadsAdapter mLaunchPadsAdapter;
    private int mTotalLaunchPads;

    @BindView(R.id.swipe_refresh_launch_pads_list_layout)
    SwipeRefreshLayout mSwipeRefreshLaunchPadsLayout;
    @BindView(R.id.launch_pads_rv)
    RecyclerView mLaunchPadsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launch_pads_list, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mLaunchPadsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mLaunchPadsRecyclerView.addItemDecoration(mDividerItemDecoration);
        mLaunchPadsRecyclerView.setLayoutManager(linearLayoutManager);
        mLaunchPadsRecyclerView.setHasFixedSize(false);
        mLaunchPadsAdapter = new LaunchPadsAdapter(getContext(), this);
        mLaunchPadsRecyclerView.setAdapter(mLaunchPadsAdapter);

        mDb = AppDatabase.getInstance(getContext());

        // If launch pads were already loaded once, just query the DB and display them, otherwise init the loader
        if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
            // Load data
            setupViewModel();
        } else {
            // Get data
            getData();
        }

        mSwipeRefreshLaunchPadsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                    mLaunchPadsAdapter.setLaunchPads(launchPads);
                    mTotalLaunchPads = launchPads.size();
                }
            }
        });
    }

    private void getData() {
        //Init mission loader
        getLoaderManager().restartLoader(LAUNCH_PADS_LOADER_ID, null, this);
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
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.launchPadDao().insertLaunchPads(data);
                        SpaceXPreferences.setLaunchPadsStatus(getContext(), true);
                    }
                });

                setupViewModel();

                break;
            default:
                break;
        }
    }

    //TODO: Show snakbar message if update was done

    @Override
    public void onLoaderReset(@NonNull Loader<List<LaunchPad>> loader) {
        mLaunchPadsAdapter.setLaunchPads(null);
    }
}
