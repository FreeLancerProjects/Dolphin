package com.appzone.dolphin.service;

import com.appzone.dolphin.Models.ClientNotificationModel;
import com.appzone.dolphin.Models.ClientOrderModel;
import com.appzone.dolphin.Models.Country_City_Model;
import com.appzone.dolphin.Models.NotificationCountModel;
import com.appzone.dolphin.Models.BillModel;
import com.appzone.dolphin.Models.OrderStateModel;
import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.Models.TechnicalNotificationModel;
import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.Models.TermsModel;
import com.appzone.dolphin.Models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Service {

    @GET("Api/Services")
    Call<List<ServiceModel>> getAllServices();

    @GET("AboutApp/TermsAndConditions")
    Call<TermsModel> getTerms_Condition();

    @FormUrlEncoded
    @POST("AboutApp/ContactUs")
    Call<ResponseModel> sendContactUs(@Field("name") String name,
                                      @Field("phone") String phone,
                                      @Field("message") String message
                                      );
    @GET("AboutApp/Countries")
    Call<List<Country_City_Model>> getCountry_City();

    @FormUrlEncoded
    @POST("AppUser/Login")
    Call<UserModel> signIn(@Field("user_phone") String user_phone,
                           @Field("user_pass") String user_pass
                           );

    @FormUrlEncoded
    @POST("AppUser/SignUp")
    Call<UserModel> memberSignUp(@Field("user_pass")String user_pass,
                                 @Field("user_phone")String user_phone,
                                 @Field("user_country")String user_country,
                                 @Field("user_email")String user_email,
                                 @Field("user_full_name")String user_full_name,
                                 @Field("user_token_id")String user_token_id,
                                 @Field("user_google_lat")double user_google_lat,
                                 @Field("user_google_long")double user_google_long,
                                 @Field("user_city")String user_city,
                                 @Field("user_address")String user_address,
                                 @Field("user_type")String user_type
                                 );

    @Multipart
    @POST("AppUser/SignUp")
    Call<UserModel> technicalSignUp(@Part("user_pass") RequestBody user_pass,
                                    @Part("user_phone")RequestBody user_phone,
                                    @Part("user_country")RequestBody user_country,
                                    @Part("user_email")RequestBody user_email,
                                    @Part("user_full_name")RequestBody user_full_name,
                                    @Part("user_token_id")RequestBody user_token_id,
                                    @Part("user_google_lat")RequestBody user_google_lat,
                                    @Part("user_google_long")RequestBody user_google_long,
                                    @Part("user_city")RequestBody user_city,
                                    @Part("user_address")RequestBody user_address,
                                    @Part("user_specialization_id_fk")RequestBody user_specialization_id_fk,
                                    @Part("user_type")RequestBody user_type,
                                    @Part MultipartBody.Part user_photo
                                    );

    @FormUrlEncoded
    @POST("AppUser/UpdateLocation/{user_id}")
    Call<ResponseModel> updateLocation(@Path("user_id")String user_id,
                                       @Field("user_google_lat") double user_google_lat,
                                       @Field("user_google_long") double user_google_long
                                       );

    @FormUrlEncoded
    @POST("AppUser/UpdateTokenId/{user_id}")
    Call<ResponseModel> updateToken(@Path("user_id")String user_id,
                                    @Field("user_token_id") String user_token_id
                                    );


    @GET("AppUser/Logout/{user_id}")
    Call<ResponseModel> logout(@Path("user_id") String user_id);

    @GET("ApiClient/MyNotifications/{user_id}")
    Call<List<ClientNotificationModel>> getClientNotifications(@Path("user_id") String user_id);

    @GET("ApiClient/ReadAlerts/{user_id}")
    Call<NotificationCountModel> getClientUnReadNotificationCount(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("ApiClient/ReadAlerts/{user_id}")
    Call<ResponseModel> readClientNotification(@Path("user_id") String user_id,
                                                        @Field("read_all") String read_all
                                                  );

    @GET("ApiTechnical/AlertCount/{user_id}")
    Call<NotificationCountModel> getTechnicalUnReadNotificationCount(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("ApiTechnical/AlertCount/{user_id}")
    Call<ResponseModel> readTechnicalNotification(@Path("user_id") String user_id,
                                                  @Field("read_all") String read_all
    );

    @FormUrlEncoded
    @POST("ApiClient/TechnicalEvaluation/{user_id}/{order_id}")
    Call<ResponseModel> evaluateTechnical(@Path("user_id") String user_id,
                                          @Path("order_id") String order_id,
                                          @Field("evaluation_rate") String evaluation_rate,
                                          @Field("evaluation_note") String evaluation_note
                                          );

    @FormUrlEncoded
    @POST("ApiClient/OfficeEvaluation/{user_id}/{order_id}")
    Call<ResponseModel> evaluateQualityOfficer(@Path("user_id") String user_id,
                                          @Path("order_id") String order_id,
                                          @Field("evaluation_rate") String evaluation_rate
    );

    @FormUrlEncoded
    @POST("ApiClient/Confirm/{user_id}/{order_id}")
    Call<ResponseModel> clientConfirmOrder(@Path("user_id") String user_id,
                                           @Path("order_id") String order_id,
                                           @Field("order_date") String order_date,
                                           @Field("confirm_type") String confirm_type
                                           );

    @GET("ApiClient/MyOrders/{user_id}")
    Call<List<ClientOrderModel>> getClientCurrentOrder(@Path("user_id") String user_id);

    @GET("ApiClient/NewOrders/{user_id}")
    Call<List<ClientOrderModel>> getClientNewOrder(@Path("user_id") String user_id);

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book(@Path("user_id") String user_id,
                                               @Path("id_services") String id_services,
                                               @Part("order_type") RequestBody order_type,
                                               @Part("order_hours") RequestBody order_hours,
                                               @Part("order_details") RequestBody order_details,
                                               @Part("order_date") RequestBody order_date,
                                               @Part("order_address") RequestBody order_address,
                                               @Part("order_address_lat") RequestBody order_address_lat,
                                               @Part("order_address_long") RequestBody order_address_long
    );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_image_sound_video(@Path("user_id") String user_id,
                                               @Path("id_services") String id_services,
                                               @Part("order_type") RequestBody order_type,
                                               @Part("order_hours") RequestBody order_hours,
                                               @Part("order_details") RequestBody order_details,
                                               @Part("order_date") RequestBody order_date,
                                               @Part("order_address") RequestBody order_address,
                                               @Part("order_address_lat") RequestBody order_address_lat,
                                               @Part("order_address_long") RequestBody order_address_long,
                                               @Part MultipartBody.Part order_image,
                                               @Part MultipartBody.Part order_voice,
                                               @Part MultipartBody.Part order_video
                             );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_image(@Path("user_id") String user_id,
                                               @Path("id_services") String id_services,
                                               @Part("order_type") RequestBody order_type,
                                               @Part("order_hours") RequestBody order_hours,
                                               @Part("order_details") RequestBody order_details,
                                               @Part("order_date") RequestBody order_date,
                                               @Part("order_address") RequestBody order_address,
                                               @Part("order_address_lat") RequestBody order_address_lat,
                                               @Part("order_address_long") RequestBody order_address_long,
                                               @Part MultipartBody.Part order_image
    );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_sound(@Path("user_id") String user_id,
                                   @Path("id_services") String id_services,
                                   @Part("order_type") RequestBody order_type,
                                   @Part("order_hours") RequestBody order_hours,
                                   @Part("order_details") RequestBody order_details,
                                   @Part("order_date") RequestBody order_date,
                                   @Part("order_address") RequestBody order_address,
                                   @Part("order_address_lat") RequestBody order_address_lat,
                                   @Part("order_address_long") RequestBody order_address_long,
                                   @Part MultipartBody.Part order_voice
    );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_video(@Path("user_id") String user_id,
                                   @Path("id_services") String id_services,
                                   @Part("order_type") RequestBody order_type,
                                   @Part("order_hours") RequestBody order_hours,
                                   @Part("order_details") RequestBody order_details,
                                   @Part("order_date") RequestBody order_date,
                                   @Part("order_address") RequestBody order_address,
                                   @Part("order_address_lat") RequestBody order_address_lat,
                                   @Part("order_address_long") RequestBody order_address_long,
                                   @Part MultipartBody.Part order_video
    );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_image_sound(@Path("user_id") String user_id,
                                         @Path("id_services") String id_services,
                                         @Part("order_type") RequestBody order_type,
                                         @Part("order_hours") RequestBody order_hours,
                                         @Part("order_details") RequestBody order_details,
                                         @Part("order_date") RequestBody order_date,
                                         @Part("order_address") RequestBody order_address,
                                         @Part("order_address_lat") RequestBody order_address_lat,
                                         @Part("order_address_long") RequestBody order_address_long,
                                         @Part MultipartBody.Part order_image,
                                         @Part MultipartBody.Part order_voice
                                         );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_image_video(@Path("user_id") String user_id,
                                         @Path("id_services") String id_services,
                                         @Part("order_type") RequestBody order_type,
                                         @Part("order_hours") RequestBody order_hours,
                                         @Part("order_details") RequestBody order_details,
                                         @Part("order_date") RequestBody order_date,
                                         @Part("order_address") RequestBody order_address,
                                         @Part("order_address_lat") RequestBody order_address_lat,
                                         @Part("order_address_long") RequestBody order_address_long,
                                         @Part MultipartBody.Part order_image,
                                         @Part MultipartBody.Part order_video
    );

    @Multipart
    @POST("ApiClient/Resevation/{user_id}/{id_services}")
    Call<ResponseModel> book_video_sound(@Path("user_id") String user_id,
                                         @Path("id_services") String id_services,
                                         @Part("order_type") RequestBody order_type,
                                         @Part("order_hours") RequestBody order_hours,
                                         @Part("order_details") RequestBody order_details,
                                         @Part("order_date") RequestBody order_date,
                                         @Part("order_address") RequestBody order_address,
                                         @Part("order_address_lat") RequestBody order_address_lat,
                                         @Part("order_address_long") RequestBody order_address_long,
                                         @Part MultipartBody.Part order_video,
                                         @Part MultipartBody.Part order_voice

    );

    @GET("ApiTechnical/Notifications/{user_id}")
    Call<List<TechnicalNotificationModel>> getTechnicalNotification(@Path("user_id")String user_id);

    @GET("ApiTechnical/MyNewOrders/{user_id}")
    Call<List<TechnicalOrderModel>> getTechnicalNewOrder(@Path("user_id") String user_id);

    @GET("ApiTechnical/MyCurrentOrders/{user_id}")
    Call<List<TechnicalOrderModel>> getTechnicalCurrentOrder(@Path("user_id") String user_id);

    @GET("ApiTechnical/MyOldOrders/{user_id}")
    Call<List<TechnicalOrderModel>> getTechnicalPreviousOrder(@Path("user_id") String user_id);

    @FormUrlEncoded
    @POST("ApiTechnical/MyMovement/{user_id}")
    Call<ResponseModel> startWork(@Path("user_id") String user_id,
                                  @Field("movement_type")String movement_type,
                                  @Field("technical_google_long")double technical_google_long,
                                  @Field("technical_google_lat")double technical_google_lat

                                  );

    @FormUrlEncoded
    @POST("ApiTechnical/Payment/{user_id}/{order_id}")
    Call<ResponseModel> payment(@Path("user_id") String user_id,
                                @Path("order_id") String order_id,
                                @Field("client_id_fk")String client_id_fk,
                                @Field("work_hours")String work_hours,
                                @Field("hour_cost")String hour_cost,
                                @Field("transfer_cost")String transfer_cost,
                                @Field("total_cost")String total_cost,
                                @Field("discount")String discount,
                                @Field("payment_method")String payment_method

                                );

    @GET("ApiTechnical/OrderBill/{user_id}/{order_id}")
    Call<List<BillModel>> getBill(@Path("user_id") String user_id,
                            @Path("order_id") String order_id
                            );

    @GET("ApiTechnical/OrderState/{user_id}/{order_id}")
    Call<OrderStateModel> getOrderState(@Path("user_id") String user_id,
                                  @Path("order_id") String order_id
    );

    @FormUrlEncoded
    @POST("ApiTechnical/MyMovement/{user_id}")
    Call<ResponseModel> arrival(@Path("user_id") String user_id,
                                @Field("movement_type") String movement_type,
                                @Field("technical_google_long") String technical_google_long,
                                @Field("technical_google_lat") String technical_google_lat,
                                @Field("order_id_fk") String order_id_fk,
                                @Field("client_id_fk") String client_id_fk

                                );

    @Multipart
    @POST("AppUser/Profile/{user_id}")
    Call<UserModel> UpdateProfileImage(@Path("user_id") String user_id,
                                       @Part("user_phone") RequestBody user_phone,
                                       @Part("user_country") RequestBody user_country,
                                       @Part("user_email") RequestBody user_email,
                                       @Part("user_full_name") RequestBody user_full_name,
                                       @Part("user_city") RequestBody user_city,
                                       @Part("user_address") RequestBody user_address,
                                       @Part MultipartBody.Part user_photo
    );

    @Multipart
    @POST("AppUser/Profile/{user_id}")
    Call<UserModel> UpdateProfileData(@Path("user_id") String user_id,
                                      @Part("user_phone") RequestBody user_phone,
                                      @Part("user_country") RequestBody user_country,
                                      @Part("user_email") RequestBody user_email,
                                      @Part("user_full_name") RequestBody user_full_name,
                                      @Part("user_city") RequestBody user_city,
                                      @Part("user_address") RequestBody user_address
    );


    @FormUrlEncoded
    @POST("AppUser/UpdatePass/{user_id}")
    Call<UserModel> UpdatePassword(@Path("user_id") String user_id,
                                   @Field("user_old_pass") String user_old_pass,
                                   @Field("user_new_pass") String user_new_pass
    );


}
