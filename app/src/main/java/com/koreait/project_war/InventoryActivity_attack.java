package com.koreait.project_war;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class InventoryActivity_attack extends AppCompatActivity {
    ListView invenListView;
    LayoutInflater mInflater;
    InvenModelAdapter adapter;
    int user_idx, total_atk;
    List<InvenUnitVO> invenUnitVOS;

    TextView total_atk_txt;
    Button btn_invent, btn_market, btn_map;
    Button undeployed_btn, attack_btn, defence_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_attack);
        invenListView = findViewById(R.id.invenListView);

        btn_invent = findViewById(R.id.btn_invent);
        btn_market = findViewById(R.id.btn_market);
        btn_map = findViewById(R.id.btn_map);
        undeployed_btn = findViewById(R.id.undeployed_btn);
        attack_btn = findViewById(R.id.attack_btn);
        defence_btn = findViewById(R.id.defence_btn);
        total_atk_txt = findViewById(R.id.total_atk_txt);

        btn_invent.setOnClickListener(intent);
        btn_market.setOnClickListener(intent);
        btn_map.setOnClickListener(intent);
        undeployed_btn.setOnClickListener(intent);
        attack_btn.setOnClickListener(intent);
        defence_btn.setOnClickListener(intent);

        user_idx = getIntent().getIntExtra("user_idx",0);
        total_atk = 0;

        Log.i("show", ""+user_idx);
        new InvenAsyncTask().execute(user_idx);

        //???????????? ?????? -> user_idx??? inventory??????????????? unit_idx??? ??? ?????????
        //select distinct unit_idx from inventory where user_idx = 1; ???
        // unit_idx??? ??????,???,???,??????,??????,????????? ?????????
        //unit_idx??? unit_amount??? (???)??? inventory?????????????????????
        //select unit_amount from inventory where unit_idx = 7 and user_idx = 1; ??????????????? ?????????????????????

    }

    View.OnClickListener intent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i;
            switch (view.getId()){
                case R.id.btn_invent:
                    break;
                case R.id.btn_market:
                    i = new Intent(InventoryActivity_attack.this, MarketActivity.class);
                    i.putExtra("user_idx", user_idx);
                    startActivity(i);
                    finish();
                    break;
                case R.id.btn_map:
                    finish();
                    break;

                case R.id.undeployed_btn:
                    i = new Intent(InventoryActivity_attack.this, InventoryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.putExtra("user_idx", user_idx);
                    startActivity(i);
                    finish();
                    break;
                case R.id.attack_btn:
                    return;
                case R.id.defence_btn:
                    i = new Intent(InventoryActivity_attack.this, InventoryActivity_defence.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.putExtra("user_idx", user_idx);
                    startActivity(i);
                    finish();
                    break;
            }

        }
    };

    class InvenAsyncTask extends AsyncTask<Integer, Void, List<InvenUnitVO>> {


        @Override
        protected List<InvenUnitVO> doInBackground(Integer... integers) {
            //DB????????????
            //???????????? ?????? -> user_idx??? inventory??????????????? unit_idx??? ??? ?????????
            //select distinct unit_idx from inventory where user_idx = 1; ???
            String str = "";
            String ip = Util.IP;
            String json;

            String serverIp = Util.SERVER_IP_INVEN;//????????? ????????????
            String result = "user_idx=" + integers[0];
            user_idx = integers[0];

            try {
                URL url = new URL(serverIp);
                //????????????
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //????????????
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //list.jsp?id=aa&pwd=111&type=type_regi
                String sendMsg = result;


                //????????? ???????????? ??????
                osw.write(sendMsg);
                osw.flush();

                //????????? ???????????? ???????????? ????????? ???????????? ?????????
                //conn.getResponseCode() : 200?????? ????????????!
                //conn.getResponseCode() : 404, 500?????? ????????? ??????
                if (conn.getResponseCode() == conn.HTTP_OK) {

                    //????????? ???????????? ??????!
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
            Log.i("arrayLength", "InvenAsyncTask: "+invenUnitVOS.size());
            return invenUnitVOS;
        }


        @Override
        protected void onPostExecute (List<InvenUnitVO> invenUnitVOList) {
            //???????????????
            //???????????????
            //????????? int????????? ??????????????? ???????????? ?????? unit_idx??? -> unit??????????????? ??????????????????
            // ??????????????? unit????????? ??????
            // ???????????? ??????????????? ????????? unit_idx??? ??????,?????????,???????????????
            // unit_idx??? unit_amount??? (???)??? inventory?????????????????????
            // select unit_amount from inventory where unit_idx = 7 and user_idx = 1; ??????????????? ?????????????????????

            new InvenUnitInfoAsync().execute(invenUnitVOList);
        }
    }


    class InvenUnitInfoAsync extends AsyncTask<List<InvenUnitVO>, Void, List<InvenUnitVO>>{

        @Override
        protected List<InvenUnitVO> doInBackground(List<InvenUnitVO>... lists) {
            String str = "";
            String ip = Util.IP;
            ArrayList<UnitVO> list = new ArrayList<UnitVO>();
            String serverIp = Util.SERVER_IP_MARKET;//????????? ????????????
            try {
                URL url = new URL(serverIp);
                //????????????
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //????????????
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
                        String unit_price = jObject.getString("unit_price");
                        String unit_attack = jObject.getString("unit_attack");
                        String unit_defence = jObject.getString("unit_defence");
                        String unit_name = jObject.getString("unit_name");
                        String unit_img = jObject.getString("unit_img");
                        String idx = jObject.getString("idx");
                        String can_attack  = jObject.getString("can_attack");
                        String can_defence = jObject.getString("can_defence");

                        for(int j=0;j<invenUnitVOS.size();j++){
                            if(Integer.toString(invenUnitVOS.get(j).getUnit_idx()).equals(idx)){

                                Log.i("position", ""+invenUnitVOS.get(j).getUnit_idx());
                                invenUnitVOS.get(j).setUnit_attack(Integer.parseInt(unit_attack));
                                invenUnitVOS.get(j).setUnit_defence(Integer.parseInt(unit_defence));
                                invenUnitVOS.get(j).setUnit_name(unit_name);
                                invenUnitVOS.get(j).setUnit_img(unit_img);
                                invenUnitVOS.get(j).setCan_attack(Integer.parseInt(can_attack));
                                invenUnitVOS.get(j).setCan_defence(Integer.parseInt(can_defence));

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
        protected void onPostExecute(List<InvenUnitVO> invenUnitVOList) {

            //???????????? state = 1
            for(int i=0;i<invenUnitVOList.size();i++){
                if(invenUnitVOList.get(i).getUnit_state() == 0 || invenUnitVOList.get(i).getUnit_state() == 2 ){
                    invenUnitVOList.remove(i);
                    i--;
                }
            }

            for(int i=0;i<invenUnitVOList.size();i++){
                total_atk += invenUnitVOList.get(i).getUnit_attack()*invenUnitVOList.get(i).getUnit_amount();

            }

            total_atk_txt.setText(""+total_atk);

            if(!invenUnitVOList.isEmpty()){

                if (adapter == null) {

                    adapter = new InvenModelAdapter(InventoryActivity_attack.this, R.layout.inventory_list_attack, invenUnitVOList);
                    invenListView.setAdapter(adapter);

                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}


