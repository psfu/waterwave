package shui.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BeanTest t = new BeanTest();
		
		System.out.println(getAllFields(t.getClass(),t));
	}

	@SuppressWarnings("unused")
	static class BeanTest extends BeanTest0 {
		private int a;
		private int b;
		private int c;
		{
			a = 1;
			b = 2;
			c = 3;
		}


	}
	
	static class BeanTest0 {
		int a;
		int b;
		int e;
		protected int f;
		public int g;
		{
			a = 0;
			b = 0;
			e = 100;
			f = 100;
			g = 100;
		}

	}
	
	public static <T> Map<String, Object> getAllFields(T in) {
		return getAllFields(in.getClass(),in);
	}

	public static <T> Map<String, Object> getAllFields(Class<? extends T> c, T in) {

		Map<String, Object> r = new HashMap<>();
		
		Class<?> p = c.getSuperclass();
		while(p != Object.class) {
			getFields(p, in, r);
			p = p.getSuperclass();
		}
		getFields(c, in, r);

		return r;

	}

	private static <T> void getFields(Class<? extends T> c, T in, Map<String, Object> r) {
		Field[] fields = c.getDeclaredFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);

				Object v = field.get(in);
				r.put(field.getName(), v);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
