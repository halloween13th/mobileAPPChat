package com.example.snowman.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editText_username,editText_password;
    Button bt_signin;
    String  text;
    JSONObject jsonObject;
    HashMap<String,String> hashMap = new HashMap<>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if(pref.getString("username",null) != null){
            startActivity(new Intent(MainActivity.this,ContactActivity.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_main);
        init();
        editor.clear();
        editor.commit();
    }

    private void init(){

        pref = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = pref.edit();

        editText_password = (EditText)findViewById(R.id.edit_password);
        editText_username = (EditText)findViewById(R.id.edit_username);
        bt_signin = (Button)findViewById(R.id.button);
        bt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMap.put("username",editText_username.getText().toString());
                hashMap.put("password",editText_password.getText().toString());

                Retriver retriver = new Retriver();
                retriver.execute();
            }
        });
    }

    private class Retriver extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpHelper httpHelper = new OkHttpHelper();
            try {
                text = httpHelper.post("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/signIn",hashMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsonObject =  new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if(jsonObject.getString("type").equalsIgnoreCase("error")){
                    Toast.makeText(getBaseContext(),"Username/Password is invalid",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this,ContactActivity.class);
                    //String session = jsonObject.getString("content");
                    //intent.putExtra("session",session);
                   //intent.putExtra("username",editText_username.getText().toString());

                    editor.putString("session", jsonObject.getString("content"));
                    editor.putString("username",editText_username.getText().toString());
                    editor.commit();

                    startActivity(intent);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
