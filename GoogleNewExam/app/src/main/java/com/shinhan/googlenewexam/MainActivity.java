package com.shinhan.googlenewexam;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    //private static String rssUrl = "http://api.sbs.co.kr/xml/news/rss.jsp?pmDiv=entertainment";
    private static String rssUrl = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=4128558000";
    String[] weather = {"","","","","","",""};

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
    ArrayList<RSSNewsItem> newsItemList = new ArrayList<RSSNewsItem>();  // 동적배열
    RSSListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        newsItemList.add(new RSSNewsItem("제목","https://m.naver.com","설명1"));
        newsItemList.add(new RSSNewsItem("제목","https://m.naver.com","설명2"));
        newsItemList.add(new RSSNewsItem("제목","https://m.naver.com","설명3"));
        ListView listView = (ListView)findViewById(R.id.listview);
        listAdapter = new RSSListAdapter(MainActivity.this);
        listView.setAdapter(listAdapter);
        EditText editText = (EditText)findViewById(R.id.input01);  //rssUrl 상수값 디폴트 세팅
        editText.setText(rssUrl);
    }

    class RSSListAdapter extends ArrayAdapter {

        public RSSListAdapter(Context context) {
            super(context, R.layout.listitem, newsItemList);
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
            TextView dataItem01 = (TextView)view.findViewById(R.id.dataItem01);
            TextView dataItem02 = (TextView)view.findViewById(R.id.dataItem02);
            TextView dataItem03 = (TextView)view.findViewById(R.id.dataItem03);
            WebView dataItem04 = (WebView)view.findViewById(R.id.dataItem04);
            dataItem01.setText(newsItemList.get(position).hour);
            dataItem02.setText(newsItemList.get(position).day);
            dataItem03.setText(newsItemList.get(position).wfKor);
            return view;
        }


    }



    public void onButtonClicked(View view) {
        EditText editText = (EditText)findViewById(R.id.input01);
        String urlString = editText.getText().toString();
        if (urlString.indexOf("http") != -1) {   //http라는 문자열이 포함되어 있는지 확인
            new LoadXML().execute(urlString);  //입력한 url에 접속
        }
    }

    class LoadXML extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {  //백그라운드 직전에 수행
            super.onPreExecute();
            dialog.setMessage("뉴스 RSS 요청중 ....");
            dialog.show();   //프로그레스 다이얼로그 보여주기
        }

        @Override
        protected void onPostExecute(String s) {   //백그라운드 직후에 수행
            super.onPostExecute(s);
            dialog.dismiss(); //프로그레스 다이얼로그 닫기
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
        newsItemList.clear(); //동적배열 초기화
        Element documentElement = document.getDocumentElement();
        NodeList nodelist = documentElement.getElementsByTagName("data");
        if((nodelist != null) && (nodelist.getLength()>0)) {
            for (int i = 0; i < nodelist.getLength(); i++) { //아이템 개수만큼 반복
                RSSNewsItem newsItem = dissectNode(nodelist, i); //아이템정보 추출

                if(newsItem != null) {
                    newsItemList.add(newsItem);
                    count++;
                }
            }
        }
        return count;
    }

    private RSSNewsItem dissectNode(NodeList nodelist, int index) {  //아이템정보 추출
        RSSNewsItem newsItem = null ;
        try{
            Element entry = (Element)nodelist.item(index); //아이템객체 추출
            Element hour = (Element)entry.getElementsByTagName("hour").item(0);
            Element day = (Element)entry.getElementsByTagName("day").item(0);
            Element wfKor = (Element)entry.getElementsByTagName("wfKor").item(0);

            //String seqValue = getElementString(seq);
            //Log.i("+++++ seq:", seqValue.toString());
            String hourValue = getElementString(hour);
            String dayValue = getElementString(day);
            String wfKorValue = getElementString(wfKor);

            if ((index == 0) || (dayValue.equals("1") && hourValue.equals("9")) ||
                    (dayValue.equals("2") && hourValue.equals("9")) ){
                newsItem = new RSSNewsItem(hourValue, dayValue, wfKorValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsItem;
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
