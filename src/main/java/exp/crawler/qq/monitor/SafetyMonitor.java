package exp.crawler.qq.monitor;

import exp.certificate.api.Certificate;
import exp.certificate.bean.AppInfo;
import exp.crawler.qq.Config;

/**
 * <PRE>
 * 软件授权监控
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-29
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class SafetyMonitor {

	/** 软件授权页(Github) : 测试服务器 (需支持TLSv1.2协议才能访问此网址) */
	private final static String GITHUB_URL = Config.getInstn().TEST_SERVER();
	
	/** 软件授权页(Gitee) : 正式服务器 */
	private final static String GITEE_URL = Config.getInstn().OFFICIAL_SERVER();
	
	/** 软件名称 */
	private String appName;

	/** 单例 */
	private static volatile SafetyMonitor instance;
	
	/**
	 * 构造函数
	 */
	private SafetyMonitor() {
		this.appName = Config.APP_NAME;
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	public static SafetyMonitor getInstn() {
		if(instance == null) {
			synchronized (SafetyMonitor.class) {
				if(instance == null) {
					instance = new SafetyMonitor();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 检查使用软件的QQ是否在白名单内
	 * @param QQ 使用软件的QQ
	 * @return true:在白名单内; false:不在白名单内
	 */
	public boolean isInWhitelist(String QQ) {
		AppInfo appInfo = getAppInfo();	// 提取软件授权信息
		return (appInfo != null && appInfo.getWhitelist().contains(QQ));
	}
	
	/**
	 * 检查被爬取数据的QQ是否在黑名单内
	 * @param QQ 被爬取数据的QQ
	 * @return true:在黑名单内; false:不在黑名单内
	 */
	public boolean isInBlacklist(String QQ) {
		AppInfo appInfo = getAppInfo();	// 提取软件授权信息
		return (appInfo != null && appInfo.getBlacklist().contains(QQ));
	}
	
	/**
	 * 提取软件授权信息
	 * @return
	 */
	private AppInfo getAppInfo() {
		
		// 先尝试用Gitee(国内)获取授权页, 若失败则从GitHub(国际)获取授权页
		AppInfo appInfo = Certificate.getAppInfo(GITEE_URL, appName);
		if(appInfo == null) {
			appInfo = Certificate.getAppInfo(GITHUB_URL, appName);
		}
		return appInfo;
	}
	
}
