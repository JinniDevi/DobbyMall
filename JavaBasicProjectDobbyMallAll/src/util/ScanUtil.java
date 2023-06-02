package util;

import java.util.Scanner;

public class ScanUtil {

		private static Scanner sc = new Scanner(System.in);
		
		public static String nextLine() {
			return sc.nextLine();
		}
		public static int nextInt() {
			int result = 0;
			
			while (true) {
				try {
					result = Integer.parseInt(sc.nextLine());
					break;
				} catch(NumberFormatException e) {
					System.out.println("숫자를 입력해주세요!");
				}
			}
			return result;
		}
		
}
