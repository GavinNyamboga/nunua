package com.dev.nunua.Services;

import com.dev.nunua.Model.AccessToken;
import com.dev.nunua.Model.STKPush;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface STKPushService {

    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPush>sendPush(@Body STKPush stkPush);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();
    @GET("payment status")
    Call<ResponseBody> checkPaymentStatus();
}
