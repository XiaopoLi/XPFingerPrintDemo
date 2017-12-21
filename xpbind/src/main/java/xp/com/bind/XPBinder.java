package xp.com.bind;

import android.app.Activity;
import android.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by lixiaopo on 2017/12/21.
 */

/**
 * 绑定实现类
 */
public class XPBinder {
    /**
     * 绑定Activity
     *
     * @param activity
     */
    public static void bind(Activity activity) {
        // 首先拿到类型
        Class cls = activity.getClass();
        // 获取对象的成员字段
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            // 判断是否添加了注解，而且是XPBind的注解
            boolean isExist = field.isAnnotationPresent(XPBind.class);
            if (!isExist) {
                continue;
            }

            // 设置字段可以访问
            field.setAccessible(true);
            // 获取资源id
            XPBind bind = field.getAnnotation(XPBind.class);
            int sourceId = bind.value();
            try {
                field.set(activity, activity.findViewById(sourceId));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绑定fragment
     * @param fragment
     */
    public static void bind(Fragment fragment) {
        bind(fragment.getActivity());
    }
}
