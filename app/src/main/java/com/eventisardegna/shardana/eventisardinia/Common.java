package com.eventisardegna.shardana.eventisardinia;

import com.eventisardegna.shardana.eventisardinia.Remote.APIService;
import com.eventisardegna.shardana.eventisardinia.Remote.RetrofitClient;

public class Common {
    public static String currentToken = "";

    private static String baseUrl = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }
}
