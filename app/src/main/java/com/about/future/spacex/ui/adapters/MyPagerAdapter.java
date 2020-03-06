package com.about.future.spacex.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.ui.fragments.LandingPadDetailsFragment;
import com.about.future.spacex.ui.fragments.LaunchPadDetailsFragment;
import com.about.future.spacex.ui.fragments.MissionDetailsFragment;
import com.about.future.spacex.ui.fragments.RocketDetailsFragment;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends FragmentStateAdapter {
    private List<Mission> missions = new ArrayList<>();
    private List<Rocket> rockets = new ArrayList<>();
    private List<LaunchPad> launchPads = new ArrayList<>();
    private List<LandingPad> landingPads = new ArrayList<>();

    public MyPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
        notifyDataSetChanged();
    }

    public void setRockets(List<Rocket> rockets) {
        this.rockets = rockets;
        notifyDataSetChanged();
    }

    public void setLaunchPads(List<LaunchPad> launchPads) {
        this.launchPads = launchPads;
        notifyDataSetChanged();
    }

    public void setLandingPads(List<LandingPad> landingPads) {
        this.landingPads = landingPads;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (missions != null && missions.size() > 0) {
            MissionDetailsFragment fragment = new MissionDetailsFragment();
            fragment.setMission(missions.get(position));
            return fragment;
        } else if (rockets != null && rockets.size() > 0) {
            RocketDetailsFragment fragment = new RocketDetailsFragment();
            fragment.setRocket(rockets.get(position));
            return fragment;
        } else if (launchPads != null && launchPads.size() > 0) {
            LaunchPadDetailsFragment fragment = new LaunchPadDetailsFragment();
            fragment.setLaunchPad(launchPads.get(position));
            return fragment;
        } else {
            LandingPadDetailsFragment fragment = new LandingPadDetailsFragment();
            fragment.setLandingPad(landingPads.get(position));
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        if (missions != null && missions.size() > 0) {
            return missions.size();
        } else if (rockets != null && rockets.size() > 0) {
            return rockets.size();
        } else if (launchPads != null && launchPads.size() > 0) {
            return launchPads.size();
        } else {
            return landingPads.size();
        }
    }
}
