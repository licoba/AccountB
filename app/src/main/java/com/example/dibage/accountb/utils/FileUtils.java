package com.example.dibage.accountb.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    /**
     * 如果文件不存在，就创建文件
     *
     * @param path 文件路径
     * @return
     */
    public static String createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return path;
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 目标文件全路径
     * @param data     要写入的数据
     * @return true表示写入成功  false表示写入失败
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 从文件中读取数据
     *
     * @param file
     * @return
     */
    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;

    }

    /**
     * 向文件中写入字符串String类型的内容
     *
     * @param file    文件路径
     * @param content 文件内容
     * @param charset 写入时候所使用的字符集
     */
    public static void writeString(String file, String content, String charset) {
        try {
            byte[] data = content.getBytes(charset);
            writeBytes(file, data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }





    /**
     * 从文件中读取数据，返回类型是字符串String类型
     *
     * @param file    文件路径
     * @param charset 读取文件时使用的字符集，如utf-8、GBK等
     * @return
     */
    public static String readString(String file, String charset) {
        byte[] data = readBytes(file);
        String ret = null;
        try {
            ret = new String(data, charset);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;


    }

    /**
     * 存储文件
     * @param path 文件路径
     * @param data 以字符串形式来存储数据
     * @return
     */
    public static boolean saveData(String path, String data) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = data.getBytes();
            fos.write(bytes);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 通过路径和后缀名来获取文件
     * @param path
     * @param type
     * @return
     */
    public static List<String> getDataByType(String path,String type){
        List<String> resultList = new ArrayList<String>();
        File[] files = new File(path).listFiles();//获取路径下所有文件
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    if (file.getPath().substring(file.getPath().length() - type.length()).equals(type))  //判断扩展名
                        resultList.add(file.getPath());
                }
            }
        }
        return resultList;
    }


    /**
     * 根据路径删除文件
     * @param filename
     * @return
     */
    public static boolean deleteFile(String filename){
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // 調用系統方法分享文件
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(context, "com.example.dibage.accountb.fileprovider", file);
            Log.e("URI字符串",uri.toString());
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("*/*");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    /**
     * 获取文件名称
     * @param pathandname 文件的路径
     * @return
     */
    public static  String   getFileName(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }
    }


    public static void renameFile(String filePath,String reName ){
        File file = new File(filePath);
        //前面路径必须一样才能修改成功
        String path = filePath.substring(0, filePath.lastIndexOf("/")+1)+reName+filePath.substring(filePath.lastIndexOf("."), filePath.length());
        File newFile = new File(path);
        file.renameTo(newFile);
    }
}