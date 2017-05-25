package com.xgs925.xposedtest;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gus on 2017/5/25.
 */

public class Hook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {
        //这里是一个hook应用的实例，主要目的是hook com.example.crackme中的check函数，使其返回值为true
        if (!loadPackageParam.packageName.equals("com.example.crackme")) {
            //loadPackageParam为载入的package的参数，其中packageName存了包名，如果包名不一致，不做处理
            return;
        }
        //使用XposedBridge的log来输出载入的app，在tag过滤器中添加Xposed即可读取log
        XposedBridge.log("Loaded app:" + loadPackageParam.packageName);
//这里就是关键了，hook方法（函数）的时候用到这个方法
        XposedHelpers.findAndHookMethod(
                "com.example.crackme.MainActivity",//要hook的类
                loadPackageParam.classLoader,//获取classLoader
                "check",//要hook的方法（函数）
                String.class,//第一个参数
                String.class,//第二个参数
                new XC_MethodHook() {
                    //这里是hook回调函数
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //afterHookedMethod，顾名思义，在被hook的方法之后调用这个函数
                        //param.args作为数组，存了调用函数时的参数，通过getResult得到返回值
                        XposedBridge.log("after hook:" + param.args[0]);
                        XposedBridge.log("after hook 2:" + param.args[1]);
                        XposedBridge.log("result:" + param.getResult());
                        param.setResult(true);//通过setResult更改返回值
                        XposedBridge.log("result(settled):" + param.getResult());
                    }
                });
    }
}
