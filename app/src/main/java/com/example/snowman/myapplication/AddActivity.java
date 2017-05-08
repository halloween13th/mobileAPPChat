package com.example.snowman.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {
    EditText input_searchname;
    Button search_btn;
    ListView listView;
    String text,session,username;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ArrayList<String> arrayList  = new ArrayList<String>();
    HashMap<String,String> hashMap_search = new HashMap<>();
    HashMap<String,String> hashMap_add = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
    }
    private void init(){
        input_searchname = (EditText)findViewById(R.id.input_search);
        search_btn = (Button) findViewById(R.id.search_btn);
        listView = (ListView)findViewById(R.id.tv);

        Intent intent = getIntent();
        session = intent.getStringExtra("session");
        username = intent.getStringExtra("username");

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList = new ArrayList<String>();
                hashMap_search.put("sessionid",session);
                hashMap_search.put("keyword",input_searchname.getText().toString());
                Retriver_search retriver_search = new Retriver_search();
                retriver_search.execute();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retriver_add retriver_add = new Retriver_add();
                hashMap_add.put("sessionid",session);
                hashMap_add.put("username",arrayList.get(position).toString());
                retriver_add.execute();
            }
        });

    }

    private class Retriver_search extends AsyncTask<Void,Void ,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HTTPHelper httpHelper = new HTTPHelper();
            text = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/searchUser",hashMap_search);
            //Log.d("bbb",text);
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
                    //Log.d("aaa",jsonArray.getString(i));
                    arrayList.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddActivity.this,android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(adapter);

        }
    }
    private class Retriver_add extends AsyncTask<Void,Void ,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HTTPHelper httpHelper = new HTTPHelper();
            text = httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/addContact",hashMap_add);

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
            Toast.makeText(getBaseContext(),"Added Success",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddActivity.this,ContactActivity.class);
            intent.putExtra("session",session);
            intent.putExtra("username",username);
            startActivity(intent);


        }
    }
}
