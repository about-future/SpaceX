package com.android.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.android.future.spacex.launch_pad_entity.LaunchPad;

import java.util.List;

@Dao
public interface LaunchPadDao {
    @Query("SELECT * FROM launch_pads ORDER BY id")
    LiveData<List<LaunchPad>> loadAllLaunchPads();

    @Query("SELECT * FROM launch_pads WHERE id = :id")
    LiveData<LaunchPad> loadLaunchPadDetails(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLaunchPad(LaunchPad launchPad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLaunchPads(List<LaunchPad> launchPads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLaunchPad(LaunchPad launchPad);

    @Delete
    void deleteLaunchPad(LaunchPad launchPad);
}
