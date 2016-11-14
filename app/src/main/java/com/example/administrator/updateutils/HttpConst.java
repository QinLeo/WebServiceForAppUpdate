package com.example.administrator.updateutils;

/**
 * 调用webservice获取后台数据
 */
public interface HttpConst {

    /**
     * 地址
     */
    String endPoint = "http://115.28.57.142:8099/WebService1.asmx";// 正式地址
    /**
     * 命名空间
     */
    String nameSpace = "http://tempuri.org/";

    /**
     * 调用的方法名称
     */
    String methodName = "bpump_IMain";

    /**
     * SOAP Action
     */
    String soapAction = "http://tempuri.org/bpump_IMain";

/*以上为固定配置*/
    /**
     * 版本更新的回调标识
     */
    int MSG_WHAT_VERSION_UPDTAE = 0x01;

}
