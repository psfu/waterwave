package shui.common.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * String工具类
 *
 * @author shouyilin
 */
public class StringUtil {

	/**
	 * 是否有空
	 *
	 * @param inputs
	 * @return
	 */
	public static boolean isEmpty(String... inputs) {
		if (inputs == null || inputs.length == 0) {
			return true;
		}
		for (String input : inputs) {
			if (isEmpty(input)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否为空
	 *
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		return input == null || input.equals("") || input.replaceAll("[\\s　]+", "").equals("");
	}

	/**
	 * @param o
	 * @return
	 */
	public static boolean isEmpty(Object o) {
		return o == null || o.toString().equals("") || o.toString().replaceAll("[\\s　]+", "").equals("");
	}

	/**
	 * 为空转换为null
	 *
	 * @param input
	 * @return
	 */
	public static String empty(String input) {
		if (StringUtil.isEmpty(input)) {
			return null;
		}
		return input;
	}

	/**
	 * 长度
	 *
	 * @param input
	 * @return
	 */
	public static int length(String input) {
		return input == null ? 0 : input.length();
	}

	/**
	 * 去除前后空格
	 *
	 * @param input
	 * @return
	 */
	public static String trim(String input) {
		if (input == null) {
			return null;
		}
		return input.replaceAll("^[\\s　]+|[\\s　]+$", "");
	}

	public static String[] split(String input, String regex) {
		if (StringUtil.isEmpty(input) || regex == null) {
			return null;
		}
		Set<String> set = new LinkedHashSet<String>();
		String[] values = input.split(regex);
		for (String value : values) {
			if (StringUtil.isEmpty(value)) {
				continue;
			}
			if (set.contains(value)) {
				continue;
			}
			set.add(value);
		}
		return set.toArray(new String[set.size()]);
	}

	/**
	 * 去除数据重复元素，空元素，空字符串元素
	 *
	 * @param inputs
	 * @return
	 */
	public static String[] removeRepeat(String[] inputs) {
		if (inputs == null || inputs.length == 0) {
			return inputs;
		}
		Set<String> set = new HashSet<String>();
		for (String input : inputs) {
			if (!StringUtil.isEmpty(input)) {
				set.add(StringUtil.trim(input));
			}
		}
		String[] array = new String[set.size()];
		return set.toArray(array);
	}

	/**
	 * 分割后合并
	 *
	 * @param input
	 * @param splitRegex
	 * @param joinSign
	 * @param isTrim
	 * @return
	 */
	public static String splitAndjoin(String input, String splitRegex, String joinSign, boolean isTrim) {
		if (StringUtil.isEmpty(input)) {
			return null;
		}
		if (splitRegex == null || joinSign == null) {
			return input;
		}
		String[] segments = input.split(splitRegex);
		return join(segments, joinSign, isTrim);
	}

	/**
	 * 合并
	 *
	 * @param inputs
	 *            输入项数组
	 * @param joinSign
	 *            合并符号
	 * @param prefix
	 *            添加前缀
	 * @param postfix
	 *            添加后缀
	 * @param isTrim
	 *            是否去除项前后空格
	 * @param para
	 *            是否支持参数 $cur：当前项值
	 * @return
	 */
	public static String join(String[] inputs, String joinSign, String prefix, String postfix, boolean isTrim, boolean para) {
		if (inputs == null || inputs.length == 0) {
			return null;
		}
		StringBuilder builder = null;
		for (String input : inputs) {
			if (StringUtil.isEmpty(input)) {
				continue;
			}
			if (isTrim) {
				input = StringUtil.trim(input);
			}
			if (builder == null) {
				builder = new StringBuilder();
			} else {
				builder.append(joinSign);
			}
			if (prefix != null) {
				if (para) {
					builder.append(prefix.replace("$cur", input));
				} else {
					builder.append(prefix);
				}
			}
			builder.append(input);
			if (postfix != null) {
				if (para) {
					builder.append(postfix.replace("$cur", input));
				} else {
					builder.append(postfix);
				}
			}
		}
		if (builder != null) {
			return builder.toString();
		}
		return null;
	}

	/**
	 * 合并
	 * 
	 * @param inputs
	 * @param joinSign
	 * @param isTrim
	 * @return
	 */
	public static String join(String[] inputs, String joinSign, boolean isTrim) {
		return join(inputs, joinSign, null, null, isTrim, false);
	}

	/**
	 * 截取最长长度
	 *
	 * @param input
	 * @param maxLength
	 * @return
	 */
	public static String substring(String input, int maxLength) {
		if (input == null) {
			return null;
		}
		if (maxLength < 0) {
			return input;
		}
		if (input.length() > maxLength) {
			return input.substring(0, maxLength);
		}
		return input;
	}

	/**
	 * 获取字符串中指定字符个数
	 *
	 * @param input
	 * @param c
	 * @return
	 */
	public static int getCharAmount(String input, char c) {
		if (input == null) {
			return 0;
		}
		char[] cs = input.toCharArray();
		int total = 0;
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] == c) {
				total++;
			}
		}
		return total;
	}

	/**
	 * 移除字符串头
	 *
	 * @param input
	 * @param start
	 * @return
	 */
	public static String removeStart(String input, String start) {
		if (input == null) {
			return null;
		}
		if (start != null) {
			if (input.startsWith(start)) {
				return input.substring(start.length());
			}
		}
		return input;
	}

	/**
	 * 指定字符串前后添加字符串假如不存在
	 * 
	 * @param input
	 * @param add
	 * @return
	 */
	public static String addSideIfExclusive(String input, String add) {
		if (input == null || add == null) {
			return input;
		}
		String temp = input;
		if (!input.startsWith(add)) {
			temp = add + temp;
		}
		if (!input.endsWith(add)) {
			temp = temp + add;
		}
		return temp;
	}

	/**
	 * 指定字符串是否只要包含集合内一个元素
	 *
	 * @param collection
	 * @param element
	 * @return
	 */
	public static boolean isIndexOf(String input, Collection<String> collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		for (String e : collection) {
			if (e == null) {
				if (input == null) {
					return true;
				}
			} else {
				if (input != null && input.indexOf(e) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param s
	 * @param c
	 * @param count
	 * @return
	 */
	public static final String prefix(String s, char c, int count) {
		StringBuilder sb = new StringBuilder();
		int ii = count - s.length();
		for (int i = 0; i < ii; ++i) {
			sb.append(c);
		}
		sb.append(s);
		return sb.toString();
	}
}
