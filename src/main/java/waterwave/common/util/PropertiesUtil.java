package waterwave.common.util;

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
