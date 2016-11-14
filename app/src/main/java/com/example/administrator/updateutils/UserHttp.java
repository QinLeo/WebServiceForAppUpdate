package com.example.administrator.updateutils;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class UserHttp {
	private static UserHttp instance = null;
	public HttpUtils https = new HttpUtils();

	public static UserHttp newInstance() {
		if (instance == null) {
			instance = new UserHttp();
			instance.https = new HttpUtils();
		}
		return instance;
	}

	private UserHttp() {
	}

	/**
	 * 版本更新
	 */
	public void Update(Context context, final Handler handler) {
		final Message msg = new Message();
		msg.what = HttpConst.MSG_WHAT_VERSION_UPDTAE;
		msg.obj = null;
		// 指定WebService的命名空间和调用的方法名
		SoapObject soapobject = new SoapObject(HttpConst.nameSpace,
				HttpConst.methodName);
		// 将数据转换为json格式
		JSONObject upd = new JSONObject();
		// 将json数据格式放置到soapobject中
		soapobject.addProperty("sessionCheck", 1);
		soapobject.addProperty("action", "get_Version");
		soapobject.addProperty("jsonInfo", upd.toString());

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = soapobject;
		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;
		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(soapobject);

		HttpTransportSE transport = new HttpTransportSE(HttpConst.endPoint);
		try {
			// 调用WebService
			transport.call(HttpConst.soapAction, envelope);
			// SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// *******************************************返回结果***********************************************************
		// 获取返回的结果
		SoapObject object = (SoapObject) envelope.bodyIn;
		if (object == null) {
			msg.obj = "NoNet";
			handler.sendMessage(msg);
			return;
		}
		String result = object.getProperty(0).toString();
		JSONObject YZ = null;
		versionBean bean = new versionBean();
		try {
			JSONObject RESULT_YZ = new JSONObject(result);
			JSONArray arrayYZ = new JSONArray(RESULT_YZ.getString("valid"));
			for (int i = 0; i < arrayYZ.length(); i++) {
				YZ = (JSONObject) arrayYZ.get(1);
			}
			/**
			 * 没有异常则执行以下模块
			 */
			if (YZ.get("error").toString().equals("1")) {
				/**
				 * 读取数据信息的jsonobject
				 */
				JSONArray userInfor = new JSONArray(RESULT_YZ.getString("ds"));
				for (int i = 0; i < userInfor.length(); i++) {
					JSONObject verbean = (JSONObject) userInfor.get(i);
					/**
					 * 将json数据信息封装成bean
					 */
					bean = (versionBean) JsonUtils.putJsonObjectToBean(verbean,
							"com.example.administrator.updateutils.versionBean");
				}
			} else {
				bean = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 利用handler发送结果
		 */
		if (bean == null) {
			msg.obj = null;
			handler.sendMessage(msg);
		} else {
			msg.obj = bean;
			handler.sendMessage(msg);
		}
	}

}
