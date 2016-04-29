package com.drwat.lab2;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final int REQUEST_CODE = 1;
    public static final int NOTIFY_ID = 101;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCE_COUNTER = "counter";
    public static int mCounter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Grocery> groceries;
    private SharedPreferences mSettings;
    private Context context;
    private SharedPreferences preferences;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManagerCompat manager;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = getApplicationContext();
        mDatabaseHelper = new DatabaseHelper(context, DatabaseHelper.DB_NAME, null, 1);
        mDatabase = mDatabaseHelper.getReadableDatabase();
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.GROCERY_ID,DatabaseHelper.GROCERY_NAME, DatabaseHelper.GROCERY_COUNT, DatabaseHelper.GROCERY_IMAGE_PATH},
                null, null, null, null, null);
        groceries = new ArrayList<>();
        while (cursor.moveToNext()) {
            int groceryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROCERY_ID));
            String groceryName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_NAME));
            String groceryCount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_COUNT));
            String groceryImagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_IMAGE_PATH));
            groceries.add(new Grocery(groceryId, groceryName, groceryCount, groceryImagePath));
            Toast toast = Toast.makeText(getApplicationContext(),"ID = " + groceryId, Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.close();
        mDatabase.close();
        mAdapter = new GroceryRecyclerViewAdapter(groceries);
        mRecyclerView.setAdapter(mAdapter);



        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(mRecyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override
            public boolean canSwipeLeft(int position) {
                return true;
            }

            @Override
            public boolean canSwipeRight(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    mDatabase = mDatabaseHelper.getWritableDatabase();
                    int id = groceries.get(position).getId();
                    mDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.GROCERY_ID + " = " + id, null);
                    groceries.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    Toast toast = Toast.makeText(getApplicationContext(),"Deleted ID = " +id, Toast.LENGTH_SHORT);
                    toast.show();
                }
                mDatabase.close();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    mDatabase = mDatabaseHelper.getWritableDatabase();
                    int id = groceries.get(position).getId();
                    mDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.GROCERY_ID + " = " + id, null);
                    groceries.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    Toast toast = Toast.makeText(getApplicationContext(),"Deleted ID = " + id, Toast.LENGTH_SHORT);
                    toast.show();
                }
                mDatabase.close();
                mAdapter.notifyDataSetChanged();

            }
        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

    }

    public void openMapFromLatitude(String  latitude, String longitude) {
        String geoUriString = "geo:" + latitude + "," + longitude;
        Uri geoUri = Uri.parse(geoUriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
        startActivity(intent);
    }

    public void openMapFromQuery(String query, int zoom) {
        String geoUriString = "geo:0,0?q=" + query + "&z=" + zoom;
        Uri geoUri = Uri.parse(geoUriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
        startActivity(intent);
    }

    public void showSettings() {
        Intent intent = new Intent(this, Prefs.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        mDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROCERY_NAME, data.getStringExtra(AddActivity.NAME));
        values.put(DatabaseHelper.GROCERY_COUNT, data.getStringExtra(AddActivity.COUNT));
        values.put(DatabaseHelper.GROCERY_IMAGE_PATH, data.getStringExtra(AddActivity.IMAGE_PATH));
        mDatabase.insert(DatabaseHelper.TABLE_NAME, null, values);
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.GROCERY_ID,
                DatabaseHelper.GROCERY_NAME, DatabaseHelper.GROCERY_COUNT, DatabaseHelper.GROCERY_IMAGE_PATH},
                null,null,null,null,null);
        cursor.moveToLast();
        int groceryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROCERY_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_NAME));
        String count = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_COUNT));
        String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROCERY_IMAGE_PATH));
        cursor.close();
        mDatabase.close();
        Toast toast = Toast.makeText(getApplicationContext(), "Добавлено " + name + " в количестве " + count + "ID = " +groceryId, Toast.LENGTH_LONG);
            groceries.add(new Grocery(groceryId, name, count,imagePath));
            toast.show();

mRecyclerView.getAdapter().notifyDataSetChanged();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.open_map_from_latitude:
                openDialogLatitude();
                return true;
            case R.id.open_map_from_query:
                openDialogQuery();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void openDialogLatitude() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.latitude_layout, null);
        builder.setView(view);

        final EditText latitude = (EditText) view.findViewById(R.id.latitude);
        final EditText longitude = (EditText) view.findViewById(R.id.longitude);

        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    openMapFromLatitude(latitude.getText().toString(), longitude.getText().toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    openMapFromLatitude("0","0");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openDialogQuery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.query_layout, null);
        builder.setView(view);
        final EditText query = (EditText) view.findViewById(R.id.query);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    openMapFromQuery(query.getText().toString(), 2);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    openMapFromQuery("Харьков", 2);
                }
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferences.getBoolean("notification", true) && groceries.size()>0) {
            showNotification();
        }
    }

    public void showNotification() {
        long[] vibrate = new long[]{1000};
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = context.getResources();
        builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent).setSmallIcon(android.R.drawable.stat_sys_upload)
                .setLargeIcon(BitmapFactory.decodeResource(res, android.R.drawable.ic_dialog_info))
                .setTicker(res.getString(R.string.warning))
                .setTicker("Последнее предупредение")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.title))
                .setContentTitle("Напоминание")
                .setContentText("У вас есть " + groceries.size() + " незавершенные покупки");
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.vibrate = vibrate;
        notification.ledARGB = Color.RED;
        notification.ledOffMS = 0;
        notification.ledOnMS = 1;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
        manager = NotificationManagerCompat.from(this);
        manager.notify(NOTIFY_ID, notification);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCE_COUNTER, mCounter);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean settings = preferences.getBoolean("settings", false);
        boolean notification = preferences.getBoolean("notification", false);
        boolean ringtone = preferences.getBoolean("ringtone", false);
        String sett = "Настройки " + ((settings) ? "включены. " : "выключены. ");
        String notif = "Уведомления " + ((notification) ? "включены. " : "выключены. ");
        String ring = "Звук в уведомлениях " + ((ringtone) ? "включен. " : "выключен. ");
        String text = sett.concat(notif).concat(ring);
        if (mSettings.contains(APP_PREFERENCE_COUNTER)) {
            mCounter = mSettings.getInt(APP_PREFERENCE_COUNTER, 0);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
