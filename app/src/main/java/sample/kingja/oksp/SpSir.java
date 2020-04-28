package sample.kingja.oksp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description:TODO
 * Create Time:2020/4/28 0028 上午 11:21
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SpSir {
    private static SpSir spSir;
    private final SharedPreferences sp;

    public SpSir(Context context) {
        sp = context.getSharedPreferences("name", Context.MODE_PRIVATE);
    }

    public SpSir getDefault(Context context) {
        if (spSir == null) {
            spSir = new SpSir(context);
        }
        return spSir;
    }

    public void setName(String name) {
        sp.edit().putString("name",name).apply();
    }
    public void getName() {
        sp.getString("name","default");
    }
}
