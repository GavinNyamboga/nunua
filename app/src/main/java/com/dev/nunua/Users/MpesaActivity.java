package com.dev.nunua.Users;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.Model.AccessToken;
import com.dev.nunua.Model.STKPush;
import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.dev.nunua.Services.ApiClient;
import com.dev.nunua.Util.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.dev.nunua.Util.Constants.BUSINESS_SHORT_CODE;
import static com.dev.nunua.Util.Constants.CALLBACKURL;
import static com.dev.nunua.Util.Constants.PARTYB;
import static com.dev.nunua.Util.Constants.PASSKEY;
import static com.dev.nunua.Util.Constants.TRANSACTION_TYPE;

public class MpesaActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.etAmount)
    EditText mAmount;
    @BindView(R.id.etPhone)
    EditText mPhone;
    @BindView(R.id.btnPay)
    Button mPay;
    @BindView(R.id.payable_txt)
    TextView payableAmount;
    private ApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private SweetAlertDialog pDialog;
    private String totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);
        ButterKnife.bind(this);

        totalAmount = getIntent().getStringExtra("Amount payable");

        mProgressDialog = new ProgressDialog(this);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.


        payableAmount.setText("Amount Payable: ksh " + totalAmount);


        mAmount.setText(totalAmount);
        mAmount.setVisibility(View.GONE);


        mPay.setOnClickListener(this);

        getAccessToken();
    }


    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }


            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mPay) {
            String phone_number = mPhone.getText().toString();
            String amount = mAmount.getText().toString();
            performSTKPush(phone_number, amount);
        }
    }

    public void performSTKPush(String phone_number, String amount) {

        pDialog.setTitleText("Processing your request")
                .setContentText("please wait...")
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();

        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "test", //Account reference
                "test"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);


        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {

                pDialog.dismiss();


                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());

                        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                                .child("Orders")
                                .child(Prevalent.currentOnlineUsers.getPhone());
                        HashMap<String, Object> ordersMap = new HashMap<>();
                        ordersMap.put("payment", "paid by Mpesa");

                        ordersRef.updateChildren(ordersMap);

                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {

                pDialog.dismiss();
                Timber.e(t);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
