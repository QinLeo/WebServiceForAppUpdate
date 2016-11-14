package com.example.administrator.updateutils;


import java.lang.reflect.Field;

import org.json.JSONObject;

/**
 * 将获取到的json数据转换为bean对象
 */
public class JsonUtils {
	public static Object putJsonObjectToBean(JSONObject jsonObj,
			String beanWholeName) {
		/*
		 * 可以通过java中的一种反射机制，获取某个对象中的所有属性的名称及属性的值
		 */
		try {
			Class cls = Class.forName(beanWholeName);// 将表示某个对象的完整路径的字符串转换为一对象
			Object bean = cls.newInstance();// 实例化转换后的这个对象
			// 得到这个object类型对象中的所有属性名
			Field[] fs = cls.getDeclaredFields();
			// 得到这个对象的所有属性名的数组集合后，可以根据这个对象中的属性名称来动态设置其属性的值
			for (Field f : fs) {// Field保存的是cls对象中每个属性中相关信息（包括：属性的名称、属性的值、属性的数据类型、属性的修辞符...)
				f.setAccessible(true);// 设置当前这个属性是否可修改（因为private修辞的属性要在外部类中去修改时，会提示权限不够，设置此值为true后，即可在外部类中去修改对象中的私有属性值
				// 设置这个属性的值
				// 先判断这个属性的数据类型 : f.getType() :此方法可以获取该属性的数据类型
				String type = f.getType().toString();
				if (type.endsWith("boolean") || type.endsWith("Boolean")) {
					f.setBoolean(bean, jsonObj.getBoolean(f.getName()));
				} else if (type.endsWith("int") || type.endsWith("Integer")) {
					f.setInt(bean, jsonObj.getInt(f.getName()));
				} else if (type.endsWith("double") || type.endsWith("Double")) {
					f.setDouble(bean, jsonObj.getDouble(f.getName()));
				} else {// 如果上面判断的三种类型都不是，则直接保存为字符串类型
					f.set(bean, jsonObj.get(f.getName()).toString());
				}
			}
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
