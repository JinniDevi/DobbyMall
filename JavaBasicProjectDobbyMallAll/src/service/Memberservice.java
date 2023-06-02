package service;
//멤버서비스
//싱글톤
//회원로그인 정보 퍼플릭 스태틱
//221211 초급 프로젝트
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.MemberDAO;
import util.EllipseUtil;
import util.JDBCUtil;
import util.ScanUtil;
import util.SpaceUtil;
import util.ValidPassUtil;
import util.View;

public class Memberservice {
	private static Memberservice instance = null;
	private Memberservice() {}
	public static Memberservice getInstance() {
		if(instance == null) instance = new Memberservice();
		return instance;
	}
	
	MemberDAO memberDAO = MemberDAO.getInstance();
	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	
	int current_page =1;
	int page_row = 10;
	

	public static boolean isLogin() {
		if(Controller.sessionStorage.get("loginInfo") == null) {
			return false;
		}else {
			return true;
		}
	}
	
	// 신규회원 등록
	public int memEnroll() {
		if (!isLogin())	return View.MEMBER_LOGIN;

		System.out.println("--------------- 회원등록 -----------------");
		System.out.println("사원번호를 입력하세요. (숫자 6자리) >> \n0.취소");
		int memNo = ScanUtil.nextInt();
		
		List<Object> param = new ArrayList<>();
		param.add(memNo);
		
		if(memNo == 0) {
			return View.ADMINHOME;
		}else if( (int)(Math.log10(memNo)+1) != 6 ) {
			System.out.println("사원번호는 숫자 6자리입니다.");
			return View.MEMBER_ENROLL;
		}else {
			Map<String, Object> checkmember = memberDAO.memNoCheck(param);
			
			if (checkmember != null) {
				System.out.println("이미 등록된 사원번호입니다.");
				return View.MEMBER_ENROLL;
			} else {
				System.out.print("이름을 입력하세요. >> ");
				String memName = ScanUtil.nextLine();
				String memPass = Integer.toString(memNo);
				int memPoint = 500000;
				
				param = new ArrayList<>();
				param.add(memNo);
				param.add(memPass);
				param.add(memName);
				param.add(memPoint);
				
				int result = memberDAO.memEnroll(param);
				
				if (result > 0) {
					System.out.println("회원등록 성공!");
					return View.ADMINHOME;
				} else {
					System.out.println("회원등록 실패!");
					return View.ADMINHOME;
				}
			}
			
		}

	}

	// 회원정보 수정
	public int memModify() {
		if (!isLogin())	return View.MEMBER_LOGIN;

		int memNo;
		List<Object> param = new ArrayList<>();
		Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		
		boolean adminCheck = false;
		
		if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
			adminCheck = true;
			System.out.println("수정할 회원의 사원번호를 입력하세요.  0.취소");
			memNo = ScanUtil.nextInt();
			if(memNo == 0) {
				return View.ADMINHOME;
			}
			param.add(memNo);

			Map<String, Object> result = memberDAO.memNoCheck(param);
			if (result != null) {

			} else {
				System.out.println("해당 회원이 없습니다.");
				return View.MEMBER_MODIFY;
			}

		} else {
			memNo = Integer.parseInt(memberInfo.get("MEM_NO").toString());
		}
		
		param = new ArrayList<>();
		
		
		System.out.println("수정할 정보를 선택하세요.");
		if(! adminCheck) {
			System.out.println("1.이름  2.주소  3.생일  4.비밀번호  5.전화번호  0.취소");
		}else {
			System.out.println("1.이름  2.주소  3.생일  4.비밀번호  5.전화번호  \n6.입사일  7.퇴사일  8.포인트  0.취소");
		}
		
		int result=0;
		String what="";
		
		switch (ScanUtil.nextInt()) {
		case 1:
			System.out.println("수정할 이름을 입력하세요. >> ");
			String memName = ScanUtil.nextLine();
			param.add(memName);
			param.add(memNo);
			what="이름";
			result = memberDAO.memUpdateInfo(param,"memName");
			break;
		case 2:
			System.out.println("수정할 주소를 입력하세요. >> ");
			String memAddr = ScanUtil.nextLine();
			param.add(memAddr);
			param.add(memNo);
			what="주소";
			result = memberDAO.memUpdateInfo(param,"memAddr");
			break;
		case 3:
			System.out.println("수정할 생일을 입력하세요. (YYYYMMDD) >> ");
			String memBir = ScanUtil.nextLine();
			memBir = memBir.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
			param.add(memBir);
			param.add(memNo);
			what="생일";
			result = memberDAO.memUpdateInfo(param,"memBir");
			break;
		case 4:
			System.out.println("수정할 비밀번호를 입력하세요. >> ");
			System.out.println("(비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다.)");
			String memPass = ScanUtil.nextLine();
			
			ValidPassUtil vpu = new ValidPassUtil();
			String checkPass = vpu.checkPassword(memPass, memNo+"");
			
			if( checkPass.equals("성공") || adminCheck == true ) {
				// 회원일 땐 비번변경규칙 성공이어야만 조건문 부합, 관리자일 땐 아무렇게나 변경 가능
				param.add(memPass);
				param.add(memNo);
				what="비밀번호";
				result = memberDAO.memUpdateInfo(param,"memPass");
			} else {
				System.out.println(checkPass);
				return View.MEMBER_MODIFY;
			}
			break;
		case 5:
			System.out.println("수정할 전화번호를 입력하세요. >> ");
			String memTel = ScanUtil.nextLine();
			param.add(memTel);
			param.add(memNo);
			what="전화번호";
			result = memberDAO.memUpdateInfo(param,"memTel");
			break;
		case 6:
			if(adminCheck) {
				System.out.println("수정할 입사일을 입력하세요. (YYYYMMDD) >> ");
				String memHiredate = ScanUtil.nextLine();
				memHiredate = memHiredate.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
				param.add(memHiredate);
				param.add(memNo);
				what="입사일";
				result = memberDAO.memUpdateInfo(param,"memHiredate");
			}else {
				return View.HOME;
			}
			break;
		case 7:
			if(adminCheck) {
				System.out.println("수정할 퇴사일을 입력하세요. (YYYYMMDD) >> ");
				String memRetiredate = ScanUtil.nextLine();
				memRetiredate = memRetiredate.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
				param.add(memRetiredate);
				param.add(memNo);
				what="퇴사일";
				result = memberDAO.memUpdateInfo(param,"memRetiredate");
			}else {
				return View.HOME;
			}
			break;
		case 8:
			if(adminCheck) {
				System.out.println("수정할 포인트를 입력하세요. >> ");
				String memPoint = ScanUtil.nextLine();
				param.add(memPoint);
				param.add(memNo);
				what="포인트";
				result = memberDAO.memUpdateInfo(param,"memPoint");
			}else {
				return View.HOME;
			}
			break;
		case 0:
			if(adminCheck) {
				return View.ADMINHOME;
			}else {
				return View.HOME;
			}
		default: return View.MEMBER_MODIFY;
		}
		
		if (result > 0) {
			// 비밀번호변경일 업뎃
			List<Object> paramPasschange = new ArrayList<>();
			paramPasschange.add(memNo);
			memberDAO.updateDate(paramPasschange, "memPasschange");
			
			if(!adminCheck) {
				// 업뎃 후 업뎃회원정보 불러오기
				Map<String, Object> loginInfo = memberDAO.getMemListDetail( Integer.parseInt( memberInfo.get("MEM_NO").toString() ) );
				// DB 조회해서 새로 가져온 정보 + 수정 시 마다 세션에 업뎃
				Controller.sessionStorage.put("loginInfo", loginInfo);
			}
			System.out.println(what + " 변경 성공! enter");
			ScanUtil.nextLine();
			if(! adminCheck) {
				return View.MEMBER_MYINFO;
			}else {
				return View.ADMINHOME;
			}
		} else {
			System.out.println(what + " 변경 실패! enter");
			if(! adminCheck) {
				return View.MEMBER_MYINFO;
			}else {
				return View.ADMINHOME;
			}
		}
	}

	// 회원 삭제 - 관리자 여부 체크 컬럼 추가
	public int memDelete() {
		if (!isLogin()) return View.MEMBER_LOGIN;

		int memNo;
		List<Object> param = new ArrayList<>(); // 회원정보 체크 파라미터
		Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		
		List<Object> paramDeleteTarget = new ArrayList<>(); // 삭제/탈퇴 하고자 하는 사원정보 입력 파라미터
		
		boolean adminCheck = false;
		String memberName = "";
		
		if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
			adminCheck = true;
			System.out.println("삭제할 회원의 사원번호를 입력하세요.  0.종료");
			memNo = ScanUtil.nextInt();
			if(memNo == 0) {
				return View.ADMINHOME;
			}else if(memNo == 100000) {
				System.out.println("관리자는 삭제할 수 없습니다.");
				return View.ADMINHOME;
			}
			
			param.add(memNo); // ??????????????? 왜 있었지...
			// 관리자는 memNo을 입력한 값을 받아옴

			Map<String, Object> result = memberDAO.memNoCheck(param);
			if (result != null) {
				memberName = result.get("MEM_NAME").toString();

			} else {
				System.out.println("해당 회원이 없습니다.");
				return View.MEMBER_DELETE;
			}

		} else {
			// 일반회원은 memNo을 세션 로그인정보에서 받아옴
			memNo = Integer.parseInt(memberInfo.get("MEM_NO").toString());
			System.out.println("비밀번호를 입력하세요.");
			String password = ScanUtil.nextLine();
			param.add(memNo);
			param.add(password);
			Map<String, Object> passCheck = memberDAO.memLogin(param);
			
			if(passCheck == null) {
				System.out.println("비밀번호가 틀렸습니다.");
				return View.HOME; 
			}else {
				paramDeleteTarget.add(memNo);
			}
		}
		 
		if(adminCheck) {
			System.out.println(memberName +" 회원을 삭제하시겠습니까?  1.삭제  0.취소");
		} else {
			System.out.println("정말 탈퇴하시겠습니까?  1.탈퇴  0.취소");
		}
		
		switch(ScanUtil.nextInt()) {
		case 1:
			int result = memberDAO.memDelete(paramDeleteTarget);
			
			if(result > 0) {
				System.out.println("처리되었습니다.");
			}else {
				System.out.println("실패하였습니다.");
			}
			break;
		case 0:
			if(adminCheck) {
				return View.ADMINHOME;
			}else {
				return View.HOME;
			}
		default:
			return View.MEMBER_DELETE;
		}
		
		
		if(adminCheck) {
			return View.ADMINHOME;
		}else {
			return View.MEMBER_LOGOUT;
		}
	}

	// 회원 로그인
	public int memLogin() {

		System.out.println("-- 로그인 --");
		System.out.print("사원번호 >> ");
		int memNo = ScanUtil.nextInt();
		System.out.print("비밀번호 >> ");
		String memPass = ScanUtil.nextLine();

		List<Object> param = new ArrayList<>();
		param.add(memNo);
		param.add(memPass);
		Map<String, Object> memberInfo = memberDAO.memLogin(param);
		
		List<Object> paramMemLastLogin = new ArrayList<>();
		
		paramMemLastLogin.add(memNo);
		Map<String, Object> memDateCheckMap = memberDAO.memDateCheck(paramMemLastLogin);

		if (memberInfo == null) {
			System.out.println("일치하는 사원번호 혹은 비밀번호가 없습니다.");
			return View.HOME;
			
//		} else if(Controller.sessionStorage.get("MEM_DELETED") != null){ // 세션 저장 후의 정보
		} else if(memberInfo.get("MEM_DELETED") != null) {
			System.out.println("탈퇴한 회원입니다.");
			return View.HOME;

		} else if (memberInfo.get("MEM_1STLOGIN").equals("N")) { // 세션 저장 전의 DB 정보
			System.out.println(memberInfo.get("MEM_NAME") + "님 최초 로그인하셨습니다!");
			System.out.println("비밀번호를 다시 설정해주세요!");
			System.out.println("(비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다.)");
			memPass = ScanUtil.nextLine();
			
			ValidPassUtil vpu = new ValidPassUtil();
			String checkPass = vpu.checkPassword(memPass, memNo+"");
			
			if( checkPass.equals("성공")) {
				// 회원일 땐 비번변경규칙 성공이어야만 조건문 부합
			} else {
				System.out.println(checkPass);
				return View.MEMBER_LOGIN;
			}

			param = new ArrayList<>();
			param.add(memPass);
			param.add(memNo);

			int result = memberDAO.memUpdatePassword1st(param);

			if (result > 0) {
				Controller.sessionStorage.put("loginInfo", memberInfo);
				System.out.println("비밀번호 변경 성공!");
				
				// 비밀번호변경일 업뎃
				List<Object> paramPasschange = new ArrayList<>();
				paramPasschange.add(memNo);
				memberDAO.updateDate(paramPasschange, "memPasschange");
				System.out.println(memberInfo.get("MEM_NAME") + "님 환영합니다! enter");
				ScanUtil.nextLine();
				return View.HOME;
			} else {
				System.out.println("비밀번호 변경 실패!");
				return View.HOME;
			}
			
		// 휴면회원 체크
		} else if( Integer.parseInt( memDateCheckMap.get("MEM_LASTLOGIN").toString() )  >= 365  ) {
			System.out.println("휴면 상태입니다.");
			System.out.println("이름을 입력하세요.");
			
			
			if( !ScanUtil.nextLine().equals(memberInfo.get("MEM_NAME")) ) {
				System.out.println("이름이 일치하지 않습니다.");
				return View.HOME;
			} 
			
			System.out.println("전화번호를 입력하세요.");
			if( !ScanUtil.nextLine().replaceAll("-", "").equals( memberInfo.get("MEM_TEL").toString().replaceAll("-", "") ) ) {
				System.out.println("전화번호가 일치하지 않습니다.");
				return View.HOME;
			}
			
			memberDAO.updateDate(paramMemLastLogin, "memLastlogin");
			System.out.println("휴면 상태를 해제했습니다. \n다시 로그인 해주세요");
			return View.MEMBER_LOGIN;
			
		// 비번변경대상자 체크
		} else if( Integer.parseInt( memDateCheckMap.get("MEM_PASSCHANGE").toString() )  >= 90 ) {
			System.out.println("비밀번호 변경 대상자입니다.");
			System.out.println("현재 비밀번호를 입력하세요.");
			
			if( !ScanUtil.nextLine().equals(memberInfo.get("MEM_PASS")) ) {
				System.out.println("현재 비밀번호와 일치하지 않습니다.");
				return View.HOME;
			} 
			
			System.out.println("변경할 비밀번호를 입력하세요.");
			System.out.println("(비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다.)");
			
			memPass = ScanUtil.nextLine();
			
			ValidPassUtil vpu = new ValidPassUtil();
			String checkPass = vpu.checkPassword(memPass, memNo+"");
			
			if( checkPass.equals("성공")) {
				// 회원일 땐 비번변경규칙 성공이어야만 조건문 부합
			} else {
				System.out.println(checkPass);
				return View.MEMBER_LOGIN;
			}
			
			// 목록은 List 상세는 Map
			List<Object> paramPassChange = new ArrayList<>();
			paramPassChange.add(memPass);
			paramPassChange.add(memNo);
			
			// 비번 업뎃
			int result = memberDAO.memUpdatePassword1st(paramPassChange);

			if (result > 0) {
				// 세션에 회원정보 저장
				Controller.sessionStorage.put("loginInfo", memberInfo);
				System.out.println("비밀번호 변경 성공!");
				
				// 비밀번호변경일 업뎃
				List<Object> paramPasschange = new ArrayList<>();
				paramPasschange.add(memNo);
				memberDAO.updateDate(paramPasschange, "memPasschange");
				System.out.println(memberInfo.get("MEM_NAME") + "님 환영합니다! enter");
				ScanUtil.nextLine();
				return View.HOME;
			} else {
				System.out.println("비밀번호 변경 실패!");
				return View.HOME;
			}
			
			
		} else if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
			Controller.sessionStorage.put("loginInfo", memberInfo);
			
			// 로그인 시 최근접속 업뎃
			List<Object> paramLastlogin = new ArrayList<>();
			paramLastlogin.add(memNo);
			memberDAO.updateDate(paramLastlogin, "memLastlogin");
			
			System.out.println("관리자 모드로 로그인하셨습니다. enter");
			ScanUtil.nextLine();
			return View.ADMINHOME;
		
		} else {
			Controller.sessionStorage.put("loginInfo", memberInfo);
			
			// 로그인 시 최근접속 업뎃
			List<Object> paramLastlogin = new ArrayList<>();
			paramLastlogin.add(memNo);
			memberDAO.updateDate(paramLastlogin, "memLastlogin");
			
			System.out.println(memberInfo.get("MEM_NAME") + "님 환영합니다! enter");
			ScanUtil.nextLine();
			return View.HOME;

		}

	}

	// 회원 로그아웃
	public int memLogout() {
		if (!isLogin())	return View.MEMBER_LOGIN;
		
		Object o = Controller.sessionStorage.get("loginInfo");
		Map<String, Object> loginInfo = (Map<String, Object>) o;
		
//		System.out.println("로그인:" +Controller.sessionStorage);
		
		System.out.println(loginInfo.get("MEM_NAME") + "님 로그아웃 되었습니다.");
		
		Controller.sessionStorage.put("loginInfo", null);
		Controller.sessionStorage.remove("loginInfo");
		
//		System.out.println("로그아웃:" +Controller.sessionStorage);
		
		return View.HOME;
	}

	//2022-12-12**************************************************************************
	
	// 페이징
	public List<Object> page(int current_page) {
		int page_row = 10;
		
		List<Object> page = new ArrayList<>();
		if(current_page < 1) current_page =1; 
		
		page.add(page_row);
		page.add(current_page);
		page.add(page_row);
		page.add(current_page);
		
		return page;
	}
	
	// 회원 조회
	public int memList() {
		if (!isLogin())	return View.MEMBER_LOGIN;
		
		seletedMemListDetail = -1; // 리스트로 돌아가면 멤버선택디테일 초기화
		
		showMemList(memberDAO.getMemList(page(current_page)));
		
		while(true) {
		System.out.println("1.회원선택  2.이전페이지  3.다음페이지  0.돌아가기  >> ");
		System.out.println("번호를 선택하세요. >> ");
		
			switch(ScanUtil.nextInt()) {
			case 1 : return View.MEMBER_LISTDETAIL;
			case 2 : 
				current_page--;
				if(current_page < 1) current_page =1; 
				showMemList(memberDAO.getMemList(page(current_page)));
				break;
			case 3 : 
				// 다음 페이지가 널인지 아닌지 검증
				if(memberDAO.getMemList(page(current_page + 1)) != null) {
					current_page++;
					showMemList(memberDAO.getMemList(page(current_page)));
					break;
				}else { // 마지막 페이지에서 첫페이지로
					System.out.println("첫페이지로 돌아왔습니다.");
					current_page = 1;
					showMemList(memberDAO.getMemList(page(current_page)));
					break;
				}
			case 0 : return View.ADMINHOME;
			default :
				System.out.println("잘못 눌렀습니다.");
				return View.MEMBER_LIST;
			}
		}
	}
	
	// 회원정보 페이징
	public int showMemList(List<Map<String, Object>> list) {
		System.out.println("------------------------------------------------- 회원 목록 --------------------------------------------------");
		System.out.println(" 사원번호   이름     포인트       생일            주소           전화번호         최근접속        비밀번호변경    최초로그인");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		if(list == null) {
			System.out.println("등록된 회원이 없습니다. enter");
			ScanUtil.nextLine();
			return View.ADMINHOME;
		}else {
			for(int i = 0; i < list.size(); i++) {
				Map<String, Object> row = list.get(i);
				String str = "";
				str += SpaceUtil.format(row.get("MEM_NO").toString(), 8, 0);
				str += SpaceUtil.format(row.get("MEM_NAME"), 9, 0);
				str += SpaceUtil.format(row.get("MEM_POINT").toString(), 8, 0);
				str += SpaceUtil.format(row.get("MEM_BIR"), 15, 0);
				str += SpaceUtil.format(EllipseUtil.parse(row.get("MEM_ADDR"), 20), 20, -1);
				str += SpaceUtil.format(row.get("MEM_TEL"), 15, -1);
				str += SpaceUtil.format(EllipseUtil.parse(row.get("MEM_LASTLOGIN"), 15), 15, -1);
				str += SpaceUtil.format(EllipseUtil.parse(row.get("MEM_PASSCHANGE"), 15), 15, -1);
				str += SpaceUtil.format(row.get("MEM_1STLOGIN"), 3, 0);
				
				System.out.println(str);
			}
			System.out.println("-------------------------------------------------- "+ current_page + "페이지 --------------------------------------------------");
			return 0;
		}
	}
	
	
	//2022-12-13******************************************************************************************************************
	
	
	public static int seletedMemListDetail = -1;
	
	
	public int memListDetail() {
		if (!isLogin())	return View.MEMBER_LOGIN;
		
		int memNumber = 0;
		boolean isSelectedMem = false;
		
		
		if(seletedMemListDetail == -1) {
			System.out.println("사원번호를 선택하세요. >> ");
			memNumber = ScanUtil.nextInt();
			seletedMemListDetail = memNumber;
			isSelectedMem = true;
		}else {
			memNumber = seletedMemListDetail;
		}
		
		Map<String, Object> meminfo;
		meminfo = memberDAO.getMemListDetail(memNumber);
		
		if(meminfo == null) {
			System.out.println("해당 회원이 없습니다. enter");
			ScanUtil.nextLine();
			return View.MEMBER_LIST;
		}else {
			if(isSelectedMem) memberDAO.getMemListDetail(memNumber);
			
			System.out.println("-----------------------------------------------------------------");
			System.out.println("사원번호 : " + meminfo.get("MEM_NO"));
			System.out.println("이름 : " + meminfo.get("MEM_NAME"));
			System.out.println("포인트 : " + meminfo.get("MEM_POINT"));
			System.out.println("생일 : " + meminfo.get("MEM_BIR"));
			System.out.println("주소 : " + meminfo.get("MEM_ADDR"));
			System.out.println("전화번호 : " + meminfo.get("MEM_TEL"));
			System.out.println("입사일 : " + meminfo.get("MEM_HIREDATE"));
			System.out.println("퇴사일 : " + meminfo.get("MEM_RETIREDATE"));
			System.out.println("-----------------------------------------------------------------");
			System.out.println("최근접속일 : " + meminfo.get("MEM_LASTLOGIN"));
			System.out.println("비밀번호변경일 : " + meminfo.get("MEM_PASSCHANGE"));
			System.out.println("최초로그인여부 : " + meminfo.get("MEM_1STLOGIN"));
			System.out.println("-----------------------------------------------------------------");
			System.out.println("1.회원정보수정  2.회원삭제  0.뒤로");
			
			
			Object o = Controller.sessionStorage.get("loginInfo");
			Map<String, Object> loginInfo = (Map<String, Object>) o;
			
			switch(ScanUtil.nextInt()) {
			case 1:
				return View.MEMBER_LISTDETAIL_MODIFY;
			case 2:
				return View.MEMBER_LISTDETAIL_DELETE;
			case 0:
				return View.MEMBER_LIST;
			default:
				System.out.println("잘못 누르셨습니다.");
				return View.MEMBER_LISTDETAIL;
				
			}
			
			
		}
		
		
	}

	public int memListDetailModify() {
		if (!isLogin())	return View.MEMBER_LOGIN;

		List<Object> param = new ArrayList<>();
		
		Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		
		
		// 선택된 회원의 정보 맵에 저장
		Map<String, Object> selectedMem = memberDAO.getMemListDetail(seletedMemListDetail);
//		System.out.println(selectedMem);
		
		// 맵에 저장된 회원의 MEM_NO 추출하여 memNo에 저장 
		// >> 추출된 정보는 object 타입이므로 toStirng 해주고 integer 변환
		int memNo = Integer.parseInt(selectedMem.get("MEM_NO").toString());
		
		boolean adminCheck = false;
		// 관리자라면
		if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
			adminCheck = true;
			
			
			if(adminCheck) {
				System.out.println("수정할 정보를 선택하세요.");
				System.out.println("1.비밀번호  2.이름  3.포인트  4.생일  5.주소");
				System.out.println("6.전화번호  7.입사일  8.퇴사일  0.취소");
				
				param = new ArrayList<>();
				int result = 0;
				String what = "";
				
				
				switch (ScanUtil.nextInt()) {
				
				case 1:
					System.out.println("수정할 비밀번호를 입력하세요. >> ");
					String memPass = ScanUtil.nextLine();
					param.add(memPass);
					param.add(memNo);
					what="비밀번호";
					result = memberDAO.memUpdateInfo(param,"memPass");
					break;
				case 2:
					System.out.println("수정할 이름을 입력하세요. >> ");
					String memName = ScanUtil.nextLine();
					param.add(memName);
					param.add(memNo);
					what="이름";
					result = memberDAO.memUpdateInfo(param,"memName");
					break;
				case 3:
					System.out.println("수정할 포인트를 입력하세요. >> ");
					String memPoint = ScanUtil.nextLine();
					param.add(memPoint);
					param.add(memNo);
					what="포인트";
					result = memberDAO.memUpdateInfo(param,"memPoint");
					break;
				case 4:
					System.out.println("수정할 생일을 입력하세요. (YYYYMMDD) >> ");
					String memBir = ScanUtil.nextLine();
					memBir = memBir.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
					param.add(memBir);
					param.add(memNo);
					what="생일";
					result = memberDAO.memUpdateInfo(param,"memBir");
					break;
				case 5:
					System.out.println("수정할 주소를 입력하세요. >> ");
					String memAddr = ScanUtil.nextLine();
					param.add(memAddr);
					param.add(memNo);
					what="주소";
					result = memberDAO.memUpdateInfo(param,"memAddr");
					break;
				case 6:
					System.out.println("수정할 전화번호를 입력하세요. (000-0000-0000) >> ");
					String memTel = ScanUtil.nextLine();
					memTel = memTel.replaceAll(" ", "").replaceAll("-","");
					param.add(memTel);
					param.add(memNo);
					what="전화번호";
					result = memberDAO.memUpdateInfo(param,"memTel");
					break;
				case 7:
					System.out.println("수정할 입사일을 입력하세요. (YYYYMMDD) >> ");
					try{
						String memHiredate = ScanUtil.nextLine();
						memHiredate = memHiredate.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
						param.add(memHiredate);
						param.add(memNo);
						what="입사일";
						result = memberDAO.memUpdateInfo(param,"memHiredate");
					}catch(Exception e){
						System.out.println("날짜 형식에 맞게 입력하세요.");
					}
					break; 
				case 8:
					System.out.println("수정할 퇴사일을 입력하세요. (YYYYMMDD) >> ");
					String memRetiredate = ScanUtil.nextLine();
					memRetiredate = memRetiredate.replace("년", "-").replace("월", "-").replace("일", "").replaceAll(" ","");
					param.add(memRetiredate);
					param.add(memNo);
					what="퇴사일";
					result = memberDAO.memUpdateInfo(param,"memRetiredate");
					break;
				case 0:
					return View.MEMBER_LIST;
				default: 
					System.out.println("잘못 누르셨습니다.");
					return View.MEMBER_LISTDETAIL;
				}
				
				if(result>0) {
					
					// 비밀번호변경일 업뎃
					List<Object> paramPasschange = new ArrayList<>();
					paramPasschange.add(memNo);
					memberDAO.updateDate(paramPasschange, "memPasschange");
					
					System.out.println(what + " 수정 성공!");
				}else {
					System.out.println(what +" 수정 실패!");
				}
				
			// 관리자가 아니라면	
			}else {
				return View.MEMBER_LOGIN;
			}
			
		// 관리자가 아니라면
		} else {
			return View.MEMBER_LOGIN;
		}
		
		return View.MEMBER_LISTDETAIL;
	}

	public int memListDetailDelete() {
		if (!isLogin()) return View.MEMBER_LOGIN;

		List<Object> param = new ArrayList<>();
		Map<String, Object> memberInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		
		Map<String, Object> selectedMem = memberDAO.getMemListDetail(seletedMemListDetail);
		int memNo = Integer.parseInt(selectedMem.get("MEM_NO").toString());
		
		boolean adminCheck = false;
		String memberName = "";
		
		// 관리자라면
		if (Integer.parseInt(memberInfo.get("MEM_NO").toString()) == 100000) {
			adminCheck = true;
			
			if(memNo == 100000) {
				System.out.println("관리자 정보는 삭제할 수 없습니다.");
				return View.MEMBER_LISTDETAIL;
			}else {
				System.out.println("선택한 " + selectedMem.get("MEM_NAME").toString() + "님의 정보를 삭제하시겠습니까?  1.삭제  0.취소");
				
				switch(ScanUtil.nextInt()) {
				case 1:
					param.add(memNo); // 삭제 쿼리 실행 전에 채워줘야 함
					int result = memberDAO.memDelete(param); // 삭제 행위를 result에 담음
					if(result>0) {
						System.out.println("해당 회원이 삭제되었습니다.");
						return View.MEMBER_LIST;
					}else {
						System.out.println("삭제 실패하였습니다.");
						return View.MEMBER_LISTDETAIL;
					}
				case 0:
					if(adminCheck) {
						return View.MEMBER_LISTDETAIL;
					}else {
						return View.HOME;
					}
				default:
					System.out.println("잘못 누르셨습니다.");
					return View.MEMBER_LISTDETAIL;
				}
			
			}
		// 관리자 아니라면
		} else {
			return View.MEMBER_LOGIN;
		}
				
		
		
	}

	public int memMyInfo() {
		if (!isLogin())	return View.MEMBER_LOGIN;
		
		// 로그인 시 세션에 저장된 정보
		Map<String, Object> sessionLoginInfo = (Map<String, Object>) Controller.sessionStorage.get("loginInfo");
		
		// DB 조회해서 새로 가져온 정보 + 수정 시 마다 세션에 업뎃
		Map<String, Object> loginInfo = memberDAO.getMemListDetail( Integer.parseInt( sessionLoginInfo.get("MEM_NO").toString() ) );
	
		// 오라클에서 문자열로 받음, 널값도 문자열
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("사원번호 : " + loginInfo.get("MEM_NO"));
		System.out.println("이름 : " + loginInfo.get("MEM_NAME"));
		System.out.println("포인트 : " + loginInfo.get("MEM_POINT"));
		System.out.println("생일 : " + loginInfo.get("MEM_BIR"));
		System.out.println("주소 : " + loginInfo.get("MEM_ADDR"));
		System.out.println("전화번호 : " + loginInfo.get("MEM_TEL"));
		System.out.println("입사일 : " + loginInfo.get("MEM_HIREDATE"));
		System.out.println("퇴사일 : " + loginInfo.get("MEM_RETIREDATE")); 
		System.out.println("-----------------------------------------------------------------");
		System.out.println("1.회원정보수정  2.회원탈퇴  0.뒤로");
		

		switch(ScanUtil.nextInt()) {
		case 1:
			return View.MEMBER_MODIFY;
		case 2:
			return View.MEMBER_DELETE;
		case 0:
			return View.HOME;
		default:
			System.out.println("잘못 누르셨습니다.");
			return View.MEMBER_MYINFO;
			
		}
		
	}
		
}
