package com.koreait.project_war;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText et_id, et_pw;
    Button btn_login, btn_register;
    CheckBox auto_login;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        auto_login = findViewById(R.id.auto_login);

        Intent i = getIntent();
        int logout = i.getIntExtra("logout", 0);
        if(logout == 1){
            editor.clear();
            editor.commit();
        }


        settings = getSharedPreferences("setting", 0);
        editor = settings.edit();


        Intent regiintent = getIntent();
        if(regiintent.hasExtra("id"))
        {
            String id = regiintent.getStringExtra("id");
            String pw = regiintent.getStringExtra("pw");
            String result = "id=" + id + "&pw=" + pw;

            new LoginTask().execute(result, Util.TYPE_LOGIN);
        }//register intent

        if(settings.getString("ID", null) != null)
        {
            String id = settings.getString("ID", null);
            String pw = settings.getString("PW", null);
            String result = "id=" + id + "&pw=" + pw;

            new LoginTask().execute(result, Util.TYPE_LOGIN);
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        }); //btn_register

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = et_id.getText().toString().trim();
                String pw = et_pw.getText().toString().trim();
                String result ="id=" + id + "&pw=" + pw;

                new LoginTask().execute(result, Util.TYPE_LOGIN);
            }
        }); //btn_login

        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(auto_login.isChecked())
                {
                    String id = et_id.getText().toString().trim();
                    String pw = et_pw.getText().toString().trim();

                    editor.putString("ID", id);
                    editor.putString("PW", pw);
                    editor.putBoolean("Auto-Login", true);
                    editor.commit();
                }
                else
                {
                    editor.clear();
                    editor.commit();
                }
            }
        });

    }//onCreate

    class LoginTask extends AsyncTask<String, Void, String>
    {
        String svr = Util.SVR;
        String sendMsg, receiveMsg;
        String idx;
        String svr_addr = Util.SVR_ADDR;

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

                //list.jsp?id=aa&pwd=111&type="type_login"
                sendMsg = strings[0] + "&type=" + strings[1];

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
                    String result = jObject.getString("result");

                    jObject = jarray.getJSONObject(1);
                    String user_idx = jObject.getString("idx");

                    if(result.equals("success"))
                    {
                        receiveMsg ="Login success";
                        idx = user_idx;
                    }
                    else
                    {
                        receiveMsg = "Please check ID / PW once again";
                        idx = "";
                    }
                }
            }
            catch (Exception e)
            {
            }

            return receiveMsg;

        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            if(s.equals("Login success"))
            {
                String id = et_id.getText().toString().trim();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("id", id);
                i.putExtra("user_idx", idx);
                startActivity(i);
                finish();
            }
        }

    }//LoginTask

}