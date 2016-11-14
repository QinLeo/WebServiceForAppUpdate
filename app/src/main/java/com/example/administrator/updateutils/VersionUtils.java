package com.example.administrator.updateutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtils {
	/**
	 * 获取当前app的versionCode return int
	 */
	public static int getversionCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.example.administrator.updateutils", 0).versionCode;
		} catch (Exception e) {
			android.util.Log.e("mUtils-gerversionCode", e.getMessage());
		}
		return verCode;
	}
}
