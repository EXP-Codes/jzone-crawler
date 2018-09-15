# jzone-crawler
　QQ空间爬虫（Java版）

> ***暗恋神器***
<br/>　*在你心中是否有一个默默关注的小姐姐？*
<br/>　*你是否想知道在遇见她之前在她身边的一切？*
<br/>　*确认过眼神，让你总在对的时间遇上对的人*

------


## 运行环境

　![](https://img.shields.io/badge/Platform-Windows%20x64-brightgreen.svg)  ![](https://img.shields.io/badge/JDK-1.7%2B-brightgreen.svg)


## 软件介绍

　此插件主要用于[`QQ空间`](https://user.qzone.qq.com/)，主要功能包括：
- 01.　模拟QQ登陆
- 02.　爬取目标QQ空间的【相册数据】，根据相册专辑分类，自动下载高清原图、以及图片描述等
- 03.　爬取目标QQ空间的【说说数据】，根据说说页数分类，自动下载高清原图、以及说说内容等
- 04.　自动整理所下载的【相册/说说数据】
- 05.　支持两种爬取数据的模式：
<br/>　　xhr: 默认模式，用于发布版，爬取速度快，但QQ空间协议经常变动，需定期更新协议避免失效
<br/>　　selenium-phantomjs: 仿真模式，仅限开发版，爬取速度慢，但能确保在QQ空间协议变动不大时依然有效

      
## 运行界面展示

### ※ 登陆界面
![登陆界面](https://raw.githubusercontent.com/lyy289065406/jzone-crawler/master/doc/%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE/01-%E7%99%BB%E9%99%86QQ%E7%A9%BA%E9%97%B4.png)

### ※ 爬取QQ空间相册
![爬取QQ空间相册](https://raw.githubusercontent.com/lyy289065406/jzone-crawler/master/doc/%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE/02-%E7%88%AC%E5%8F%96QQ%E7%A9%BA%E9%97%B4%E7%9B%B8%E5%86%8C.png)

### ※ 爬取QQ空间说说
![爬取QQ空间说说](https://raw.githubusercontent.com/lyy289065406/jzone-crawler/master/doc/%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE/03-%E7%88%AC%E5%8F%96QQ%E7%A9%BA%E9%97%B4%E8%AF%B4%E8%AF%B4.png)

### ※ 分类整理所下载的图文数据
![分类整理所下载的图文数据](https://raw.githubusercontent.com/lyy289065406/jzone-crawler/master/doc/%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE/04-%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84.png)


## 安装与使用

- 01.　安装JDK环境【[jdk-7u72-windows-x64.exe](https://github.com/lyy289065406/environment/tree/master/environment/java/JDK/windows/x64/jdk-7u72-windows-x64.exe)】，一直 下一步 到完成即可（无需修改安装路径）
- 02.　解压并运行【QQ空间爬虫.exe】
- 03.　选择【爬取相册数据】或【爬取说说数据】即可自动批量下载 `图片及其相关信息` 
- 04.　所下载的数据会自动整理到程序根目录下的 `data` 文件夹：
<br/>　　○ 不同的QQ空间数据，存储在对应的 [QQ号文件夹] 内
<br/>　　○ [album] 文件夹下保存了目标QQ的每一个相册（非加密相册）的 [相册信息]、[照片]、[照片信息] 
<br/>　　○ [mood] 文件夹下保存了目标QQ的所有说说的 [图文信息]
<br/>　　○ [mood/content] 文件夹根据页数保存了该页内所有说说的 [图文信息]
<br/>　　○ [mood/photos] 文件夹汇总了所有说说的图片


> ***注：***
<br/>　*此程序需要授权才能使用（防止恶意使用），请加QQ群209442488申请*
<br/>　*此程序不包含盗号后门，若不放心请勿使用*
<br/>　*此程序不包含破解功能，请确保登陆的QQ号有访问对方QQ空间和相册的权限*



## 升级记录

### v2.2版本 (2018-09-15) : 
- 01.　更新软件授权通道为SSL协议
- 02.　修正因QQ空间升级导致xhr协议失效问题


### v2.1版本 (2018-08-06) : 
- 01.　修正软件授权校验接口


### v2.0版本 (2018-05-27) : 
- 01.　增加自动升级功能


### v1.1版本 (2018-05-26) : 
- 01.　相册爬取
- 02.　优化相册和说说的爬取模式为XHR
- 03.　同时支持前后端爬虫
- 04.　增加软件授权校验


### v1.0版本 (2016-06-09) : 
- 01.　支持说说图文爬取



## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------

