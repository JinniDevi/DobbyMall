package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.MemberDAO;
import service.BoardService;
import service.BuyService;
import service.Co_mentService;
import service.Memberservice;
import service.ProductService;
import util.ScanUtil;
import util.View;

public class Controller {
	public static Map<String, Object> sessionStorage = new HashMap<>();
	
	public static void main(String[] args) {
		new Controller().controller();
	}

	
	Memberservice memberService = Memberservice.getInstance();
	BoardService boardService = BoardService.getInstance();
	ProductService productService = ProductService.getInstance();
	BuyService buyService = BuyService.getInstance();
	Co_mentService commentService = Co_mentService.getInstance();
	
	MemberDAO memberDAO = MemberDAO.getInstance();

	//테스트 모드는 둘 중 하나만 true/false
	boolean isTestAdminMode = false;
	boolean isTestUserMode = false;
	
	private void controller() {
		int view = View.HOME;
		while (true) {
			switch (view) {
			case View.HOME : view = home(); break;
			case View.ADMINHOME : view = adminHome(); break;
			
			//회원담당
//			case View.MEMBER_LOGIN : view = memberService.memlogin(); break;
//			case View.MEMBER_SINGUP : view = memberService.memsignUp(); break;
			case View.MEMBER_ENROLL : view = memberService.memEnroll(); break;
			case View.MEMBER_MODIFY : view = memberService.memModify(); break;
			case View.MEMBER_DELETE : view = memberService.memDelete(); break;
			case View.MEMBER_LOGIN : view = memberService.memLogin(); break;
			case View.MEMBER_LOGOUT: view = memberService.memLogout(); break;
			case View.MEMBER_LIST: view = memberService.memList(); break;
			case View.MEMBER_LISTDETAIL: view = memberService.memListDetail(); break;
			case View.MEMBER_LISTDETAIL_MODIFY: view = memberService.memListDetailModify(); break;
			case View.MEMBER_LISTDETAIL_DELETE: view = memberService.memListDetailDelete(); break;
			case View.MEMBER_MYINFO: view = memberService.memMyInfo(); break;
			
			//상품담당
//			case View.PRODUCT : view = productService.productHome(); break;
			case View.PRODUCT_LIST : view = productService.productlistAll(); break;
			case View.PRODUCT_DETAIL : view = productService.productDetail(); break;
			case View.PRODUCT_INSERT : view = productService.insertProduct(); break;
			case View.PRODUCT_DELETE : view = productService.deleteProduct(); break;
			case View.BUY_LIST : view = buyService.buylistAll(); break;
			case View.BUY_SELECT : view = buyService.selectproduct(); break;
			
			//게시판담당
			case View.BOARD_LIST: view = boardService.boardList(); break; 
			case View.BOARD_DETAIL: view = boardService.viewPost(); break; 
			case View.BOARD_INSERT: view = boardService.addPost(); break; 
			case View.BOARD_UPDATE: view = boardService.modifyPost(); break; 
			case View.BOARD_DELETE: view = boardService.removePost(); break; 
			case View.BOARD_CO_MENT_INSERT: view = commentService.addComment(); break; // View에서 이름변경해야함
			case View.BOARD_CO_MENT_DELETE: view = commentService.removeComment(); break; // View에서 이름변경해야함
			case View.BUY_REVIEW_LIST: view = buyService.reviewList(); break;
			case View.BUY_REVIEW_ISNERT: view = buyService.addReview(); break;
			case View.BUY_REVIEW_DELETE: view = buyService.removeReview(); break; 
			default:
				break;
			}
		}
	
	}



	private int home() {
		//사용자 테스트 시 실행
		if(isTestUserMode == true) {
			List<Object> param = new ArrayList<>();
			param.add(100001);
			param.add("100001");
			Map<String, Object> memberInfo = memberDAO.memLogin(param);
			Controller.sessionStorage.put("loginInfo", memberInfo);
		}
		
		
//		List<Object> param = new ArrayList<>();
//		//관리자 : 100000
//		//관리자 pw : admin1234
//		//사원 : 100001
//	    //비번 : 100001
//		param.add(100001);
//		param.add("100001");
//		Map<String, Object> memberInfo = memberDAO.memLogin(param);
//		Controller.sessionStorage.put("loginInfo", memberInfo);
//		
////		System.out.println(sessionStorage.get("loginInfo").toString());
//	
//		if(memberInfo.get("MEM_NO").toString().equals("100000".toString())){
//			return View.ADMINHOME;
//		}
		
//		else {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("================ Dobby 뷰티 임직원 몰 ==================");
		if(Memberservice.isLogin()) {
			System.out.println("1.내정보  2.내정보수정  3.사러가기  0.로그아웃"); // 비밀번호 찾기
			System.out.println("4.구매목록조회   5.Q&A게시판   6.회원탈퇴"); // 비밀번호 찾기
		System.out.println("========================================================");
			System.out.print("선택 >> ");
			switch (ScanUtil.nextInt()) {
			
			case 1: return View.MEMBER_MYINFO;
			case 2: return View.MEMBER_MODIFY;
			case 3: return View.PRODUCT_LIST;
			case 4: return View.BUY_LIST;
			case 5: return View.BOARD_LIST;
			case 6: return View.MEMBER_DELETE;			
			case 0: return View.MEMBER_LOGOUT;
			default: return View.HOME;
			}
		}else {
			return View.MEMBER_LOGIN;
		}
//		}
	}
	private int adminHome() {
		
		//관리자 테스트 시 실행
		if(isTestAdminMode == true) {
			List<Object> param = new ArrayList<>();
			param.add(100000);
			param.add("admin1234");
			Map<String, Object> memberInfo = memberDAO.memLogin(param);
			Controller.sessionStorage.put("loginInfo", memberInfo);
		}
		
//		List<Object> param = new ArrayList<>();
//		//관리자 : 100000
//		//관리자 pw : admin1234
//		
//		param.add(100000);
//		param.add("admin1234");
//		Map<String, Object> memberInfo = memberDAO.memLogin(param);
//		Controller.sessionStorage.put("loginInfo", memberInfo);
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("================ Dobby 뷰티 임직원 몰 관리자 ==================");
		System.out.println("1.신규회원등록  2.회원정보수정  3.회원조회  4.회원삭제");
		System.out.println("5.상품등록      6.상품삭제      7.QnA게시판 0.로그아웃");
		System.out.println("===============================================================");
		System.out.print("선택 >> ");
		switch (ScanUtil.nextInt()) {
		case 1: return View.MEMBER_ENROLL;
		case 2: return View.MEMBER_MODIFY;
		case 3: return View.MEMBER_LIST;
		case 4: return View.MEMBER_DELETE;
		case 5: return View.PRODUCT_INSERT;
		case 6: return View.PRODUCT_DELETE;
		case 7: return View.BOARD_LIST;
		case 0: return View.MEMBER_LOGOUT;
		default: return View.ADMINHOME;
		}
		
		
	
	}

}
