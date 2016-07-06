package com.example.pdf;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import cache.DataCache;


public class HttpDownloader {

	private URL url = null;

	/**
	 * 根据提供的URL地址下载文件，所下载的文件必须是文本文件。
	 * *************************************************** 1、创建一个URL对象
	 * 2、通过URL对象，创建一个HttpURLConnection对象 3、通过HttpURLConnection对象，得到一个InputStream
	 * 4、从InputStream中读取数据 ***************************************************
	 * 
	 * @param urlStr
	 *            需要下载的文件的URL
	 * @return 返回所下载文件的内容
	 */
	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		InputStream input = null;

		try {
			// 使用getInputStreamFromUrl得到一个InputStream
			input = this.getInputStreamFromUrl(urlStr);
			// 使用IO流读取数据
			buffer = new BufferedReader(new InputStreamReader(input));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭相关的资源
			try {
				if (buffer != null) {
					buffer.close();
					buffer = null;
				}
				if (input != null) {
					input.close();
					input = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 根据提供的URL地址下载文件，文件可以是任意的文件。
	 * 
	 * @param urlStr
	 *            代表需要下载文件的url字符串
	 * @param path
	 *            将下载的文件存放的目录
	 * @param fileName
	 *            下载的文件的名字
	 * @return 返回-1：代表文件下载出错； 返回0：代表文件下载成功； 返回1：代表文件已经存在。
	 */
	public int downloadFile(String urlStr, String path, String fileName) {
		InputStream input = null;
		try {
			FileUtils fileUtils = new FileUtils();
			if (fileUtils.isFileExist(path + fileName)) {
				return 1;
			} else {
				// 使用getInputStreamFromUrl得到一个InputStream
				input = this.getInputStreamFromUrl(urlStr);
				// 调用fileUtils对象的write2SDFromInput方法下载文件
				File resultFile = fileUtils.write2SDFromInput(path, fileName,
						input);
				if (resultFile == null) {
					return -1;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public File downloadReturnFile(String urlStr, String path, String fileName){
		InputStream input = null;
		File resultFile = null;
		FileOutputStream fos = null;
		try {
			FileUtils fileUtils = new FileUtils();
			
			if (fileUtils.isFileExist(path + fileName)) {
				return new File(FileUtils.SDPATH+path+fileName);
			} else {
				// 使用getInputStreamFromUrl得到一个InputStream
				int bufferSize = 1024;
				byte buf[] = new byte[bufferSize];
				input = this.getInputStreamFromUrl(urlStr);
				fileUtils.creatSDDir(path);  
				resultFile = fileUtils.creatSDFile(path + fileName);
				fos = new FileOutputStream(resultFile);
				do{
					int numread = input.read(buf);
					if(numread <=0){
						break;
					}
					fos.write(buf, 0, numread);
					
				}while(!cancel);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
				if(fos != null){
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultFile;
	}

	/**
	 * 根据URL得到HttpURLConnection的输入流
	 * @param urlStr
	 *            代表url的字符串
	 * @return 返回一个输入流
	 * @throws MalformedURLException
	 *             , IOException
	 */
	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		// 通过传入的urlStr字符串对象创建一个URL对象
		url = new URL(urlStr);
		// 通过创建的URL对象创建一个Http连接
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//		String accessToken = accessToken = DataCache.getDataCache().queryCache(
//				"access_token");
//		if (!TextUtils.isEmpty(accessToken)) {
//			Log.e("access_token", accessToken);
//		}
//		urlConn.setRequestProperty("User-Agent", "MSIE 7.0");
//		urlConn.setRequestProperty("Authorization", "Bearer" + " " + accessToken);
//		urlConn.setConnectTimeout(5000);
		// 通过Http连接得到一个输入流
		InputStream input = urlConn.getInputStream();
		return input;
	}
	
	private boolean cancel;
}
