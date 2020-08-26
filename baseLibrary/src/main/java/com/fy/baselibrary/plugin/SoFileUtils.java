package com.fy.baselibrary.plugin;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

/**
 * description </p>
 * Created by fangs on 2020/8/26 10:06.
 */
public class SoFileUtils {
    private static final String TAG = "PluginManager";
    private static int BUFFER = 4096;

    public static String getSoPath(Context context) {
        return context.getDir("libs", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + Build.CPU_ABI;
//        return "/data/data/" + context.getPackageName() + File.separator + name.replace(".apk", "") + File.separator + "lib" + File.separator + Build.CPU_ABI;
    }

    public static String copySo(Context context, String dexPath) {
        String fileName = dexPath.substring(dexPath.lastIndexOf("/"), dexPath.length()).replace("/", "");
        String unZipPath = FileUtils.folderIsExists(FileUtils.ZIP, ConfigUtils.getType()).getPath() + File.separator + fileName.replace(".apk", "") + File.separator;

        String soPath = getSoPath(context);

        if ((new File(soPath)).exists()) {
            Log.i(TAG, "soPath exists");
            return soPath;
        }

        ZipUtils.unZipFolder(dexPath, unZipPath, null);//解压插件apk

        List<File> fileList = FileUtils.recursionGetFile(new File(unZipPath + "lib/"), null);
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            String parentPath = file.getParentFile().getAbsolutePath();
            Log.i(TAG, "parentPath " + Build.CPU_ABI);
            if (parentPath.contains(Build.CPU_ABI)) {
                Log.i(TAG, "parentPath " + soPath);
                try {
                    FileUtils.copyFolder(parentPath, soPath);//copy插件的so到唯一单独文件夹
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        FileUtils.recursionDeleteFile(new File(unZipPath));//删除解压包
        return soPath;
    }

    //根据 Build.CPU_ABI 来判断当前适用的so架构，然把对应架构的so库复制到宿主apk对应的data so目录下
    public static void insertNativeLibraryPathElements(File soDirFile, Context context){
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        if(pathList != null) {
            Field nativeLibraryPathElementsField = null;
            try {
                Method makePathElements;
                Object invokeMakePathElements;
                boolean isNewVersion = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1;
                //调用makePathElements
                makePathElements = isNewVersion?pathList.getClass().getDeclaredMethod("makePathElements", List.class):pathList.getClass().getDeclaredMethod("makePathElements", List.class,List.class,ClassLoader.class);
                makePathElements.setAccessible(true);
                ArrayList<IOException> suppressedExceptions = new ArrayList<>();
                List<File> nativeLibraryDirectories = new ArrayList<>();
                nativeLibraryDirectories.add(soDirFile);
                List<File> allNativeLibraryDirectories = new ArrayList<>(nativeLibraryDirectories);
                //获取systemNativeLibraryDirectories
                Field systemNativeLibraryDirectoriesField = pathList.getClass().getDeclaredField("systemNativeLibraryDirectories");
                systemNativeLibraryDirectoriesField.setAccessible(true);
                List<File> systemNativeLibraryDirectories = (List<File>) systemNativeLibraryDirectoriesField.get(pathList);
                Log.i("insertNativeLibrary","systemNativeLibraryDirectories "+systemNativeLibraryDirectories);
                allNativeLibraryDirectories.addAll(systemNativeLibraryDirectories);
                invokeMakePathElements = isNewVersion?makePathElements.invoke(pathClassLoader, allNativeLibraryDirectories):makePathElements.invoke(pathClassLoader, allNativeLibraryDirectories,suppressedExceptions,pathClassLoader);
                Log.i("insertNativeLibrary","makePathElements "+invokeMakePathElements);

                nativeLibraryPathElementsField = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
                nativeLibraryPathElementsField.setAccessible(true);
                Object list = nativeLibraryPathElementsField.get(pathList);
                Log.i("insertNativeLibrary","nativeLibraryPathElements "+list);
                Object dexElementsValue = combineArray(list, invokeMakePathElements);
                //把组合后的nativeLibraryPathElements设置到系统中
                nativeLibraryPathElementsField.set(pathList,dexElementsValue);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object combineArray(Object hostDexElementValue, Object pluginDexElementValue) {
        //获取原数组类型
        Class<?> localClass = hostDexElementValue.getClass().getComponentType();
        Log.i("insertNativeLibrary","localClass "+localClass);
        //获取原数组长度
        int i = Array.getLength(hostDexElementValue);
        //插件数组加上原数组的长度
        int j = i + Array.getLength(pluginDexElementValue);
        //创建一个新的数组用来存储
        Object result = Array.newInstance(localClass, j);
        //一个个的将dex文件设置到新数组中
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(hostDexElementValue, k));
            } else {
                Array.set(result, k, Array.get(pluginDexElementValue, k - i));
            }
        }
        return result;
    }

    public static Object getPathList(Object classLoader) {
        Class cls = null;
        String pathListName = "pathList";
        try {
            cls = Class.forName("dalvik.system.BaseDexClassLoader");
            Field declaredField = cls.getDeclaredField(pathListName);
            declaredField.setAccessible(true);
            return declaredField.get(classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
