package com.ge.apm.service.utils;

public class PasswordUtil {
	public static final String DEFAULT_PWD = "G125@e";
	public static String checkPwd(String str){
		StringBuilder sb = new StringBuilder();
		
		if(str == null){
			sb.append("密码不能为空!");
			return sb.toString();
		}
		str = str.trim();
		if(!str.matches("^.{6,}$")){
			sb.append("密码长度不能少于6位!");
			return sb.toString();
		}
		Character first = str.charAt(0);
		Character last = str.charAt(str.length() -1);
		
		if(first < 65 || first > 90){
			sb.append("首字母必须大写!");
			return sb.toString();
		}
		if(last < 97 || last > 122){
			sb.append("尾字母必须小写!");
			return sb.toString();
		}
		if(str.matches("^.*[\\s]+.*$")){
			sb.append("不能包含空格、制表符、换页符等空白字符!");
			return sb.toString();
		}
		// '/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}'
		if(!(str.matches("^.*[a-zA-Z]+.*$") && str.matches("^.*[0-9]+.*$")&& str.matches("^.*[!@#$%&]+.*$"))){
			sb.append("必须包含数字、字母、特殊字符!");
			return sb.toString();
		}
		if(str.matches("^.*(.)\\1{2,}+.*$")){
			sb.append("不能有三个连续相同的字符!");
			return sb.toString();
		}
		char[] cc = str.toCharArray();

		for(int i = 0; i< cc.length - 3; i++){
			if(Math.abs(cc[i] - cc[i+1]) == 1 && (cc[i] - cc[i+1]) == (cc[i+1] - cc[i+2])){
				sb.append("不能有三个连续的字符!");
				return sb.toString();
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(checkPwd("A252*b"));
	}
}
