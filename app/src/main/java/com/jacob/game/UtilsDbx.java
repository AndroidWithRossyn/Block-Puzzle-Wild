package com.jacob.game;

import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.webkit.ValueCallback;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;


public class UtilsDbx{
    private HttpsURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private UtilsAwv mwebView = null;
    public boolean check_net = false;
    private URL url;
    private String token = null;
    public String user_id = null;
    public String user_name = "G"+"uest";
    public String user_email = "";
    public String avatar = "0";
    public String game_id = null;
    public String game_name = null;
    public String package_name = null;
    public String score = "0";
    public String level = "1";


    public interface Method {
        public void call(String val);
    }

    private void getItem(String item, Method func){
        /*getItem("user_id", val -> {
            if(val.equals("null")){
                val = String.valueOf(System.currentTimeMillis());
                setItem("user_id", val);
            }
            user_id = val;
        });*/

        /*
        setItem("game_id", game_id);*/
        mwebView.evaluateJavascript("(function() { return localStorage.getItem('\"+item+\"'); })();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                //Log.d("Jacob", s);
                func.call(s);
            }
        });
    }

    private void setItem(String item, String val){
        mwebView.evaluateJavascript("(function() { localStorage.setItem('"+item+"','"+val+"'); return 'OK';})();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                //Log.d("Jacob", s);
            }
        });
    }


    public void getUserInfos(Context context) {
        //to reduce permissions, we will not use this function now.

        /*
        Account[] accounts = AccountManager.get(context).getAccounts();
        Log.d("jacob_mlk", "call accounts ...................................");
        if (accounts.length > 0) {
            Log.d("jacob_mlk", "account exist................................");
            user_email = accounts[0].name;
            String[] parts = user_email.split("@");
            if (parts.length > 0 && parts[0] != null)
                user_name = parts[0];
            else
                user_name = "Guest";
        }
         */
    }


    protected void initData(UtilsAwv mwebView){
        this.mwebView = mwebView;
        String postData = null;
        try {
            postData = "leaderboard=200&game_id=" + URLEncoder.encode(game_id, "UTF-8") + "&user_id=" + URLEncoder.encode(user_id, "UTF-8");
            mwebView.postUrl(token,postData.getBytes());

        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
    }

    public void saveScore(String score, String level){
        //Log.d("Jacob_mlk", "save_score : "+score+" / "+level);
        if(!check_net){
            return;
        }

        this.score = score;
        this.level = level;

        try {
            url = new URL(token);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("savescore", "200")
                    .appendQueryParameter("user_id", user_id)
                    .appendQueryParameter("user_name", user_name)
                    .appendQueryParameter("user_email", user_email)
                    .appendQueryParameter("game_id", game_id)
                    .appendQueryParameter("game_name", game_name)
                    .appendQueryParameter("package_name", package_name)
                    .appendQueryParameter("level", level)
                    .appendQueryParameter("score", score);
            String query = builder.build().getEncodedQuery();
            //Log.d("Jacob_mlk", query);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            getResponse();
        } catch (IOException e) {
            Log.e("Jacob", "IO Exception", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Jacob", "Error closing stream", e);
                }
            }
        }
    }

    public void saveUser(String user_name, String avatar){
        //Log.d("Jacob_mlk", "save_user : "+score+" / "+level);
        if(!check_net){
            return;
        }

        this.user_name = user_name;
        this.avatar = avatar;

        try {
            url = new URL(token);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("saveuser", "200")
                    .appendQueryParameter("user_id", user_id)
                    .appendQueryParameter("user_name", user_name)
                    .appendQueryParameter("game_id", game_id)
                    .appendQueryParameter("avatar", avatar);
            String query = builder.build().getEncodedQuery();
            //Log.d("Jacob_mlk", query);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            getResponse();
        } catch (IOException e) {
            Log.e("Jacob", "IO Exception", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Jacob", "Error closing stream", e);
                }
            }
        }
    }

    public UtilsDbx(){
        token = new String(Base64.decode("aHR0cHM6Ly9pbnN0aXR1dGphY29iLmNvbS8", Base64.DEFAULT));
    }

    protected String test() {
        try {
            url = new URL(token);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            if (buffer.length() == 0)
                return null;

            //Log.d("Jacob", buffer.toString());
            /*
            JSONObject json = new JSONObject(response);
            JSONObject jsonResponse = json.getJSONObject("response");
            String team = jsonResponse.getString("Team");
             */
            return buffer.toString();
        } catch (IOException e) {
            Log.e("Jacob", "IO Exception", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Jacob", "Error closing stream", e);
                }
            }
        }
    }

    private String getResponse(){
        String response="";
        try {
            int responseCode= 0;
            responseCode = urlConnection.getResponseCode();

            //Log.d("jacob_mlk", urlConnection.getResponseMessage());

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response    += line +"\n";
                }
            }
            else {
                response="";

            }
            //Log.d("Jacob_mlk", ">>"+response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


}
