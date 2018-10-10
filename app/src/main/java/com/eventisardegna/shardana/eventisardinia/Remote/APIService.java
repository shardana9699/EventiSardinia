package com.eventisardegna.shardana.eventisardinia.Remote;

import com.eventisardegna.shardana.eventisardinia.Model.MyResponse;
import com.eventisardegna.shardana.eventisardinia.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {"Content-Type:application/json",
            "Authorization:key=AAAA7qQbUAM:APA91bGQjd6mZbi_6IjjnDZaFUztbffdnw6qeShEVbmcx98gm-j7yTB1VibKJM7O_o96C1m_Fjy4F2Xz7_947Dd3jAGtaOqyZSUJXpBY5Ss2KpB__mrKFvRDc9uZHLn_j_qeRnKWWY4y"}
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
