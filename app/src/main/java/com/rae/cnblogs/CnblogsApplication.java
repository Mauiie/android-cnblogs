package com.rae.cnblogs;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.feedback.FeedbackThread;
import com.rae.cnblogs.sdk.UserProvider;
import com.rae.cnblogs.sdk.bean.UserInfoBean;
import com.rae.cnblogs.sdk.db.DbFactory;
import com.rae.swift.session.SessionManager;
import com.tencent.bugly.Bugly;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import skin.support.SkinCompatManager;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * 集成热更新的应用程序
 * Created by ChenRui on 2017/7/25 0025 19:15.
 */
public class CnblogsApplication extends TinkerApplication {

    public CnblogsApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.rae.cnblogs.CnblogsApplicationProxy");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 级别较高的初始化操作
        if (!BuildConfig.DEBUG) {
            // 正式环境
            Bugly.init(getApplication(), BuildConfig.BUGLY_APP_ID, false);
        } else {
            // 开发环境
        }

//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

        DbFactory.init(this);

        // LeanCloud用户反馈初始化，要在主线程
        AVOSCloud.initialize(getApplication(), BuildConfig.LEAN_CLOUD_APP_ID, BuildConfig.LEAN_CLOUD_APP_KEY);
        FeedbackThread.getInstance();

        MobclickAgent.setCatchUncaughtExceptions(BuildConfig.DEBUG);

        // 加载皮肤
        SkinCompatManager.withoutActivity(getApplication()).loadSkin();
        SkinCompatManager.getInstance()
                .addHookInflater(new ThemeCompat.CnblogsThemeHookInflater())
                .addInflater(new CnblogsLayoutInflater())
                .addInflater(new SkinMaterialViewInflater());

        // 一些要求不高的初始化操作放到线程中去操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserProvider.init(getApplication());
                SessionManager.initWithConfig(new SessionManager.ConfigBuilder().context(getApplication()).userClass(UserInfoBean.class).build());
                initUmengShareConfig();
            }
        }).start();
    }

    /**
     * 清除应用
     */
    public void clearCache() {
        // 清除图片缓存
        RaeImageLoader.clearCache(getApplicationContext());
        // 清除数据库
        DbFactory.getInstance().clearCache();
        new AppDataManager(this).clearCache();
    }


    /**
     * 友盟分享
     */
    private void initUmengShareConfig() {
        UMShareAPI.get(getApplication());
        PlatformConfig.setWeixin(AppConstant.WECHAT_APP_ID, AppConstant.WECHAT_APP_SECRET);
        PlatformConfig.setSinaWeibo(AppConstant.WEIBO_APP_ID, AppConstant.WEIBO_APP_SECRET, "http://www.raeblog.com/cnblogs/index.php/share/weibo/redirect");
        PlatformConfig.setQQZone(AppConstant.QQ_APP_ID, AppConstant.QQ_APP_SECRET);
    }

    public Application getApplication() {
        return this;
    }


    /**
     * 获取渠道包
     */
    public String getChannel() {
        try {
            return getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA)
                    .metaData
                    .getString("UMENG_CHANNEL", "official");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "official";
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base); // 解决Tinker存在的BUG，一定要在这之前初始化
        super.attachBaseContext(base);
    }

    public int getVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
}
