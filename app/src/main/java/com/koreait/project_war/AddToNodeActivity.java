package com.koreait.project_war;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddToNodeActivity extends AppCompatActivity {

    Button btn_back;
    ListView InvenListView;

    int node_idx, user_idx;
    InvenModelAdapter adapter;

    List<InvenUnitVO> invenUnitVOS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_node);

        Intent i = getIntent();
        node_idx = i.getIntExtra("node_idx", 0);
        user_idx = i.getIntExtra("user_idx", 0);

        btn_back = findViewById(R.id.btn_back);
        InvenListView = findViewById(R.id.InvenListView);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new DefenceUnitAsync().execute(user_idx, node_idx);
    }

    class DefenceUnitAsync extends AsyncTask<Integer, Void, List<InvenUnitVO>>{

        @Override
        protected List<InvenUnitVO> doInBackground(Integer... integers) {
            //DB접속해서
            //인벤토리 클릭 -> user_idx로 inventory테이블에서 unit_idx를 다 긁어옴
            //select distinct unit_idx from inventory where user_idx = 1; 로
            String str = "";
            String ip = Util.IP;
            String json;

            String serverIp = Util.SERVER_IP_INVEN;//연결할 서버주소
            String result = "user_idx=" + integers[0];
            int user_idx = integers[0];

            try {
                URL url = new URL(serverIp);
                //서버연결
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //전송방식
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //list.jsp?id=aa&pwd=111&type=type_regi
                String sendMsg = result;


                //서버로 파라미터 전달
                osw.write(sendMsg);
                osw.flush();

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

                    json = buffer.toString();
                    Log.i("res","UNits:"+json);

                    JSONArray jsonArray = new JSONArray(json);
                    Log.i("arrayLength", "InvenAsyncTask: "+jsonArray.length());

                    InvenUnitVO invenVO;
                    invenUnitVOS = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        invenVO = new InvenUnitVO();
                        invenVO.setInven_idx(Integer.parseInt(jsonObject.getString("idx")));
                        invenVO.setUser_idx(user_idx);
                        invenVO.setUnit_idx(Integer.parseInt(jsonObject.getString("unit_idx")));
                        invenVO.setUnit_amount(Integer.parseInt(jsonObject.getString("unit_amount")));
                        invenVO.setUnit_position(Integer.parseInt(jsonObject.getString("unit_position")));
                        invenVO.setUnit_state(Integer.parseInt(jsonObject.getString("unit_state")));

                        invenUnitVOS.add(invenVO);
                    }


                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return invenUnitVOS;
        }

        @Override
        protected void onPostExecute(List<InvenUnitVO> invenUnitVOS) {

            new DefenceUnit2Async().execute(invenUnitVOS);
        }
    }

    class DefenceUnit2Async extends AsyncTask<List<InvenUnitVO>, Void, List<InvenUnitVO>>{
        @Override
        protected List<InvenUnitVO> doInBackground(List<InvenUnitVO>... lists) {
            String str = "";
            String ip = Util.IP;
            ArrayList<UnitVO> list = new ArrayList<UnitVO>();
            String serverIp = Util.SERVER_IP_MARKET;//연결할 서버주소
            try {
                URL url = new URL(serverIp);
                //서버연결
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //전송방식
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                if (conn.getResponseCode() == conn.HTTP_OK) {

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }

                    String json = buffer.toString();

                    JSONArray jsonArray = new JSONObject(json).getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        int unit_price = jObject.getInt("unit_price");
                        int unit_attack = jObject.getInt("unit_attack");
                        int unit_defence = jObject.getInt("unit_defence");
                        String unit_name = jObject.getString("unit_name");
                        String unit_img = jObject.getString("unit_img");
                        int idx = jObject.getInt("idx");
                        int can_attack  = jObject.getInt("can_attack");
                        int can_defence = jObject.getInt("can_defence");

                        for(int j=0;j<invenUnitVOS.size();j++){
                            if(invenUnitVOS.get(j).getUnit_idx() == idx){

                                invenUnitVOS.get(j).setUnit_attack(unit_attack);
                                invenUnitVOS.get(j).setUnit_defence(unit_defence);
                                invenUnitVOS.get(j).setUnit_name(unit_name);
                                invenUnitVOS.get(j).setUnit_img(unit_img);
                                invenUnitVOS.get(j).setCan_attack(can_attack);
                                invenUnitVOS.get(j).setCan_defence(can_defence);
                            }

                        }
                    }//for
                }//if

            } catch (Exception e) {
                e.printStackTrace();
            }
            return invenUnitVOS;
        }

        @Override
        protected void onPostExecute(List<InvenUnitVO> invenUnitVOS) {
            //걸러내기 state = 2
            for(int i=0;i<invenUnitVOS.size();i++){
                if(invenUnitVOS.get(i).getUnit_state() == 1 || invenUnitVOS.get(i).getUnit_state() == 0 ){
                    invenUnitVOS.remove(i);
                    i--;
                }
            }

            Toast.makeText(AddToNodeActivity.this, ""+invenUnitVOS.size(), Toast.LENGTH_SHORT).show();
            if(!invenUnitVOS.isEmpty()){

                if (adapter == null) {

                    adapter = new InvenModelAdapter(AddToNodeActivity.this, R.layout.inventory_list_defence2, invenUnitVOS);
                    InvenListView.setAdapter(adapter);

                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}