package xp.com.fingerprint;

/**
 * Created by lixiaopo on 2017/12/21.
 */


import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * 指纹检测
 */
public class XPFingerPrint {
    public static CancellationSignal mCancellationSignal;

    /**
     * 指纹检测
     *
     * @param callback
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void call(@NonNull Context context, @NonNull final IFingerListenCallback callback) {
        // 首先检测版本号，android6之后才有指纹检测
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onSupportFailed();
            return;
        }

        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        // 检测硬件是否支持
        if (!fingerprintManager.isHardwareDetected()) {
            callback.onSupportFailed();
            return;
        }

        // 判断设备是否在安全保护中
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {
            callback.OnInSecurity();
            return;
        }

        // 判断是否注册过指纹
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            callback.onEnrollFailed();
            return;
        }

        cancel();
        mCancellationSignal = new CancellationSignal();
        callback.onAuthenticationStart();
        fingerprintManager.authenticate(null, mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                callback.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthenticationSucceeded();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthenticationFailed();
            }
        }, null);
    }

    /**
     * 取消检测
     */
    public static void cancel() {
        if (null != mCancellationSignal) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }


    public interface IFingerListenCallback {
        /**
         * 当前android版本不支持
         */
        void onSupportFailed();

        /**
         * 设备处于安全保护中
         */
        void OnInSecurity();

        /**
         * 设备没有注册过指纹
         */
        void onEnrollFailed();

        /**
         * 指纹检测开始
         */
        void onAuthenticationStart();

        /**
         * 指纹检测失败
         *
         * @param errorCode    失败原因码(比如多次检测都失败)
         * @param errorMessage 失败原因
         */
        void onAuthenticationError(int errorCode, CharSequence errorMessage);

        /**
         * 指纹检测失败
         */
        void onAuthenticationFailed();

        /**
         * 检测帮助
         *
         * @param messageId
         * @param message
         */
        void onAuthenticationHelp(int messageId, CharSequence message);

        /**
         * 验证成功
         */
        void onAuthenticationSucceeded();
    }
}
