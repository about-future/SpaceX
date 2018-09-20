package com.about.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT flight_number, mission_name, launch_date_unix, mission_patch_small, rocket_name, block, payloads FROM missions ORDER BY launch_date_unix DESC")  //flight_number DESC
    LiveData<List<MissionMini>> loadAllMissions();

    @Query("SELECT * FROM missions WHERE flight_number = :flightNumber")
    LiveData<Mission> loadMissionDetails(int flightNumber);

    @Query("SELECT * FROM missions WHERE launch_date_unix > :now ORDER BY launch_date_unix ASC LIMIT 1")
    Mission findUpcomingMission(long now);

    /* Left here on purpose
    @Query("SELECT COUNT(*) FROM missions")
    int countMissions(); */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMissions(List<Mission> missions);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMission(Mission mission);
}
