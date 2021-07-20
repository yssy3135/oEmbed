package com.ys.oembed.service;


import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Service
public class oembed {

    private static final String GET_URL = "https://oembed.com/providers.json";
    private static ArrayList<String> providerList = null;

    /**
     * provider주소에 접근하여
     * provider url 리스트를 생성
     * @throws IOException
     */
    public void getProvider() throws IOException {
        providerList = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(GET_URL);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        String res = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");


        JSONArray json = new JSONArray(res);

        for(int i = 0 ; i < json.length();i++ ){
            JSONObject getData = (JSONObject) json.get(i);

            String endpoints = getData.get("endpoints").toString();

            JSONArray json2 = new JSONArray(endpoints);
            JSONObject endInfo = (JSONObject) json2.get(0);

            providerList.add(endInfo.get("url").toString());
        }
        httpClient.close();
        return ;
    }

    /**
     * 입력된 url 에서 비교할 주소를 뽑는다.
     * @param url
     * @return
     */
    public static String getName(String url){

        //http://가 붙어있을 경우
        String str[] = url.split("://")[1].split("\\.");

        if(str.length == 3){
            return str[1];
        }else {
            return str[0];
        }
    }


    /**
     * provider 주소와 요청된 url주소를 합해서
     * oEmbed 정보 요청 url주소 생성
     * @param url
     * @return
     * @throws IOException
     */
    public String makeRequestURL(String url) throws IOException {
        String name = getName(url);
        getProvider();

        String providerURL = "";

        for(String str : providerList){
            if(str.contains(name)){

                if(str.contains("{format}")){
                    str = str.replace("{format}","json");
                }
                providerURL = str;
                break;
            }

        }

        String reqURL = providerURL+"?url="+url+"&format=json";
        return reqURL;
    }

    /**
     * 만들어진 oEmbed 요청url을 통해
     * oEmbed 정보를 가져와 반환할 정보 API 세팅
     * @param url
     * @return
     * @throws IOException
     */
    public Map getOembedInfo(String url) throws IOException {
        String reqURL = makeRequestURL(url);


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(reqURL);

        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        String res = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");


        JSONObject jsonRes = new JSONObject(res);

        Map<String,String> oembedInfoMap = new HashMap<>();
        Iterator<String> iter = jsonRes.keys();

        while(iter.hasNext()){
            String key = iter.next();
            oembedInfoMap.put(key, jsonRes.get(key).toString());
        }

        return oembedInfoMap;
    }




}
