package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class StreetDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public StreetDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Street street = (Street) item;
        try {
            index = helper.getStreetDao().create(street);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Street street = (Street) item;

        try {
            helper.getStreetDao().update(street);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Street street = (Street) item;

        try {
            helper.getStreetDao().delete(street);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Street street = null;
        try {
            street = helper.getStreetDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }
        return street;
    }

    @Override
    public List<?> findAll() {

        List<Street> items = new ArrayList<>();

        try {
            items = helper.getStreetDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        Street street = null;
        try {
            QueryBuilder<Street, Integer> qBuilder = helper.getStreetDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            street = helper.getStreetDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return street;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Street street = (Street) item;
        try {
            if (helper.getStreetDao().idExists(street.getId())) {
                if (helper.getStreetDao().queryForId(street.getId()).equals(street))
                    index = street.getId();
                else
                    index = helper.getStreetDao().update(street);
            } else
                index = helper.getStreetDao().create(street);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    StreetDAO.class.getSimpleName());
        }

        return index;
    }
}
