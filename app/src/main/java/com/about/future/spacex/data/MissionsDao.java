package com.about.future.spacex.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;

import java.util.List;

@Dao
public interface MissionsDao {
    @Query("SELECT * FROM missions ORDER BY flight_number ASC")
    LiveData<List<Mission>> loadMissions();

    @Query("SELECT flight_number, mission_name, launch_date_unix, small_patch, rocket FROM missions ORDER BY flight_number DESC")
    LiveData<List<MissionMini>> loadMiniMissions();

    @Query("SELECT flight_number, mission_name, launch_date_unix, small_patch, rocket " +  //rocket_name, block, payloads
            "FROM missions " +
            "WHERE flight_number IN(:flightNumbers) " +
            "ORDER BY flight_number ASC")
    LiveData<List<MissionMini>> loadMiniMissions(int[] flightNumbers);

    @Query("SELECT flight_number, mission_name, launch_date_unix, small_patch, rocket " +
            "FROM missions " +
            "WHERE launch_date_unix > :now " +
            "ORDER BY launch_date_unix ASC")
    LiveData<List<MissionMini>> loadUpcomingMiniMissions(long now);

    @Query("SELECT flight_number, mission_name, launch_date_unix, small_patch, rocket " +
            "FROM missions " +
            "WHERE launch_date_unix <= :now " +
            "ORDER BY launch_date_unix DESC")
    LiveData<List<MissionMini>> loadPastMiniMissions(long now);

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

    @Query("DELETE FROM missions")
    void deleteAllMissions();
}
