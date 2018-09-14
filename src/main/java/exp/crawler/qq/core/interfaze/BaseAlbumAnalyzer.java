package exp.crawler.qq.core.interfaze;

import java.util.List;
import java.util.Map;

import exp.crawler.qq.Config;
import exp.crawler.qq.bean.Album;
import exp.crawler.qq.bean.Photo;
import exp.crawler.qq.cache.Browser;
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
 * 【空间相册】解析器: 基类
 * </PRE>
 * <br/><B>PROJECT : </B> qzone-crawler
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2018-03-23
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public abstract class BaseAlbumAnalyzer {

	/** 相册信息保存文件名 */
	private final static String ALBUM_INFO_NAME = "AlbumInfo-[相册信息].txt";
	
	/** 被爬取数据的目标QQ */
	protected final String QQ;
	
	/** 相册保存目录 */
	private final String ALBUM_DIR;
	
	/**
	 * 构造函数
	 * @param QQ 被爬取数据的目标QQ
	 */
	protected BaseAlbumAnalyzer(String QQ) {
		this.QQ = StrUtils.isTrimEmpty(QQ) ? "0" : QQ;
		this.ALBUM_DIR = StrUtils.concat(Config.DATA_DIR, this.QQ, "/album/");
		init();
	}
	
	/**
	 * 初始化
	 */
	protected abstract void init();
	
	/**
	 * 执行空间相册解析, 并下载所有相册及其内的照片
	 */
	public void execute() {
		try {
			
			// 清除上次下载的数据
			FileUtils.delete(ALBUM_DIR);
			FileUtils.createDir(ALBUM_DIR);
			
			// 下载相册及照片
			download(getAlbums());
			UIUtils.log("任务完成: QQ [", QQ, "] 的空间相册已保存到 [", ALBUM_DIR, "]");
			
		} catch(Exception e) {
			UIUtils.log(e, "任务失败: 下载 QQ [", QQ, "] 的空间相册时发生异常");
		}
	}
	
	/**
	 * 提取所有相册及其内的照片信息
	 * @return 
	 */
	protected abstract List<Album> getAlbums();
	
	/**
	 * 获取相册列表(仅相册信息, 不含内部照片信息)
	 * @return
	 */
	protected abstract List<Album> _getAlbumLists();
	
	/**
	 * 打开相册, 提取其中的所有照片信息
	 * @param album 相册信息
	 * @return
	 */
	protected abstract void _open(Album album);
	
	/**
	 * 获取相册的分页照片信息
	 * @param album 相册信息
	 * @param page 页数
	 * @return
	 */
	protected abstract List<Photo> _getPagePhotos(Album album, int page);
	
	/**
	 * 下载所有相册及其内的照片
	 * @param albums 相册集（含照片信息）
	 */
	protected void download(List<Album> albums) {
		if(ListUtils.isEmpty(albums)) {
			return;
		}
		
		UIUtils.log("提取QQ [", QQ, "] 的相册及照片完成, 开始下载...");
		for(Album album : albums) {
			FileUtils.createDir(ALBUM_DIR.concat(album.NAME()));
			StringBuilder albumInfos = new StringBuilder(album.toString());
			
			UIUtils.log("正在下载相册 [", album.NAME(), "] 的照片...");
			int cnt = 0;
			for(Photo photo : album.getPhotos()) {
				boolean isOk = _download(album, photo);
				cnt += (isOk ? 1 : 0);
				albumInfos.append(photo.toString(isOk));
				
				UIUtils.log(" -> 下载照片进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", album.PIC_NUM());
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
			UIUtils.log(" -> 相册 [", album.NAME(), "] 下载完成, 成功率: ", cnt, "/", album.PIC_NUM());
			
			// 保存下载信息
			String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", ALBUM_INFO_NAME);
			FileUtils.write(savePath, albumInfos.toString(), Config.CHARSET, false);
		}
	}
	
	/**
	 * 下载单张照片
	 * @param album 照片所属的相册信息
	 * @param photo 照片信息
	 * @return 是否下载成功
	 */
	protected boolean _download(Album album, Photo photo) {
		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
		header.put(HttpHead.KEY.HOST, XHRUtils.toHost(photo.URL()));
		header.put(HttpHead.KEY.REFERER, album.URL());

		boolean isOk = false;
		String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", photo.NAME());
		for(int retry = 0; !isOk && retry < Config.RETRY; retry++) {
			isOk = HttpURLUtils.downloadByGet(savePath, photo.URL(), header, null);
			if(isOk == false) {
				FileUtils.delete(savePath);
				ThreadUtils.tSleep(Config.SLEEP_TIME);
			}
		}
		return isOk;
	}
	
}
