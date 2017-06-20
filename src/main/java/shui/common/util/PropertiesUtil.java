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


package shui.common.util;

import java.util.Properties;

public class PropertiesUtil {
	Properties pp;

	public PropertiesUtil(Properties pp) {
		super();
		this.pp = pp;
	}

	public String getString(String name) {
		return pp.getProperty(name);
	}
	
	public String getString(String name, String defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		return v;
	}
	
	public <T extends Enum<T>> T getEnum(String name, Class<T> c,T d) {
		String v = getString(name);
		if (v == null) {
			return d;
		}
		;
		return Enum.valueOf(c, v);
	}

	public boolean getBoolean(String name, boolean defaultValue) {
		String v = getString(name);
		try {
			return Boolean.parseBoolean(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public int getInt(String name, int defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public long getLong(String name, long defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public short getShort(String name, short defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		try {
			return Short.parseShort(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public double getDouble(String name, double defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public float getFloat(String name, float defaultValue) {
		String v = getString(name);
		if (v == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(v);
		} catch (Exception e) {
		}
		return defaultValue;
	}

}
