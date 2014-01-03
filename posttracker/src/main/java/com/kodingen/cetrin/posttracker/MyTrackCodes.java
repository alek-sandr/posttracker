package com.kodingen.cetrin.posttracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MyTrackCodes extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CM_DELETE_ID = 0;
    private DBHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_track_codes);
        setContentView(R.layout.fragment_my_track_codes);
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        dbHelper = new DBHelper(this);
        // формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.COL_TRACKCODE, DBHelper.COL_DESCRIPTION, DBHelper.COL_LASTCHECK, DBHelper.COL_SENDDATE};
        int[] to = new int[] { R.id.tvTrackCodeItem, R.id.tvItemDescr, R.id.tvItemLastChecked, R.id.tvDaysLeft };
        adapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lv = (ListView) findViewById(R.id.lvMyCodes);
        Time now = new Time();
        now.setToNow();
        long currentTime = now.toMillis(false);
        adapter.setViewBinder(new MyBinder(currentTime));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvTrackCode = (TextView) view.findViewById(R.id.tvTrackCodeItem);
                Intent intent = new Intent(TrackCodeInfo.ACTION_SHOWINFO);
                intent.putExtra(TrackCodeInfo.TRACKCODE, tvTrackCode.getText());
                startActivity(intent);
            }
        });
        registerForContextMenu(lv);
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_track_codes, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_code);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            dbHelper.delRecord(acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper.isOpen()) {
            dbHelper.close();
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyCursorLoader(this, dbHelper);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {

    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_my_track_codes, container, false);
//            //lv = (ListView) rootView.findViewById(R.id.lvMyCodes);
//            return rootView;
//        }
//    }

    static class MyCursorLoader extends CursorLoader {
        private DBHelper dbHelper;

        public MyCursorLoader(Context context, DBHelper dbHelper) {
            super(context);
            this.dbHelper = dbHelper;
        }

        @Override
        public Cursor loadInBackground() {
            if (!dbHelper.isOpen()) { //when rotate activity it closes dbHelper
                dbHelper.open();
            }
            return dbHelper.getAllData();
        }
    }

    private class MyBinder implements SimpleCursorAdapter.ViewBinder {
        private final long currentTime;

        public MyBinder(long currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public boolean setViewValue(View view, Cursor cursor, int i) {
            if (view.getId() != R.id.tvDaysLeft) {
                return false;
            }
            String name = cursor.getColumnName(i);
            if (name.equals(DBHelper.COL_SENDDATE)) {
                long sendDate = cursor.getLong(i);
                int maxDays = cursor.getInt(i + 1); //COL_DAYSFORDELIVERY next column to COL_SENDDATE
                int daysLeft = (int) (sendDate + maxDays * 86400000 - currentTime) / 86400000;
                if (sendDate == 0 || daysLeft == 0) { //fields not specified
                    view.setVisibility(View.GONE);
                    return true;
                }
                ((TextView) view).setText(Integer.toString(daysLeft));
                if (daysLeft < 5) {
                    view.setBackgroundColor(Color.RED);
                }
                return true;
            }
            return false;
        }
    }
}
