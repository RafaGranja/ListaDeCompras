package com.example.projetobase;

import android.content.ContentProviderOperation;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import java.io.*;
import okhttp3.*;

public class Conexao {

    public static String getApi(String _url){

        BufferedReader buffer = null;

        try{
            URL url = new URL(_url);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            StringBuilder stringBuilder = new StringBuilder();
            buffer = new BufferedReader(new InputStreamReader((http.getInputStream())));

            String Linha;

            while((Linha=buffer.readLine())!=null){
                stringBuilder.append(Linha+"\n");
            }

            return stringBuilder.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(buffer != null){
                try{
                    buffer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

