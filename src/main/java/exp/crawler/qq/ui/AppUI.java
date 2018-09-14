package exp.crawler.qq.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import exp.crawler.qq.Config;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseAlbumAnalyzer;
import exp.crawler.qq.core.interfaze.BaseLander;
import exp.crawler.qq.core.interfaze.BaseMoodAnalyzer;
import exp.crawler.qq.monitor.SafetyMonitor;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.webkit.WebDriverType;
import exp.libs.warp.thread.ThreadPool;
import exp.libs.warp.ui.BeautyEyeUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.MainWindow;

/**
 * <PRE>
 * QQ空间爬虫主界面
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AppUI extends MainWindow {

	/** 唯一序列号 */
	private static final long serialVersionUID = -7825507638221203671L;

	/** 界面宽度 */
	private final static int WIDTH = 750;
	
	/** 界面高度 */
	private final static int HEIGHT = 600;
	
	/** 界面文本框最大缓存行数 */
	private final static int MAX_LINE = 500;
	
	/** 换行符 */
	private final static String LINE_END = "\r\n";
	
	/** 登陆说明 */
	private final static String LOGIN_DESC = "登陆 QQ 空间";
	
	/** 注销登陆说明 */
	private final static String LOGOUT_DESC = "注销";
	
	/** 爬取数据的目标QQ号输入框 */
	private JTextField qqTF;
	
	/** QQ登陆账号输入框 */
	private JTextField unTF;
	
	/** QQ登陆密码输入框 */
	private JPasswordField pwTF;
	
	/**
	 * 【WEB模式】选项.
	 * 	  XHR模式为后端爬虫模式(默认)
	 *    WEB模式为前端仿真模式
	 */
	private JRadioButton webBtn;
	
	/** 【记住登陆信息】选项 */
	private JRadioButton rememberBtn;
	
	/** 登陆按钮 */
	private JButton loginBtn;
	
	/** 是否登陆成功 */
	private boolean isLogin;
	
	/** 【相册】爬取按钮 */
	private JButton albumBtn;
	
	/** 【说说】爬取按钮 */
	private JButton moodBtn;
	
	/** 日志输出区 */
	private JTextArea consoleTA;
	
	/** 线程池 */
	private ThreadPool tp;
	
	/** 单例 */
	private static volatile AppUI instance;
	
	/**
	 * 构造函数
	 */
	private AppUI() {
		super("QQ空间爬虫 - By EXP (QQ:272629724)", WIDTH, HEIGHT);
	}
	
	/**
	 * 创建实例
	 * @param args main入参
	 */
	public static void createInstn(String[] args) {
		getInstn();
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	public static AppUI getInstn() {
		if(instance == null) {
			synchronized (AppUI.class) {
				if(instance == null) {
					instance = new AppUI();
					instance.setMini(TO_MINI);
				}
			}
		}
		return instance;
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.qqTF = new JTextField("");
		this.unTF = new JTextField("");
		this.pwTF = new JPasswordField("");
		qqTF.setToolTipText("需要爬取数据的目标QQ号");
		unTF.setToolTipText("请确保此QQ具有查看对方空间权限 (不负责权限破解)");
		pwTF.setToolTipText("此软件不盗号, 不放心勿用");
		
		this.webBtn = new JRadioButton("web模式");
		this.rememberBtn = new JRadioButton("记住我");
		if(recoveryLoginInfo()) {
			rememberBtn.setSelected(true);
		}
		
		this.loginBtn = new JButton(LOGIN_DESC);
		this.albumBtn = new JButton("爬取【空间相册】图文数据");
		this.moodBtn = new JButton("爬取【空间说说】图文数据");
		
		albumBtn.setEnabled(false);
		moodBtn.setEnabled(false);
		BeautyEyeUtils.setButtonStyle(NormalColor.green, loginBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, albumBtn);
		BeautyEyeUtils.setButtonStyle(NormalColor.lightBlue, moodBtn);
		loginBtn.setForeground(Color.BLACK);
		albumBtn.setForeground(Color.BLACK);
		moodBtn.setForeground(Color.BLACK);
		
		this.consoleTA = new JTextArea();
		consoleTA.setEditable(false);
		
		this.isLogin = false;
		this.tp = new ThreadPool(10);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(getCtrlPanel(), BorderLayout.NORTH);
		rootPanel.add(getConsolePanel(), BorderLayout.CENTER);
	}
	
	private JPanel getCtrlPanel() {
		JPanel panel = SwingUtils.getVGridPanel(
				SwingUtils.getPairsPanel("QQ账号", unTF), 
				SwingUtils.getPairsPanel("QQ密码", pwTF), 
				SwingUtils.getPairsPanel("目标QQ", qqTF), 
				SwingUtils.getEBorderPanel(loginBtn, SwingUtils.addBorder(
						SwingUtils.getHGridPanel(webBtn, rememberBtn))), 
				SwingUtils.getHGridPanel(albumBtn, moodBtn)
		);
		SwingUtils.addBorder(panel, "control");
		return panel;
	}
	
	private JScrollPane getConsolePanel() {
		JScrollPane scollPanel = SwingUtils.addAutoScroll(consoleTA);
		SwingUtils.addBorder(scollPanel, "console");
		return scollPanel;
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		setNumTextFieldListener(unTF);
		setNumTextFieldListener(qqTF);
		setWebBynListener();
		setLoginBtnListener();
		setAlbumBtnListener();
		setMoodBtnListener();
	}
	
	private void setNumTextFieldListener(final JTextField textField) {
		textField.addKeyListener(new KeyListener() {

		    @Override
		    public void keyTyped(KeyEvent e) {
		        String text = textField.getText();  // 当前输入框内容
		        char ch = e.getKeyChar();   // 准备附加到输入框的字符

		        // 限制不能输入非数字
		        if(!(ch >= '0' && ch <= '9')) {
		            e.consume();    // 销毁当前输入字符

		        // 限制不能是0开头
		        } else if("".equals(text) && ch == '0') {   
		            e.consume();
		        }
		    }

		    @Override
		    public void keyReleased(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }

		    @Override
		    public void keyPressed(KeyEvent e) {
		        // TODO Auto-generated method stub
		    }
		});
	}
	
	private void setWebBynListener() {
		webBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isLogin == true) {
					webBtn.setSelected(!webBtn.isSelected());
					SwingUtils.warn("非登录状态下才允许切换爬虫模式");
					return;
				}
				
				if(webBtn.isSelected()) {
					if(!FileUtils.exists(WebDriverType.PHANTOMJS.DRIVER_PATH())) {
						webBtn.setSelected(false);
						UIUtils.log("切换爬虫模式失败: 仿真浏览器丢失");
						
					} else {
						UIUtils.log("切换爬虫模式: 仿真浏览器  (不推荐: 速度较慢, 成功率低)");
					}
				} else {
					UIUtils.log("切换爬虫模式: XHR协议  (推荐: 速度较快, 成功率高)");
				}
			}
		});
	}
	
	private void setLoginBtnListener() {
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!LOGOUT_DESC.equals(loginBtn.getText())) {
					_login();
					
				} else {
					_logout();
				}
			}
		});
	}
	
	private void _login() {
		final String username = unTF.getText();
		final String password = String.valueOf(pwTF.getPassword());
		if(StrUtils.isEmpty(username, password)) {
			SwingUtils.warn("账号或密码不能为空");
			return;
			
		} else if(!SafetyMonitor.getInstn().isInWhitelist(username)) {
			SwingUtils.warn(CryptoUtils.deDES("3DAE8A67B609563341FAEC071AC31480BC61074A466C072D7459240BCD26494B508505E9FA4C9365"));
			return;
		}
		
		loginBtn.setEnabled(false);
		tp.execute(new Thread() {
			
			@Override
			public void run() {
				BaseLander lander = webBtn.isSelected() ? 
						new exp.crawler.qq.core.web.Lander(username, password) : 
						new exp.crawler.qq.core.xhr.Lander(username, password);
				isLogin = lander.execute();
				
				if(isLogin == true) {
					loginBtn.setText(LOGOUT_DESC);
					albumBtn.setEnabled(true);
					moodBtn.setEnabled(true);
					unTF.setEditable(false);
					pwTF.setEditable(false);
					
					if(rememberBtn.isSelected()) {
						backupLoginInfo();
					} else {
						deleteLoginInfo();
					}
				} else {
					Browser.quit();
				}
				
				loginBtn.setEnabled(true);
			}
		});
	}
	
	private void _logout() {
		if(!albumBtn.isEnabled() || !moodBtn.isEnabled()) {
			SwingUtils.warn("任务完成后才能注销登陆 !!!");
			return;
		}
		
		if(SwingUtils.confirm("确认注销登陆吗 ?")) {
			Browser.quit();
			Browser.clearCookies();
			
			loginBtn.setText(LOGIN_DESC);
			isLogin = false;
			
			albumBtn.setEnabled(false);
			moodBtn.setEnabled(false);
			unTF.setEditable(true);
			pwTF.setEditable(true);
			UIUtils.log("QQ [", unTF.getText(), "] 已注销登陆");
		}
	}
	
	private void setAlbumBtnListener() {
		albumBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(moodBtn.isEnabled() == false) {
					SwingUtils.warn("请先等待【空间说说】下载完成...");
					
				} else {
					albumBtn.setEnabled(false);
					qqTF.setEditable(false);
					
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							String QQ = qqTF.getText();
							if(SafetyMonitor.getInstn().isInBlacklist(QQ)) {
								SwingUtils.warn(CryptoUtils.deDES("CBE925DFC86BAE34CE0E0C979A9E85725774A822AF89D1C83735A49161F2EBC8"));
								
							} else {
								BaseAlbumAnalyzer analyzer = webBtn.isSelected() ? 
										new exp.crawler.qq.core.web.AlbumAnalyzer(QQ) : 
										new exp.crawler.qq.core.xhr.AlbumAnalyzer(QQ);
								analyzer.execute();
							}
							
							albumBtn.setEnabled(true);
							qqTF.setEditable(true);
						}
					});
				}
			}
		});
	}

	private void setMoodBtnListener() {
		moodBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(albumBtn.isEnabled() == false) {
					SwingUtils.warn("请先等待【空间相册】下载完成...");
					
				} else {
					moodBtn.setEnabled(false);
					qqTF.setEditable(false);
					
					tp.execute(new Thread() {
						
						@Override
						public void run() {
							String QQ = qqTF.getText();
							if(SafetyMonitor.getInstn().isInBlacklist(QQ)) {
								SwingUtils.warn(CryptoUtils.deDES("CBE925DFC86BAE34CE0E0C979A9E85725774A822AF89D1C83735A49161F2EBC8"));
								
							} else {
								BaseMoodAnalyzer analyzer = webBtn.isSelected() ? 
										new exp.crawler.qq.core.web.MoodAnalyzer(QQ) : 
										new exp.crawler.qq.core.xhr.MoodAnalyzer(QQ);
								analyzer.execute();
							}
							
							moodBtn.setEnabled(true);
							qqTF.setEditable(true);
						}
					});
				}
			}
		});
	}
	
	@Override
	protected void AfterView() {
		SafetyMonitor.getInstn();
		printVersionInfo();
	}

	@Override
	protected void beforeHide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeExit() {
		Browser.quit();
	}
	
	/**
	 * 附加信息到控制台
	 * @param msg
	 */
	public void toConsole(String msg) {
		if(StrUtils.count(consoleTA.getText(), '\n') >= MAX_LINE) {
			consoleTA.setText("");
		}
		
		consoleTA.append(msg.concat(LINE_END));
		SwingUtils.toEnd(consoleTA);
	}
	
	/**
	 * 备份登陆信息
	 */
	private void backupLoginInfo() {
		String username = unTF.getText();
		String password = String.valueOf(pwTF.getPassword());
		String QQ = qqTF.getText();
		
		String loginInfo = StrUtils.concat(
				CryptoUtils.toDES(username), LINE_END, 
				CryptoUtils.toDES(password), LINE_END, 
				CryptoUtils.toDES(QQ)
		);
		FileUtils.write(Config.LOGIN_INFO_PATH, loginInfo, Charset.ISO, false);
	}
	
	/**
	 * 还原登陆信息
	 */
	private boolean recoveryLoginInfo() {
		boolean isOk = false;
		List<String> lines = FileUtils.readLines(Config.LOGIN_INFO_PATH, Charset.ISO);
		if(lines.size() == 3) {
			unTF.setText(CryptoUtils.deDES(lines.get(0).trim()));
			pwTF.setText(CryptoUtils.deDES(lines.get(1).trim()));
			qqTF.setText(CryptoUtils.deDES(lines.get(2).trim()));
			isOk = true;
			
		} else {
			deleteLoginInfo();
		}
		return isOk;
	}
	
	/**
	 * 删除登陆信息
	 */
	private void deleteLoginInfo() {
		FileUtils.delete(Config.LOGIN_INFO_PATH);
	}
	
	/**
	 * 打印授权版本信息
	 */
	public void printVersionInfo() {
		toConsole("**********************************************************");
		toConsole(" [EXP (QQ:272629724)] 享有本软件的完全著作权");
		toConsole(" 未经许可严禁擅自用于商业用途, 违者保留追究其法律责任的权利");
		toConsole("**********************************************************");
	}

}
