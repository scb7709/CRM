package com.yun.ycw.crm.constants;


/**
 * 常量类
 * Created by wdyan on 2016/2/17.
 */
public class Constants {
      public final static String BASE_URL = "http://api.yuncaiwang.net";//云材网基地址
    //     public final static String BASE_URL = "http://api.dev.yuncaiwang.net";//云材网基地址
    //  public final static String BASE_URL = "http://api.sendbox.yuncaiwang.net";//云材网基地址
    public final static String FILE_BASE_URL = "http://file.yuncaiwang.net/";//云材网文件基地址
    public final static String STAFF_HIERARCHY = BASE_URL + "/crm/sys/sellman";//员工层级关系
    public final static String GOODS_CATGORY = BASE_URL + "/crm/sys/product-category";//商品分类表

    public final static String UNDONE_ORDER_URL = BASE_URL + "/crm/order/customer";//待回款订单的地址
    public final static String GOODS_BASE_URL = "http://file.dev.yuncaiwang.net";//商品图片的基地址
    public final static String ORDER_BASE_URL = BASE_URL + "/crm/order/";//客户的订单的基地址
    public final static String ORDER_SELL_URL = BASE_URL + "/crm/order?sell_id=";//销售专员的订单列表
    public final static String ORDER_DETAIL_URL = BASE_URL + "/crm/order?customer_id=";//某一具体客户的订单的基地址
    public final static String UNDONE_ORDER_LEADER = BASE_URL + "/crm/order/recover?user_id=";//某一个销售专员下的待回款订单
    public final static String GENERAL_URL = BASE_URL + "/crm/staff/";//首页工作台的地址
    public final static String MY_CUSTOMER_URL = BASE_URL + "/crm/customer?user_id=";//“我的客户”的列表
    public final static String SPECIFIC_STAFF_INFO = BASE_URL + "/crm/staff/43/stat";//员工号为43的员工的业绩信息（新工作台）
    public final static String STAFF_URL = BASE_URL + "/crm/staff/";//获取下属业绩列表
    public final static String STAFFAPPLY_URL = BASE_URL + "/crm/staff-apply";//获取管理的申请列表
    public final static String GOODS_MALL = BASE_URL + "/crm/sys/products";//获取商城
    public final static String SCHEDULE_URL = BASE_URL + "/crm/staff-plan?user_id=";//获取某一个销售下的周计划列表
    public final static String CHECKIN_URL = BASE_URL + "/crm/area-sign";//添加签到
    public final static String LEVEL_RELATIONSHIP = BASE_URL + "/crm/sys/sellman";//层级关系
    public final static String UPLOAD_CHECKIN_IMG = BASE_URL + "/file-upload";//上传签到照片的地址

    public final static int PROGRESS_CREATED = 1;    //创建订单
    public final static int PROGRESS_WAITING_FIRST_AUDIT = 100;  //待审核
    public final static int PROGRESS_FIRST_AUDITED = 130;  //已审核
    public final static int PROGRESS_CHOOSE_WAREHOUSE = 140;  //已分仓
    public final static int PROGRESS_STOREKEEPER_AUDITED = 150;  //库管已审核,待打印
    public final static int PROGRESS_PICKING = 200;  //已打印和拣货,待发货
    public final static int PROGRESS_IN_TRANSIT = 500;  //已出库 (待回款)
    public final static int PROGRESS_APPLY_CANCEL = 800;  //申请取消订单
    public final static int PROGRESS_CANCEL_AUDITED = 810;  //取消订单申请已审核
    public final static int PROGRESS_APPLY_REFUND = 900;  //已申请退货
    public final static int PROGRESS_REFUND_AUDITED = 910;  //退货申请已审核
    public final static int PROGRESS_SIGNATURE = 950;  //已签收 (待回款)
    public final static int PROGRESS_RECEIPT = 1000; //已收款
    public final static int PROGRESS_CLEARING = 1100; //已结算 (历史订单)
    public final static int PROGRESS_COMPLETE = 1200; //已完成 (历史订单)
    public final static int PROGRESS_CANCEL = 1210; //已取消 (历史订单)

    public final static int ACTIVITE_APPLY = 2222; //激活申请页面返回的邀请码

    public static String getPayType(String type) {
        String mPayType = "";
        switch (type) {
            case "weixin":
                mPayType = "微信支付";
                break;
            case "alipay":
                mPayType = "支付宝支付";
                break;
            case "cash":
                mPayType = "余额支付";
                break;
            case "daofu":
                mPayType = "货到付款";
                break;
            case "firm":
                mPayType = "企业支付";
                break;
            default:
                break;
        }
        return mPayType;
    }

    public static String getSource(int status) {
        String iStatus = "";
        switch (status) {
            case 1:
                iStatus = "社区开发";
                break;
            case 2:
                iStatus = "客户介绍";
                break;
            case 3:
                iStatus = "400电话";
                break;
            case 4:
                iStatus = "工作活动";
                break;
            case 5:
                iStatus = "网络";
                break;
            default:
                break;
        }
        return iStatus;
    }

    public static String getOrderStatus(int status) {
        String mStatus = "";
        switch (status) {
            case PROGRESS_CREATED:
                mStatus = "待审核";
                break;
            case PROGRESS_WAITING_FIRST_AUDIT:
                mStatus = "待审核";
                break;
            case PROGRESS_FIRST_AUDITED:
                mStatus = "已审核";
                break;
            case PROGRESS_CHOOSE_WAREHOUSE:
                mStatus = "已分仓";
                break;
            case PROGRESS_STOREKEEPER_AUDITED:
                mStatus = "库管已审核,待打印";
                break;
            case PROGRESS_PICKING:
                mStatus = "已打印和拣货,待发货";
                break;
            case PROGRESS_IN_TRANSIT:
                mStatus = "已出库";
                break;
            case PROGRESS_APPLY_CANCEL:
                mStatus = "申请取消订单";
                break;
            case PROGRESS_CANCEL_AUDITED:
                mStatus = "取消订单申请已审核";
                break;
            case PROGRESS_APPLY_REFUND:
                mStatus = "已申请退货";
                break;
            case PROGRESS_REFUND_AUDITED:
                mStatus = "退货申请已审核";
                break;
            case PROGRESS_SIGNATURE:
                mStatus = "已签收";
                break;
            case PROGRESS_RECEIPT:
                mStatus = "已收款";
                break;
            case PROGRESS_CLEARING:
                mStatus = "已结算";
                break;
            case PROGRESS_COMPLETE:
                mStatus = "已完成";
                break;
            case PROGRESS_CANCEL:
                mStatus = "已取消";
                break;
            default:
                break;
        }
        return mStatus;
    }
}
