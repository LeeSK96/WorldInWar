package com.koreait.project_war;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MarketActivity extends AppCompatActivity {

    Button btn_buy,btn_buy1,btn_end, btn_invent, btn_map, btn_market;
    TextView user_money;
    EditText et_cnt;
    ListView unitListView;
    ImageView unit_img;
    UnitModelAdapter adapter;
    Dialog dialog;
    LayoutInflater mInflater;
    UnitVO vo;
    MediaPlayer mp;

    int user_idx;
    String u_money;// 메인에서 받은 유저 인덱스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        mp = MediaPlayer.create(this, R.raw.inventory_shop);
        mp.setLooping(true);
        mp.start();

        Intent userintent = getIntent();
        user_idx = userintent.getIntExtra("user_idx",0);
        Log.i("intent", ""+user_idx);
        String param = "u_idx=" + user_idx;

        new MoneyTask().execute(param);

        unitListView = findViewById(R.id.unitListView);

        btn_invent = findViewById(R.id.btn_invent);
        btn_map = findViewById(R.id.btn_map);
        btn_market = findViewById(R.id.btn_market);

        btn_invent.setOnClickListener(mainClick);
        btn_map.setOnClickListener(mainClick);
        btn_market.setOnClickListener(mainClick);

        new UnitDBTask().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mp = MediaPlayer.create(this, R.raw.map);
        mp.setLooping(true);
        mp.start();
    }

    //사용자의 돈을 가져오기 DB
    class MoneyTask extends AsyncTask<String, Void, String>{
        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/money.jsp";

        @Override
        protected String doInBackground(String... strings) {

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = strings[0];

                //서버로 파라미터 전달
                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK)
                {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();

                    while((str = reader.readLine()) != null)
                    {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("res");
                    JSONObject jObject = jarray.getJSONObject(0);
                    u_money = jObject.getString("money");

                }
            }
            catch (Exception e)
            {
            }

            return sendMsg;

        }// doInBackground

        @Override
        protected void onPostExecute(String s) {

            user_money = findViewById(R.id.user_money);

            user_money.setText(u_money);
        }
    }

    //리스트뷰 데이터용 어싱크 클래스
    class UnitDBTask extends AsyncTask<String, Void, ArrayList<UnitVO>> {

        @Override
        protected ArrayList<UnitVO> doInBackground(String... strings) {
            //json타입의 데이터를 jsp url로 부터 가져와서 list에담고 리턴해주면
            // onPostExecute에서 어댑터에 list를 보내서 연결후 작업 결과 반영
            // 여기까지 그럼 리스트뷰에데이터를 넣는거 완료 그후
            // unit 어싱크 온포스테에서는 다른작업 하면 됨
            String str = "";
            String ip = Util.IP;
            ArrayList<UnitVO> list = new ArrayList<UnitVO>();
            String serverIp = Util.SERVER_IP_TEST;//연결할 서버주소
                try {
                    URL url = new URL(serverIp);
                    //서버연결
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST"); //전송방식
                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                    //list.jsp?id=aa&pwd=111&type=type_regi
                    //    sendMsg = strings[0] + "&type=" + strings[1];


                    //서버로 파라미터 전달
                    //  osw.write(sendMsg);
                    //  osw.flush();

                    //전송이 완료되면 서버에서 처리한 결과값을 받는다
                    //conn.getResponseCode() : 200이면 문제없음!
                    //conn.getResponseCode() : 404, 500이면 비정상 전송
                    if (conn.getResponseCode() == conn.HTTP_OK) {

                        //서버의 데이터를 읽기!
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                        BufferedReader reader = new BufferedReader(tmp);

                        StringBuffer buffer = new StringBuffer();
                        while ((str = reader.readLine()) != null) {

                            buffer.append(str);

                        }

                        String json = buffer.toString();

                        JSONArray jsonArray = new JSONObject(json).getJSONArray("result");
                        Log.i("good",""+jsonArray.length());
                        for (int i = 1; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            String unit_price = jObject.getString("unit_price");
                            String unit_attack = jObject.getString("unit_attack");
                            String unit_defence = jObject.getString("unit_defence");
                            String unit_name = jObject.getString("unit_name");
                            String unit_img = jObject.getString("unit_img");
                            String idx = jObject.getString("idx");
                            String can_attack  = jObject.getString("can_attack");
                            String can_defence = jObject.getString("can_defence");
                            Log.i("MY2",jObject.getString("unit_price"));

                            vo = new UnitVO();
                            vo.setUnit_defence(Integer.parseInt(unit_defence));
                            vo.setUnit_attack(Integer.parseInt(unit_attack));
                            vo.setUnit_name(unit_name);
                            vo.setUnit_price(Integer.parseInt(unit_price));
                            vo.setUnit_img(unit_img);
                            vo.setIdx(Integer.parseInt(idx));
                            vo.setCan_attack(Integer.parseInt(can_attack));
                            vo.setCan_defence(Integer.parseInt(can_defence));
                            list.add(vo);

                        }// for

                    }

                } catch (Exception e) {

                }

                return list;
            }

        @Override
        protected void onPostExecute(ArrayList<UnitVO> unitVOS) {
            if( adapter == null ) {
                adapter = new UnitModelAdapter(MarketActivity.this, R.layout.war_unit, unitVOS, user_idx , u_money);

                unitListView.setAdapter(adapter);

            }

            adapter.notifyDataSetChanged();

            /*Intent marketintent = new Intent(MarketActivity.this, MarketActivity.class);
            marketintent.putExtra("idx", u_idx);
            startActivity(marketintent);*/

        }
    }



    // 버튼 클릭 리스너 (인벤토리, 맵, 상점)
    View.OnClickListener mainClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = null;
            switch (view.getId()){

                case R.id.btn_invent:// 인벤토리 버튼 클릭시
                    mp.stop();
                    i = new Intent(MarketActivity.this, InventoryActivity.class);
                    i.putExtra("user_idx", user_idx);
                    startActivity(i);
                    finish();
                    break;

                case R.id.btn_map:// 맵 버튼 클릭시
                    mp.stop();
                    i = new Intent(MarketActivity.this, MainActivity.class);
                    i.putExtra("user_idx", ""+user_idx);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;

                case R.id.btn_market:// 상점 버튼 클릭시
                    i = new Intent(MarketActivity.this, MarketActivity.class);
                    i.putExtra("user_idx", user_idx);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;

            }// switch
        }
    };


}

