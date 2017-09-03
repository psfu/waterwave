package shui.common.util;

import java.util.Calendar;

public class DateUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		c.set(2018, 2, 31, 00,00,00);
		System.out.println(c.getTimeInMillis());
		System.out.println(c.getTime());
	}

}
