package com.example.rpc;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InterfaceProxyHandler implements InvocationHandler {
    private static final Gson GSON = new Gson();
    private Class<?> interfaceClazz;
    private String socket = "127.0.0.1:8081";

    public InterfaceProxyHandler(Class<?> interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getSimpleName();
        String serviceBeanName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1) + "Impl";
        String methodName = method.getName();
        String requestUrl = "http://" + this.socket + "/" + serviceBeanName + "/" + methodName;
        LinkedHashMap<String, String> linkedHashMap = args2LinkedHashMap(method, args);
        String json = postJson(requestUrl, GSON.toJson(linkedHashMap));
        Class<?> returnType = method.getReturnType();
        return GSON.fromJson(json, returnType);
    }

    private LinkedHashMap<String, String> args2LinkedHashMap(Method method, Object[] args) {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            if (null == args[i]) {
                linkedHashMap.put(parameterTypes[i].getName() + "_" + i, "");
            } else {
                linkedHashMap.put(args[i].getClass().getName() + "_" + i, String.class.equals(args[i].getClass()) ? String.valueOf(args[i]) : GSON.toJson(args[i]));
            }
        }
        return linkedHashMap;
    }


    public static String postJson(String requestUrl, String params) throws Exception {
        System.out.println(params);
        System.out.println("??????????????????:" + requestUrl);
        URL url = new URL(requestUrl);
        // ?????????URL???????????????
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("?????????????????????????????????" + new Date().getTime() / 1000);
        connection.setRequestMethod("POST");
        // ???????????????????????????
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // ??????????????????????????????
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(params.getBytes("UTF-8"));
        out.flush();
        out.close();

        // ?????????????????????
        connection.connect();
        // ???????????????????????????
        Map<String, List<String>> headers = connection.getHeaderFields();
        // ??????????????????????????????
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // ?????? BufferedReader??????????????????URL?????????
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.out.println("????????????" + new Date().getTime() / 1000);
        System.out.println("result:" + result);
        return result;
    }

}
