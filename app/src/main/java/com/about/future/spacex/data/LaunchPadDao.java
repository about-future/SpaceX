package com.about.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.about.future.spacex.model.launch_pad.LaunchPad;

import java.util.List;

@Dao
public interface LaunchPadDao {
    @Query("SELECT * FROM launch_pads ORDER BY padId")
    LiveData<List<LaunchPad>> loadAllLaunchPads();

    @Query("SELECT * FROM launch_pads WHERE padId = :padId")
    LiveData<LaunchPad> loadLaunchPadDetails(int padId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLaunchPads(List<LaunchPad> launchPads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLaunchPad(LaunchPad launchPad);

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void updateLaunchPads(List<LaunchPad> launchPads);

    @Query("DELETE FROM launch_pads")
    void deleteAllLaunchPads();
}
