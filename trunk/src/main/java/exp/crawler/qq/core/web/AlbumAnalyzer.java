package exp.crawler.qq.core.web;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Album;
import exp.crawler.qq.bean.Photo;
import exp.crawler.qq.cache.Browser;
import exp.crawler.qq.core.interfaze.BaseAlbumAnalyzer;
import exp.crawler.qq.envm.URL;
import exp.crawler.qq.utils.PicUtils;
import exp.crawler.qq.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.os.ThreadUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.webkit.WebUtils;

/**
 * <PRE>
 * 【空间相册】解析器
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class AlbumAnalyzer extends BaseAlbumAnalyzer {

	/** 目标QQ空间首页 */
	private final String QZONE_HOMR_URL;
	
	/**
	 * 构造函数
	 * @param QQ 被爬取数据的目标QQ
	 */
	public AlbumAnalyzer(String QQ) {
		super(QQ);
		this.QZONE_HOMR_URL = URL.QZONE_HOMR_URL(this.QQ);
	}
	
	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		// Undo
	}

	/**
	 * 提取所有相册及其内的照片信息
	 * @return 
	 */
	@Override
	protected List<Album> getAlbums() {
		List<Album> albums = new LinkedList<Album>();
		if(switchToAlbumPage() == true) {
			albums = _getAlbumLists();
			for(Album album : albums) {
				_open(album);
			}
		}
		return albums;
	}
	
	/**
	 * 切换到相册列表页面
	 * @return 是否切换成功
	 */
	private boolean switchToAlbumPage() {
		UIUtils.log("正在打开QQ [", QQ, "] 的空间首页...");
		Browser.open(QZONE_HOMR_URL);
		
		UIUtils.log("正在切换到QQ [", QQ, "] 的相册列表...");
		boolean isOk = false;
		WebElement a = Browser.findElement(By.id("QM_Profile_Photo_A"));
		if(a != null) {
			isOk = true;
			WebUtils.click(Browser.DRIVER(), a);	// 选中
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			WebUtils.click(Browser.DRIVER(), a);	// 点击
			
		} else {
			UIUtils.log("切换到QQ [", QQ, "] 的相册列表失败");
		}
		return isOk;
	}
	
	/**
	 * 获取相册列表(仅相册信息, 不含内部照片信息)
	 * @return
	 */
	@Override
	protected List<Album> _getAlbumLists() {
		UIUtils.log("正在提取QQ [", QQ, "] 的相册列表...");
			
		// 切换到【相册列表】的嵌套页
		Browser.switchToFrame(By.id("tphoto"));
		ThreadUtils.tSleep(Config.SLEEP_TIME);
		
		// 获取相册列表
		List<Album> albums = new LinkedList<Album>();
		try {
			WebElement ul = Browser.findElement(By.className("js-album-list-ul"));
			List<WebElement> list = ul.findElements(By.xpath("li"));
			for(WebElement li : list) {
				WebElement div = li.findElement(By.className("js-album-item"));
				WebElement desc = div.findElement(By.className("js-album-desc-a"));
				WebElement picNum = div.findElement(By.className("pic-num"));
				
				String name = desc.getAttribute("title");
				String question = div.getAttribute("data-question");
				
				if(StrUtils.isEmpty(question)) {
					int total = NumUtils.toInt(picNum.getText().trim(), 0);
					String id = div.getAttribute("data-id");
					String url = URL.ALBUM_URL(QQ, id);
					
					albums.add(new Album(id, name, url, total));
					UIUtils.log("获得相册 [", name, "] (照片x", total, "), 地址: ", url);
					
				} else {
					UIUtils.log("相册 [", name, "] 被加密, 无法读取");
				}
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取QQ [", QQ, "] 的相册列表异常");
		}
		
		UIUtils.log("提取QQ [", QQ, "] 的相册列表完成: 共 [", albums.size(), "] 个相册");
		return albums;
	}

	/**
	 * 打开相册, 提取其中的所有照片信息
	 * @param album 相册信息
	 * @return
	 */
	@Override
	protected void _open(Album album) {
		UIUtils.log("正在读取相册 [", album.NAME(), "] (共", 
				album.PAGE_NUM(), "页, 照片x", album.TOTAL_PIC_NUM(), ")");
		Browser.open(album.URL());
		Browser.switchToFrame(By.id("tphoto"));
		
		// 提取相册内所有照片信息
		for(int page = 1; ; page++) {
			UIUtils.log(" -> 正在提取第 [", page, "] 页的照片信息...");
			List<Photo> pagePhotos = _getPagePhotos(album, page);
			album.addPhotos(pagePhotos);
			
			UIUtils.log(" -> 第 [", page, "] 页照片提取完成, 当前进度: ", 
					album.PIC_NUM(), "/", album.TOTAL_PIC_NUM());
			ThreadUtils.tSleep(Config.SLEEP_TIME);
			
			if(_nextPage() == false) {
				break;
			}
		}
	}
	
	/**
	 * 获取相册的分页照片信息
	 * @param album 相册信息
	 * @param page 页数
	 * @return
	 */
	@Override
	protected List<Photo> _getPagePhotos(Album album, int page) {
		List<Photo> photos = new LinkedList<Photo>();
		try {
			
			// 加载本页所有照片
			while(true) {
				WebElement more = Browser.findElement(By.className("j-pl-photolist-tip-more"));
				if(more == null) {
					break;
				}
				more.click();
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
			
			// 提取本页所有照片的信息
			WebElement ul = Browser.findElement(By.className("j-pl-photolist-ul"));
			List<WebElement> list = ul.findElements(By.xpath("li"));
			for(WebElement li : list) {
				
				// 取照片描述
				WebElement title = li.findElement(By.xpath("div/div[1]/div/div"));
				String desc = title.getAttribute("title");
				
				// 取照片上传日期
				WebElement span = li.findElement(By.xpath("div/div[2]/div/span"));
				String time = span.getAttribute("title");
				
				// 取照片地址
				WebElement img = li.findElement(By.xpath("div/div[1]/a/img"));
				String url = img.getAttribute("src");
				if(url == null) {
					url = img.getAttribute("data-src");
				}
				url = PicUtils.convert(url);
				
				// 保存照片信息(用于下载)
				photos.add(new Photo(desc, time, url));
			}
		} catch(Exception e) {
			UIUtils.log(e, "提取相册 [", album.NAME(), "] 第", page, "页的照片信息异常");
		}
		return photos;
	}
	
	/**
	 * 切换到下一页
	 * @return true:已切换到下一页; false:已是最后一页
	 */
	private boolean _nextPage() {
		boolean hasNext = false;
		for(int retry = 1; !hasNext && retry <= Config.RETRY; retry++) {
			try {
				WebElement next = Browser.findElement(By.id("pager_next_1"));
				if(next != null) {
					next.click();
					hasNext = true;
				}
			} catch(Exception e) {
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
		}
		return hasNext;
	}
	
}
