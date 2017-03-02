/*
 * Licensed to waterwave under one or more contributor
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package shuisea.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import shuisea.common.log.Logger;
import shuisea.common.log.SimpleLogger;

public class Common {

	// static final String initPpFile = "log-client.properties";
	static Logger log = new SimpleLogger();

	public static Properties loadPropertiesfile(String filePath) {
		Properties properties = new Properties();
		try {
			//file system
			if(filePath.startsWith("/") || filePath.indexOf(":") == 1) {
				File f = new File(filePath);
				FileReader r = new FileReader(f);
				properties.load(r);
			//resources
			}else {
				properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
			}
			
			
		} catch (IOException e) {
			log.log(9, "The properties file is not loaded.", e);
			throw new IllegalArgumentException("The properties file is not loaded.", e);
		}

		return properties;
	}

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

	public static String setPp(String[] args, String ppfile) {
		if (args != null && args.length > 0) {
			if (!"null".equals(args[0])) {
				return args[0];
			}
		}
		return ppfile;
	}

	public static void setArgs(String[] args, Properties pp) {
		try {
			if (args != null && args.length > 1) {
				String[] params = args[1].split(",");
				for (String p : params) {
					String[] p0 = p.split("=");
					pp.put(p0[0], p0[1]);
				}

			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("wrong args!");
			System.exit(0);
		}
	}

	public final static String getStringFromException(Throwable e) {

		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);

		e.printStackTrace(ps);

		return sw.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties pp = new Properties();

		setArgs(new String[] { "1", "abc=123,efg=123,dd=235" }, pp);
		System.out.println(pp);
	}

}
