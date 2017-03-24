package com.shinhan.mymanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    class DailyItem {
        String day;
        String date;
        String text;
        DailyItem(String day, String date, String text) {   // data 저장용 class
            this.day = day;
            this.date = date;
            this.text = text;
        }
    }
    ArrayList<DailyItem> dailyItemList = new ArrayList<DailyItem>();  // 동적배열
    DailyListAdapter listAdapter;
    SQLiteDatabase db;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

           TextView title = (TextView)findViewById(R.id.title);

           Date cDate = new Date();
           Calendar c = Calendar.getInstance ( );
           c.setTime ( cDate );

           String fTitle = new SimpleDateFormat("yyyy.MM").format(cDate);
           title.setText(fTitle);

           final Date[] dates = new Date[7];
           String yo_il="";
           String dd="";
           //String testdate = new SimpleDateFormat("yyyyMMdd").format(dates[i]);

            for (int i=0 ; i < 7; i++) {
                dates[i] = c.getTime();
                dd = new SimpleDateFormat("yyyyMMdd").format(dates[i]);

                readDatabase(dd); //해당일의 DB 내용 읽기

                switch (c.get(Calendar.DAY_OF_WEEK)) {
                    case 1:
                        yo_il = "SUN";   break;
                    case 2:
                        yo_il = "MON";   break;
                    case 3:
                        yo_il = "TUE";   break;
                    case 4:
                        yo_il = "WEN";   break;
                    case 5:
                        yo_il = "THU";   break;
                    case 6:
                        yo_il = "FRI";   break;
                    case 7:
                        yo_il = "SAT";   break;
                }

                dailyItemList.add(new DailyItem(yo_il.toString(), String.valueOf(c.get(Calendar.DATE)), dd.toString()));
                c.add(Calendar.DATE, 1);
            }

        ListView listView = (ListView)findViewById(R.id.listview);
        listAdapter = new DailyListAdapter(ListActivity.this);
        listView.setAdapter(listAdapter);

           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   Intent intent = new Intent(ListActivity.this, DailyActivity.class);
                   intent.putExtra("date", dates[position].getTime());
                   startActivity(intent);
               }
           });
    }



    class DailyListAdapter extends ArrayAdapter {
        public DailyListAdapter(Context context) {
            super(context, R.layout.listitem, dailyItemList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null, true);
            }

            TextView day = (TextView)view.findViewById(R.id.day);
            TextView date = (TextView)view.findViewById(R.id.date);
            TextView text = (TextView)view.findViewById(R.id.text);
            day.setText(dailyItemList.get(position).day);
            date.setText(dailyItemList.get(position).date);
            text.setText(dailyItemList.get(position).text);
            return view;
        }
    }

    public void onButtonClicked (View view) {
        Intent intent = new Intent(ListActivity.this, DailyActivity.class);
        startActivity(intent);
    }


    public void readDatabase(String dd) {
        Daily daily = new Daily(ListActivity.this);
        Log.i("++++++dd : ",dd.toString());
        SQLiteDatabase database = daily.getReadableDatabase();
        //Cursor cursor = database.rawQuery("select * from "+Daily.TABLE_NAME+" where dd="+dd, null);
        Cursor cursor = database.rawQuery("select * from "+daily.TABLE_NAME, null);
        Log.i("++++++count : ",cursor.getCount()+"");

    }
}
