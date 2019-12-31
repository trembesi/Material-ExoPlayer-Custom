package com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.utils;

import android.util.Log;

import com.blogspot.materialexoplayercustom.BuildConfig;

public class LogUtils
{
	public static void log(String x){
		if(BuildConfig.DEBUG)
		Log.i("Naveed",x);
	}
	public static void log(int x){
		if(BuildConfig.DEBUG)
			Log.i("Naveed",String.valueOf(x));
	}
	
}
