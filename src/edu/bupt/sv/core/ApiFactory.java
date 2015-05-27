package edu.bupt.sv.core;

import android.content.Context;

public class ApiFactory {

	private ApiFactory() {}
	
	private static CoreApi api = null;
	
	public static CoreApi getInstance(Context ctx) {
		if(api == null)
			api = new CoreApi(ctx);
		return api;
	}
}
