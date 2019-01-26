package com.example.dell.mapa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.StrictMode;


public class ListActivity extends AppCompatActivity  {
    EditText productText;
    CheckedTextView lipa;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    LinearLayout menu_photos;

@Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    setContentView(R.layout.activity_list);

        new ParseTask().execute();

    Button refButton = (Button) findViewById(R.id.refbutton);
    refButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ParseTask().execute();

        }
    });

    productText = (EditText) findViewById(R.id.addText);
    Button addButton = (Button) findViewById(R.id.addbutton);
    addButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String productname = productText.getText().toString();
             String  url = "http://10.7.71.228:5002/list/" + productname;
               connect(url);
           }
       });

}

    private void connect(String url){
         String sendURL = null;
        DownloadUrl downloadURL = new DownloadUrl();
        try {
            sendURL = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {

            try {
                String $url_json = "http://10.7.71.228:5002/list";
                URL url = new URL($url_json);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                Log.d("FOR_LOG", resultJson);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            final ListView lView = (ListView) findViewById(R.id.lvMain);

            String[] from = {"name_item"};
            int[] to = {R.id.name_item};
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashmap;

            try {
                JSONObject json = new JSONObject(strJson);
                JSONArray jArray = json.getJSONArray("list");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject friend = jArray.getJSONObject(i);
                    String nameOS = friend.getString("product");
                    Log.d("FOR_LOG", nameOS);

                    hashmap = new HashMap<String, String>();
                    hashmap.put("name_item", "" + nameOS);
                    arrayList.add(hashmap);
                }

                final SimpleAdapter adapter = new SimpleAdapter(ListActivity.this, arrayList, R.layout.item, from, to);
               lView.setAdapter(adapter);
                lView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position,long id) {
                        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.name_item);
                        boolean checked = checkedTextView.isChecked();
                            String s;
                        if (checked) s = "true";
                        else s = "false";
                        if (checked) {
                            checkedTextView.setChecked(false);
                            Log.d("OFF", s);
                            System.out.println(
                                    checkedTextView.getText()+":"+
                                            !checkedTextView.isChecked() //because the onItemClick fired before checkedTextView change its state
                            );
                        } else {
                            checkedTextView.setChecked(true);
                            Log.d("ON", s);
                            System.out.println(
                                    checkedTextView.getText()+":"+
                                            !checkedTextView.isChecked() //because the onItemClick fired before checkedTextView change its state


                            );
                                String url = "http://10.7.71.228:5002/list_cut/" + checkedTextView.getText();
                            connect(url);
                            Log.d("SEND DELETE URL: ", url);

                        }
return false;
                    }
                });




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}