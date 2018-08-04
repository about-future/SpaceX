package com.about.future.spacex.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Toast;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.RocketLoader;
import com.about.future.spacex.model.rocket.CompositeFairing;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.viewmodel.RocketViewModel;
import com.about.future.spacex.viewmodel.RocketViewModelFactory;
import com.google.gson.Gson;

import java.text.NumberFormat;
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
    private boolean mIsMetric;

    @BindView(R.id.rocket_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_rocket_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rocket_toolbar_layout)
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

    // Gallery
    @BindView(R.id.gallery)
    ImageView mGalleryImageView;

    // Rocket Image
    @BindView(R.id.rocket_core_image)
    ImageView mCoreImageView;
    @BindView(R.id.rocket_payload_image)
    ImageView mPayloadImageView;

    // Payload
    @BindView(R.id.payload_option)
    TextView mPayloadOption;
    @BindView(R.id.payload_height)
    TextView mPayloadHeight;
    @BindView(R.id.payload_diameter)
    TextView mPayloadDiameter;
    @BindView(R.id.payload_option_label)
    TextView mPayloadOptionLabel;

    // Second Stage
    @BindView(R.id.second_stage_engines)
    TextView mSecondStageEngines;
    @BindView(R.id.second_stage_fuel_amount)
    TextView mSecondStageFuelAmount;
    @BindView(R.id.second_stage_burn_time)
    TextView mSecondStageBurnTime;
    @BindView(R.id.second_stage_thrust)
    TextView mSecondStageThrust;

    // First Stage
    @BindView(R.id.first_stage_engines)
    TextView mFirstStageEngines;
    @BindView(R.id.first_stage_engines_type)
    TextView mFirstStageEnginesType;
    @BindView(R.id.first_stage_fuel_amount)
    TextView mFirstStageFuelAmount;
    @BindView(R.id.first_stage_burn_time)
    TextView mFirstStageBurnTime;
    @BindView(R.id.first_stage_thrust_at_sea_level)
    TextView mFirstStageThrustAtSeaLevel;
    @BindView(R.id.first_stage_thrust_in_vacuum)
    TextView mFirstStageThrustInVacuum;

    // Dimensions
    @BindView(R.id.rocket_height)
    TextView mRocketHeight;
    @BindView(R.id.rocket_diameter)
    TextView mRocketDiameter;
    @BindView(R.id.rocket_mass)
    TextView mRocketMass;
    @BindView(R.id.rocket_stages)
    TextView mRocketStages;
    @BindView(R.id.payload_to_leo)
    TextView mPayloadMassToLeo;
    @BindView(R.id.payload_to_gto)
    TextView mPayloadMassToGto;
    @BindView(R.id.payload_to_mars)
    TextView mPayloadMassToMars;
    @BindView(R.id.payload_to_pluto)
    TextView mPayloadMassToPluto;
    // Pairs
    @BindView(R.id.payload_to_leo_label)
    TextView mPayloadMassToLeoLabel;
    @BindView(R.id.payload_to_gto_label)
    TextView mPayloadMassToGtoLabel;
    @BindView(R.id.payload_to_mars_label)
    TextView mPayloadMassToMarsLabel;
    @BindView(R.id.payload_to_pluto_label)
    TextView mPayloadMassToPlutoLabel;

    private boolean mIsCoreSwitched = false;

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
        // If there is a network connection, refresh data
        if (NetworkUtils.isConnected(getActivityCast())) {
            //Init rocket loader
            getLoaderManager().initLoader(ROCKET_LOADER_ID, null, this);
        } else {
            // Display connection error message
            Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ROCKET_ID_KEY, mRocketId);
    }

    private void bindViews(final Rocket rocket) {
        if (mRootView == null) {
            return;
        }

        if (rocket != null) {
            mIsMetric = SpaceXPreferences.isMetric(getActivityCast());

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

            // Set rocket image (payload and core)
            ConstraintLayout.LayoutParams paramsPayload = (ConstraintLayout.LayoutParams) mPayloadImageView.getLayoutParams();
            ConstraintLayout.LayoutParams paramsCore = (ConstraintLayout.LayoutParams) mCoreImageView.getLayoutParams();
            float[] screenSize = ScreenUtils.getScreenSize(getActivityCast());

            // Set Backdrop, Rocket Patch, Gallery Sample & Rocket image, depending on rocket id
            switch (rocket.getId()) {
                case "falcon1":
                    mBackdropImageView.setImageResource(R.drawable.falcon1);
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_f1_small);
                    mGalleryImageView.setImageResource(R.drawable.falcon1_gallery);
                    mPayloadImageView.setImageResource(R.drawable.payload_falcon1);
                    mCoreImageView.setImageResource(R.drawable.core_falcon1);
                    paramsPayload.setMarginEnd(48);
                    paramsCore.setMarginEnd(48);
                    break;
                case "falcon9":
                    mBackdropImageView.setImageResource(R.drawable.falcon9);
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_f9_small);
                    mGalleryImageView.setImageResource(R.drawable.falcon9_gallery);
                    mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                    mCoreImageView.setImageResource(R.drawable.core_block5);
                    paramsPayload.setMarginEnd(48);
                    paramsCore.setMarginEnd(48);
                    break;
                case "falconheavy":
                    mBackdropImageView.setImageResource(R.drawable.falcon_heavy);
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_fh_small);
                    mGalleryImageView.setImageResource(R.drawable.falcon_heavy_backdrop);
                    mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                    mCoreImageView.setImageResource(R.drawable.falcon_heavy_block4);
                    if (ScreenUtils.isPortraitMode(getActivityCast())) {
                        paramsPayload.setMarginEnd(69);
                        paramsCore.setMarginEnd(20);
                    } else {
                        paramsPayload.setMarginEnd(90);
                        paramsCore.setMarginEnd(9);
                    }
                    break;
                case "bfr":
                    mBackdropImageView.setImageResource(R.drawable.bfr1);
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_bfr_small);
                    mGalleryImageView.setImageResource(R.drawable.bfr_gallery);
                    mPayloadImageView.setImageResource(R.drawable.payload_bfr);
                    mCoreImageView.setImageResource(R.drawable.core_bfr);
                    paramsPayload.setMarginEnd(24);
                    paramsCore.setMarginEnd(24);
                    break;
                default:
                    // Other new type of rocket
                    mBackdropImageView.setImageResource(R.drawable.rocket);
                    mRocketPatchImageView.setImageResource(R.drawable.default_patch_dragon_small);
                    mGalleryImageView.setImageResource(R.drawable.rocket);
                    mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                    mCoreImageView.setImageResource(R.drawable.core_block4);
                    paramsPayload.setMarginEnd(48);
                    paramsCore.setMarginEnd(48);
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

            // First flight
            mFirstFlightTextView.setText(DateUtils.shortDateFormat(getActivityCast(), rocket.getFirstFlight()));

            // Cost per launch
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

            if (mIsMetric) {
                // Set values in metric system
                mRocketHeight.setText(String.format(getString(R.string.dimensions_meters), rocket.getHeight().getMeters()));
                mRocketDiameter.setText(String.format(getString(R.string.dimensions_meters), rocket.getDiameter().getMeters()));
                mRocketMass.setText(String.format(getString(R.string.mass_kg), TextsUtils.formatThrust(rocket.getMass().getKg())));
            } else {
                // Set values in imperial system
                mRocketHeight.setText(String.format(getString(R.string.dimensions_feet), rocket.getHeight().getFeet()));
                mRocketDiameter.setText(String.format(getString(R.string.dimensions_feet), rocket.getDiameter().getFeet()));
                mRocketMass.setText(String.format(getString(R.string.mass_lbs), TextsUtils.formatThrust(rocket.getMass().getLb())));
            }

            // Stages
            mRocketStages.setText(String.valueOf(rocket.getStages()));

            // Payload Weights
            int i = 0;
            while (i < rocket.getPayloadWeights().size()) {
                switch (rocket.getPayloadWeights().get(i).getId()) {
                    case "leo":
                        TextsUtils.formatPayloadMass(
                                getActivityCast(),
                                mPayloadMassToLeo,
                                mPayloadMassToLeoLabel,
                                rocket.getPayloadWeights().get(0).getKg(),
                                rocket.getPayloadWeights().get(0).getLb(),
                                mIsMetric);
                        break;
                    case "gto":
                        TextsUtils.formatPayloadMass(
                                getActivityCast(),
                                mPayloadMassToGto,
                                mPayloadMassToGtoLabel,
                                rocket.getPayloadWeights().get(0).getKg(),
                                rocket.getPayloadWeights().get(0).getLb(),
                                mIsMetric);
                        break;
                    case "mars":
                        TextsUtils.formatPayloadMass(
                                getActivityCast(),
                                mPayloadMassToMars,
                                mPayloadMassToMarsLabel,
                                rocket.getPayloadWeights().get(0).getKg(),
                                rocket.getPayloadWeights().get(0).getLb(),
                                mIsMetric);
                        break;
                    case "pluto":
                        TextsUtils.formatPayloadMass(
                                getActivityCast(),
                                mPayloadMassToPluto,
                                mPayloadMassToPlutoLabel,
                                rocket.getPayloadWeights().get(0).getKg(),
                                rocket.getPayloadWeights().get(0).getLb(),
                                mIsMetric);
                        break;
                }
                i++;
            }

            // Second State
            // Payload Options
            switch (rocket.getName()) {
                case "Falcon 9":
                    mPayloadOptionLabel.setText(R.string.label_payload_option1);
                    mPayloadOption.setText(TextsUtils.firstLetterUpperCase(rocket.getSecondStage().getPayloads().getOption2()));
                    break;
                case "Falcon Heavy":
                    mPayloadOptionLabel.setText(R.string.label_payload_option1);
                    mPayloadOption.setText(TextsUtils.firstLetterUpperCase(rocket.getSecondStage().getPayloads().getOption2()));
                    break;
                default:
                    mPayloadOptionLabel.setText(R.string.label_payload_option);
                    mPayloadOption.setText(TextsUtils.firstLetterUpperCase(rocket.getSecondStage().getPayloads().getOption1()));
                    break;
            }

            final CompositeFairing payload = rocket.getSecondStage().getPayloads().getCompositeFairing();

            mPayloadImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.equals(rocket.getName(), "Falcon 9") || TextUtils.equals(rocket.getName(), "Falcon Heavy")) {
                        if (TextUtils.equals(mPayloadOption.getText().toString(), "Dragon")) {
                            mPayloadOption.setText(TextsUtils.firstLetterUpperCase(rocket.getSecondStage().getPayloads().getOption2()));
                            mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                            mPayloadOptionLabel.setText(R.string.label_payload_option1);

                            // Payload Height
                            if (payload.getHeight() != null && payload.getHeight().getMeters() > 0) {
                                if(mIsMetric) {
                                    mPayloadHeight.setText(String.format(getString(R.string.dimensions_meters), payload.getHeight().getMeters()));
                                } else {
                                    mPayloadHeight.setText(String.format(getString(R.string.dimensions_feet), payload.getHeight().getFeet()));
                                }
                            } else {
                                mPayloadHeight.setText(getString(R.string.label_unknown));
                            }

                            // Payload Diameter
                            if (payload.getDiameter() != null && payload.getDiameter().getMeters() > 0) {
                                if(mIsMetric) {
                                    mPayloadDiameter.setText(String.format(getString(R.string.dimensions_meters), payload.getDiameter().getMeters()));
                                } else {
                                    mPayloadDiameter.setText(String.format(getString(R.string.dimensions_feet), payload.getDiameter().getFeet()));
                                }
                            } else {
                                mPayloadDiameter.setText(getString(R.string.label_unknown));
                            }
                        } else {
                            mPayloadOption.setText(TextsUtils.firstLetterUpperCase(rocket.getSecondStage().getPayloads().getOption1()));
                            mPayloadImageView.setImageResource(R.drawable.payload_dragon2);
                            mPayloadOptionLabel.setText(R.string.label_payload_option2);

                            // Set payload height and diameter
                            if(mIsMetric) {
                                mPayloadHeight.setText(getString(R.string.dragon_height_metric));
                                mPayloadDiameter.setText(getString(R.string.dragon_diameter_metric));
                            } else {
                                mPayloadHeight.setText(getString(R.string.dragon_height_imperial));
                                mPayloadDiameter.setText(getString(R.string.dragon_diameter_imperial));
                            }
                        }
                    }
                }
            });

            mCoreImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mIsCoreSwitched) {
                        if (TextUtils.equals(rocket.getName(), "Falcon 9")) {
                            mCoreImageView.setImageResource(R.drawable.core_block4);
                        }
                        if (TextUtils.equals(rocket.getName(), "Falcon Heavy")) {
                            mCoreImageView.setImageResource(R.drawable.falcon_heavy_block5);
                        }
                    } else {
                        if (TextUtils.equals(rocket.getName(), "Falcon 9")) {
                            mCoreImageView.setImageResource(R.drawable.core_block5);
                        }
                        if (TextUtils.equals(rocket.getName(), "Falcon Heavy")) {
                            mCoreImageView.setImageResource(R.drawable.falcon_heavy_block4);
                        }
                    }
                    mIsCoreSwitched = !mIsCoreSwitched;
                }
            });

            // Payload Height
            if (payload.getHeight() != null && payload.getHeight().getMeters() > 0) {
                if(mIsMetric) {
                    mPayloadHeight.setText(String.format(getString(R.string.dimensions_meters), payload.getHeight().getMeters()));
                } else {
                    mPayloadHeight.setText(String.format(getString(R.string.dimensions_feet), payload.getHeight().getFeet()));
                }
            } else {
                mPayloadHeight.setText(getString(R.string.label_unknown));
            }

            // Payload Diameter
            if (payload.getDiameter() != null && payload.getDiameter().getMeters() > 0) {
                if(mIsMetric) {
                    mPayloadDiameter.setText(String.format(getString(R.string.dimensions_meters), payload.getDiameter().getMeters()));
                } else {
                    mPayloadDiameter.setText(String.format(getString(R.string.dimensions_feet), payload.getDiameter().getFeet()));
                }
            } else {
                mPayloadDiameter.setText(getString(R.string.label_unknown));
            }

            // Engines
            mSecondStageEngines.setText(String.valueOf(rocket.getSecondStage().getEngines()));
            // Fuel Amount
            if (rocket.getSecondStage().getFuelAmountTons() > 0) {
                mSecondStageFuelAmount.setText(String.format(getString(R.string.fuel_amount),
                        TextsUtils.formatFuel(rocket.getSecondStage().getFuelAmountTons())));
            } else {
                mSecondStageFuelAmount.setText(getString(R.string.label_unknown));
            }
            // Burn Time
            if (rocket.getSecondStage().getBurnTimeSec() > 0) {
                mSecondStageBurnTime.setText(String.format(getString(R.string.burn_time),
                        rocket.getSecondStage().getBurnTimeSec()));
            }else {
                mSecondStageBurnTime.setText(getString(R.string.label_unknown));
            }
            // Thrust
            if (mIsMetric) {
                mSecondStageThrust.setText(String.format(getString(R.string.thrust_kN), TextsUtils.formatThrust(rocket.getSecondStage().getThrust().getKN())));
            } else {
                mSecondStageThrust.setText(String.format(getString(R.string.thrust_lbf), TextsUtils.formatThrust(rocket.getSecondStage().getThrust().getLbf())));
            }

            // First Stage
            // Engines
            mFirstStageEngines.setText(String.valueOf(rocket.getFirstStage().getEngines()));
            // Engine Types
            mFirstStageEnginesType.setText(TextsUtils.firstLetterUpperCase(rocket.getEngines().getType()));
            if (!TextUtils.isEmpty(rocket.getEngines().getVersion())) {
                mFirstStageEnginesType.append(" " + rocket.getEngines().getVersion());
            }
            // Fuel Amount
            mFirstStageFuelAmount.setText(String.format(getString(R.string.fuel_amount), TextsUtils.formatFuel(rocket.getFirstStage().getFuelAmountTons())));
            // Burn Time
            if (rocket.getFirstStage().getBurnTimeSec() > 0) {
                mFirstStageBurnTime.setText(String.format(getString(R.string.burn_time), rocket.getFirstStage().getBurnTimeSec()));
            } else {
                mFirstStageBurnTime.setText(getString(R.string.label_unknown));
            }
            // Thrust Sea Level & Thrust in Vacuum
            if (mIsMetric) {
                mFirstStageThrustAtSeaLevel.setText(String.format(getString(R.string.thrust_kN), TextsUtils.formatThrust(rocket.getFirstStage().getThrustSeaLevel().getKN())));
                mFirstStageThrustInVacuum.setText(String.format(getString(R.string.thrust_kN), TextsUtils.formatThrust(rocket.getFirstStage().getThrustVacuum().getKN())));
            } else {
                mFirstStageThrustAtSeaLevel.setText(String.format(getString(R.string.thrust_lbf), TextsUtils.formatThrust(rocket.getFirstStage().getThrustSeaLevel().getLbf())));
                mFirstStageThrustInVacuum.setText(String.format(getString(R.string.thrust_lbf), TextsUtils.formatThrust(rocket.getFirstStage().getThrustVacuum().getLbf())));
            }
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
