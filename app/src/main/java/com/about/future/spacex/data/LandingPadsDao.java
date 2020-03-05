package com.about.future.spacex.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.pads.LandingPad;

import java.util.List;

@Dao
public interface LandingPadsDao {
    @Query("SELECT * FROM landing_pads ORDER BY id")
    LiveData<List<LandingPad>> loadAllLandingPads();

    @Query("SELECT * FROM landing_pads WHERE id = :padId")
    LiveData<LandingPad> loadLandingPadDetails(String padId);

    @Query("SELECT id FROM landing_pads WHERE id = :landingPadId")
    int getLandingPadId(String landingPadId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLandingPads(List<LandingPad> landingPads);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLandingPad(LandingPad landingPad);

    @Query("DELETE FROM landing_pads")
    void deleteAllPads();
}
