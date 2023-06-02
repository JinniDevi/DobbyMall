package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class ProductDAO {
	private static ProductDAO instance = null;

	private ProductDAO() {}
	public static ProductDAO getInstance() {
		if (instance == null) instance = new ProductDAO();
		return instance;
	}

	JDBCUtil jdbc = JDBCUtil.getInstance();

	public static String orderByColumnName = "PD_NAME";
	public static String orderType = "ASC";
	public static int rowNumber = 0;
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
	
	public Map<String, Object> getproduct(List<Object> target) {
		String sql=" SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM(SELECT * FROM PRODUCT ORDER BY "+orderByColumnName+" "+orderType+") A) B" 
				+ " WHERE RN >=(1 + (? * (? -1)))" 
				+ " AND RN  < (1 + (? * ?))"
				+ " AND RN =" + rowNumber 
				;
		
		return jdbc.selectOne(sql,target);
	}
	public List<Map<String, Object>> getListsearch(List<Object> param) {
		String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A. * FROM(SELECT * FROM PRODUCT WHERE PD_NAME LIKE  '%" + searchName +"%' "
				+ "ORDER BY "+orderByColumnName+" "+orderType+") A) B" 
				+ " WHERE RN >=(1 + (? * (? -1)))" 
				+ " AND RN  < (1 + (? * ?))"
				;
		
		
		return jdbc.selectList(sql,param);
	}
	public List<Map<String, Object>> getlguList() {
		String sql = " SELECT ROWNUM AS RN, LGU_ID,LGU_NAME FROM PD_LGU";
		return jdbc.selectList(sql);
	}
	
//	public static String lgu_id;
	public static int pd_cost;
	
	public int insertNewPd(List<Object> prarm) {
		String  sql = " INSERT INTO PRODUCT(PD_ID,PD_NAME,LGU_ID,PD_COST,PD_PRICE,PAYBACK_P)" 
				+ " VALUES(new_pd_id(?), " 
				+ "?, (SELECT LGU_ID FROM PD_LGU WHERE LGU_ID=?)"
				+ ", "+ pd_cost +", ROUND(("+ pd_cost +"*1.3),-1),ROUND(("+ pd_cost +"*1.3),-1)*0.03)"
				;
		return jdbc.update(sql, prarm);
	}
	
	public int deletePd(List<Object> prarm) {
		String sql = " DELETE FROM PRODUCT WHERE PD_ID=?" ;
		
		return jdbc.update(sql, prarm);
	}
	
	
	
	
	
	
	
	
	
	
	
}

