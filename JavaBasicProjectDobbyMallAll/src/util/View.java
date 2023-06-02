package util;

public interface View {
	int HOME = 1; /* static final */
	int ADMINHOME = 11;
	
	//회원담당 : 지윤
	int MEMBER = 2;
	int MEMBER_ENROLL = 21;
	int MEMBER_MODIFY = 22;
	int MEMBER_DELETE = 23;
	int MEMBER_LOGIN = 24;
	int MEMBER_LOGOUT = 25;
	int MEMBER_LIST = 26;
	int MEMBER_LISTDETAIL = 27;
	int MEMBER_LISTDETAIL_MODIFY = 28;
	int MEMBER_LISTDETAIL_DELETE = 29;
	int MEMBER_MYINFO = 30;
	
	
	
	
	//상품담당 : 혜연
	int PRODUCT = 3;
	int PRODUCT_LIST = 31;
	int PRODUCT_DETAIL = 32;
	int PRODUCT_INSERT = 33;
	int PRODUCT_UPDATE = 34;
	int PRODUCT_DELETE = 35;
	int PRODUCT_REVIEW_ISNERT = 36;
	int PRODUCT_REVIEW_DELETE = 37;
	// BUY
	int BUY_LIST = 61;
	int BUY_SELECT = 62;
	int BUY_REVIEW_LIST = 63;
	int BUY_REVIEW_ISNERT = 64;
	int BUY_REVIEW_DELETE = 65;
	
	//게시판담당 : 지현
	int BOARD = 4;
	int BOARD_LIST = 41;
	int BOARD_DETAIL = 42;
	int BOARD_INSERT = 43;
	int BOARD_UPDATE = 44;
	int BOARD_DELETE = 45;
	int BOARD_CO_MENT_INSERT = 46;
	int BOARD_CO_MENT_DELETE = 47;
	
	
	
}
