package com.fraglist;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Vpsingh on 1/3/2017.
 */
public class Dashboard extends android.support.v4.app.Fragment {
    ArrayList<HashMap<String, String>> imglist;
    ImageView listbanner;
    ImgListAdapter imgListAdapter;
    ListView ls;
    TextView tx;
    private Context context;
    private LayoutInflater inflater;


    /*public Dashboard(){}*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        imglist = new ArrayList<HashMap<String, String>>();
        ls = (ListView)getActivity().findViewById(R.id.ls);
        new ProductListAsyncTask().execute();
        return rootView;
    }

    public class ProductListAsyncTask extends AsyncTask<Void, Void, String> {
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressDialog(Dashboard.this);
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn;
            try {
                String url = "http://classifieds.mindzenmedia.com/classic_json/imgList.json";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                res = response.toString();
                System.out.println(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // dismissProgressDialog();
            try {
                HashMap<String, String> hashMap;
                JSONObject jsonObject = new JSONObject(s);
                String response = jsonObject.getString("status");

                if (response.equals("200")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        hashMap = new HashMap<String, String>();
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        hashMap.put("id", jsonObject2.getString("id"));
                        hashMap.put("img", jsonObject2.getString("img"));
                        imglist.add(hashMap);
                    }

                    if (imglist.size() > 0) {
                        imgListAdapter = new ImgListAdapter();
                        ls.setAdapter(imgListAdapter);
                    } else {
                      Toast.makeText(getActivity(), "Internet Connection not available!!! Please Verify your connection...", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public class ImgListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imglist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.img_list, null);
            }
            listbanner = (ImageView) convertView.findViewById(R.id.listbanner);

            if (imglist.get(position).get("img") != null) {
                if (!imglist.get(position).get("img").equals("")) {
                    Picasso.with(getActivity())
                            .load(imglist.get(position).get("img"))
                            .noFade().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(listbanner);
                }
            }
            return convertView;
        }

    }
}
