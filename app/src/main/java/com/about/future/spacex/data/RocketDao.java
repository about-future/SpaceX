package com.about.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.about.future.spacex.model.rocket.Rocket;

import java.util.List;

@Dao
public interface RocketDao {
    @Query("SELECT * FROM rockets")
    LiveData<List<Rocket>> loadAllRockets();

    @Query("SELECT * FROM rockets WHERE id = :id")
    LiveData<Rocket> loadRocketDetails(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRockets(List<Rocket> rockets);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRocket(Rocket rocket);
}
