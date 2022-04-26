package com.ezpay.core.utils;

import java.util.HashMap;
import java.util.Map;

public class VnPayTransactionStatusUtil {
    public static final Map<String, String> STATUS_TRANSACTIONS = new HashMap<String, String>() {
        {
            put("00", "Giao dịch thành công");
            put("01", "Giao dịch chưa hoàn tất");
            put("02", "Giao dịch bị lỗi");
            put("04", "Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD " +
                    "chưa thành công ở VNPAY)");
            put("05", "VNPAY đang xử lý giao dịch này (GD hoàn tiền)");
            put("06", "VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền) ");
            put("07", "Giao dịch bị nghi ngờ gian lận");
            put("09", "GD Hoàn trả bị từ chối");
            put("10", "Đã giao hàng");
            put("20", "Giao dịch đã được thanh quyết toán cho merchant");
        }
    };
}
