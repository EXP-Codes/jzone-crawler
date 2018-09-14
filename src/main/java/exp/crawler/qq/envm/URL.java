package exp.crawler.qq.envm;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * URL枚举
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class URL {

	/** 获取登陆用SIG的URL */
	public final static String SIG_URL = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin";
	
	/** 获取登陆验证码的URL */
	public final static String VCODE_URL = "https://ssl.ptlogin2.qq.com/check";
	
	/** 获取登陆验证码图片的URL */
	public final static String VCODE_IMG_URL = "https://ssl.captcha.qq.com/getimage";
	
	/** QQ空间登陆URL(XHR方式) */
	public final static String XHR_LOGIN_URL = "https://ssl.ptlogin2.qq.com/login";
	
	/** QQ空间登陆URL(WEB方式) */
	public final static String WEB_LOGIN_URL = "http://qzone.qq.com/";
	
	/** QQ空间域名地址(前缀) */
	private final static String QZONE_DOMAIN = "https://user.qzone.qq.com/";
	
	/**
	 * QQ空间地址
	 * @param QQ
	 * @return
	 */
	public final static String QZONE_HOMR_URL(final String QQ) {
		return QZONE_DOMAIN.concat(QQ);
	}
	
	/** 获取相册列表URL */
	public final static String ALBUM_LIST_URL = 
			"https://h5.qzone.qq.com/proxy/domain/photo.qzone.qq.com/fcgi-bin/fcg_list_album_v3";
	
	/**
	 * 相册地址
	 * @param QQ
	 * @param AID 相册ID
	 * @return
	 */
	public final static String ALBUM_URL(final String QQ, final String AID) {
		return StrUtils.concat(QZONE_HOMR_URL(QQ), "/photo/", AID);
	}
	
	/** 获取照片列表URL */
	public final static String PHOTO_LIST_URL = 
			"https://h5.qzone.qq.com/proxy/domain/photo.qzone.qq.com/fcgi-bin/cgi_list_photo";
	
	
	/** 获取说说分页内容URL */
	public final static String MOOD_URL = 
			"https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";
	
	/** 说说引用地址 */
	public final static String MOOD_REFERER = 
			"https://qzs.qq.com/qzone/app/mood_v6/html/index.html";
	
	/** 说说域名地址 */
	public final static String MOOD_DOMAIN = 
			"http://taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";
			
}
