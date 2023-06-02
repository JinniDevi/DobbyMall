package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.BoardDAO;
import dao.BuyDAO;
import dao.ProductDAO;
import util.EllipseUtil;
import util.ScanUtil;
import util.SpaceUtil;
import util.View;

public class ProductService {
	private static ProductService instance = null;
	private ProductService () {}
	public static ProductService getInstance() {
		if(instance == null) instance = new ProductService();
		return instance;
	}
	
	Memberservice memberService = Memberservice.getInstance();
	BoardDAO boardDAO = BoardDAO.getInstance();
	ProductDAO productDAO = ProductDAO.getInstance();
	BuyDAO buyDAO = BuyDAO.getInstance();
	
	int current_page =1;
	public static int lastNumber = -1;
	
	public List<Object> page(int current_page) {
		int page_row = 5;
		List<Object> page = new ArrayList<>();
		if(current_page < 1) current_page =1; 
		page.add(page_row);
		page.add(current_page);
		page.add(page_row);
		page.add(current_page);
		return page;
	}
	
	//입력받은 상품 담기
	public static Map<Integer, Map<String, Object>> getproduct = new HashMap<>();
	List<Map<String, Object>> showproductList = new ArrayList();
	
	int putkey=0;
	int targetNum;
	int inputNum;
	
	private void putitem(int input) {
		//만약에 getproduct의 값이 없으면 putkey =1 로 저장
		System.out.println();
		if(getproduct.containsValue(null)) putkey= 1;
		if(getproduct.containsKey(putkey)) putkey ++;
		targetNum = Integer.parseInt(showproductList.get(input-1).get("RN").toString()); 
		ProductDAO.rowNumber = targetNum;
//		Map<String, Object> item = productDAO.getproduct(page(current_page));
		Map<String, Object> item = showproductList.get(input-1);
		getproduct.put(putkey,item);
		System.out.printf("[%s] 상품을 담았습니다.\n\n\n", showproductList.get(input-1).get("PD_NAME"));
	}
	
	public int productlistAll() {
		if( ! memberService.isLogin()) return View.MEMBER_LOGIN;
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("====================== 상품 게시판 =======================");
		ProductDAO.orderByColumnName = "PD_NAME";
		ProductDAO.orderType = "ASC";
		showList(productDAO.getListAll(page(current_page)));
		while (true) {
		System.out.println("6.검색하기 7.가격낮은순보기 8.가격높은순보기 9.장바구니 및 결제");
		System.out.println("66.이전페이지 77.다음페이지 0.목록으로 돌아가기 99.HOME으로 돌아가기");
		System.out.println("----------------------------------------------------------");
		System.out.print("입력 및 상품 선택 >> ");
		inputNum = ScanUtil.nextInt();
		switch (inputNum) {
		case 1 : 
			return View.PRODUCT_DETAIL;
		case 2 : 
			return View.PRODUCT_DETAIL;
		case 3 : 
			return View.PRODUCT_DETAIL;
		case 4 : 
			return View.PRODUCT_DETAIL;
		case 5 : 
			return View.PRODUCT_DETAIL;
		case 6: //검색하기
			System.out.print("검색어를 입력하세요 >> ");
			String inputString = ScanUtil.nextLine();
			ProductDAO.searchName = inputString;
			showList(productDAO.getListAll(page(current_page)));
			break;
		case 7: //가격낮은순
			current_page =1;
			ProductDAO.orderByColumnName = "PD_PRICE";
			ProductDAO.orderType = "ASC";
			showList(productDAO.getListAll(page(current_page)));
			break;
		case 8: //가격높은순
			current_page =1;
			ProductDAO.orderByColumnName = "PD_PRICE";
			ProductDAO.orderType = "DESC";
			showList(productDAO.getListAll(page(current_page)));
			break;
		case 9 : //장바니 확인
			showCartList();
		case 66: //이전
			current_page --;
			if(current_page < 1) current_page =1; 
			showList(productDAO.getListAll(page(current_page)));
			break;
		case 77: //다음
			current_page++;
			showList(productDAO.getListAll(page(current_page)));
			break;
		case 0: //홈으로
			ProductDAO.searchName = "";
			return View.PRODUCT_LIST; 
		case 99 : 
			return View.HOME; 
		default:
			System.out.println("잘못입력했습니다.");
			return View.HOME; 
		}
	}
}	
	int memP = 500000;
	int sum =0;
	int pointSum = 0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
	String today = sdf.format(new Date()).toString();
	String lgu_id;
	
	private Object showCartList() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("==================== 장바구니 및 결제 ===================");
		if(getproduct == null) {
			System.out.println("선택하신 상품이 없습니다.");
			System.out.println();
			return View.PRODUCT_LIST;
		}
		System.out.println("---------------------------------------------------------");
		System.out.println("  [번호]    상품코드	    상품명		     가격");
		System.out.println("---------------------------------------------------------");
		for (int i = 0; i < getproduct.size(); i++) {
			Map<String, Object> list = getproduct.get(i);
				String str = "";
				str += SpaceUtil.format(i+1 + " ", 10, 0); 
				str += SpaceUtil.format(list.get("PD_ID") + " ", 15, 0); 
				str += SpaceUtil.format(EllipseUtil.parse(list.get("PD_NAME"),25) + " ", 25, -1); 
				str += SpaceUtil.format(list.get("PD_PRICE") + " ", 8, 1); 
				System.out.println(str);
				sum += Integer.parseInt(list.get("PD_PRICE").toString());
				pointSum += Integer.parseInt(list.get("PAYBACK_P").toString());
				lgu_id = list.get("PD_ID").toString();
			}
		System.out.println("---------------------------------------------------------");
		System.out.println();
		System.out.printf("[주문  총  금액  : %,d원]\n", sum);
		System.out.printf("[예상 획득포인트 : %,d P]\n", pointSum);
		System.out.println("---------------------------------------------------------");
		System.out.print("1.결제하기 2. 장바구니 비우기 0. 돌아가기 ");
		switch (ScanUtil.nextInt()) {
		case 1:
			// 회원 주문번호 쿼리 생성 
			Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
			List<Object> prarm = new ArrayList();
			int memid = Integer.parseInt(memberInfo.get("MEM_NO").toString());
			prarm.add(memid);
			prarm.add(today);
			prarm.add(sum);
			int result = buyDAO.insertOrderNumber(prarm);
			System.out.print("결제하시겠습니까? (y/n)>> ");
			String check = ScanUtil.nextLine();
			if(check.equals("y")) payment();
			getproduct = null;
			return View.PRODUCT_LIST;
		case 2: 
			System.out.println("/n/n");
			getproduct = null;
			System.out.println("장바구니를 비웠습니다.");
			return View.PRODUCT_LIST;
		case 0:
			return View.PRODUCT_LIST;
		default:
			System.out.println("잘못입력했습니다.");
			break;
		}
		return View.PRODUCT_LIST;
	}

	
	
	private void payment() {
		Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		List<Object> oderNumber = new ArrayList<>();
		int memid = Integer.parseInt(memberInfo.get("MEM_NO").toString());
		oderNumber.add(lgu_id);
		oderNumber.add(memid);
		oderNumber.add(today.substring(2, 8));
		oderNumber.add(today);
		oderNumber.add(memid);
		int return2 = buyDAO.insertOrderProduct(oderNumber);
		
		
		if(return2 > 0 ) {
			System.out.println();
			System.out.println("결제가 완료되었습니다.");
		} else {
			System.out.println("결제번호등록 실패!");
		}
		
		
		memP -= sum ;
		memP += pointSum;
		
		
		//마일리지 결제 - update
		List<Object> param3 = new ArrayList<>();
		param3.add(memid);
		param3.add(memid);
		int result3 = buyDAO.updatePoint(param3);
		System.out.println("enter >> ");
		ScanUtil.nextLine();
		System.out.printf("%s님의 잔여 PAYBACK POINT는 %,d P 입니다.\n"
				, memberInfo.get("MEM_NAME"), memP/*Integer.parseInt(memberInfo.get("MEM_POINT").toString())*/);
	}
	public Object showList(List<Map<String, Object>>list) {
		if(list == null) {
			System.out.println("등록된 상품이 없습니다.");
			return View.HOME; 
		} else {
			System.out.println("---------------------------------------------------------");
			System.out.println("  [번호]    상품코드	    상품명		     가격");
			System.out.println("---------------------------------------------------------");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> row = list.get(i);
				String str = "";
				str += SpaceUtil.format(i+1 + " ", 10, 0); 
				str += SpaceUtil.format(row.get("PD_ID") + " ", 15, 0); 
				str += SpaceUtil.format(EllipseUtil.parse(row.get("PD_NAME"),25) + " ", 25, -1); 
				str += SpaceUtil.format(row.get("PD_PRICE") + " ", 8, 1); 
				System.out.println(str);
			}
			System.out.println("---------------------- "+ current_page + "페이지 -----------------------");
			showproductList = list;
		}
		return showproductList;
	}
	
	
	public int productDetail() {//상세페이지
		List<Map<String, Object>>  product = showproductList;
		int rn = inputNum;
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("======================= 제품 상세페이지 =======================");
		System.out.println();
		System.out.println(" 상품코드 : " + product.get(rn-1).get("PD_ID"));
		System.out.println(" 상 품 명 : " + product.get(rn-1).get("PD_NAME"));
		System.out.println(" 상품가격 : " + product.get(rn-1).get("PD_PRICE"));
		System.out.println(" PayBack P: " + product.get(rn-1).get("PAYBACK_P"));
		System.out.println();
		System.out.println("---------------------------------------------------------------");
		System.out.print("1.상품리뷰  2.장바구니  0.돌아가기 >> ");
		
		switch (ScanUtil.nextInt()) {
		case 1: return View.BUY_REVIEW_LIST;  
		case 2:
			System.out.print("상품을 담으시겠습니까? (y/n) >> ");
			String productget = ScanUtil.nextLine();
			if(productget.equals("y")) {
				putitem(inputNum);
			}
			return View.PRODUCT_DETAIL;
		case 0: 
			return View.PRODUCT_LIST;
		default:
			System.out.println("잘못 누르셨습니다.");
			return View.PRODUCT_LIST;
		}
		
		
//		System.out.print("상품을 담으시겠습니까? (y/n) >> ");
//		String productget = ScanUtil.nextLine();
//		if(productget.equals("y")) {
//			putitem(inputNum);
//		} else {
//			return View.PRODUCT_DETAIL;
//		}
//		return 0;
	}
	
	public int insertProduct() {
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		if(loginInfo.get("MEM_NO").toString().equals("100000".toString())) {
			System.out.print("상품을 등록하시겠습니까? (y/n) >> ");
			String inputString = ScanUtil.nextLine();
			if(inputString.equals("y")) {
				//상품분류코드 리스트
				showLguList(productDAO.getlguList());
				return View.ADMINHOME;
			} else {
				System.out.println("잘못입력했습니다.");
				return View.ADMINHOME; 
			}
			
		} else {
			System.out.println("관리자가 아닙니다.");
			return View.HOME;
		}
	}
	private int showLguList(List<Map<String, Object>> list) {
		System.out.println("-----------------------------------------------");
		System.out.println("  [번호]	분류코드	   분류코드명");
		System.out.println("-----------------------------------------------");
		
		if(list == null) {
			System.out.println("분류코드가 없습니다..");
			return View.HOME; 
		} else {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> row = list.get(i);
				String str = "";
				str += SpaceUtil.format(row.get("RN") + " ", 10, 0); 
				str += SpaceUtil.format(row.get("LGU_ID") + " ", 25, 0); 
				str += SpaceUtil.format(row.get("LGU_NAME")+ "  ", 10, 0); 
				System.out.println(str);
				System.out.println();
			}
			System.out.println("-----------------------------------------------");
		}
			System.out.println();
			System.out.print("상품분류코드 입력 >> ");
			//상품분류코드 입력
			int rn = ScanUtil.nextInt();
			if(rn < 1 || rn > list.size()) {
				System.out.println("잘못입력");
				return View.ADMINHOME;
			}
			String lguId = list.get(rn - 1).get("LGU_ID").toString();
			
			//상품명 입력
			System.out.print("상품명 입력 >> ");
			String pdName = ScanUtil.nextLine();
			//원가 입력
			System.out.print("원가 입력 >> ");
			int cost = ScanUtil.nextInt();
			
			List<Object> prarm = new ArrayList();
			prarm.add(lguId);
			prarm.add(pdName);
			prarm.add(lguId);
			
			ProductDAO.pd_cost = cost;
			int result = productDAO.insertNewPd(prarm);
			
			if(result > 0 ) {
				System.out.println("상품등록 성공!");
			} else {
				System.out.println("상품등록 실패!");
			}
			
		return View.ADMINHOME;
	}
	
	public int deleteProduct() {
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		if(loginInfo.get("MEM_NO").toString().equals("100000".toString())) {
			System.out.print("상품을 삭제하시겠습니까? (y/n) >> ");
			String inputString = ScanUtil.nextLine();
			if(inputString.equals("y")) {
				ProductDAO.orderByColumnName = "PD_NAME";
				ProductDAO.orderType = "ASC";
				showList(productDAO.getListAll(page(current_page)));
				while(true) {
				System.out.println(" 1. 상품삭제하기 2.이전페이지 3. 다음페이지 0. 돌아가기");
				System.out.print("선택 >> ");
				switch (ScanUtil.nextInt()) {
				case 1 : 
					System.out.print("삭제하려는 상품ID 를 입력하세요 >> ");
					String inpuString = ScanUtil.nextLine();
					
					List<Object> prarm = new ArrayList();
					prarm.add(inpuString);
					int result = productDAO.deletePd(prarm);
					if(result > 0 ) {
						System.out.println("상품삭제 성공!");
					} else {
						System.out.println("상품삭제 실패!");
					}
					return View.ADMINHOME;
				case 2: //이전
					current_page --;
					if
					(current_page < 1) current_page =1; 
					showList(productDAO.getListAll(page(current_page)));
					break;
				case 3: //다음
					current_page++;
					showList(productDAO.getListAll(page(current_page)));
					break;
				case 0: return View.ADMINHOME;
				default:
					System.out.println("잘못 입력했습니다.");
					return View.ADMINHOME;
				}
				}
			 } else {
				System.out.println("잘못입력했습니다.");
				return View.ADMINHOME;
			}
			
		}
		return 0;
	}
}
