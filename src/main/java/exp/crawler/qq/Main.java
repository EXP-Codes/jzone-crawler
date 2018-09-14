package exp.crawler.qq;

import exp.au.api.AppVerInfo;
import exp.crawler.qq.ui.AppUI;
import exp.libs.utils.other.LogUtils;
import exp.libs.warp.ui.BeautyEyeUtils;

/**
 * <PRE>
 * QQ空间爬虫:
 * 	可爬取相册和说说图文信息
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-22
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Main {

	/**
	 * 程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		LogUtils.loadLogBackConfig();
		BeautyEyeUtils.init();
		Config.getInstn();
		AppVerInfo.export(Config.APP_NAME);
		
		AppUI.createInstn(args);
	}
	
}
