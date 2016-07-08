package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class RoomPhotoDAO implements Crud {

    private DatabaseHelper helper;

    public RoomPhotoDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;
        try {
            index = helper.getRoomPhotoDao().create(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;

        try {
            helper.getRoomPhotoDao().update(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;

        try {
            helper.getRoomPhotoDao().delete(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int room_id, int photo_id) {

        RoomPhoto roomPhoto = null;
        try {
            QueryBuilder<RoomPhoto, Integer> qb = helper.getRoomPhotoDao().queryBuilder();
            qb.where().eq("room_id", room_id).and().eq("photo_id", photo_id);
            PreparedQuery<RoomPhoto> pc = qb.prepare();
            roomPhoto = helper.getRoomPhotoDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }
        return roomPhoto;
    }

    @Override
    public List<?> findAll() {

        List<RoomPhoto> items = new ArrayList<>();

        try {
            items = helper.getRoomPhotoDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return items;
    }
}


