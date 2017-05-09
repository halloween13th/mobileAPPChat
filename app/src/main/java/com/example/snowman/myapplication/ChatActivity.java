package com.example.snowman.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    EditText messageEdit;
    ListView listView;
    String session;
    String username;
    String friend,sendMsg;
    String text;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ArrayList<String> arrayList;
    Button chatSendButton;
    HashMap<String,String> hashMap_getMsg;
    HashMap<String,String> hashMap_posMsg;
    JSONArray jsonArrayChatAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }



    private void init(){
        arrayList = new ArrayList<>();
        hashMap_getMsg = new HashMap<>();
        hashMap_posMsg = new HashMap<>();
        Intent intent = getIntent();
        session = intent.getStringExtra("session");
        username = intent.getStringExtra("username");
        friend = intent.getStringExtra("friend");
        jsonArray = new JSONArray();

        getSupportActionBar().setTitle(friend);
        messageEdit = (EditText)findViewById(R.id.msgEdit);
        listView = (ListView)findViewById(R.id.messagesContainer);
        chatSendButton = (Button)findViewById(R.id.chatSendButton);

        hashMap_getMsg.put("sessionid",session);
        hashMap_getMsg.put("seqno","0");
        hashMap_getMsg.put("limit","1000");

        Retriever_getMsg retriever_getMsg = new Retriever_getMsg();
        retriever_getMsg.execute();

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg = messageEdit.getText().toString();
                Retriever_posMsg retriever_posMsg = new Retriever_posMsg();
                retriever_posMsg.execute(sendMsg);

            }
        });


    }


    private class Retriever_getMsg extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpHelper httpHelper = new  OkHttpHelper();


            try {
                text = httpHelper.post("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/getMessage",hashMap_getMsg);
                jsonObject  = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                jsonArrayChatAll = jsonObject.getJSONArray("content");

                for(int i =  0 ; i < jsonArrayChatAll.length()  ;i ++){
                    jsonObject = jsonArrayChatAll.getJSONObject(i);
                    String seqno = jsonObject.getString("seqno");
                    String from = jsonObject.getString("from");
                    String to = jsonObject.getString("to");
                    String datetime = jsonObject.getString("datetime");
                    String message = jsonObject.getString("message");
                    if((from.equals(username)&& to.equals(friend) ) || (from.equals(friend) && to.equals(username))) {
                        jsonArray.put(jsonObject);
                        arrayList.add(from + " : " + message);

                    }
                    Log.d("jsonObject",jsonObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChatActivity.this,android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(adapter);
        }
    }

    private class Retriever_posMsg extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... params) {
            OkHttpHelper httpHelper = new OkHttpHelper();
            hashMap_posMsg.put("sessionid",session);
            hashMap_posMsg.put("targetname",friend);
            hashMap_posMsg.put("message",params[0]);
            try {
                text = httpHelper.post("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/postMessage",hashMap_posMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            init();

            try {
                jsonObject  = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messageEdit.setText(null);
        }
    }

}
