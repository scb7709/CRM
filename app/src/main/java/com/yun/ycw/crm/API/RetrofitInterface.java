package com.yun.ycw.crm.API;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.Goods;
import retrofit.Callback;
import retrofit.http.*;
import java.util.List;
import java.util.Map;
/**
 * Created by wdyan on 2016/2/17.
 */
public interface RetrofitInterface {
    //直接访问一个post地址，使用Body传递参数,
    //需要注意的是，使用这种方式来传递参数的话，需要自定义Converter，具体请参考StringConverter的toBody方法
    @POST("/crm/login")
    void callPostParamsBody(@Body Customer customer, Callback<String> call);

    //直接访问一个post地址，使用FieldMap传递参数
    @POST("/crm/login")
    @FormUrlEncoded
    void callPostParamsMap(@FieldMap Map<String, String> params, Callback<String> call);

    //直接访问一个post地址，使用FieldMap传递参数
    @POST("/crm/customer")
    @FormUrlEncoded
    void addCustomer(@FieldMap Map<String, Object> params, Callback<String> call);

    //更新客户信息
    @PATCH("/crm/customer/{customer_id}")
    @FormUrlEncoded
    void updateCustomer(@Path("customer_id") String customer_id, @FieldMap Map<String, String> params, Callback<String> call);

    //转移客户
    @PATCH("/crm/customer/{customer_id}/transfer")
    @FormUrlEncoded
    void transferCustomer(@Path("customer_id") String customer_id, @FieldMap Map<String, String> params, Callback<String> call);

    //删除客户信息
    @DELETE("/crm/customer/{customer_id}")
    void deleteCustomer(@Path("customer_id") String customer_id, Callback<String> call);

    //放弃客户，扔到公海池
    @PATCH("/crm/customer/{customer_id}")
    @FormUrlEncoded
    void giveUpCustomer(@Path("customer_id") String customer_id, @FieldMap Map<String, String> params,Callback<String> call);

    //同意激活申请
    @PATCH("/crm/staff-apply/{id}")
    @FormUrlEncoded
    void agreeactiviteCustomer(@Path("id") String id, @FieldMap Map<String, String> params,Callback<String> call);


    //申请激活客户
    @POST("/crm/staff-apply")
    @FormUrlEncoded
    void activiteCustomer(@FieldMap Map<String, String> params, Callback<String> call);


    @GET("/crm/sys/version")
    void getCrmVersion(Callback<String> call);

    @GET("/crm/staff/{userid}/sub-list")
    void getTeam(@Path("userid") String userid, Callback<String> call);


    @GET("/crm/order/{orderid}")
    List<Goods> getOrderGoods(@Path("orderid") String orderid, Callback<String> call);

    //修改device_token
    @PATCH("/crm/staff/{sell_id}")
    @FormUrlEncoded
    void deleteDevice_token(@Path("sell_id") String sell_id, @FieldMap Map<String, String> params, Callback<String> call);

    //增加计划
    @POST("/crm/staff-plan")
    @FormUrlEncoded
    void addSchedule(@FieldMap Map<String, Object> params, Callback<String> call);


}
