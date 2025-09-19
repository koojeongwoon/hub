package com.tinyquest.hub.shared.utils;

import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public final class IpUtils {

    private IpUtils() {
        // Utility class - 생성자 막기
    }

    public static String getLocalIp() {
        try {
            InetAddress[] inetAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress inetAddress : inetAddresses) {
                String ip = inetAddress.getHostAddress();
                if (isValidPublicIp(ip)) {
                    return ip;
                }
            }
            throw new BusinessException(ErrorCode.IP_ERROR, ErrorCode.IP_ERROR.getMessage());
        } catch (Exception e) {
            log.error("서버 IP 확인 실패", e);
            throw new BusinessException(ErrorCode.IP_ERROR, ErrorCode.IP_ERROR.getMessage());
        }
    }

    private static boolean isValidPublicIp(String ip) {
        return ip != null
                && !ip.startsWith("127.")   // 루프백 제외
                && !ip.startsWith("::")      // IPv6 제외
                && ip.contains(".");         // IPv4만 허용
    }
}
