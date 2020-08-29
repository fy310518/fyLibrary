package com.fy.plugin;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DESCRIPTION：TODO
 * Created by fangs on 2020/8/29 23:27.
 */
public class FyFileUtils {
    public static final String TAG = "FyFileUtils";

    private FyFileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取应用 在SD卡保存数据的私有路径
     * @return "SDCard/Android/data/你的应用的包名/files/ "
     */
    public static String getExternalFiles(){
        return PluginManager.getInstance().getContext().getExternalFilesDir(null)
                .getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 内部具体数据存储目录
     * @return "/data/data/你的应用包名/files/"
     */
    public static String getFilesDir(){
        return PluginManager.getInstance().getContext().getFilesDir()
                .getAbsolutePath() + File.separator;
    }

    /**
     * 到得文件的放置路径
     * @param aModuleName 模块名字 (如："head.img.temp")
     * @param type        类型 0：应用私有存储路径；1：应用私有缓存路径 （是否存在SD卡，存在则表示 应用SD卡，否则表示应用内部存储）
     * @return
     */
    public static String getPath(String aModuleName, int type) {
        String modulePath = aModuleName.replace(".", File.separator);
        String fDirStr = File.separator + modulePath + File.separator;

        File dirpath;
        if (isSDCardEnable())
            dirpath = new File(getExternalFiles(), fDirStr);
        else
            dirpath = new File(getFilesDir(), fDirStr);

        return dirpath.getPath();
    }

    /**
     * 判断指定路径的 文件夹 是否存在，不存在创建文件夹 (内部执行了 getPath())
     * @param filePath
     * @return
     */
    public static File folderIsExists(String filePath, int type) {
        File folder = new File(getPath(filePath, type));
        try {
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folder;
    }

    /**
     * 判断指定路径的 文件 是否存在，不存在创建文件
     * @param filePath
     * @return
     */
    public static File fileIsExists(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExist(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdirs();
        } else {
            return;//如果存在直接返回
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + file.separator + filePath[i])).isDirectory()) {
                copyFolder(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

            if (new File(oldPath + file.separator + filePath[i]).isFile()) {
                copyFile(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }
        }
    }

    /**
     * 复制单个文件
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copyFile(String source, String target) {
        copyFile(new File(source), new File(target));
    }

    public static void copyFile(File source, File target) {
        String targetPath = target.getPath();
        deleteFileSafely(target);//先删除已存在的
        target = fileIsExists(targetPath);//再创建一个文件；最后写入文件流

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归获取文件夹中所有的文件
     * @param file  文件夹
     * @param data  得到的文件集合，可空
     */
    public static List<File> recursionGetFile(File file, List<File> data) {
        if (null == data) data = new ArrayList<>();

        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isFile()) {
                data.add(f);
            } else {
                recursionGetFile(f, data);
            }
        }

        return data;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void recursionDeleteFile(File file) {
        if (!file.exists()) return;

        if (file.isFile()) {
            deleteFileSafely(file);//删除当前 文件
            return;
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (null == childFile || childFile.length == 0) {
                deleteFileSafely(file);//删除 当前目录
                return;
            }

            for (File f : childFile) {
                recursionDeleteFile(f);
            }

            deleteFileSafely(file);//删除 当前目录
        }
    }

    /**
     * 安全删除文件
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (null != file) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }


    /**
     * 解压zip到指定的路径
     * @param zipFileString  要解压的 文件（可以是 手机sd卡 中的文件，也可以是 assets 目录下的文件）
     * @param outPathString
     */
    public static void unZipFolder(String zipFileString, String outPathString) {
        InputStream is = null;
        try {
            if (fileIsExist(zipFileString)) {
                is = new FileInputStream(zipFileString);
            } else {
                is = PluginManager.getInstance().getContext().getAssets().open(zipFileString);
            }

            unZipFolder(is, outPathString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压zip到指定的路径
     * @param is            要解压的 文件 输入流
     * @param outPathString 要解压缩路径
     */
    public static void unZipFolder(InputStream is, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(is);
        ZipEntry zipEntry;
        String szName = "";
        long count = 0;

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
}
