package com.okmart.app.interfaces;

import com.okmart.app.model.ChargeModel;
import com.okmart.app.model.CreateCustomerCardModel;
import com.okmart.app.model.CreateCustomerModel;
import com.okmart.app.model.DeleteCard;
import com.okmart.app.model.FetchCardListModel;
import com.okmart.app.utilities.Constants;

import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST(Constants.customers)
    Call<CreateCustomerModel> attemptToCreateCustomer(@Header("Authorization") String auth, @FieldMap WeakHashMap<String, String> signUpDetails);

    @FormUrlEncoded
    @POST(Constants.customers+"/"+"{id}"+"/"+"sources")
    Call<CreateCustomerCardModel> attemptToCreateCard(@Header("Authorization") String auth, @Path("id") String id, @FieldMap WeakHashMap<String, String> signUpDetails);

    @FormUrlEncoded
    @POST(Constants.charges)
    Call<ChargeModel> attemptToCharge(@Header("Authorization") String auth, @FieldMap WeakHashMap<String, Object> signUpDetails);

    @GET(Constants.customers+"/"+"{cus_id}"+"/"+"sources?object=card&limit=10")
    Call<FetchCardListModel> attemptToFetchCards(@Header("Authorization") String auth, @Path("cus_id") String cusId);

    @DELETE(Constants.customers+"/"+"{cus_id}"+"/"+"sources"+"/"+"{card_id}")
    Call<DeleteCard> attemptToDelete(@Header("Authorization") String auth, @Path("cus_id") String cusId, @Path("card_id") String cardId);

}