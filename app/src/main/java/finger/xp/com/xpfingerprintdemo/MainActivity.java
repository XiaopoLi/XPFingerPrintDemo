package finger.xp.com.xpfingerprintdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import xp.com.bind.XPBind;
import xp.com.bind.XPBinder;
import xp.com.fingerprint.XPFingerPrint;

public class MainActivity extends AppCompatActivity {
    @XPBind(R.id.tv_status)
    private TextView tvStatus;
    @XPBind(R.id.btn_detect)
    private Button btnDetect;
    private boolean mIsStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XPBinder.bind(this);

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        if (false == mIsStarted) {
                            start();
                        } else {
                            stop();
                        }
                    }
                });
            }
        });
    }

    /**
     * 开始
     */
    private void start() {
        XPFingerPrint.call(this, new XPFingerPrint.IFingerListenCallback() {
            @Override
            public void onSupportFailed() {
                tvStatus.setText("设备不支持");
            }

            @Override
            public void OnInSecurity() {
                tvStatus.setText("设备在安全保护中");
            }

            @Override
            public void onEnrollFailed() {
                tvStatus.setText("设备没有设置指纹");
            }

            @Override
            public void onAuthenticationStart() {
                tvStatus.setText("开始验证指纹");
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errorMessage) {
                tvStatus.setText("指纹验证错误(" + errorCode + ") : " + errorMessage);
            }

            @Override
            public void onAuthenticationFailed() {
                tvStatus.setText("指纹验证失败");
            }

            @Override
            public void onAuthenticationHelp(int messageId, CharSequence message) {
                tvStatus.setText("指纹验证出现问题(" + messageId + ") : " + message);
            }

            @Override
            public void onAuthenticationSucceeded() {
                tvStatus.setText("指纹验证成功");
            }
        });
    }

    /**
     * 结束
     */
    private void stop() {
        XPFingerPrint.cancel();
        tvStatus.setText("结束");
    }
}
