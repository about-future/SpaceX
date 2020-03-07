package com.about.future.spacex.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.rocket.Capsule;

import java.util.List;

@Dao
public interface CapsulesDao {
    @Query("SELECT * FROM capsules ORDER BY capsule_serial")
    LiveData<List<Capsule>> loadAllCapsules();

    @Query("SELECT * FROM capsules WHERE capsule_serial = :coreSerial")
    LiveData<Capsule> loadCapsuleDetails(String coreSerial);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCapsule(List<Capsule> capsule);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCapsule(Capsule capsule);

    @Query("DELETE FROM capsules")
    void deleteAllCapsules();
}
