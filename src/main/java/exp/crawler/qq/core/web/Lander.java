package exp.crawler.qq.core.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.crawler.qq.Config;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseLander;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.EncryptUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * QQ空间登陆器
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-26
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Lander extends BaseLander {
	
	/**
	 * 构造函数
	 * @param QQ 所登陆的QQ
	 * @param password 所登陆的QQ密码
	 */
	public Lander(String QQ, String password) {
		super(QQ, password);
	}
	
	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		UIUtils.log("正在初始化登陆环境...");
		
		Browser.init(false);
		Browser.clearCookies();
	}
	
	/**
	 * 执行登陆操作
	 * @return true:登陆成功; false:登陆失败
	 */
	@Override
	public boolean execute() {
		boolean isOk = false;
		try {
			isOk = switchLoginMode();	// 使用帐密登陆模式
			if(isOk == true) {
				fill("u", QQ);			// 填写账号
				fill("p", password);	// 填写密码
				ThreadUtils.tSleep(Config.SLEEP_TIME);
				
				isOk = login();			// 登陆
				if(isOk == true) {
					isOk = takeGTKAndToken("");	// 生成GTK与QzoneToken
					if(isOk == true) {
						UIUtils.log("登陆QQ [", QQ, "] 成功");
						
						// web仿真模式下不能关闭浏览器，否则QQ空间要重新登陆
						//  (GTK与的QzoneToken存在使得保存cookie也无效)
//						Browser.quit();	
						
					} else {
						isOk = false;
						UIUtils.log("登陆QQ [", QQ, "] 失败: 无法提取GTK或QzoneToken");
					}
				} else {
					UIUtils.log("登陆QQ [", QQ, "] ", (isOk ? "成功" : "失败: 账号或密码错误"));
				}
			} else {
				UIUtils.log("切换帐密登陆模式失败");
			}
		} catch(Exception e) {
			UIUtils.log(e, "登陆QQ [", QQ, "] 失败: 内置浏览器异常");
		}
		return isOk;
	}
	
	/**
	 * 切换登陆方式为[帐密登陆]
	 * return 是否切换成功
	 */
	private boolean switchLoginMode() {
		UIUtils.log("正在打开QQ登陆页面: ", URL.WEB_LOGIN_URL);
		Browser.open(URL.WEB_LOGIN_URL);
		
		// QQ空间的【登陆操作界面】是通过【iframe】嵌套在【登陆页面】中的子页面
		UIUtils.log("正在切换为帐密登陆模式...");
		Browser.switchToFrame(By.id("login_frame"));
		ThreadUtils.tSleep(Config.SLEEP_TIME);
		
		// 切换帐密登陆方式为 [帐密登陆]
		boolean isOk = true;
		for(int retry = 1; retry <= Config.RETRY; retry++) {
			try {
				WebElement switchBtn = Browser.findElement(By.id("switcher_plogin"));
				switchBtn.click();
				break;
				
			} catch(Exception e) {
				isOk = false;	// 有时操作过快可能会报元素不存在异常
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
		}
		return isOk;
	}
	
	/**
	 * 填写输入框的值
	 * @param name 输入框名称
	 * @param value 填写到输入框的值
	 */
	private void fill(String name, String value) {
		WebElement input = Browser.findElement(By.id(name));
		Browser.fill(input, value);
	}
	
	/**
	 * 登陆
	 * @return true:登陆成功; false:登陆失败
	 */
	private boolean login() {
		UIUtils.log("正在登陆QQ [", QQ, "] ...");
		final String UNLOGIN_URL = Browser.getCurURL();	// 登录前URL
		
		// 点击登陆按钮
		WebElement loginBtn = Browser.findElement(By.id("login_button"));
		loginBtn.click();
		
		// 轮询是否登陆成功（发生页面切换）
		boolean isOk = true;
		long bgnTime = System.currentTimeMillis();
		while(UNLOGIN_URL.equals(Browser.getCurURL())) {
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			if(System.currentTimeMillis() - bgnTime >= Config.TIMEOUT) {
				isOk = false;	// 超时未切换页面则认为登陆失败
				break;
			}
		}
		return isOk;
	}
	
	/**
	 * 从QQ空间首页首页源码中提取GTK与 QZoneToken.
	 * 	类似于gtk, qzonetoken 在每次登陆时自动生成一个固定值, 但是生成算法相对复杂（需要jother解码）, 
	 *  因此此处取巧, 直接在页面源码中提取明文
	 *  
	 * @return unuse 无用参数
	 */
	@Override
	protected boolean takeGTKAndToken(String unuse) {
		UIUtils.log("正在提取本次登陆的 GTK 与 QzoneToken ...");
		
		// 从Cookie提取p_skey，计算GTK
		Browser.backupCookies();	
		UIUtils.log("本次登陆生成的 GTK: ", Browser.GTK());
		
		// 从页面源码提取QzoneToken
		Browser.open(URL.QZONE_HOMR_URL(QQ));
		String qzoneToken = EncryptUtils.getQzoneToken(Browser.getPageSource());
		Browser.setQzoneToken(qzoneToken);
		UIUtils.log("本次登陆生成的 QzoneToken: ", Browser.QZONE_TOKEN());
		
		return StrUtils.isNotEmpty(Browser.GTK(), Browser.QZONE_TOKEN());
	}
	
}
