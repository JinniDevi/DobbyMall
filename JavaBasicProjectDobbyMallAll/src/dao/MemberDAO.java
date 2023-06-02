package dao;
//싱글톤

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class MemberDAO {
	private static MemberDAO instance = null;
	private MemberDAO () {}
	public static MemberDAO getInstance() {
		if(instance == null) instance = new MemberDAO();
		return instance;
	}
	
	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	public int memEnroll(List<Object> param) {
		String sql = "INSERT INTO MEMBER "
				+ " (MEM_NO, MEM_PASS, MEM_NAME, MEM_POINT) "
				+ " VALUES "
				+ " (?, ?, ?, ?) ";
		return jdbc.update(sql, param);
	}

	public Map<String, Object> memLogin(List<Object> param) {
		String sql = "SELECT * FROM "
				+ " (SELECT "
				+ " MEM_NO, MEM_NAME, MEM_PASS, TRIM(TO_CHAR(MEM_POINT,'999,999,999'))||'원' AS MEM_POINT, "
				+ " NVL(MEM_BIR, '0') AS MEM_BIR, MEM_ADDR, SUBSTR(MEM_TEL,1,3)||'-'||SUBSTR(MEM_TEL,4,4)||'-'||SUBSTR(MEM_TEL,8,4) AS MEM_TEL, "
				+ " NVL(MEM_HIREDATE, '0') AS MEM_HIREDATE, NVL(MEM_RETIREDATE, '0') AS MEM_RETIREDATE, "
				+ " MEM_1STLOGIN, NVL(MEM_LASTLOGIN, '0') AS MEM_LASTLOGIN, NVL(MEM_PASSCHANGE, '0') AS MEM_PASSCHANGE, "
				+ " MEM_ROCKED, MEM_DELETED "
				+ " FROM MEMBER ) A"
				+ " WHERE A.MEM_NO=? AND A.MEM_PASS=? ";
		return jdbc.selectOne(sql, param);
	}
	
	public int memUpdatePassword1st(List<Object> param) {
		String sql = "UPDATE MEMBER SET MEM_PASS = ?, MEM_1STLOGIN = 'Y' "
				+ " WHERE MEM_NO=? ";
		return jdbc.update(sql, param);
	}
	
	public Map<String, Object> memNoCheck(List<Object> param) {
		String sql = "SELECT * FROM MEMBER "
				+ " WHERE MEM_NO=? ";
		return jdbc.selectOne(sql, param);
	}
	
	public int memUpdateInfo(List<Object> param, String what) {
		String sql = " UPDATE MEMBER SET ";
		if (what.equals("memName")) {
			sql += " MEM_NAME =? ";
		} else if(what.equals("memAddr")) {
			sql += " MEM_ADDR = ? ";
		} else if(what.equals("memBir")) {
			sql += " MEM_BIR = TO_DATE( ? , 'YYYY-MM-DD') ";
		} else if(what.equals("memPass")) {
			sql += " MEM_PASS = ? ";
		} else if(what.equals("memHiredate")) {
			sql += " MEM_HIREDATE = TO_DATE( ? , 'YYYY-MM-DD') ";
		} else if(what.equals("memRetiredate")) {
			sql += " MEM_RETIREDATE = TO_DATE( ? , 'YYYY-MM-DD') ";
		} else if(what.equals("memPoint")) {
			sql += " MEM_POINT= ? ";
		} else if(what.equals("memTel")) {
			sql += " MEM_TEL= ? ";
		}
		sql += " WHERE MEM_NO = ?";
		return jdbc.update(sql, param);
	}
	
	public int memDelete(List<Object> paramDeleteTarget) {
		String sql = "UPDATE MEMBER SET MEM_DELETED = TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') WHERE MEM_NO = ?" ;
		return jdbc.update(sql, paramDeleteTarget);
	}
	
	//2022-12-12*************************************************************************
	
	public List<Map<String, Object>> memList() {
		String sql = " SELECT * FROM "
				+ " (SELECT MEM_NO, MEM_NAME, MEM_POINT, NVL(TO_DATE(MEM_BIR,'YYYY-MM-DD'), 0) AS MEM_BIR, MEM_ADDR, MEM_TEL, "
				+ " MEM_1STLOGIN, NVL(TO_DATE(MEM_LASTLOGIN, 'YYYY-MM-DD'), '0') AS MEM_LASTLOGIN, NVL(TO_DATE(MEM_PASSCHANGE, 'YYYY-MM-DD), '0') AS MEM_PASSCHANGE"
				+ " FROM MEMBER) "
				+ " ORDER BY MEM_NO DESC ";
		return jdbc.selectList(sql);
	}


	
	public List<Map<String, Object>> getMemList(List<Object> param){
		/*String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM "
				+ " (SELECT * FROM MEMBER ORDER BY MEM_NO DESC) A) B " 
				+ " WHERE RN >= (1 + (? * (? - 1))) "
				+ " AND RN  < (1 + (? * ?)) ";*/
		
		String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM "
				+ " (SELECT MEM_NO, MEM_NAME, MEM_POINT, "
				+ "	NVL(MEM_BIR, '0') AS MEM_BIR, MEM_ADDR, SUBSTR(MEM_TEL,1,3)||'-'||SUBSTR(MEM_TEL,4,4)||'-'||SUBSTR(MEM_TEL,8,4) AS MEM_TEL, "
				+ "	NVL(MEM_HIREDATE, '0') AS MEM_HIREDATE, NVL(MEM_RETIREDATE, '0') AS MEM_RETIREDATE, "
				+ " MEM_1STLOGIN, NVL(MEM_LASTLOGIN, '0') AS MEM_LASTLOGIN, NVL(MEM_PASSCHANGE, '0') AS MEM_PASSCHANGE, "
				+ " MEM_ROCKED, MEM_DELETED "
				+ " FROM MEMBER ORDER BY MEM_NO DESC) A) B " 
				+ " WHERE RN >= (1 + (? * (? - 1))) "
				+ " AND RN  < (1 + (? * ?)) "
				+ " AND B.MEM_DELETED IS NULL ";
		
		
		return jdbc.selectList(sql, param);
	}
	
	
	public Map<String, Object> getMemListDetail(int memNumber) {
//		String sql = "SELECT * FROM MEMBER WHERE MEM_NO="+memNumber;
		
		String sql = "SELECT * FROM "
				+ " (SELECT "
				+ " MEM_NO, MEM_NAME, TRIM(TO_CHAR(MEM_POINT,'999,999,999'))||'P' AS MEM_POINT, "
				+ " NVL(MEM_BIR, '0') AS MEM_BIR, MEM_ADDR, SUBSTR(MEM_TEL,1,3)||'-'||SUBSTR(MEM_TEL,4,4)||'-'||SUBSTR(MEM_TEL,8,4) AS MEM_TEL, "
				+ " NVL(MEM_HIREDATE, '0') AS MEM_HIREDATE, NVL(MEM_RETIREDATE, '0') AS MEM_RETIREDATE, "
				+ " MEM_1STLOGIN, NVL(MEM_LASTLOGIN, '0') AS MEM_LASTLOGIN, NVL(MEM_PASSCHANGE, '0') AS MEM_PASSCHANGE, "
				+ " MEM_ROCKED, MEM_DELETED "
				+ " FROM MEMBER ) A"
				+ " WHERE A.MEM_DELETED IS NULL AND A.MEM_NO = " + memNumber;
		
		/*
		String sql = "SELECT * FROM "
				+ " (SELECT "
				+ " MEM_NO, MEM_NAME, MEM_POINT, "
				+ " MEM_BIR, MEM_ADDR, SUBSTR(MEM_TEL,1,3)||'-'||SUBSTR(MEM_TEL,4,4)||'-'||SUBSTR(MEM_TEL,8,4) AS MEM_TEL, "
				+ " TO_CHAR(NVL(MEM_HIREDATE, '9999-12-31')) AS MEM_HIREDATE, TO_CHAR(NVL(MEM_RETIREDATE, '9999-12-31')) AS MEM_RETIREDATE "
				+ " FROM MEMBER ) A"
				+ " WHERE A.MEM_NO =" + memNumber;
		*/
		return jdbc.selectOne(sql);
	}
	
	
	public int updateDate(List<Object> param, String what) {
		String sql = "UPDATE MEMBER SET ";
		if(what.equals("memLastlogin")) {
			sql += "MEM_LASTLOGIN ";	
		}else if(what.equals("memPasschange")){
			sql += "MEM_PASSCHANGE ";
		}
		sql += "  = TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') WHERE MEM_NO = ? "; 
		return jdbc.update(sql, param);
		
	}
	
	public Map<String, Object> memDateCheck(List<Object> param) {
		String sql = " SELECT "
				+ " NVL(TRUNC(SYSDATE - TO_DATE(SUBSTR(REPLACE(MEM_LASTLOGIN,'-',''), 1,8), 'YYYY-MM-DD')), '0') AS MEM_LASTLOGIN, "
				+ "	NVL(TRUNC(SYSDATE - TO_DATE(SUBSTR(REPLACE(MEM_PASSCHANGE,'-',''), 1,8), 'YYYY-MM-DD')), '0') AS MEM_PASSCHANGE "
				+ "	FROM MEMBER"
				+ "	WHERE MEM_NO = ? ";
		return jdbc.selectOne(sql, param);
	}
	
}
