package com.yun.ycw.crm.utils;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yun.ycw.crm.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by scb on 2016/2/22.
 */
public class GoodsCatgoryUtils {

    public static void downCatgory(final Context context) {
        if (InternetUtils.internet(context)) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.GOODS_CATGORY, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    Log.i("2222222222222", s);
                    SharedUtils.putGoodsCatgory("GoodsCatgory", s, context);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("2222222222222", "失败");
                }
            });
        }

    }

    public static String getCatgory() {
   /*     String s = SharedUtils.getGoodsCatgory("GoodsCatgory", context);
        Log.i("333333", s);
        if (s.length() != 0) {
            return SharedUtils.getGoodsCatgory("GoodsCatgory", context);
        }
        else {*/
        String s = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"水工\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"电工\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"木工\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 4,\n" +
                "    \"name\": \"瓦工\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 5,\n" +
                "    \"name\": \"油工\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 43,\n" +
                "    \"name\": \"腻子石膏\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 7,\n" +
                "    \"name\": \"工具\",\n" +
                "    \"parentid\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 8,\n" +
                "    \"name\": \"PPR水管\",\n" +
                "    \"parentid\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 9,\n" +
                "    \"name\": \"PVC下水管\",\n" +
                "    \"parentid\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 12,\n" +
                "    \"name\": \"水工工具及耗材\",\n" +
                "    \"parentid\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 14,\n" +
                "    \"name\": \"PVC穿线管\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 15,\n" +
                "    \"name\": \"PVC线槽\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 16,\n" +
                "    \"name\": \"镀锌管\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 17,\n" +
                "    \"name\": \"电线\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 18,\n" +
                "    \"name\": \"网线\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 19,\n" +
                "    \"name\": \"电视线\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 20,\n" +
                "    \"name\": \"电话线\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 21,\n" +
                "    \"name\": \"配电箱及相关\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 23,\n" +
                "    \"name\": \"电工工具及耗材\",\n" +
                "    \"parentid\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 24,\n" +
                "    \"name\": \"石膏板\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 25,\n" +
                "    \"name\": \"轻钢龙骨\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 26,\n" +
                "    \"name\": \"木龙骨\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 27,\n" +
                "    \"name\": \"大芯板\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 28,\n" +
                "    \"name\": \"奥松板\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 29,\n" +
                "    \"name\": \"欧松板\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 30,\n" +
                "    \"name\": \"胶类\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 31,\n" +
                "    \"name\": \"保温系列\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 33,\n" +
                "    \"name\": \"木工工具及耗材\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 34,\n" +
                "    \"name\": \"防水\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 36,\n" +
                "    \"name\": \"勾缝剂\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 37,\n" +
                "    \"name\": \"粘接剂\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 38,\n" +
                "    \"name\": \"沙石类\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 39,\n" +
                "    \"name\": \"拉渣土\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 42,\n" +
                "    \"name\": \"瓦工辅料\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 44,\n" +
                "    \"name\": \"胶及界面剂\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 45,\n" +
                "    \"name\": \"快粘粉\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 46,\n" +
                "    \"name\": \"石膏线\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 47,\n" +
                "    \"name\": \"乳胶漆\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 48,\n" +
                "    \"name\": \"木器漆\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 49,\n" +
                "    \"name\": \"油工辅料\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 53,\n" +
                "    \"name\": \" 抗开裂系列\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 57,\n" +
                "    \"name\": \"集成材\",\n" +
                "    \"parentid\": 3\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 70,\n" +
                "    \"name\": \"瓦工工具\",\n" +
                "    \"parentid\": 4\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 60,\n" +
                "    \"name\": \"油工工具\",\n" +
                "    \"parentid\": 5\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 61,\n" +
                "    \"name\": \"开孔系列\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 62,\n" +
                "    \"name\": \"切割系列\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 63,\n" +
                "    \"name\": \"钻头系列\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 64,\n" +
                "    \"name\": \"劳保系列\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 65,\n" +
                "    \"name\": \"成品保护\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 66,\n" +
                "    \"name\": \"测量工具\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 67,\n" +
                "    \"name\": \"钉类\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 68,\n" +
                "    \"name\": \"胶类\",\n" +
                "    \"parentid\": 7\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 69,\n" +
                "    \"name\": \"梯子\",\n" +
                "    \"parentid\": 7\n" +
                "  }\n" +
                "]";
        return s;
    }
}
