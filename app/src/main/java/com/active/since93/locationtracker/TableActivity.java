package com.active.since93.locationtracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.active.since93.locationtracker.adapter.TableListAdapter;
import com.active.since93.locationtracker.constants.Constants;
import com.active.since93.locationtracker.database.DatabaseHandler;
import com.active.since93.locationtracker.model.LocationData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by myzupp on 18-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class TableActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerViewTable;
    private List<LocationData> locationDataList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private TableListAdapter adapter;
    private Button btnSelectDate;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        context = TableActivity.this;
        databaseHandler = DatabaseHandler.getInstance(context);

        setupToolbar();

        btnSelectDate = (Button) findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(this);
        recyclerViewTable = (RecyclerView) findViewById(R.id.recyclerViewTable);
        recyclerViewTable.setLayoutManager(new LinearLayoutManager(this));

        locationDataList = databaseHandler.getAllLocationData();
        setRecyclerViewAdapter(locationDataList);
    }

    private void setupToolbar() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.table));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setRecyclerViewAdapter(List<LocationData> locationDataList) {
        adapter = new TableListAdapter(context, locationDataList);
        recyclerViewTable.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            long todayOffset = currentTime % Constants.TWENTY_FOUR_HOURS;
            long currentDateMillis = currentTime - todayOffset;

            calendar.setTimeInMillis(currentDateMillis);

            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);

            long selectedDateStartMillis = calendar.getTimeInMillis();
            locationDataList = databaseHandler.getSelectedLocationData(selectedDateStartMillis
                    , selectedDateStartMillis + Constants.TWENTY_FOUR_HOURS);
            setRecyclerViewAdapter(locationDataList);

            btnSelectDate.setText(dayOfMonth + "/" + month + "/" + year);
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSelectDate:
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(this, myDateListener
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
        }
    }
}
