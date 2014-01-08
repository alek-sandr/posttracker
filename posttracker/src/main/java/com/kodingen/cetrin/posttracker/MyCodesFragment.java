package com.kodingen.cetrin.posttracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MyCodesFragment extends Fragment implements DialogResultReceiver, LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int CM_DELETE_ID = 0;
    private static final int CM_EDIT_ID = 1;
    private DBHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_track_codes, container, false);
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        // формируем столбцы сопоставления
        String[] from = new String[] { DBHelper.COL_TRACKCODE, DBHelper.COL_DESCRIPTION, DBHelper.COL_LASTCHECK, DBHelper.COL_SENDDATE};
        int[] to = new int[] { R.id.tvTrackCodeItem, R.id.tvItemDescr, R.id.tvItemLastChecked, R.id.tvDaysLeft };
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.item, null, from, to, 0);
        lv = (ListView) v.findViewById(R.id.lvMyCodes);
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
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_my_track_codes);
//        //setContentView(R.layout.fragment_my_track_codes);
////
////        if (savedInstanceState == null) {
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.container, new PlaceholderFragment())
////                    .commit();
////        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit);
        menu.add(0, CM_DELETE_ID, 1, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CM_DELETE_ID:
                // получаем из пункта контекстного меню данные по пункту списка
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                // извлекаем id записи и удаляем соответствующую запись в БД
                dbHelper.delRecord(acmi.id);
                // получаем новый курсор с данными
                getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
                return true;
            case CM_EDIT_ID:
                // получаем из пункта контекстного меню данные по пункту списка
                AdapterView.AdapterContextMenuInfo acmi2 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                BarcodeInfo codeInfo = dbHelper.getInfo(acmi2.id);
                Executor<BarcodeInfo> ex = new Executor<BarcodeInfo>() {
                    @Override
                    public boolean execute(BarcodeInfo barcodeInfo) {
                        return dbHelper.updateTrackInfo(barcodeInfo) > 0;
                    }
                };
                DialogBuilder.getEditDialog(getActivity(), codeInfo, this, ex).show();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper.isOpen()) {
            dbHelper.close();
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyCursorLoader(getActivity(), dbHelper);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onSuccess() {
        getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onFail() {
    }

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
                if (sendDate == 0 || maxDays == 0) { //fields not specified
                    view.setVisibility(View.GONE);
                    return true;
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                long daysLeft = (sendDate + maxDays * 86400000L - currentTime) / 86400000L;
                ((TextView) view).setText(Long.toString(daysLeft));
                view.setBackgroundResource(R.drawable.days_left_indicator_green);
                if (daysLeft < 5) {
                    view.setBackgroundResource(R.drawable.days_left_indicator_red);
                }
                return true;
            }
            return false;
        }
    }
}
