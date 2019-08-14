package com.dili.http.okhttp.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asiamaster on 2017/9/30 0030.
 */
public class CompileUtil {
	private final static JavaStringCompiler compiler;
	public final static Map<String, Class<?>> classes = new HashMap<>();
	static {
		compiler = new JavaStringCompiler();
	}

	/**
	 * 编译
	 * @param classContent 字符串类内容
	 * @param classFullname	类全名: com.xxx.service.XxxService
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("all")
	public static Class<?> compile(String classContent, String classFullname)  {
//		System.out.println("========================================================");
//		System.out.println("classFullname:"+classFullname);
//		System.out.println("compile:"+classContent);
//		System.out.println("========================================================");
		try {
			String cn = classFullname.substring(classFullname.lastIndexOf(".")+1);
			Map<String, byte[]> results = compiler.compile(cn+".java", classContent);
			Class<?> clazz = compiler.loadClass(classFullname, results);
			classes.put(clazz.getName(), clazz);
			return clazz;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
