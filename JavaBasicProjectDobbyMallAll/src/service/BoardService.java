package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.BoardDAO;
import dao.Co_mentDAO;
import util.EllipseUtil;
import util.JDBCUtil;
import util.ScanUtil;
import util.SpaceUtil;
import util.View;

public class BoardService {

	private static BoardService instance = null;
	private BoardService () {}
	public static BoardService getInstance() {
		if(instance == null) instance = new BoardService();
		return instance;
	}


	BoardDAO boardDAO = BoardDAO.getInstance();
	Co_mentDAO commentDAO = Co_mentDAO.getInstance();
	JDBCUtil jdbc = JDBCUtil.getInstance();

	public static int lastBoNum = -1;
	
	int current_page = 1;
	int page_row = 5;
	
	// 페이징
	public List<Object> page(int current_page) {
		int page_row = 5;
		
		List<Object> page = new ArrayList<>();
		if(current_page < 1) current_page =1; 
		
		page.add(page_row); // 1번째 ?
		page.add(current_page); // 2번째 ?
		page.add(page_row); // 3번째 ?
		page.add(current_page); // 4번째 ?
		
		return page;
	}
		
	
	// 문의글 목록
	public int boardList() {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		List<Object> param = new ArrayList<>();
	    Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		showBoardList(boardDAO.getBoardList(page(current_page)));
		boolean adminCheck = false;
	    if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
	         adminCheck = true;}
		while(true) {
			System.out.print("1.조회   2.등록   3.이전페이지   4.다음페이지   0.메인화면 >> "); 
			switch (ScanUtil.nextInt()) {
			case 1: return View.BOARD_DETAIL; // 문의글 내용
			case 2: return View.BOARD_INSERT; // 문의글 추가
			case 3: current_page--;
					if(current_page < 1) current_page = 1; 
					showBoardList(boardDAO.getBoardList(page(current_page)));
					break;
			case 4: current_page++; 
					showBoardList(boardDAO.getBoardList(page(current_page)));
					break;
			case 0: 
				if(adminCheck) {
		            return View.ADMINHOME;
		         }else {
		            return View.HOME;
		         }
			default:
				System.out.println("잘못 누르셨습니다.");
				return View.BOARD_LIST;
			}
		}
	}
	
	
	// 문의글 조회
	public int showBoardList(List<Map<String, Object>> list) {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		System.out.println();
		System.out.println("============================= QnA 게시판 =============================");
		System.out.println("----------------------------------------------------------------------");
		System.out.println("[번호]          제목             작성자     조회수      작성날짜 ");
		System.out.println("----------------------------------------------------------------------");
		
		if(list == null) {
			System.out.println("등록된 게시물이 없습니다. Enter");
			ScanUtil.nextLine();
			return View.HOME;
		}else {		
			for(int i = 0; i < list.size(); i++) {
				Map<String, Object> row = list.get(i);
				String str = "";
				str += SpaceUtil.format(row.get("BO_NO")+"   ", 6, 1);
				str += SpaceUtil.format(EllipseUtil.parse(row.get("BO_TITLE")+"  ", 25)+" ", 25, -1);
				str += SpaceUtil.format(row.get("MEM_NAME")+"  ", 12, 0);
				str += SpaceUtil.format(row.get("BO_CNT")+"  ", 7, 1);
				str += SpaceUtil.format(row.get("BO_DATE")+" ", 16, 1);
				System.out.println(str);
			}
			System.out.println("----------------------------- " + current_page + " 페이지 -------------------------------");
			return 0;
		}
	}


	// 문의글 추가
	public int addPost() { // 비밀번호 선택사항 기능 추가
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		String boWriter = "";
		String boTitle = "";
		String boContent = "";
		
		System.out.print("사원번호 >> ");
		int memNo = ScanUtil.nextInt();
		if(memNo == Integer.parseInt(loginInfo.get("MEM_NO").toString())) { // 입력한 사원번호, 로그인사원번호 비교
			boWriter = loginInfo.get("MEM_NAME").toString();
			
			System.out.print("제목 >> ");
			boTitle = ScanUtil.nextLine();
			
			
			System.out.print("문의 내용을 입력해주세요.(500자 이내) >> ");
			boContent = ScanUtil.nextLine();
			
			// BO_NO, MEM_NO, MEM_NAME, BO_TITLE, BO_CONTENT, BO_DATE, BO_CNT
			List<Object> param = new ArrayList<Object>();
			param.add(memNo);
			param.add(boWriter);
			param.add(boTitle);
			param.add(boContent);
			
			int result = boardDAO.insertRow(param);
			
			if(result > 0) {
				System.out.println("게시물이 등록되었습니다.");
			}else {
				System.out.println("게시물 등록에 실패하였습니다.");
			}
			
		}else {
			System.out.println("작성권한이 없습니다.");
			return View.BOARD_LIST;
		}
		return View.BOARD_LIST;
	}
	
	// 문의글 내용
	public int viewPost() {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		lastBoNum = -1;
		boolean enteredPost = false;
		int boNumber = 0;
		if(lastBoNum == -1) {
			System.out.print("게시물 번호 >> ");
			boNumber = ScanUtil.nextInt();
			lastBoNum = boNumber;
			enteredPost = true;
		}else {
			boNumber = lastBoNum;
		}	
		
		Map<String, Object> post;
		post = boardDAO.getPost(boNumber); 
		
		if(post == null) {
			System.out.println("해당 게시물이 없습니다. Enter");
			ScanUtil.nextLine();
			return View.BOARD_LIST;
		}else {
			if(enteredPost) boardDAO.increaseCount(boNumber);
			
			List<Map<String, Object>> commentList = commentDAO.getCommentList(boNumber);
			
			System.out.println("----------------------------------------------------------------------");
			System.out.println("제    목 : " + post.get("BO_TITLE"));
			System.out.println("작 성 자 : " + post.get("MEM_NAME"));
			System.out.println("작성일시 : " + post.get("BO_DATE"));
			if(enteredPost) {
				System.out.println("조 회 수 : " + (Integer.parseInt(post.get("BO_CNT").toString())+1) );
			}else {
				System.out.println("조 회 수 : " + Integer.parseInt(post.get("BO_CNT").toString()) );
			}
			System.out.println("내    용 : \n" + post.get("BO_CONTENT"));
			System.out.println("----------------------------------------------------------------------");
			
			if (commentList != null ) {
				for(int i = 0; i < commentList.size(); i++) {
					Map<String, Object> comment = commentList.get(i);
					String str = "";
					str += i + 1 + " :: ";
					str += comment.get("C_CONTENT") + "\t";
					str += comment.get("MEM_NAME") + "\t";
					str += comment.get("C_DATE");
					System.out.println(str);
				}
			}else {
				System.out.println("");
			}
			System.out.println("----------------------------------------------------------------------");
			
			Object o = Controller.sessionStorage.get("loginInfo");
			Map<String, Object> loginInfo = (Map<String, Object>) o;
			
			if(Integer.parseInt(loginInfo.get("MEM_NO").toString()) == 100000) { // 관리자
				System.out.print("1.답글작성  2.답글삭제  3.문의글삭제  0.목록 >> ");
				switch (ScanUtil.nextInt()) {
				case 1: return View.BOARD_CO_MENT_INSERT;
				case 2: return View.BOARD_CO_MENT_DELETE;
				case 3: return View.BOARD_DELETE;
				case 0: return View.BOARD_LIST;
				default:
					System.out.println("잘못 눌렀습니다.");
					return View.BOARD_DETAIL;
				}
			}else if(post.get("MEM_NAME").equals(loginInfo.get("MEM_NAME").toString())){ // 작성자
				System.out.print("1.문의글수정  2.댓글작성  3.댓글삭제  0.목록 >> ");
				switch (ScanUtil.nextInt()) {
				case 1: return View.BOARD_UPDATE;
				case 2: return View.BOARD_CO_MENT_INSERT;
				case 3: return View.BOARD_CO_MENT_DELETE;
				case 0: return View.BOARD_LIST;
				default:
					System.out.println("잘못 눌렀습니다.");
					return View.BOARD_DETAIL;
				}
			}else { // 일반
				System.out.print("1.댓글작성  2.댓글삭제  0.목록 >> ");
				switch (ScanUtil.nextInt()) {
				case 1: return View.BOARD_CO_MENT_INSERT;
				case 2: return View.BOARD_CO_MENT_DELETE;
				case 0: return View.BOARD_LIST;
				default:
					System.out.println("잘못 눌렀습니다.");
					return View.BOARD_DETAIL;
				}
			}
		}
	}
	
	
	// 문의글 수정
	public int modifyPost() {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		Map<String, Object> post;
		post = boardDAO.getPost(lastBoNum);
		if(post == null) {
			System.out.println("게시물이 없습니다.");
			return View.BOARD_LIST;
		}

		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		
		if(post.get("MEM_NAME").toString().equals(loginInfo.get("MEM_NAME").toString())) { // 작성자와 사용자가 같은지 확인
			String columnName = "";
			System.out.print("1.제목  2.내용  0.목록 >> ");
			switch (ScanUtil.nextInt()) {
			case 1: columnName = "BO_TITLE"; break;
			case 2: columnName = "BO_CONTENT"; break;
			default: case 0:
				return View.BOARD_LIST;
			}
			System.out.print("변경 내용 >> ");
			String value = ScanUtil.nextLine();
			
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			String boDatetime = sdf.format(new Date());
			
			List<Object> param = new ArrayList<Object>();
			param.add(value);
			param.add(boDatetime);
			param.add(lastBoNum);
			int result = boardDAO.updateRow(columnName, param);
			
			if(result > 0) {
				System.out.println("내용이 변경되었습니다.");
			}else {
				System.out.println("변경 내용을 다시 확인해 주세요.");
			}
			
		}else {
			System.out.println("글 수정 권한이 없습니다.");
		}
		return View.BOARD_DETAIL;
	}
	
	
	// 문의글 삭제
	public int removePost() {
		if( ! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		if(lastBoNum == -1) {
			System.out.print("게시물 번호 >> ");
			lastBoNum = ScanUtil.nextInt();
		}
		
		Map<String, Object> post; // 게시물 확인
		post = boardDAO.getPost(lastBoNum);
		if(post == null) {
			System.out.println("게시물이 없습니다.");
			return View.BOARD_LIST;
		}
		
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		
		if(Integer.parseInt(loginInfo.get("MEM_NO").toString()) == 100000){
			
			System.out.print("정말 삭제하시겠습니까? (y/n) >> ");
			if(ScanUtil.nextLine().equals("n")) return View.BOARD_DETAIL;
			
			int result = boardDAO.deleteRow(lastBoNum);
			if(result > 0) {
				System.out.println("문의글이 삭제되었습니다.");
				return View.BOARD_LIST;
			}
		} else {
			System.out.println("글 삭제 권한이 없습니다.");
		}
		return View.BOARD_DETAIL;
	}
	
	
	
	
	
	
}
