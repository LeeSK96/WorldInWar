package com.koreait.project_war;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class DeployActivity extends AppCompatActivity {

    TextView node_name, total_def_txt;
    Button btn_home, btn_deploy;

    ListView NodeUnitListView;
    int u_idx, node_idx;

    NodeUnitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy);

        Intent i = getIntent();
        u_idx = i.getIntExtra("u_idx", 0);
        node_idx = i.getIntExtra("node_idx", 0);

        btn_home = findViewById(R.id.btn_home);
        btn_deploy = findViewById(R.id.btn_deploy);
        node_name = findViewById(R.id.node_name);
        total_def_txt = findViewById(R.id.total_def_txt);
        NodeUnitListView = findViewById(R.id.NodeUnitListView);

        node_name.setText(i.getStringExtra("node_name"));

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_deploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DeployActivity.this, AddToNodeActivity.class);
                i.putExtra("node_idx", node_idx);
                i.putExtra("user_idx", u_idx);
                startActivity(i);
            }
        });

        Toast.makeText(DeployActivity.this, ""+u_idx+" / "+node_idx, Toast.LENGTH_SHORT).show();
        new NodeUnitAsyncTask().execute();
    }

    class NodeUnitAsyncTask extends AsyncTask<Integer, Void, List<InvenUnitVO>>{
        @Override
        protected List<InvenUnitVO> doInBackground(Integer... integers) {
            String str = "";
            String ip = Util.IP;
            String json;
            String serverIp = "http://"+ ip +":9090/Project_war/node_units.jsp";//연결할 서버주소
            List<InvenUnitVO> invenUnitVOList = new ArrayList<>();

            try {
                URL url = new URL(serverIp);
                //서버연결

                //list.jsp?id=aa&pwd=111&type=type_regi
                String sendMsg = "user_idx="+u_idx+"&unit_position="+node_idx;

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //전송방식
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());


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
                    Log.i("res2","Units:"+json);

                    JSONArray jsonArray = new JSONArray(json);

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int user_idx = Integer.parseInt(jsonObject.getString("user_idx"));
                        int unit_idx = Integer.parseInt(jsonObject.getString("unit_idx"));
                        int unit_amount = Integer.parseInt(jsonObject.getString("unit_amount"));
                        int unit_position = Integer.parseInt(jsonObject.getString("unit_position"));
                        int unit_state = Integer.parseInt(jsonObject.getString("unit_state"));

                        InvenUnitVO vo = new InvenUnitVO();
                        vo.setUser_idx(user_idx);
                        vo.setUnit_idx(unit_idx);
                        vo.setUnit_amount(unit_amount);
                        vo.setUnit_position(unit_position);
                        vo.setUnit_state(unit_state);

                        Log.i("haha", ""+vo.getUnit_idx());
                        invenUnitVOList.add(vo);
                    }


                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return invenUnitVOList;
        }

        @Override
        protected void onPostExecute(List<InvenUnitVO> invenUnitVOS) {
            new NodeUnitAsyncTask2().execute(invenUnitVOS);
        }
    }

    class NodeUnitAsyncTask2 extends AsyncTask<List<InvenUnitVO>, Void, List<InvenUnitVO>>{

        String serverip = Util.SERVER_IP_TEST;
        String sendMsg, receiveMsg;

        @Override
        protected List<InvenUnitVO> doInBackground(List<InvenUnitVO>... voids) {

            List<InvenUnitVO> invenUnitVOS = voids[0];
            try {
                String str = "";
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); //전송방식
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "";

                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    while((str = reader.readLine())!=null){
                        buffer.append(str);
                    }

                    receiveMsg = buffer.toString();

                    JSONArray jsonArray = new JSONObject(receiveMsg).getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) { //예비군 포함

                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String unit_name = jObject.getString("unit_name");
                        int can_defence = jObject.getInt("can_defence");
                        int can_attack  = jObject.getInt("can_attack");
                        int unit_defence = jObject.getInt("unit_defence");
                        String unit_img = jObject.getString("unit_img");
                        int unit_price = jObject.getInt("unit_price");
                        int idx = jObject.getInt("idx");
                        int unit_attack = jObject.getInt("unit_attack");

                        for(int j=0;j<invenUnitVOS.size();j++){


                            /*Log.i("haha", ""+j);
                            Log.i("haha", ""+invenUnitVOList.get(j).getUnit_idx());*/
                            if(invenUnitVOS.get(j).getUnit_idx() == idx){

                                invenUnitVOS.get(j).setUnit_name(unit_name);
                                invenUnitVOS.get(j).setCan_defence(can_defence);
                                invenUnitVOS.get(j).setCan_attack(can_attack);
                                invenUnitVOS.get(j).setUnit_defence(unit_defence);
                                invenUnitVOS.get(j).setUnit_img(unit_img);
                                invenUnitVOS.get(j).setUnit_price(unit_price);
                                invenUnitVOS.get(j).setUnit_attack(unit_attack);


                            }
                        }

                    }
                }// if

            }catch (Exception e){

            }

            return invenUnitVOS;
        }

        @Override
        protected void onPostExecute(List<InvenUnitVO> invenUnitVOS) {

            if(!invenUnitVOS.isEmpty()){
                int def = 0;
                for(int i=0;i<invenUnitVOS.size();i++){
                    def += invenUnitVOS.get(i).getUnit_defence()*invenUnitVOS.get(i).getUnit_amount();
                }

                total_def_txt.setText(""+def);

                if (adapter == null) {

                    adapter = new NodeUnitAdapter(DeployActivity.this, R.layout.nodeunit_list, invenUnitVOS);
                    NodeUnitListView.setAdapter(adapter);

                }
                adapter.notifyDataSetChanged();
            }

        }
    }

}