package com.koreait.project_war;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.android.colorpicker.ColorPanelView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity implements ColorPickerDialogListener {

    EditText et_id, et_pw, et_email, et_name;
    Button btn_select, btn_register, btn_cancel;
    ColorPanelView color_view; //https://github.com/jaredrummler/ColorPicker
    String color_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);

        btn_select = findViewById(R.id.btn_select);
        btn_register = findViewById(R.id.btn_register);
        btn_cancel = findViewById(R.id.btn_cancel);

        color_view = findViewById(R.id.color_view);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }); //btn_cancel

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(false)
                        .show(RegisterActivity.this);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = et_id.getText().toString().trim();
                String pw = et_pw.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String name = et_name.getText().toString().trim();
                String color;
                if (color_value != null) {
                    color = color_value.trim();
                }
                else
                {
                    color = "FF000000";
                }

                String result ="id=" + id + "&pw=" + pw + "&email=" + email + "&name=" + name + "&color=" + color;
                Log.i("MY", result);

                new RegiTask().execute(result, Util.TYPE_REGI);

            }
        });

    }//onCreate()

    @Override
    public void onColorSelected(int dialogId, int color) {
        color_view.setColor(color);
        color_value = Integer.toHexString(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    class RegiTask extends AsyncTask<String, Void, String>
    {
        String svr = Util.SVR;
        String sendMsg, receiveMsg;
        String svrAddr = Util.SVR_ADDR;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str = "";
                URL url = new URL(svrAddr);

                HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = strings[0] + "&type=" + strings[1];

                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK)
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

                    if(result.equals("success"))
                    {
                        receiveMsg = "success";
                    }
                    else
                    {
                        receiveMsg = "failed";
                    }
                }
            }
            catch(Exception e)
            {

            }

            Log.i("MY", receiveMsg);
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success"))
            {
                String id = et_id.getText().toString().trim();
                String pw = et_pw.getText().toString().trim();
                String name = et_name.getText().toString().trim();
                Log.i("MY", id+"/"+pw+"/"+name);

                Toast.makeText(getApplicationContext(), "Welcome, " + name, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.putExtra("id", id);
                i.putExtra("pw", pw);
                startActivity(i);
                finish();
            }
        }
    }
}