package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.BuyDAO;
import dao.ProductDAO;
import oracle.net.aso.l;
import util.EllipseUtil;
import util.ScanUtil;
import util.SpaceUtil;
import util.View;

public class BuyService {
	private static BuyService instance = null;
	private BuyService () {}
	public static BuyService getInstance() {
		if(instance == null) instance = new BuyService();
		return instance;
	}
	BuyDAO buyDAO = BuyDAO.getInstance();
	Memberservice memberService = Memberservice.getInstance();
	ProductDAO productDAO = ProductDAO.getInstance();
	ProductService productService = ProductService.getInstance();
	
	
	int current_page = 1;
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
	
	//구매상품 목록보기
	public int buylistAll() {
		if( ! memberService.isLogin()) return View.MEMBER_LOGIN;
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		System.out.println("/n/n/n");
		//로그인한 사람 = 구매목록의 MEM_NO 같다면 
		System.out.println("====================== 구매목록조회 =====================");
		System.out.print("1.조회하기 2.돌아가기 >> " );
		switch (ScanUtil.nextInt()) {
		case 1:
			List<Object> param = new ArrayList();
			int id = Integer.parseInt(loginInfo.get("MEM_NO").toString());
			param.add(id);
			showcartlistAll(buyDAO.getcartListAll(param));
			System.out.println("돌아가기 enter");
			ScanUtil.nextLine();
			return View.HOME; 
		case 2: return View.HOME;  
		default:
			System.out.println("잘못눌렀습니다.");
			break;
		}
		return 0;

	}
	
	private int showcartlistAll(List<Map<String, Object>>list) {
			if(list == null) {
				System.out.println("구매하신 상품이 없습니다.");
				return View.HOME; 
			} else {
				for (int i = 0; i < list.size(); i++) {
					System.out.println();
					Map<String, Object> date = list.get(i);
					System.out.println("구매날짜 : " + date.get("ORDR_DATE").toString());
				
				System.out.println("---------------------------------------------------------");
				System.out.println("   구매번호      상품명		수량      가격");
				System.out.println("---------------------------------------------------------");
				for (int j = 0; j < 1; j++) {
					Map<String, Object> row = list.get(i);
					String str = "";
					str += SpaceUtil.format(row.get("OR_PDBUY_NO") + " ", 15, 0); 
					str += SpaceUtil.format(EllipseUtil.parse(row.get("PD_NAME"),25) + " ", 20, -1); 
					str += SpaceUtil.format(row.get("ORPD_QTY") + " ", 5, -1); 
					str += SpaceUtil.format(row.get("ORPD_PRICE") + " ", 8, 1); 
					System.out.println(str);
				}
				System.out.println("---------------------------------------------------------");
			}
			}
		return 0;
	}
	
	public int reviewList() {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		lastNumber = -1;
		
		List<Map<String, Object>> reviewList = null;
		reviewList = buyDAO.getReviewListAll();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("============================== 상품 리뷰 =============================");
		System.out.println("----------------------------------------------------------------------");
		System.out.println("[번호]      구매번호             내용                  작성날짜 ");
		System.out.println("----------------------------------------------------------------------");
		
		if(reviewList == null) {
			System.out.println("등록된 리뷰가 없습니다. >> Enter");
			ScanUtil.nextLine();
			return View.BUY_LIST;
		}else {	
			// R_NO, MEM_NO, R_CONTENT, R_DATE, OR_PDBUY_NO, PD_ID
			for(int i = 0; i < reviewList.size(); i++) {
				Map<String, Object> row = reviewList.get(i);
				String str = "";
				str += SpaceUtil.format(row.get("R_NO")+"     ", 10, 0);
				str += SpaceUtil.format(row.get("OR_PDBUY_NO")+"  ", 15, 0);
				str += SpaceUtil.format(EllipseUtil.parse(row.get("R_CONTENT")+" ", 23)+" ", 25, -1);
				str += SpaceUtil.format(row.get("R_DATE")+" ", 13, 1);
				System.out.println(str);
			}
		}
		System.out.println("----------------------------------------------------------------------");		
		System.out.print("돌아가기 >> Enter "); // 리뷰등록, 삭제 구현 실패!!
		ScanUtil.nextLine();
		return View.PRODUCT_DETAIL;
	}
	
	
	public int addReview() {
		if(! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		
		String pdNo = "";
		String reContent = "";
		
		System.out.print("사원번호 >> ");
		int memNo = ScanUtil.nextInt();
		if(memNo == Integer.parseInt(loginInfo.get("MEM_NO").toString())) {
			
			System.out.print("상품코드 >> ");
			pdNo = ScanUtil.nextLine();
			
			System.out.print("상품리뷰 >> ");
			reContent = ScanUtil.nextLine();
			
			// R_NO, MEM_NO, R_CONTENT, R_DATE, OR_PDBUY_NO, PD_ID
			List<Object> param = new ArrayList<Object>();
			param.add(memNo);
			param.add(reContent);
			param.add(pdNo);
			
			int result = buyDAO.insertRow(param);
			
			if(result > 0) {
				System.out.println("상품리뷰를 등록하셨습니다.");
			}else {
				System.out.println("다시 시도해주세요.");
			}	
		}else {
			System.out.println("작성 권한이 없습니다. >> Enter");
			ScanUtil.nextLine();
			return View.BUY_REVIEW_LIST;
		}
		return View.BUY_REVIEW_LIST;
	}
	
	
	public int removeReview() {
		if(! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		List<Map<String, Object>> pdReviewList; // 리뷰 넘버
		pdReviewList = buyDAO.getPdReviewList(ProductService.lastNumber);//이해 안가~
		
		if(pdReviewList == null) {
			System.out.println("댓글이 없습니다. >> Enter");
			ScanUtil.nextLine();
			return View.BUY_LIST;
		}else {
			System.out.print("삭제 할 상품리뷰 번호 >> ");
			int input = ScanUtil.nextInt();
			
			if(input < 1 || input > pdReviewList.size()) {
				System.out.println("번호를 잘못 입력했습니다.");
				return View.BUY_REVIEW_LIST;
			}
			
			Map<String, Object> targetRow = pdReviewList.get(input - 1);
			
			Object o = Controller.sessionStorage.get("loginInfo");
			Map<String, Object> loginInfo = (Map<String, Object>) o;
			
			System.out.print("사원번호 >> ");
			int memNo = ScanUtil.nextInt();
			if(memNo == Integer.parseInt(loginInfo.get("MEM_NO").toString())) {
				System.out.print("정말 삭제하시겠습니까? (y/n) >> ");
				if(ScanUtil.nextLine().equals("n")) return View.BUY_REVIEW_LIST;
			}
			
			int result = buyDAO.deletRow(Integer.parseInt(targetRow.get("R_NO").toString()));
			
			if(result > 0) {
				System.out.println("선택하신 리뷰가 삭제되었습니다.");
				return View.BUY_REVIEW_LIST;
			}
		}
		System.out.println("글 삭제 권한이 없습니다.");
		return  View.BUY_REVIEW_LIST;
	}
	
	
	
	
	
	
	public int selectproduct() {
		productService.showList(productDAO.getListAll(page(current_page)));
		System.out.println("선택번호 입력 >>");
		int rn = ScanUtil.nextInt();
		while (true) {
			System.out.println("1.이전페이지 2.다음페이지 0.돌아가기");
			switch (ScanUtil.nextInt()) {
			case 1:
				current_page --;
				if(current_page < 1) current_page =1; 
				productService.showList(productDAO.getListAll(page(current_page)));
				System.out.println("선택번호 입력 >>");
				int rn1 = ScanUtil.nextInt();
				break;
			case 2:
				current_page++;
				productService.showList(productDAO.getListAll(page(current_page)));
				System.out.println("선택번호 입력 >>");
				int rn2 = ScanUtil.nextInt();
			case 0:
				return View.PRODUCT_LIST;
			default:
				System.out.println("잘못 입력했습니다.");
				return View.HOME; 
			}
		}
		}
		
	
	
	
	
	
	
}
