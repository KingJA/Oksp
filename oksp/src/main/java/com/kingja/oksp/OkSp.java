package com.kingja.oksp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description:TODO
 * Create Time:2019/8/22 0022 下午 3:52
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class OkSp {
    private IEncryption iEncryption;

    public void init(Context context, String name, int mode) {

    }

    static class Builder {
        /*文件名*/
        private String fileName;
        /*文件读写模式*/
        private int mode;
        /*加密方式*/
        private IEncryption iEncryption;


        public void setSpName(String name) {

        }

    }


}
