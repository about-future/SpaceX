package com.about.future.spacex.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.launch_pad.LaunchPad;

import java.util.List;

@Dao
public interface LaunchPadsDao {
    @Query("SELECT * FROM launch_pads ORDER BY padId")
    LiveData<List<LaunchPad>> loadAllLaunchPads();

    @Query("SELECT * FROM launch_pads WHERE padId = :padId")
    LiveData<LaunchPad> loadLaunchPadDetails(int padId);

    @Query("SELECT padid FROM launch_pads WHERE id = :launchPadId")
    int getLaunchPadId(String launchPadId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLaunchPads(List<LaunchPad> launchPads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLaunchPad(LaunchPad launchPad);

    @Query("DELETE FROM launch_pads")
    void deleteAllPads();
}
