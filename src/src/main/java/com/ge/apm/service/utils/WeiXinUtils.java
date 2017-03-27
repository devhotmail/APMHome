package com.ge.apm.service.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeiXinUtils {
	public static List<Integer> removeDuplicateId(List<Integer> users) {
		Set<Integer> set = new HashSet<Integer>(users);
		return new ArrayList<Integer>(set);
	}
}