package util;

public class EllipseUtil {
	public static String parse(Object o, int length) {
		String result = "";
		String str = o.toString();
		int cnt = 0;
		for(int i = 0; i < str.length(); i++) {
			if(cnt >= length - 4) {
				result += "..";
				break;
			}
			result += str.charAt(i);
			if(str.charAt(i) > 'ㄱ' || str.charAt(i) > '힣') {
				cnt+=2;
			}else {
				cnt ++;
			}
		}
		return result;
	}
}
