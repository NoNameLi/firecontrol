package cn.turing.firecontrol.device.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;





/**
 * 单文件、多文件上传类
 * 
 * @author 
 * @version 
 */
public class FileUpload implements Serializable {

	private static final Log log = LogFactory.getLog(FileUpload.class);
	//上传文件的最大限制 单位M
	public static long MaxFileUploadSize=5;

	public final static String BASE_PATH = "d:/ldz";

	private String savePath = BASE_PATH;

	private String realPath = "";

	private int maxFileSize = 1024 * 1024;
	
	private int maxFileNameLength=50;
	
	private int bufferSize=2 * (1024 * 1024);

	private File upload;//单个文件上传

	private File[] files;//多个文件上传

	private String uploadFileName;//单个文件上传的文件名

	private String uploadContentType;//单个文件上传的文件类型

	private String[] filesFileName;//多个文件上传的文件名数组

	private String[] filesContentType;//多个文件上传的文件名类型数组

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}
	
	public String[] getFilesContentType() {
		return filesContentType;
	}

	public void setFilesContentType(String[] filesContentType) {
		this.filesContentType = filesContentType;
	}

	public String[] getFilesFileName() {
		return filesFileName;
	}

	public void setFilesFileName(String[] filesFileName) {
		this.filesFileName = filesFileName;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public int getMaxFileNameLength() {
		return maxFileNameLength;
	}

	public void setMaxFileNameLength(int maxFileNameLength) {
		this.maxFileNameLength = maxFileNameLength;
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	/**
	 * 基本构造器
	 */
	public FileUpload() {
		new File(getSavePath()).mkdirs();
	}

	/**
	 * 建议单个文件上传，调用这个构造器
	 * 
	 * @param file
	 *            文件页面
	 * @param fileName
	 * @param savePath
	 */
	public FileUpload(File file, String fileName,String savePath) {
		this();
		if (!StringUtils.isEmpty(savePath)) {
			this.savePath = savePath;
		}
		this.upload = file;
		this.uploadFileName = fileName;
	}

	/**
	 * 建议多文件上传，调用这个构造器
	 * 
	 * @param files
	 * @param filesName
	 */
	public FileUpload(File[] files, String[] filesName,String savePath) {
		this();
		if (!StringUtils.isEmpty(savePath)) {
			this.savePath = savePath;
		}
		this.files = files;
		this.filesFileName = filesName;
	}

	/**
	 * 单个文件上传保存
	 * 
	 * @return
	 */
	public boolean saveSingleFile() {
		String path = mergePath(getSavePath(), this.getUploadFileName());

		if (upload != null && upload.isFile()) {
			try {
				return save(path,this.getUpload());
			} catch (IOException e) {
				log.info(" 文件读写失败! (File IO error)");
				return false;
			} catch (Exception e) {
				log.info(" 文件保存失败! (File save error)");
				return false;
			}

		}
		return false;
	}

	/**
	 * 多文件上传保存
	 * 
	 * @return
	 */
	public boolean saveMultiFile() {
		File[] files = getFiles();
		for (int i = 0; i < files.length; i++) {
			String path = mergePath(getSavePath(), this.getFilesFileName()[i]);
			try {
				if (!save(path,files[i])) {
					return false;
				}
			} catch (IOException e) {
				log.info(" 文件读写失败! (File IO error)");
				return false;
			} catch (Exception e) {
				log.info(" 文件保存失败! (File save error)");
				return false;
			}
		}
		return true;
	}

	/**
	 * 得到文件输入流
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public FileInputStream getFileIn(File file) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(file);
		return input;
	}

	/**
	 * 得到文件输出流
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public FileOutputStream getFileOut(File file) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(file);
		return out;
	}

	
	
	/**
	 * 保存方法
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private boolean save(String path,File file) throws Exception {
		byte[] buffer = new byte[bufferSize];
		FileInputStream input = getFileIn(file);
		FileOutputStream output = getFileOut(new File(path));
		int size = 0;
		try {
			while ((size = input.read(buffer)) != -1) {
				output.write(buffer, 0, size);
			}
			return true;
		} catch (IOException e) {
			log.info(" 文件保存失败! (File save fail)");
			return false;
		} finally {
			output.close();
			input.close();
		}
	}

	/**
	 * 是否超过文件上传大小限制
	 * 
	 * @return
	 */
	public boolean isAllowedMaxSize(File file) 
	{
		if (file != null && file.isFile()) 
		{
			if (file.length() <= getMaxFileSize()*this.MaxFileUploadSize)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 生成合并保存路径
	 * 
	 * @param savePath
	 * @param fileName
	 * @return
	 */
	private String mergePath(String savePath, String fileName) {
		String path = savePath + "/" + fileName;
		this.realPath = path;
		return path;
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("file.separator"));
	}
	
}
