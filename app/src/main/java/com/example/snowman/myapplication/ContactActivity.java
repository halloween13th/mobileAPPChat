package com.example.snowman.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    Button addButton;
    ListView myListView;
    HashMap<String,String>hashMap;
    String session;
    String username;
    JSONObject jsonObject;
    String  text;
    JSONArray jsonArray;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        init();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_contact);
        init();
    }

    private void init(){
        hashMap = new HashMap<>();
        arrayList = new ArrayList<String>();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        session = pref.getString("session",null);
        username = pref.getString("username",null);

        addButton = (Button)findViewById(R.id.addButton);
        myListView = (ListView)findViewById(R.id.myListView);

        Intent intent = getIntent();
       // session = intent.getStringExtra("session");
        //username = intent.getStringExtra("username");
        hashMap.put("sessionid",session);

        Retriver retriver = new Retriver();
        retriver.execute();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this,AddActivity.class);
                intent.putExtra("session",session);
                intent.putExtra("username",username);
                startActivity(intent);

            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactActivity.this,ChatActivity.class);
                intent.putExtra("session",session);
                intent.putExtra("username",username);
                intent.putExtra("friend",arrayList.get(position));
                startActivity(intent);
            }
        });

    }

    private class Retriver extends AsyncTask<Void,Void ,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpHelper httpHelper = new OkHttpHelper();
            try {
                text = httpHelper.post("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/getContact",hashMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("bbb",text);
            try {
                jsonObject = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                jsonArray = jsonObject.getJSONArray("content");
                for(int i =  0 ; i < jsonArray.length() ;i ++){
                    Log.d("aaa",jsonArray.getString(i));
                    arrayList.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this,android.R.layout.simple_list_item_1,arrayList);
            myListView.setAdapter(adapter);
        }
    }
}
