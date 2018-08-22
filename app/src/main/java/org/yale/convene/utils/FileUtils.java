package org.yale.convene.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import org.yale.convene.UpdateMasterLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    private final static String FILE_EXTENSION_SEPARATOR = ".";
    public final static String TAG = "FileUtils";

    private FileUtils() {
        throw new AssertionError();
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }


    /**
     * Creates the directory named by the trailing filename of this file, including the complete directory path required
     * to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target directory already exists, false one of
     *         the directories can not be created.
     *         <ul>
     *         <li>if {@link FileUtils#getFolderName(String)} return null, return false</li>
     *         <li>if target directory already exists, return true</li>
     *         <li>return {@link java.io.File#mkdirs}</li>
     *         </ul>
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        if(folder.exists())
            folder.delete();
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     *
     * @param zipFile
     * @param sourceFolder
     * @param fileList
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void zipIt(String zipFile, String sourceFolder, List<String> fileList) {

        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (String file : fileList) {
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                methodToGetFiles(file,sourceFolder,zos);
            }
            System.out.println("Done");
        } catch (IOException ex) {
            Logger.logE("", "", ex);
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void methodToGetFiles(String sourceFolder, String file, ZipOutputStream zos) {
        byte[] buffer = new byte[1024];
        try(FileInputStream in = new FileInputStream(sourceFolder + File.separator + file)){
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }catch (Exception e){
            Logger.logE("","",e);
        }
    }

    /**
     *
     * @param subPath
     * @return
     */
    public static List<String> getFilesList(String subPath) {
        File sdCardRoot = new File(subPath);
        List<String> filesList = new ArrayList<>();

        for (File f : sdCardRoot.listFiles()) {
            if (f.isFile())
                filesList.add(f.getName());
        }
        return filesList;
    }

    public static String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName, String dirName, String uId) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(new Date());
        String pathOfImage = Environment.getExternalStorageDirectory()+dirName+"/"+ uId+"/"+fileName+timeStamp+".jpg";
        File direct = new File(Environment.getExternalStorageDirectory() + dirName+"/"+ uId+"/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + dirName+"/"+ uId);
            wallpaperDirectory.mkdirs();
        }
        File file = new File(pathOfImage);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            pathOfImage="";
            Logger.logE(TAG, "createDirectoryAndSaveFile : ", e);
        }
        return pathOfImage;
    }

    /**
     * Copying the whole database if the responsetype is 4 for updating the application
     * @param con param
     *
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void copyEncryptedDataBaseForMasterData(Context con,String dbName,String dbFileName) {
        String packageName = con.getApplicationContext().getPackageName();
        String dbPath = String.format("/data/data/%s/databases/", packageName);
        final String inFileName = dbPath + dbName;
        Logger.logV("the input file is", "the path of the file is" + inFileName);
        File dbFile = new File(inFileName);
        String outFileName = Environment.getExternalStorageDirectory().getPath() + "/" + "Cry.sqlite";

        if (dbFile.exists()) {
            try (FileInputStream fis = new FileInputStream(dbFile); FileOutputStream output = new FileOutputStream(outFileName)) {
                Logger.logV("teh file path is", "the file path is sd card is" + Environment.getExternalStorageDirectory().getPath() + "/" + dbFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                fis.close();
            } catch (Exception e) {
                Logger.logE(UpdateMasterLoading.class.getSimpleName(), " in copyEncryptedDataBase method", e);
            }
        }
    }

    /**
     * copyEncryptedDataBase method
     * @param con param
     *
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void copyEncryptedDataBase(Context con) {
        String packageName = con.getApplicationContext().getPackageName();
        String dbPath = String.format("/data/data/%s/databases/", packageName);
        final String inFileName = dbPath + "CryMaster.sqlite";
        File dbFile = new File(inFileName);
        //if file not exist not doing any thing
        if (!dbFile.exists())
            return;
        // file exist then get the file uri and write data to it
        String outFileName = Environment.getExternalStorageDirectory().getPath() + "/" + "levels.sqlite";
        try (FileInputStream fis = new FileInputStream(dbFile); OutputStream output = new FileOutputStream(outFileName)){
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Logger.logE(TAG, "Exception in copyEncryptedDataBase method", e);
        } catch (IOException e) {
            Logger.logE(TAG, "Exception in copyEncryptedDataBase method second catch", e);
        }
    }
}
