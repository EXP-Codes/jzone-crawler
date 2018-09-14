package exp.crawler.qq.core.interfaze;

import java.util.List;
import java.util.Map;

import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Mood;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.crawler.qq.utils.XHRUtils;
import exp.libs.envm.HttpHead;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpURLUtils;

/**
 * <PRE>
 * 【空间说说】解析器: 基类
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class BaseMoodAnalyzer {

	/** 说说分页信息保存文件名 */
	private final static String MOOD_INFO_NAME = "MoodInfo-[说说信息].txt";
	
	/** 被爬取数据的目标QQ */
	protected final String QQ;
	
	/** 说说保存目录 */
	private final String MOOD_DIR;
	
	/** 说说每页图文信息的保存路径前缀 */
	private final String PAGE_DIR_PREFIX;
	
	/** 说说所有照片的保存目录 */
	private final String PHOTO_DIR;
	
	/**
	 * 构造函数
	 * @param QQ 被爬取数据的目标QQ
	 */
	protected BaseMoodAnalyzer(String QQ) {
		this.QQ = StrUtils.isTrimEmpty(QQ) ? "0" : QQ;
		this.MOOD_DIR = StrUtils.concat(Config.DATA_DIR, this.QQ, "/mood/");
		this.PAGE_DIR_PREFIX = MOOD_DIR.concat("content/page-");
		this.PHOTO_DIR = MOOD_DIR.concat("photos/");
		init();
	}
	
	/**
	 * 初始化
	 */
	protected abstract void init();
	
	/**
	 * 执行空间说说解析, 并下载所有说说及相关照片
	 */
	public void execute() {
		try {
			
			// 清除上次下载的数据
			FileUtils.delete(MOOD_DIR);
			FileUtils.createDir(MOOD_DIR);
			
			// 下载说说及照片
			download(getMoods());
			UIUtils.log("任务完成: QQ [", QQ, "] 的空间说说已保存到 [", MOOD_DIR, "]");
			
		} catch(Exception e) {
			UIUtils.log(e, "任务失败: 下载 QQ [", QQ, "] 的空间说说时发生异常");
		}
	}
	
	/**
	 * 提取所有说说及相关的照片信息
	 * @return
	 */
	protected abstract List<Mood> getMoods();
	
	/**
	 * 获取说说总页数
	 * @return
	 */
	protected abstract int _getPageNum();
	
	/**
	 * 获取分页的说说内容
	 * @param page 页码
	 * @return 
	 */
	protected abstract List<Mood> _getPageMoods(int page);
	
	/**
	 * 下载所有说说及相关的照片
	 * @param moods 说说集（含照片信息）
	 */
	private void download(List<Mood> moods) {
		if(ListUtils.isEmpty(moods)) {
			return;
		}
		
		UIUtils.log("提取QQ [", QQ, "] 的说说及照片完成, 开始下载...");
		int idx = 1;
		for(Mood mood : moods) {
			FileUtils.createDir(PAGE_DIR_PREFIX.concat(mood.PAGE()));
			
			UIUtils.log("正在下载第 [", idx++, "/", moods.size(), "] 条说说: ", mood.CONTENT());
			int cnt = _download(mood);
			boolean isOk = (cnt == mood.PIC_NUM());
			UIUtils.log(" -> 说说照片下载完成, 成功率: ", cnt, "/", mood.PIC_NUM());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			// 保存下载信息
			String savePath = StrUtils.concat(PAGE_DIR_PREFIX, mood.PAGE(), "/", MOOD_INFO_NAME);
			FileUtils.write(savePath, mood.toString(isOk), Config.CHARSET, true);
		}
	}
	
	/**
	 * 下载单条说说及相关的照片
	 * @param mood 说说信息
	 * @return 成功下载的照片数
	 */
	private int _download(Mood mood) {
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		header.put(HttpHead.KEY.REFERER, URL.MOOD_REFERER);

		int idx = 0, cnt = 0;
		for(String picURL : mood.getPicURLs()) {
			String picName = PicUtils.getPicName(String.valueOf(idx++), mood.CONTENT());
			boolean isOk = _download(header, mood.PAGE(), picName, picURL);
			cnt += (isOk ? 1 : 0);
			
			UIUtils.log(" -> 下载照片进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", mood.PIC_NUM());
		}
		return cnt;
	}
	
	/**
	 * 下载单张图片到说说的分页目录，并复制到图片合集目录
	 * @param header
	 * @param page 页码索引
	 * @param picName
	 * @param picURL
	 * @return
	 */
	private boolean _download(Map<String, String> header, 
			String page, String picName, String picURL) {
		header.put(HttpHead.KEY.HOST, XHRUtils.toHost(picURL));
		
		boolean isOk = false;
		String savePath = StrUtils.concat(PAGE_DIR_PREFIX, page, "/", picName);
		for(int retry = 0; !isOk && retry < Config.RETRY; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, picURL, header, null, 
					Config.TIMEOUT, Config.TIMEOUT, Config.CHARSET);
			
			if(isOk == false) {
				FileUtils.delete(savePath);
				ThreadUtils.tSleep(Config.SLEEP_TIME);
				
			} else {
				FileUtils.copyFile(savePath, PHOTO_DIR.concat(picName));
			}
		}
		return isOk;
	}
	
}
