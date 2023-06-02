package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.Co_mentDAO;
import util.ScanUtil;
import util.View;

public class Co_mentService {
	private static Co_mentService instance = null;
	private Co_mentService () {}
	public static Co_mentService getInstance() {
		if(instance == null) instance = new Co_mentService();
		return instance;
	}
	

	Co_mentDAO commentDAO = Co_mentDAO.getInstance();
	
	public int addComment() {
		if(! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		String reWriter = "관리자";

		System.out.print("사원번호 >> ");
		int memNo = ScanUtil.nextInt();
		if(memNo == Integer.parseInt(loginInfo.get("MEM_NO").toString())) { // 입력한 사원번호, 로그인사원번호 비교
			reWriter = loginInfo.get("MEM_NAME").toString();
			
		System.out.print("답글 내용 >> ");
		String reContent = ScanUtil.nextLine();
				
		List<Object> param = new ArrayList<Object>();
		param.add(reContent);
		param.add(BoardService.lastBoNum);
		param.add(reWriter);
		
		int result = commentDAO.insertRow(param);
		
		if(result > 0) {
			System.out.println("답글을 등록하셨습니다.");
			return View.BOARD_LIST;
		}else {
			System.out.println("다시 시도해주세요.");
		}
	}else {
		System.out.println("작성 권한이 없습니다.");
		return View.BOARD_DETAIL;		
	}
		return View.BOARD_DETAIL;		
	}
	
	
	public int removeComment() {
		if(! Memberservice.isLogin()) return View.MEMBER_LOGIN;
		
		List<Map<String, Object>> commentList; // 댓글 넘버
		commentList = commentDAO.getCommentList(BoardService.lastBoNum);
		
		if(commentList == null) {
			System.out.println("댓글이 없습니다. >> Enter");
			ScanUtil.nextLine();
			return View.BOARD_DETAIL;
		}else {
			System.out.print("삭제 할 댓글 번호 >> ");
			int input = ScanUtil.nextInt();
			
			if(input < 1 || input > commentList.size()) {
				System.out.println("번호를 잘못 입력했습니다.");
				return View.BOARD_DETAIL;
			}
			
			Map<String, Object> targetRow = commentList.get(input - 1);
			
			Object o = Controller.sessionStorage.get("loginInfo");
			Map<String, Object> loginInfo = (Map<String, Object>) o;
			
			if(Integer.parseInt(loginInfo.get("MEM_NO").toString()) == 100000) {
				
				System.out.print("정말 삭제하시겠습니까? (y/n) >> ");
				if(ScanUtil.nextLine().equals("n")) return View.BOARD_DETAIL;
				
				int result = commentDAO.deletRow(Integer.parseInt(targetRow.get("C_NO").toString()));
				if(result > 0) {
					System.out.println("선택하신 답글이 삭제되었습니다.");
					return View.BOARD_LIST;
				}
			}
		}
		System.out.println("글 삭제 권한이 없습니다.");
		return View.BOARD_DETAIL;
	}
	
}
