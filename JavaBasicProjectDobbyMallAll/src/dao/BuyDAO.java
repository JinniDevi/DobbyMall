package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class BuyDAO {
	private static BuyDAO instance = null;
	private BuyDAO() {}
	public static BuyDAO getInstance() {
		if (instance == null) instance = new BuyDAO();
		return instance;
	}

	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	public static String orderByColumnName = "PD_NAME";
	public static String orderType = "ASC";
	public static String searchName ="";
	public List<Map<String, Object>> getListAll(List<Object> param) {
		if (searchName.equals("")) {
			String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM(SELECT * FROM PRODUCT ORDER BY "+orderByColumnName+" "+orderType+") A) B" 
					+ " WHERE RN >=(1 + (? * (? -1)))" 
					+ " AND RN  < (1 + (? * ?))"
					;
			return jdbc.selectList(sql,param);
		} else {
			String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM(SELECT * FROM PRODUCT WHERE PD_NAME LIKE  '%" + searchName +"%' "
					+ "ORDER BY "+orderByColumnName+" "+orderType+") A) B" 
					+ " WHERE RN >=(1 + (? * (? -1)))" 
					+ " AND RN  < (1 + (? * ?))"
					;
			return jdbc.selectList(sql,param);
		}
		
	}
	
	
	
	public List<Map<String, Object>> getcartListAll(List<Object> param) {
//		String sql = " SELECT A.OR_PDBUY_NO, C.PD_NAME, A.ORPD_QTY, A.ORPD_PRICE, " 
//				+ " B.ORDR_DATE FROM OR_PD A, ODERINFO B, PRODUCT C " 
//				+ " WHERE B.MEM_NO=? AND A.PD_ID=C.PD_ID "
//				;
		String sql = " SELECT C.OR_PDBUY_NO, B.PD_NAME, C.PD_ID, C.ORPD_QTY, C.ORPD_PRICE, " 
				+ " A.ORDR_DATE FROM ODERINFO A, PRODUCT B , OR_PD C WHERE  A.MEM_NO=?" 
				+ " AND A.ORDR_NO=C.ORDR_NO AND B.PD_ID=C.PD_ID AND C.ORPD_PRICE=B.PD_PRICE ORDER BY 6 DESC"
				;
		
		return jdbc.selectList(sql,param);
	}
	//회원의 장바구니번호 생성
	public static Object a ;
	public int insertOrderNumber(List<Object> prarm) {
		String sql = "  CALL HYHY.create_new_order_no_proc(?,TO_DATE(?),?)"
				;
		return jdbc.excute(sql, prarm);
	}
	//입력....
	public int insertOrderProduct(List<Object> oderNumber) {
		
		// 장바구니 번호, 및 구매상품 입력
		String sql= " INSERT INTO OR_PD" 
				+ " SELECT SUBSTR(A.ORDR_NO,3) AS OR_PDBUY_NO, B.PD_ID, " 
				+ " A.ORDR_NO AS ORDR_NO, 1 AS ORPD_QTY, " 
				+ " 1 * B.PD_PRICE AS ORPD_PRICE FROM ODERINFO A, " 
				+ " (SELECT PD_ID,  PD_PRICE FROM PRODUCT WHERE PD_ID=?) B" 
				+ " WHERE A.MEM_NO=? AND A.ORDR_DATE = ? " //221214
				+ " AND A.ORDR_NO=(SELECT TO_CHAR(TO_DATE(?),'YYYYMMDD')" //20221214
				+ "||'-'||LTRIM(TO_CHAR(MAX(TO_NUMBER(SUBSTR(ORDR_NO, -6))), '000000')) " 
				+ " FROM ODERINFO WHERE MEM_NO=?) " // MEM_NO = 100001
				;
		
		return jdbc.update(sql, oderNumber);
	}
	
	public List<Map<String, Object>> getordernumber(List<Object> oderNumber) {
		String sql = " SELECT ORDR_NO FROM ODERINFO" 
				+ "WHERE MEM_NO=?" //
				+ "AND ORDR_DATE=? " //
				;
		return jdbc.selectList(sql,oderNumber);
	}
	public int updatePoint(List<Object> param3) {
		
//		String sql = " UPDATE MEMBER " 
//				+ " SET MEM_POINT=(SELECT ((M.MEM_POINT - O.ORPD_PRICE) + P.PAYBACK_P) " 
//				+ " FROM MEMBER M, OR_PD O, PRODUCT P " 
//				+ " WHERE MEM_NO=?" 
//				+ " AND O.PD_ID=P.PD_ID)" 
//				+ " WHERE MEM_NO=?"
//				;
		String sql = "  UPDATE MEMBER SET MEM_POINT= (SELECT  MAX(TA.A) " 
				+ " FROM (SELECT ((M.MEM_POINT - O.ORPD_PRICE) + P.PAYBACK_P)  A" 
				+ "	FROM MEMBER M, OR_PD O, PRODUCT P " 
				+ " WHERE MEM_NO=? AND O.PD_ID=P.PD_ID ORDER BY OR_PDBUY_NO ) TA) WHERE MEM_NO=? "
				;
		
		return jdbc.update(sql, param3);
		
	}
	
	/* ----------------------------------------------------------------------------------------------*/
	
	
	public List<Map<String, Object>> getReviewListAll() {
		String sql = "SELECT * FROM PD_REVIEW ORDER BY R_NO DESC ";
		return jdbc.selectList(sql);
	}
	
	public int insertRow(List<Object> param) {
		// R_NO, MEM_NO, R_CONTENT, R_DATE, OR_PDBUY_NO, PD_ID
		String sql = " INSERT INTO PD_REVIEW "
				+ " (R_NO, MEM_NO, R_CONTENT, R_DATE, OR_PDBUY_NO, PD_ID) "
				+ " VALUES "
				+ " (R_NO_SEQ.NEXTVAL, ?, ?, TO_CHAR(SYSDATE, 'YYYY-MM-DD'), ?, ?) ";
		return jdbc.update(sql, param);	
	}
	
	public List<Map<String, Object>> getPdReviewList(int lastNumber) { // int boNumber
		String sql = "SELECT * FROM PD_REVIEW WHERE BO_NO=" + lastNumber + " ORDER BY R_NO ASC "; // 이해안가~
		return jdbc.selectList(sql);
	}
	
	public int deletRow(int reNumber) {
		String sql = "DELETE FROM PD_REVIEW WHERE R_NO= " + reNumber;
		return jdbc.update(sql);
	}
	
	
	
	
	
	
	
	
}
