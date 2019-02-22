package com.dubbo.util.context;

public class ContextUtils {
	private static ThreadLocal<Context> threadContext = new ThreadLocal<Context>();
	
	public static void setContext(Context ctx){
		threadContext.set(ctx);
	}
	
	public static Context getContext(){
		return threadContext.get();
	}
	
	public static Object get(String key){
		Context ctx = threadContext.get();
		if(ctx == null){
			return null;
		}
		return ctx.get(key);
	}
	
	public static void put(String key,Object v){
		Context ctx = threadContext.get();
		if(ctx == null){
			return;
		}
		ctx.put(key, v);
	}
	
	public static void clear(){
		threadContext.remove();
	}
	
}
