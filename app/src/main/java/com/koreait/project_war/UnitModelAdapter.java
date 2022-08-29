package com.koreait.project_war;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class UnitModelAdapter extends ArrayAdapter<UnitVO> {
   Context context;
   int resource, u_idx;
   ArrayList<UnitVO> list;
   UnitVO vo;
   Dialog dialog;
   ImageView unit_img_buy;
   Button btn_plus,btn_minus,btn_end,btn_buyItem;
   TextView et_cnt, unit_p, unit_n, unit_p_a, money_enough;

   String u_money, money_update, change_money;
   int count = 0;
   int m_amount = 0;

    public UnitModelAdapter(Context context, int resource, ArrayList<UnitVO> list, int u_idx, String u_money) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
        this.u_idx = u_idx;
        this.u_money = u_money;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater ulinf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = ulinf.inflate(resource,null);


        vo = list.get(position);
        Log.i("test",""+vo.getIdx());

        Log.i("good5",""+position);
        Log.i("good5", "" + list.size());

        TextView unit_price = convertView.findViewById(R.id.unit_price);
        TextView unit_name = convertView.findViewById(R.id.unit_name);
        TextView unit_atk = convertView.findViewById(R.id.unit_atk);
        TextView unit_def = convertView.findViewById(R.id.unit_def);
        TextView can_atk = convertView.findViewById(R.id.can_atk);
        TextView can_def = convertView.findViewById(R.id.can_def);

        Button btn_buy = convertView.findViewById(R.id.btn_buy);
        btn_buy.setTag(position);

        ImageView unit_img = convertView.findViewById(R.id.unit_img);
        Log.i("good",""+vo.getUnit_price());

        unit_price.setText("" + vo.getUnit_price());
        unit_name.setText(vo.getUnit_name());
        unit_atk.setText("" + vo.getUnit_attack());
        unit_def.setText("" + vo.getUnit_defence());
        unit_img.setImageResource(context.getResources().getIdentifier(vo.getUnit_img(),"drawable",context.getPackageName()));

        if(vo.getCan_attack() == 1 && vo.getCan_defence() == 1){
            can_atk.setVisibility(View.VISIBLE);
            can_def.setVisibility(View.VISIBLE);
        }else if( vo.getCan_attack() == 1 && vo.getCan_defence() == 0){
            can_atk.setVisibility(View.VISIBLE);
            can_def.setVisibility(View.INVISIBLE);
        }else{
            can_atk.setVisibility(View.INVISIBLE);
            can_def.setVisibility(View.VISIBLE);
        }

        Log.i("test",""+unit_name.getText());


        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("K", "tag:" + view.getTag());

                vo = list.get((int)view.getTag());

                dialog = new Dialog(context);

                dialog.setContentView(R.layout.dialog_buy);

                unit_img_buy = dialog.findViewById(R.id.unit_img_buy);
                btn_plus = dialog.findViewById(R.id.btn_plus);
                btn_minus = dialog.findViewById(R.id.btn_minus);
                et_cnt = dialog.findViewById(R.id.et_cnt);
                btn_end = dialog.findViewById(R.id.btn_end);
                btn_buyItem = dialog.findViewById(R.id.btn_buyItem);
                btn_buyItem.setTag(view.getTag());
                unit_n = dialog.findViewById(R.id.unit_n);
                unit_p = dialog.findViewById(R.id.unit_p);
                unit_p_a = dialog.findViewById(R.id.unit_p_a);
                money_enough = dialog.findViewById(R.id.money_enough);



                unit_img_buy.setImageResource(context.getResources().getIdentifier(vo.getUnit_img(),"drawable",context.getPackageName()));
                unit_n.setText(vo.getUnit_name());
                unit_p.setText(""+vo.getUnit_price());

                btn_plus.setOnClickListener(click);
                btn_minus.setOnClickListener(click);
                btn_end.setOnClickListener(click);
                btn_buyItem.setOnClickListener(click);

                dialog.show();

            }
        });

        return convertView;
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.btn_plus:
                    ++count;
                    et_cnt.setText(""+count);
                    unit_p_a.setText(""+(vo.getUnit_price() * count));

                    if(Integer.parseInt(u_money) < (vo.getUnit_price() * count) ){
                        money_enough.setVisibility(View.VISIBLE);
                    }else{
                        money_enough.setVisibility(View.INVISIBLE);
                    }

                    break;
                case R.id.btn_minus:
                    if(count > 0)
                    --count;
                    et_cnt.setText(""+count);
                    unit_p_a.setText(""+(vo.getUnit_price() * count));

                    if(Integer.parseInt(u_money) < (vo.getUnit_price() * count) ){
                        money_enough.setVisibility(View.VISIBLE);
                    }else{
                        money_enough.setVisibility(View.INVISIBLE);
                    }

                    break;

                case R.id.btn_buyItem:


                   if(count > 0 && Integer.parseInt(u_money) >= (vo.getUnit_price() * count)) {

                       m_amount = Integer.parseInt(u_money) - (vo.getUnit_price() * count);

                        String market_insert = "u_idx=" + u_idx + "&unit_idx=" + list.get((int)view.getTag()).getIdx() + "&unit_amount=" + count;
                        money_update = "u_idx=" + u_idx + "&money=" + m_amount;

                        new MarketInsertTask().execute(market_insert);//idx랑 count 넘겨야함

                    }else if(count == 0) {
                        Toast.makeText(getContext(), "0개이하는 살수없습니다", Toast.LENGTH_SHORT).show();
                    }else if(Integer.parseInt(u_money) < (vo.getUnit_price() * count)){
                        Toast.makeText(getContext(), "가지고 있는 돈이 부족합니다", Toast.LENGTH_SHORT).show();
                    }

                    count = 0;
                    dialog.dismiss();

                    break;

                case R.id.btn_end:
                    count = 0;
                    dialog.dismiss();
                    break;

            }
        }
    };

    //상품 구매 완료시 db 데이터 입력 asycTask

    class MarketInsertTask extends AsyncTask<String, Void, String>{
        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String serverIp = Util.SERVER_IP_INSERT;//연결할 서버주소


        @Override
        protected String doInBackground(String... strings) {

            try {

                String str = "";
                URL url = new URL(serverIp);

                //서버연결
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //전송방식
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //list.jsp?id=aa&pwd=111&type=type_regi
                sendMsg = strings[0];
                Log.i("market_insert", sendMsg);

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

                    receiveMsg = buffer.toString();

                    JSONArray jsonArray = new JSONObject(receiveMsg).getJSONArray("res");

                    JSONObject jObject = jsonArray.getJSONObject(0);
                    String result_type = jObject.getString("result");

                    Log.i("res",result_type);
                    if(result_type.equals("success")){
                        receiveMsg = "구매완료";
                    }else{
                        receiveMsg = "구매실패";
                    }

                }

            } catch (Exception e) {

            }

            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();

            new MoneyModifyTask().execute(money_update);

        }
    } //

    class MoneyModifyTask extends AsyncTask<String, Void, String>{
        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/moneymodify.jsp";

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
                Log.i("money_update", sendMsg);

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
                    change_money = jObject.getString("money");

                    Log.i("change_money", change_money);

                    /*Intent moneyintent = new Intent(getContext(), MarketActivity.class);
                    moneyintent.putExtra("idx", u_idx);
                    context.startActivity(moneyintent);*/



                }
            }
            catch (Exception e)
            {
            }

            return change_money;

        }// doInBackground

        @Override
        protected void onPostExecute(String s) {
           ((MarketActivity)context).user_money.setText(s);
           u_money = s;
        }
    }


}
