package com.example.videoweb.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chailei
 * @Date: 2024/8/10 12:30
 */
public class IpUtil {

    private static final String GET_IPV6 = "6.ipw.cn";
    private static final String TEST_IP_TYPE = "test.ipw.cn";

    public static void main(String[] args) {
        try {
            String wifiIpAddress = getWifiIpAddress("en0");
            System.out.println("WIFI IP Address: " + wifiIpAddress);

            String s = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", TEST_IP_TYPE), 5, TimeUnit.SECONDS);
            String s2 = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", GET_IPV6), 5, TimeUnit.SECONDS);

            System.out.println(s);
        } catch (Exception e) {
            System.err.println("Error fetching WIFI IP address: " + e.getMessage());
        }
    }

    /**
     * 获取WIFI接口的IPv4地址。
     *
     * @return WIFI接口的IPv4地址。
     * @throws SocketException 如果发生错误。
     */
    public static String getWifiIpAddress(String lanName) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // 检查是否为WIFI接口
            if (isWifiInterface(networkInterface, lanName)) {
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 检查是否为IPv4地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        return null; // 如果没有找到WIFI接口的IPv4地址
    }

    /**
     * 判断给定的网络接口是否为WIFI接口。
     *
     * @param networkInterface 要判断的网络接口。
     * @return 如果是WIFI接口则返回true，否则返回false。
     */
    private static boolean isWifiInterface(NetworkInterface networkInterface, String lanName) {
        // 这里可以根据实际情况调整判断逻辑
        // 例如，可以基于接口名称（如"wlan0"）或是否启用等条件进行判断
        String name = networkInterface.getName();
        return name != null && name.toLowerCase().startsWith(lanName);
    }
}
