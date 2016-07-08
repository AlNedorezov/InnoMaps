package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class PhotoDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public PhotoDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Photo photo = (Photo) item;
        try {
            index = helper.getPhotoDao().create(photo);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Photo photo = (Photo) item;

        try {
            helper.getPhotoDao().update(photo);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Photo photo = (Photo) item;

        try {
            helper.getPhotoDao().delete(photo);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Photo photo = null;
        try {
            photo = helper.getPhotoDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return photo;
    }

    @Override
    public List<?> findAll() {

        List<Photo> items = new ArrayList<>();

        try {
            items = helper.getPhotoDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }

        return items;
    }
}

