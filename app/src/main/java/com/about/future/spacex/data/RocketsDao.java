package com.about.future.spacex.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.model.rocket.RocketMini;

import java.util.List;

@Dao
public interface RocketsDao {
//    @Query("SELECT * FROM rockets")
//    LiveData<List<Rocket>> loadAllRockets();

    @Query("SELECT rocket_id, id, rocket_name, description FROM rockets")
    LiveData<List<RocketMini>> loadAllRockets();

    @Query("SELECT * FROM rockets WHERE id = :rocketId")
    LiveData<Rocket> loadRocketDetails(int rocketId);

    @Query("SELECT id FROM rockets WHERE rocket_name = :rocketName")
    int getRocketId(String rocketName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRockets(List<Rocket> rockets);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRocket(Rocket rocket);

    @Query("DELETE FROM rockets")
    void deleteAllRockets();
}
