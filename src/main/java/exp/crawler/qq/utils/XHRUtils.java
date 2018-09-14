package exp.crawler.qq.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import exp.crawler.qq.bean.QQCookie;
import exp.libs.envm.HttpHead;
import exp.libs.utils.verify.RegexUtils;
import exp.libs.warp.net.http.HttpClient;

/**
 * <PRE>
 * XHR工具类
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class XHRUtils {

	/** 私有化构造函数 */
	protected XHRUtils() {}
	
	/**
	 * 获取XHR请求头
	 * @param cookie
	 * @return
	 */
	public static Map<String, String> getHeader(QQCookie cookie) {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HttpHead.KEY.ACCEPT, "image/webp,image/*,*/*;q=0.8");
		header.put(HttpHead.KEY.ACCEPT_ENCODING, "gzip, deflate, sdch");
		header.put(HttpHead.KEY.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en;q=0.6");
		header.put(HttpHead.KEY.CONNECTION, "keep-alive");
		header.put(HttpHead.KEY.COOKIE, cookie.toNVCookie());
		header.put(HttpHead.KEY.USER_AGENT, HttpHead.VAL.USER_AGENT);
		return header;
	}
	
	/**
	 * 从Http会话的响应报文中提取cookie信息
	 * @param client Http会话客户端
	 * @param cookie cookie对象容器
	 */
	public static void takeResponseCookies(HttpClient client, QQCookie cookie) {
		HttpMethod method = client.getHttpMethod();
		if(method != null) {
			Header[] rspHeaders = method.getResponseHeaders();
			for(Header rspHeader : rspHeaders) {
				if(HttpHead.KEY.SET_COOKIE.equals(rspHeader.getName())) {
					cookie.add(rspHeader.getValue());
				}
			}
		}
	}
	
	/**
	 * 从XHR响应报文中的回调函数提取JSON内容
	 * @param callback 回调函数字符串
	 * @return JSON
	 */
	public static String toJson(String callback) {
		return RegexUtils.findFirst(callback.replace("\\/", "/"), "_Callback\\(([\\s\\S]*)\\);$");
	}
	
	/**
	 * 从URL地址中提取主Host地址
	 * @param url
	 * @return
	 */
	public static String toHost(String url) {
		return RegexUtils.findFirst(url, "http://([^/]*)/");
	}
	
}
