package shui.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommonUtil {
	public static String getLocalIp() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString();
			return ip;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public final static String getStringFromException(Throwable e) {

		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);

		e.printStackTrace(ps);

		return sw.toString();
	}
}
