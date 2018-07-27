package com.about.future.spacex.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.RocketLoader;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.RocketViewModel;
import com.about.future.spacex.viewmodel.RocketViewModelFactory;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.view.RocketsFragment.ROCKET_ID_KEY;

public class RocketDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Rocket> {
    private static final int ROCKET_LOADER_ID = 495835;

    private AppDatabase mDb;
    private Rocket mRocket;
    private int mRocketId;
    private View mRootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.backdrop_rocket_view)
    ImageView mBackdropImageView;
    @BindView(R.id.rocket_patch)
    ImageView mRocketPatchImageView;
    @BindView(R.id.first_flight)
    TextView mFirstFlightTextView;
    @BindView(R.id.cost_per_launch)
    TextView mCostPerLaunchTextView;
    @BindView(R.id.rocket_status)
    TextView mRocketStatus;
    @BindView(R.id.rocket_description)
    TextView mDescriptionTextView;

    @BindView(R.id.rocket_core_image)
    ImageView mCoreImageView;
    @BindView(R.id.rocket_payload_image)
    ImageView mPayloadImageView;
    @BindView(R.id.separation_line5)
    View mSeparationLine5View;

    public RocketDetailsFragment() {
        // Required empty public constructor
    }

    public static RocketDetailsFragment newInstance(int rocketId) {
        RocketDetailsFragment fragment = new RocketDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ROCKET_ID_KEY, rocketId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getInstance(getContext());

        if (getArguments() != null && getArguments().containsKey(ROCKET_ID_KEY)) {
            mRocketId = getArguments().getInt(ROCKET_ID_KEY);
        }
        setHasOptionsMenu(true);
    }

    public RocketDetailsActivity getActivityCast() {
        return (RocketDetailsActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRocketId = savedInstanceState.getInt(ROCKET_ID_KEY);
        }

        mRootView = inflater.inflate(R.layout.fragment_rocket_details, container, false);
        ButterKnife.bind(this, mRootView);

        mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        RocketViewModelFactory factory = new RocketViewModelFactory(mDb, mRocketId);
        final RocketViewModel viewModel = ViewModelProviders.of(this, factory).get(RocketViewModel.class);
        viewModel.getRocketLiveData().observe(this, new Observer<Rocket>() {
            @Override
            public void onChanged(@Nullable Rocket rocket) {
                bindViews(rocket);
                mRocket = rocket;
            }
        });

        return mRootView;
    }

    private void refreshData() {
        //Init rocket loader
        getLoaderManager().initLoader(ROCKET_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ROCKET_ID_KEY, mRocketId);
    }

    private void bindViews(Rocket rocket) {
        if (mRootView == null) {
            return;
        }

        if (rocket != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            // Title
            mCollapsingToolbarLayout.setTitle(rocket.getName());
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivityCast().onBackPressed();
                }
            });

            // Backdrop
            switch (rocket.getId()) {
                case "falcon1":
                    mBackdropImageView.setImageResource(R.drawable.falcon1);
                    break;
                case "falcon9":
                    mBackdropImageView.setImageResource(R.drawable.falcon9);
                    break;
                case "falconheavy":
                    mBackdropImageView.setImageResource(R.drawable.falcon_heavy);
                    break;
                case "bfr":
                    mBackdropImageView.setImageResource(R.drawable.bfr1);
                    break;
                default:
                    mBackdropImageView.setImageResource(R.drawable.rocket);
                    break;
            }

            // Rocket Patch
            switch (rocket.getName()) {
                case "Falcon 1":
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_f1_small);
                    break;
                case "Falcon 9":
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_dragon_small);
                    break;
                case "Falcon Heavy":
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_fh_small);
                    break;
                case "Big Falcon Rocket":
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_bfr_small);
                    break;
                default:
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_f9_small);
                    break;
            }

            // First flight
            mFirstFlightTextView.setText(DateUtils.shortDateFormat(rocket.getFirstFlight()));

            // Cost per launch
            //DecimalFormat formatter = new DecimalFormat("#,###,###,### ");
            //formatter.setCurrency(Currency.getInstance(Locale.US));
            //String formattedCost = formatter.format(rocket.getCostPerLaunch()).concat(formatter.getCurrency().getSymbol());

            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            numberFormat.setMaximumFractionDigits(0);

            mCostPerLaunchTextView.setText(numberFormat.format(rocket.getCostPerLaunch()));

            // Status
            if (rocket.isActive()) {
                mRocketStatus.setText(getString(R.string.label_active));
            } else {
                mRocketStatus.setText(getString(R.string.label_inactive));
            }

            // Description
            if (rocket.getDescription() != null && !TextUtils.isEmpty(rocket.getDescription())) {
                mDescriptionTextView.setText(rocket.getDescription());
            } else {
                mDescriptionTextView.setVisibility(View.GONE);
            }



            // Set rocket image (payload and core)
            ConstraintLayout.LayoutParams paramsPayload = (ConstraintLayout.LayoutParams) mPayloadImageView.getLayoutParams();
            ConstraintLayout.LayoutParams paramsCore = (ConstraintLayout.LayoutParams) mCoreImageView.getLayoutParams();
            ConstraintLayout.LayoutParams paramsSeparationLine5 = (ConstraintLayout.LayoutParams) mSeparationLine5View.getLayoutParams();
            float[] screenSize = ScreenUtils.getScreenSize(getActivityCast());

            // Set image, depending on rocket type
            switch (rocket.getName()) {
                case "Falcon 1":
                    mPayloadImageView.setImageResource(R.drawable.payload_falcon1);
                    mCoreImageView.setImageResource(R.drawable.core_falcon1);
                    paramsPayload.setMarginEnd(48);
                    paramsCore.setMarginEnd(48);
                    break;
                case "Falcon 9":
                    mPayloadImageView.setImageResource(R.drawable.payload_dragon2);
                    mCoreImageView.setImageResource(R.drawable.core_block5);
                    paramsPayload.setMarginEnd(48);
                    paramsCore.setMarginEnd(48);
                    break;
                case "Falcon Heavy":
                    mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                    mCoreImageView.setImageResource(R.drawable.falcon_heavy_block4);
                    if (ScreenUtils.isPortraitMode(getActivityCast())) {
                        paramsPayload.setMarginEnd(69);
                        paramsCore.setMarginEnd(20);
                        paramsSeparationLine5.setMarginEnd(0);
                    } else {
                        paramsPayload.setMarginEnd(90);
                        paramsCore.setMarginEnd(9);
                        paramsSeparationLine5.setMarginEnd(70);
                    }
                    break;
                case "BFR":
                    mPayloadImageView.setImageResource(R.drawable.payload_bfr);
                    mCoreImageView.setImageResource(R.drawable.core_bfr);
                    paramsPayload.setMarginEnd(24);
                    paramsCore.setMarginEnd(24);
                    break;
                case "Big Falcon Rocket":
                    mPayloadImageView.setImageResource(R.drawable.payload_bfr);
                    mCoreImageView.setImageResource(R.drawable.core_bfr);
                    paramsPayload.setMarginEnd(24);
                    paramsCore.setMarginEnd(24);
                    break;
            }

            // We will set the payload/core image height to be equal to:
            // {Devices max screen size in px(height or width) - [(StatusBar + ActionBar + TopMargin + 2 x BottomMargin) (in dp) * screen density]} * 30.8%(or 69.2%) of total resulted height
            // Phone: 24 + 56 + 16 + 2x16 = 128dp
            // Tablet: 24 + 64 + 24 + 2x24 = 160dp
            float maxSize = Math.max(screenSize[0], screenSize[1]);
            // If screen is in Landscape mode, show rocket image 33% bigger
            if (!ScreenUtils.isPortraitMode(getActivityCast())) {
                maxSize = (float) (maxSize * 1.5);
            }
            paramsPayload.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.308);
            paramsCore.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.692);
            paramsPayload.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            paramsCore.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            mPayloadImageView.setLayoutParams(paramsPayload);
            mCoreImageView.setLayoutParams(paramsCore);
            mSeparationLine5View.setLayoutParams(paramsSeparationLine5);

            //
            //

            // Height
            //

            // Diameter
            //

            // Mass
            //

            //TODO: complete this
        }
    }

    @NonNull
    @Override
    public Loader<Rocket> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ROCKET_LOADER_ID:
                // If the loaded id matches, return a new rocket loader
                return new RocketLoader(getActivityCast(), mRocket.getId());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Rocket> loader, final Rocket rocket) {
        if (rocket != null) {

            String rocketAsString1 = new Gson().toJson(rocket);
            String rocketAsString2 = new Gson().toJson(mRocket);

            // If the content of the two rockets is different, update the DB
            if (!rocketAsString1.equals(rocketAsString2)) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.rocketDao().updateRocket(rocket);
                    }
                });
                ScreenUtils.snakBarThis(mRootView, getString(R.string.rocket_updated));
            } else {
                ScreenUtils.snakBarThis(mRootView, getString(R.string.rocket_up_to_date));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Rocket> loader) {
        mRocket = null;
    }
}
