package com.a83ideas.cobaltConnect.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a83ideas.cobaltConnect.R;
import com.a83ideas.cobaltConnect.api.ApiAdapter;
import com.a83ideas.cobaltConnect.api.RetrofitInterface;
import com.a83ideas.cobaltConnect.generated.model.ForgotPassword;
import com.a83ideas.cobaltConnect.generated.model.ForgotPasswordResponse;
import com.a83ideas.cobaltConnect.utils.NetworkUtils;
import com.a83ideas.cobaltConnect.utils.SnakBarUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.a83ideas.cobaltConnect.api.ApiEndPoints.BASE_URL;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {


    private RetrofitInterface.MerchantForgotPasswordClient ForgotPasswordAdapter;
    Button btSubmit;
    TextView tvBack;
    EditText etCobaltId;
    String cobaltId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btSubmit = (Button) findViewById(R.id.button_submit);
        tvBack = (TextView) findViewById(R.id.text_back);
        etCobaltId = (EditText) findViewById(R.id.enter_cobalt_id);
        btSubmit.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        setUpRestAdapter();
    }


    private void setUpRestAdapter() {
        ForgotPasswordAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MerchantForgotPasswordClient.class, BASE_URL, ForgotPasswordActivity.this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_submit) {

            cobaltId = etCobaltId.getText().toString();
            if (isForgotPasswordValid())
            getForgotPassword();
        }

        else  if (view.getId() == R.id.text_back)
        {
            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean isForgotPasswordValid() {

            if (cobaltId == null || cobaltId.equals(""))
            {
                etCobaltId.setError(getString(R.string.error_compulsory_field));

            return false;
        } else
            return true;
    }


    private void getForgotPassword() {



        Call<ForgotPasswordResponse> call = ForgotPasswordAdapter.merchantForgotPassword(new ForgotPassword(cobaltId,"forgotpassword"));
        if (NetworkUtils.isNetworkConnected(ForgotPasswordActivity.this)) {
            call.enqueue(new Callback<ForgotPasswordResponse>() {

                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {

                    if (response.isSuccessful()) {


                        if (response.body().getType() == 1) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Details",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {

                }


            });

        } else {
            SnakBarUtils.networkConnected(ForgotPasswordActivity.this);
        }
    }
}
