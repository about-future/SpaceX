package com.about.future.spacex.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.about.future.spacex.model.core.Core;

import java.util.List;

@Dao
public interface CoresDao {
    @Query("SELECT * FROM cores ORDER BY core_serial")
    LiveData<List<Core>> loadAllCores();

    @Query("SELECT * FROM cores WHERE core_serial = :coreSerial")
    LiveData<Core> loadCoreDetails(String coreSerial);

//    @Query("SELECT id FROM landing_pads WHERE id = :landingPadId")
//    int getLandingPadId(String landingPadId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCore(List<Core> core);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCore(Core core);

    @Query("DELETE FROM cores")
    void deleteAllCors();
}
