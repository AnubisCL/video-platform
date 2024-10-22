package com.example.videoweb.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chailei
 * @Date: 2024/8/10 12:30
 */
@Slf4j
public class IpUtil {

    private static final String GET_IPV6 = "6.ipw.cn";
    private static final String TEST_IP_TYPE = "test.ipw.cn";

    public static void main(String[] args) {
        try {
            //String wifiIpAddress = getWifiIpAddress("en0");
            //System.out.println("WIFI IP Address: " + wifiIpAddress);

            //String s = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", TEST_IP_TYPE), 5, TimeUnit.SECONDS);
            //String s2 = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", GET_IPV6), 5, TimeUnit.SECONDS);
            String ipv6Address = "****";
            String apikey = "***";
            String host = "***";
            String s2 = ProcessUtil.executeCommandWithResult(Arrays.asList("curl", "--location", "--request", "POST", "https://api.dnsexit.com/dns/ud/?apikey=" + apikey, "--data-urlencode", "ip="+ipv6Address, "--data-urlencode", "host=" + host),5, TimeUnit.SECONDS);
            System.out.println(s2);
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

    public static String getIpAddressProtocolType(HttpServletRequest request, String domainHost) {
        boolean isLocal = false;
        boolean isIpv4 = false;
        boolean isIpv6 = false;
        String host = request.getHeader("host");
        if (host.equals(domainHost)) return "domain";
        //ipv6
        String replace = host.replace("[", "").replace("]", "");
        // 判断host是ipv4还是ipv6还是127.0.0.1/localhost
        // 使用 InetAddress 来判断 IP 地址类型
        try {
            InetAddress inetAddress = InetAddress.getByName(replace); // 去掉可能存在的端口号
            if (inetAddress.isLoopbackAddress()) {
                isLocal = true;
            } else if (inetAddress instanceof java.net.Inet4Address) {
                isIpv4 = true;
            } else if (inetAddress instanceof java.net.Inet6Address) {
                isIpv6 = true;
            }
        } catch (UnknownHostException e) {
            // 如果 host 不是一个 IP 地址，尝试使用正则表达式判断
            if ("localhost".equalsIgnoreCase(replace)) {
                log.info("Host is localhost.");
                isLocal = true;
            } else if (replace.matches("^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})$")) {
                isIpv4 = true;
            } else if (replace.matches("^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$")) {
                isIpv6 = true;
            } else {
                log.warn("Host is not recognized as an IP address or localhost.");
            }
        }
        log.info("--- isLocal:{}, , isIpv4: {}, isIpv6:{}", isLocal, isIpv4, isIpv6);
        if (isIpv4) return "ipv4";
        else if (isIpv6) return "ipv6";
        else return "local";
    }
}
