# AccountBook：《账号册子》
> 一个在本地记录、保存账号密码以及证件照的App，支持指纹解锁。<br>
> 欢迎各位Fork以及提交MR<br>
> 如果对此App有好的idea，欢迎提issue 😁<br>


（App最近更新时间：2023/05/01）/ Android Studio Flamingo | 2022.2.1 Patch 1 / Kotlin+Java

* 支持随机密码生成、
* 支持账号密码的备份和导入
* 支持保存以及查看证件照
* 密码采用AES对称加密

## 软件截图
<img src="https://raw.githubusercontent.com/licoba/AccountB/master/apk/run.gif" alt="Image Description" width="330px">

## 开发流程

### 2023/05/01
* 迁移到Kotlin
* 解决一部分崩溃问题


### 2019/11/19
* 适配Android 9(P)以上的加密

### 2018/04/06
* 指纹识别解锁
* 实现的功能：账号的添加、搜索

### 2018/04/08
* 从相册选取照片
* 照片的添加，保存，以及私有化
* 向导页面
* 安全密码的设置和修改

### 2018/04/10
将照片存到程序的私有目录，仅本程序本用户可访问

### 2018/04/12
优化了代码，完整了添加账号功能

### 2018/04/15
随机密码布局的编写
随机密码算法设计

### 2018/04/25
程序1.0版本发布




## 功能设计
### 已实现：
- [x] 账号编辑和删除
- [x] 账号的导出导入
- [x] 账号的搜索以及排序
- [x] 证件照存储

### 待实现：

- [ ] 证件照的编辑
- [ ] 导出txt和excel



### [点击链接下载APK](https://github.com/licoba/AccountB/releases)
