package com.about.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.about.future.spacex.model.mission.Mission;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY flight_number DESC")
    LiveData<List<Mission>> loadAllMissions();

    @Query("SELECT * FROM missions WHERE flight_number = :flightNumber")
    LiveData<Mission> loadMissionDetails(int flightNumber);

    @Query("SELECT * FROM missions WHERE launch_date_unix > :now ORDER BY launch_date_unix ASC LIMIT 1")
    Mission findUpcomingMission(long now);

    @Query("SELECT COUNT(*) FROM missions")
    int countMissions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMissions(List<Mission> missions);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMission(Mission mission);
}
