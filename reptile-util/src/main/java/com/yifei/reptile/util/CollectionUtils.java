/**
 * 
 */
package com.yifei.reptile.util;

import java.util.Collection;

/**
 * 集合工具类
 * 
 * @author luqs
 * @date 2020年2月23日 下午10:25:45
 * @version v1.0.0
 */
public class CollectionUtils {
	private CollectionUtils() {
	}

	/**
	 * 判断集合是否非空
	 * 
	 * @param collection
	 *            集合
	 * @return boolean
	 */
	public static boolean isNotEmpty(Collection collection) {
		return !isEmpty(collection);
	}

	/**
	 * 判断集合是否为空
	 * 
	 * @param collection
	 *            集合
	 * @return boolean
	 */
	public static boolean isEmpty(Collection collection) {
		return size(collection) < 1;
	}

	/**
	 * 获取集合大小
	 * 
	 * @param collection
	 *            集合
	 * @return int
	 */
	public static int size(Collection collection) {
		if (collection == null) {
			return 0;
		}
		return collection.size();
	}

	/**
	 * 以分隔符连接
	 * 
	 * @param separator
	 *            分隔符
	 * @param args
	 *            数组
	 * @return String
	 */
	public static String join(String separator, String... args) {
		StringBuilder stringBuilder = new StringBuilder();
		int size = args.length;
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				stringBuilder.append(args[i]);
				continue;
			}
			stringBuilder.append(separator).append(args[i]);
		}
		return stringBuilder.toString();
	}

}
