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
    @Query("SELECT * FROM rockets")
    LiveData<List<Rocket>> loadRockets();

    @Query("SELECT rocket_id, rocket_name, description FROM rockets")
    LiveData<List<RocketMini>> loadMiniRockets();

    @Query("SELECT rocket_id, rocket_name, description FROM rockets")
    List<RocketMini> loadMiniRocketsRaw();

    @Query("SELECT * FROM rockets WHERE rocket_id = :rocketId")
    LiveData<Rocket> loadRocketDetails(String rocketId);

    @Query("SELECT rocket_name FROM rockets WHERE rocket_id = :rocketId")
    String getRocketType(String rocketId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRockets(List<Rocket> rockets);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRocket(Rocket rocket);

    @Query("DELETE FROM rockets")
    void deleteAllRockets();
}
