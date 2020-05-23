package com.sl.chat.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * charset = UTF-8
 */
public class FileUtil {
    public static void main(String[] a){
        write("/home/frontman/downloads","config1.json","{\"name\":\"石浪\"}",true);
        System.out.println( read("/home/frontman/downloads","config1.json"));
    }
    /**
     *
     * @param path 目录
     * @param name 文件名
     * @return
     */
    public static String read(String path,String name) {
        File dir = new File(path);
        if (dir.isDirectory()&&dir.exists()){
            File file = new File(dir,name);
            if (!file.exists()){
                return null;
            }
            BufferedReader reader = null;
            StringBuilder str = new StringBuilder();
            InputStreamReader inputStreamReader = null;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream,StandardCharsets.UTF_8);
                reader = new BufferedReader(inputStreamReader);
                while (true){
                    String tmp = reader.readLine();
                    if (tmp == null)break;
                    str.append(tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fileInputStream!=null)
                        fileInputStream.close();
                    if (inputStreamReader!=null)
                        inputStreamReader.close();
                    if (reader!=null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str.toString();
        }
        return null;
    }

    /**
     *
     * @param path 目录
     * @param name 文件名
     * @param json 数据
     * @param append 是否添加在文件末尾
     */
    public static void write(String path, String name, String json,boolean append) {
        File dir = new File(path);
//
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }
        File file = new File(dir, name);
        file.setWritable(true);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file,append);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream,StandardCharsets.UTF_8);
            writer = new BufferedWriter(outputStreamWriter);
            writer.write(json,0,json.length());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream!=null)
                    fileOutputStream.close();
                if (outputStreamWriter!=null)
                    outputStreamWriter.close();
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
