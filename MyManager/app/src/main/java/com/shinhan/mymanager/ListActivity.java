package com.shinhan.mymanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ListActivity extends AppCompatActivity {
    public static String content = "";
    public static String project = "";
    public static Date cDate;
    public static  Calendar c;
    public static Date toDay;
    private static String rssUrl = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=4128558000";
    public static String kweather[] = {"","","","맑음","맑음","맑음","맑음"};
    public static String tweather[] = {"","","","","","",""};

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

    class RSSNewsItem {
        String hour;
        String day;
        String wfKor;
        RSSNewsItem(String hour, String day, String wfKor) {   // data 저장용 class
            this.hour = hour;
            this.day = day;
            this.wfKor = wfKor;
        }
    }


    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button buttonNext = (Button)findViewById(R.id.next);
        Button buttonPrev = (Button)findViewById(R.id.prev);


        cDate = new Date();
        c = Calendar.getInstance();
        c.setTime(cDate);
        /* 오늘 날짜 정의 */
        toDay = new Date();
        Calendar tod = Calendar.getInstance();
        tod.setTime(toDay);

        Log.i("+++++ cDate:", c.toString());

        new LoadXML().execute(rssUrl);  //입력한 url에 접속
       // displayCalendar(c);

        buttonNext.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(cDate);
                c.add(Calendar.DATE, 7);
                cDate = new Date(c.getTimeInMillis());
                Log.i("+++++ cDate:", c.toString());
                displayCalendar(c);
            }
        });

        buttonPrev.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(cDate);
                c.add(Calendar.DATE, -7);
                cDate = new Date(c.getTimeInMillis());
                Log.i("+++++ cDate:", c.toString());
                displayCalendar(c);
            }
        });
    }

    void displayCalendar(Calendar c) {
        String startDay = "N";
        TextView title = (TextView) findViewById(R.id.title);
        String fTitle = new SimpleDateFormat("yyyy.MM").format(c.getTime());
        title.setText(fTitle);
        String ddDay = new SimpleDateFormat("yyyy.MM.dd").format(c.getTime());
        if (ddDay.equals(new SimpleDateFormat("yyyy.MM.dd").format(toDay.getTime()))) {
            Log.i("+++++Today????", "OKOKOKOKOKOK"+ddDay);
            for(int i=0; i<7; i++) {
                tweather[i] = kweather[i];
            }
        } else {
            for(int i=0; i<7; i++) {
                tweather[i] = "";
            }
        }



        final Date[] dates = new Date[7];
        final String[] projects = new String[7];
        final String[] contents = new String[7];
        String yo_il = "";
        String dd = "";
        //String testdate = new SimpleDateFormat("yyyyMMdd").format(dates[i]);
        dailyItemList.clear();


        for (int i = 0; i < 7; i++) {
            dates[i] = c.getTime();
            dd = new SimpleDateFormat("yyyyMMdd").format(dates[i]);

            readDatabase(dd); //해당일의 DB 내용 읽기

            switch (c.get(Calendar.DAY_OF_WEEK)) {
                case 1:                    yo_il = "SUN";                    break;
                case 2:                    yo_il = "MON";                    break;
                case 3:                    yo_il = "TUE";                    break;
                case 4:                    yo_il = "WEN";                    break;
                case 5:                    yo_il = "THU";                    break;
                case 6:                    yo_il = "FRI";                    break;
                case 7:                    yo_il = "SAT";                    break;
            }
            contents[i]=ListActivity.content;
            projects[i]=ListActivity.project;
            String tmpText = "";
            if (!contents[i].isEmpty()) {
                tmpText = "[" + projects[i] + "] " + contents[i];
            }
            dailyItemList.add(new DailyItem(yo_il.toString(), String.valueOf(c.get(Calendar.DATE)), tmpText));
            Log.i("+++++++weather:",i+kweather[i].toString());
                ListActivity.content = "";
            ListActivity.project = "";
            c.add(Calendar.DATE, 1);
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        listAdapter = new DailyListAdapter(ListActivity.this);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ListActivity.this, DailyActivity.class);
                intent.putExtra("date", dates[position].getTime());
                intent.putExtra("project", projects[position].toString());
                intent.putExtra("content", contents[position].toString());
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
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null, true);
            }

            TextView day = (TextView) view.findViewById(R.id.day);
            TextView date = (TextView) view.findViewById(R.id.date);
            ImageView weather = (ImageView) view.findViewById(R.id.weather);
            TextView text = (TextView) view.findViewById(R.id.text);
            day.setText(dailyItemList.get(position).day);
            date.setText(dailyItemList.get(position).date);
            if (tweather[position].equals("맑음")) {
            weather.setImageResource(R.drawable.sun); }
            else if (tweather[position].equals("구름 많음")) {
                weather.setImageResource(R.drawable.cloudmany);
            } else if (tweather[position].equals("구름 조금")) {
                weather.setImageResource(R.drawable.cloudlittle);
            } else if (tweather[position].equals("흐림")) {
                weather.setImageResource(R.drawable.cloud);
            } else if (tweather[position].equals("비")) {
                weather.setImageResource(R.drawable.rain);
            }
            //weather.setText(dailyItemList.get(position).weather);
            text.setText(dailyItemList.get(position).text);
            return view;
        }
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(ListActivity.this, DailyActivity.class);
        startActivity(intent);
    }


    public void onListClicked(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(cDate);
        c.add(Calendar.DATE, 7);
        cDate = new Date(c.getTimeInMillis());
        Log.i("+++++ cDate:", c.toString());
        displayCalendar(c);
    }

    public void readDatabase(String dd) {

        Daily daily = new Daily(ListActivity.this);
        SQLiteDatabase database = daily.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + Daily.TABLE_NAME + " where dd=" + dd, null);

        Log.i("++++++count : ", dd.toString() + "  " + cursor.getCount() + "");
        for (int i = 0; i < cursor.getCount(); i++) {   //DB내용을 문자형배열로 변환
            cursor.moveToNext();
            ListActivity.project = cursor.getString(2);
            ListActivity.content = cursor.getString(3);
        }
        Log.i("++++++db row : ", ListActivity.content);
    }
    class LoadXML extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(ListActivity.this);

        @Override
        protected void onPreExecute() {  //백그라운드 직전에 수행
            super.onPreExecute();
            dialog.setMessage("기상청 날씨정보 요청중 ....");
            dialog.show();   //프로그레스 다이얼로그 보여주기
        }

        @Override
        protected void onPostExecute(String s) {   //백그라운드 직후에 수행
            super.onPostExecute(s);
            dialog.dismiss(); //프로그레스 다이얼로그 닫기
            displayCalendar(c);
            listAdapter.notifyDataSetChanged();
            /* XML 파싱,  ListView에 출력 */
        }

        @Override
        protected String doInBackground(String... params) {  //실제 통신이 처리되는 부분
            StringBuilder output = new StringBuilder();
            try {  // 통신 부분은 반드시 try - catch 로 예외처리 해주어야 한다.
                URL url = new URL(params[0]);  //전달받은 urlString으로 URL 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    int resCode = conn.getResponseCode();  // 여기까지가 요청

                    /* 수신된 데이타를 처리하는 java 형식
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;  //웹서버로부터 수신되는 데이터를 처리
                    while ((line = reader.readLine()) != null) {
                        output.append(line);  //한줄씩 읽어서 StringBuilder 객체에 추가
                    }
                    reader.close();   */

                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = builderFactory.newDocumentBuilder();
                    InputStream inputStream = conn.getInputStream();
                    Document document = builder.parse(inputStream);
                    int count = processDocument(document);  // XML 파싱 (DOM 파서)
                    Log.i("count:",count+"");


                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        }
    }
    private int processDocument(Document document) { //XML문서 파싱
        int count = 0;
        //newsItemList.clear(); //동적배열 초기화
        Element documentElement = document.getDocumentElement();
        NodeList nodelist = documentElement.getElementsByTagName("data");
        if((nodelist != null) && (nodelist.getLength()>0)) {
            for (int i = 0; i < nodelist.getLength(); i++) { //아이템 개수만큼 반복
                dissectNode(nodelist, i); //아이템정보 추출
                count++;
            }
        }
        return count;
    }

    private void dissectNode(NodeList nodelist, int index) {  //아이템정보 추출
        RSSNewsItem newsItem = null ;
        try{
            Element entry = (Element)nodelist.item(index); //아이템객체 추출
            Element hour = (Element)entry.getElementsByTagName("hour").item(0);
            Element day = (Element)entry.getElementsByTagName("day").item(0);
            Element wfKor = (Element)entry.getElementsByTagName("wfKor").item(0);

            String hourValue = getElementString(hour);
            String dayValue = getElementString(day);
            String wfKorValue = getElementString(wfKor);

            if (index == 0)  {
            kweather[0] = wfKorValue;
                Log.i("+++++weather",index+kweather[index].toString());}
            else if (dayValue.equals("1") && hourValue.equals("9")) {
                kweather[1] = wfKorValue;
                Log.i("+++++weather",index+kweather[index].toString());}
            else if (dayValue.equals("2") && hourValue.equals("9")) {
                kweather[2] = wfKorValue;
                Log.i("+++++weather",index+kweather[index].toString());}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getElementString(Element element) {
        String value = null;
        if(element != null) {
            Node firstChild = element.getFirstChild();
            if (firstChild != null) {
                value = firstChild.getNodeValue();
            }
        }
        return value;
    }
}
