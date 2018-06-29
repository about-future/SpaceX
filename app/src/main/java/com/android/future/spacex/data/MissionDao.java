package com.android.future.spacex.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT * FROM mission ORDER BY flight_number")
    List<Mission> loadAllMissions();

    @Query("SELECT * FROM mission WHERE flight_number = :flightNumber")
    Mission loadMissionDetails(int flightNumber);

    @Insert
    void insertMission(Mission mission);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMission(Mission mission);

    @Delete
    void deleteMission(Mission mission);

}
