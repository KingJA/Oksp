package sample.kingja.oksp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private String TAG=getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "getName: "+ CommonSpService.getDetault(this).getName() );
        Log.e(TAG, "getDiscount: "+ CommonSpService.getDetault(this).getDiscount() );
        CommonSpService.getDetault(this).putAge(26);
        Log.e(TAG, "getAge: "+ CommonSpService.getDetault(this).getAge() );



    }
}
