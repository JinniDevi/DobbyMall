package util;

public class LineBreakUtil {
	public static String parse(Object o, int length) {
		String result = "";
		String str = o.toString();
		int cnt = 0;
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(cnt >= length && (str.charAt(i-1) == ' ')) {
				result += "\n";
				cnt = 0;
			}
			result += c;
			if(str.charAt(i) > 'ㄱ' || str.charAt(i) > '힣') {
				cnt+=2;
			}else {
				cnt ++;
			}
		}
		return result;
	}
}
