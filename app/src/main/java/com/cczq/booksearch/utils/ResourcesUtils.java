package com.cczq.booksearch.utils;

/**
 * Created by bb on 2016/12/9.
 */

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 处理资源的类
 *
 * @author Yang
 * @date 2015年1月19日 上午10:19:51
 */
public class ResourcesUtils {

    /**
     * 判断是否有内存卡
     *
     * @return 返回true 存在，否则不存在
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回sdcard路径,若是没有内存卡则返回null
     *
     * @return
     */
    public static String getSDPath() {
        if (hasSdcard())
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        return null;
    }

    /**
     * 写入默认的主题文件到SD卡里面。
     *
     * @param c
     *            上下文环境
     * @param dstFileName
     *            写入文件的名字
     * @param srcAssetsPath
     *            源文件的路径
     */
    public static void writeRc(Context c, String dstDir, String dstFileName,
                               String srcAssetsPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File f = new File(dstDir);
            if (!f.exists()) {
                f.mkdirs();
            }

            File ff = new File(dstDir, dstFileName);
            if (ff.exists()) {
                return;
            }
            is = c.getAssets().open(srcAssetsPath);
            fos = new FileOutputStream(ff);
            byte[] buffer = new byte[10240];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e2) {
            }
        }

    }

}
