package com.example.administrator.updateutils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class toastUtils extends Toast {
	@SuppressWarnings("unused")
	private Context context;

	public toastUtils(Context context) {
		super(context);
	}

	public static void showToast(String msg, Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
		Toast toast = new Toast(context);
		toast.setView(v);
		toast.setGravity(Gravity.TOP, 10, 10);
		TextView tv = (TextView) v.findViewById(R.id.TextViewInfo);
		tv.setText(msg);
		toast.show();
	}
	
	public static void showToastInt(int msg, Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
		Toast toast = new Toast(context);
		toast.setView(v);
		toast.setGravity(Gravity.TOP, 10, 10);
		TextView tv = (TextView) v.findViewById(R.id.TextViewInfo);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/fzlt.ttf");
		tv.setTypeface(typeFace);
		tv.setText(msg);
		toast.show();
	}
}
