package com.a83idea.cobaltconnect.api;


import com.a83idea.cobaltconnect.generated.model.ForgotPassword;
import com.a83idea.cobaltconnect.generated.model.ForgotPasswordResponse;
import com.a83idea.cobaltconnect.generated.model.MarginUpdate;
import com.a83idea.cobaltconnect.generated.model.MarginUpdateAll;
import com.a83idea.cobaltconnect.generated.model.MarginUpdateAllResponse;
import com.a83idea.cobaltconnect.generated.model.MarginUpdateResponse;
import com.a83idea.cobaltconnect.generated.model.MyCloverProduct;
import com.a83idea.cobaltconnect.generated.model.MyCloverProductResponse;
import com.a83idea.cobaltconnect.generated.model.ProductUpdate;
import com.a83idea.cobaltconnect.generated.model.ProductUpdateResponse;
import com.a83idea.cobaltconnect.generated.model.Profile;
import com.a83idea.cobaltconnect.generated.model.ProfileResponse;
import com.a83idea.cobaltconnect.generated.model.Registration;
import com.a83idea.cobaltconnect.generated.model.RegistrationResponse;
import com.a83idea.cobaltconnect.generated.model.SignIn;
import com.a83idea.cobaltconnect.generated.model.SignInResponse;
import com.a83idea.cobaltconnect.generated.model.StateList;
import com.a83idea.cobaltconnect.generated.model.StateListResponse;
import com.a83idea.cobaltconnect.generated.model.UpdateProfile;
import com.a83idea.cobaltconnect.generated.model.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RetrofitInterface {

    public interface MerchantLoginClient {

        @POST("api/")
        Call<SignInResponse> merchantSignIn(@Body SignIn signinDetails);

    }

    public interface MerchantRegisterClient {

        @POST("api/")
        Call<RegistrationResponse> merchantSignUp(@Body Registration registrationDetails);

    }

    public interface MerchantForgotPasswordClient {

        @POST("api/")
        Call<ForgotPasswordResponse> merchantForgotPassword(@Body ForgotPassword forgotPasswordDetails);

    }

    public interface MerchantStatesClient {

        @POST("api/")
        Call<StateListResponse> merchantState(@Body StateList stateList);

    }

    public interface MerchantProfileClient {

        @POST("api/")
        Call<ProfileResponse> merchantProfile(@Body Profile profile);

    }

    public interface MerchantUpdateProfileClient {

        @POST("api/")
        Call<UpdateProfileResponse> merchantUpdateProfile(@Body UpdateProfile updateProfile);

    }

    public interface MerchantUpdateProductClient {

        @POST("api/")
        Call<ProductUpdateResponse> merchantUpdateProduct(@Body ProductUpdate productUpdate);

    }

    public interface MerchantMyProductClient {

        @POST("api/")
        Call<MyCloverProductResponse> merchantMyProduct(@Body MyCloverProduct myCloverProduct);

    }

    public interface MerchantMarginUpdateClient {

        @POST("api/")
        Call<MarginUpdateResponse> merchantMarginUpdate(@Body MarginUpdate marginUpdate);

    }

    public interface MerchantUpdateToCloverClient {

        @POST("api/")
        Call<MarginUpdateAllResponse> merchantUpdateToClover(@Body MarginUpdateAll marginUpdate);

    }

    public interface MerchantFetchFromCloverClient {

        @POST("api/")
        Call<MarginUpdateAllResponse> merchantFetchFromClover(@Body MarginUpdateAll marginUpdate);

    }
}
