package com.ezpay.core.gateway.impl;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.MegaPayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MegaPayImpl implements Payment, MegaPayConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(MegaPayImpl.class);

    private String encodeKey = "6D0870CDE5F24F34F3915FB0045120DB";

    @Override
    public boolean checkFields(Map<String, String> fields, String key) {

        if (fields.get(MERCHANT_TOKEN) == null) {
            return false;
        }
        String hashed = fields.remove(MERCHANT_TOKEN);
        if (fields.get(RESULT_CD) != null) {
            if (StringUtils.hasText(key)) {
                encodeKey = key;
            }
            String merchantToken = hashFieldsNotiUrl(fields);
            if (hashed.equalsIgnoreCase(merchantToken)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPaymentLink(List<MerchantGatewaysetting> params, String payUrl) {
        String queryString = payParams(params);
        LOGGER.info("payUrl: " + payUrl + queryString);
        return payUrl + queryString;
    }

    @Override
    public String getResponseDescription(String vResponseCode) {
        Map<String, String> map_result = new HashMap<String, String>() {
            {
                put("00_000", "Giao dịch thành công");
                put("FL_900", "Lỗi kết nối");
                put("FL_901", "Lỗi kết nối socket");
                put("FL_902", "Có lỗi xảy ra trong quá trình xử lý");
                put("FL_903", "Lỗi kết nối socket quá thời gian quy định");
                put("OR_101", "MerId không hợp lệ hoặc merchant chưa được đăng ký thông tin. Liên hệ với trung tâm dịch vụ khách hàng để biết thêm thông tin");
                put("OR_102", "Hình thức thanh toán này không tồn tại hoặc chưa được kích hoạt. Liên hệ với trung tâm dịch vụ khách hàng để biết thêm thông tin");
                put("OR_103", "Mã tiền tệ chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [currencyCode] được định nghĩa");
                put("OR_104", "Tên thành phố người mua chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [buyerCity] được định nghĩa");
                put("OR_105", "Mã hóa đơn chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [invoiceNo] được định nghĩa");
                put("OR_106", "Tên hàng hóa chưa được định nghĩa hoặc sai định dạng. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [goodsNm] được định nghĩa");
                put("OR_107", "Tên hoặc họ người mua chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [buyerFirstNm] và [buyerLastNm] được định nghĩa");
                put("OR_108", "Số điện thoại người mua chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [buyerPhone] được định nghĩa");
                put("OR_109", "Địa chỉ email người mua chưa được định nghĩa hoặc chưa đúng định dạng. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [buyerEmail] được định nghĩa đúng");
                put("OR_110", "Callback URL chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [callbackUrl] được định nghĩa");
                put("OR_111", "Notification URL chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [notiUrl] được định nghĩa");
                put("OR_112", "Số tiền thanh toán không hợp lệ. Số tiền chỉ nên là số không có phần thập phân");
                put("OR_113", "Chữ ký của merchant không hợp lệ. Liên hệ với trung tâm dịch vụ khách hàng để biết thêm thông tin");
                put("OR_114", "Số tiền thanh toán phải lớn hơn 0. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [amount] được định nghĩa");
                put("OR_115", "Lỗi trường flag để xác định merchant có kiểm tra order no trùng lặp hay không bị null");
                put("OR_116", "Số hóa đơn bị trùng lặp");
                put("OR_117", "Mã giao dịch Merchant (merTrxId ) bị trùng lặp");
                put("OR_118", "Lỗi do 1 trong các nguyên nhân:\n" +
                        "+) Request domain chưa được định nghĩa\n" +
                        "+) Tổng giá trị món hàng và phí merchant không bằng tổng giá trị giao dịch thanh toán+) MerchantId do merchant gửi lên bị null\n" +
                        "+) Thông tin merchantId không khớp (Chức năng truy vấn thông tin giao dịch)");
                put("OR_120", "Lỗi trạng thái của merchant (Merchant không hoạt động)");
                put("OR_122", "Merchant Transaction ID sai định dạng hoặc rỗng");
                put("OR_123", "Lỗi merchant chưa được khai báo trên hệ thống");
                put("OR_124", "Lỗi trạng thái của merchant (Merchant không hoạt động)");
                put("OR_125", "Merchant không được đăng ký phương thức thanh toán này hoặc thời gian thanh\n" +
                        "toán Cybersource chưa được định nghĩa");
                put("OR_126", "Loại cổng thanh toán chưa được thiết lập");
                put("OR_127", "Lỗi khi kiểm tra hạn mức áp dụng của merchant");
                put("OR_128", "Số tiền thanh toán vượt quá định mức giới hạn");
                put("OR_130", "Trường thông tin xác định merchant là online hay offline chưa được định nghĩa. " +
                        "Xin vui lòng kiểm tra các tham số được yêu cầu và đảm bảo trường [merType] được " +
                        "định nghĩa");
                put("OR_131", "Loại merchant online này hiện tại chưa được kích hoạt");
                put("OR_132", "Loại merchant offline này hiện tại chưa được kích hoạt");
                put("OR_133", "Thông tin hợp đồng chưa được định nghĩa");
                put("OR_134", "Sai số tiền");
                put("OR_135", "Số tiền hàng chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của " +
                        "bạn và đảm bảo trường [ goodsAmount ] được định nghĩa");
                put("OR_136", "Cước phí user chưa được định nghĩa. Xin vui lòng kiểm tra lại các tham số được yêu cầu của bạn và đảm bảo trường [ userFee ] được định nghĩa");
                put("OR_140", "Không tìm thấy giao dịch");
                put("OR_141", "Địa chỉ người mua không được để trống");
                put("OR_142", "Bang/tỉnh thành người mua không được để trống khi đất nước là 'us' hoặc 'ca'");
                put("OR_143", "Quốc gia người mua không được để trống");
                put("OR_147", "description không hợp lệ");
                put("OR_148", "timeStamp không được để trống hoặc không hợp lệ");
                put("OR_150", "bankCode không tồn tại hoặc không hợp lệ");
                put("OR_151", "Hệ thống không hỗ trợ Tokenization cho phương thức thanh toán này");
                put("OR_152", "userId không được để trống hoặc không hợp lệ");
                put("OR_153", "payOption không đúng");
                put("DC_101", "Lỗi khi kiểm tra các trường thông tin gửi lên cũng như trả về từ NAPAS");
                put("DC_102", "Mã giao dịch chưa được tạo");
                put("DC_103", "Giao dịch đã tồn tại. Xin hãy tạo giao dịch mới");
                put("DC_104", "Số hóa đơn bị null. Xin hãy đảm bảo trường [invoiceNo] đã được khai báo");
                put("DC_105", "Lỗi dữ liệu bị null");
                put("DC_110", "Trường hình thức thanh toán không xác định. Liên hệ với nhà cung cấp Megapay để có thêm thông tin");
                put("DC_112", "Lỗi khi cập nhật hoặc thêm dữ liệu vào các bảng liên quan tới giao dịch ATM");
                put("DC_113", "Lỗi khi cập nhật giao dịch email");
                put("DC_114", "Lỗi khi lưu dữ liệu vào bảng thông báo giao dịch");
                put("DC_117", "Giao dịch chưa được đăng ký thông tin. Xin vui lòng kiểm tra lại");
                put("DC_119", "Máy chủ đang bận. Xin vui lòng thử lại sau vài phút");
                put("DC_120", "Thanh toán thành công nhưng khóa xác thực giao dịch chưa được tạo");
                put("DC_122", "Mã giao dịch gửi sang Partner không được để trống");
                put("DC_123", "Mã giao dịch gửi sang Partner không hợp lệ");
                put("DC_124", "Số tiền không hợp lệ");
                put("DC_125", "Loại tiền không hợp lệ");
                put("IC_101", "Giao dịch thất bại. Xin hãy kiểm tra thông tin thẻ và thử lại");
                put("IC_102", "Mã giao dịch chưa được định nghĩa");
                put("IC_103", "Giao dịch đã tồn tại. Xin hãy tạo giao dịch mới");
                put("IC_104", "Mã hóa đơn không xác định (null). Xin vui lòng kiểm tra lại trường [invoiceNo]");
                put("IC_105", "Thông tin thẻ của merchant chưa được khai báo");
                put("IC_107", "Lỗi xảy ra khi kết nối CyberSource");
                put("IC_110", "Phương thức thanh toán hoặc mã merchant bị thiếu");
                put("IC_112", "Lỗi khi insert dữ liệu vào các bảng liên quan tới giao dịch quốc tế");
                put("IC_113", "Lỗi xảy ra khi update bảng lưu giao dịch email");
                put("IC_115", "MID không hợp lệ, Merchant chưa được đăng ký thông tin. Liên hệ với Megapay để biết thêm thông tin");
                put("IC_117", "Giao dịch chưa được đăng ký thông tin");
                put("IC_121", "Merchant đang ở trạng thái không hoạt động");
                put("IC_122", "payToken sai định dạng hoặc rỗng");
                put("IC_123", "Merchant không hỗ trợ thanh toán bằng Tokenization đối với thẻ Quốc tế");
                put("IC_124", "Không tìm thấy Token");
                put("VA_101", "Kết nối tới hệ thống VA thất bại hoặc mã giao dịch chưa được khai báo");
                put("VA_102", "Giao dịch VA đã tồn tại");
                put("VA_103", "Thiếu thông tin merchant id");
                put("VA_104", "Lỗi khi insert dữ liệu vào bảng giao dịch VA");
                put("VA_105", "Lỗi khi insert dữ liệu và bảng kết quả giao dịch");
                put("VA_106", "Có lỗi trong quá trình tìm kiếm giao dịch hoặc giao dịch chưa tồn tại");
                put("VA_107", "Có lỗi khi insert vào bảng thông báo tới merchant");
                put("VA_109", "Sai điều kiện nhận tiền (nên bằng 03)");
                put("VA_110", "Sai ngày bắt đầu hiệu lực");
                put("VA_111", "Sai ngày hết hiệu lực");
                put("VA_112", "Giao dịch không hợp lệ");
                put("VA_113", "Không tìm thấy Ngân hàng phát hành Tài khoản chuyên dụng");
                put("CC_101", "Mã giao dịch chưa được tạo");
                put("CC_102", "MID không hợp lệ hoặc Merchant chưa được đăng ký thông tin. Liên hệ với trung tâm dịch vụ khách hàng để có thêm thông tin");
                put("CC_109", "Merchant ở trạng thái chưa được kích hoạt");
                put("CC_110", "Giao dịch chưa được đăng ký thông tin");
                put("CC_111", "Lỗi xảy ra khi số tiền hủy nhỏ hơn hoặc bằng 0 hoặc số tiền hủy không bằng số tiền đã giao dịch (đối với trường hợp hủy toàn phần)");
                put("CC_112", "Không tìm thấy giao dịch cần hủy");
                put("CC_113", "Giao dịch hủy toàn phần đã được hủy trước đó");
                put("CC_114", "Phương thức thanh toán này hiện tại chưa được kích hoạt với merchant hoặc giao dịch insert dữ liệu thông báo thất bại");
                put("CC_115", "Chữ ký của merchant không hợp lệ");
                put("CC_116", "Số tiền hủy phải bằng số tiền thanh toán");
                put("CC_117", "Số tiền hoàn/hủy không hợp lệ (Lỗi định dạng số)");
                put("CC_118", "Giao dịch đã bị hủy");
                put("CC_119", "Số tiền hoàn/hủy lớn hơn số tiền thanh toán hoặc số tiền thanh toán còn lại nhỏ hơn 0");
                put("CC_121", "Lỗi khi cập nhật thông tin giao dịch");
                put("CC_122", "Lỗi khi insert thông tin giao dịch hủy 1 phần");
                put("CC_124", "Lỗi khi thêm dữ liệu sau khi hủy");
                put("CC_125", "Lỗi khi đăng ký dữ liệu trong bảng kết quả giao dịch");
                put("CC_126", "Lỗi khi truy vấn dữ liệu");
                put("CC_127", "Cờ hủy 1 phần hoặc trạng thái không hợp lệ");
                put("CC_128", "Thông điệp hủy (cancelMsg) chưa được định nghĩa");
                put("CC_130", "Số tiền muốn hủy nhỏ hơn số tiền giao dịch thanh toán");
                put("CC_131", "Lỗi khi insert dữ liệu vào bảng giao dịch hủy");
                put("CC_132", "Lỗi khi cập nhật dữ liệu lịch sử giao dịch");
                put("CC_133", "Lỗi khi kết nối tới ngân hàng");
                put("CC_135", "Mật khẩu hủy không đúng");
                put("CC_136", "Chức năng hủy chưa sẵn sàng đối với merchant này. Xin hãy liên hệ với Megapay");
                put("PG_ER1", "Giao dịch thất bại, vui lòng thử lại");
                put("PG_ER2", "Thông tin thẻ không đúng, vui lòng thử lại");
                put("PG_ER3", "Giao dịch thất bại – Quá thời gian thanh toán");
                put("PG_ER4", "Giao dịch thất bại – Không rõ nguyên nhân, Vui lòng liên hệ với admin Megapay để được hỗ trợ");
                put("PG_ER5", "Khách hàng hủy giao dịch");
                put("PG_ER6", "Lỗi hệ thống, xin vui lòng liên hệ với Admin Megapay để được hỗ trợ");
                put("PG_ER7", "Số thẻ không đúng.");
                put("PG_ER8", "Ngày phát hành/Hết hạn không đúng");
                put("PG_ER10", "Sai địa chỉ người mua");
                put("PG_ER11", "Thẻ chưa được cấu hình Payer Authentication");
                put("PG_ER12", "Sai họ tên người mua");
                put("PG_ER13", "Sai thành phố/Bang người mua");
                put("PG_ER16", "OTP không đúng");
                put("PG_ER17", "Thông tin thẻ chưa được duyệt, vui lòng liên hệ với Ngân hàng phát hành để được hỗ trợ");
                put("PG_ER18", "Thẻ hết hạn hoặc bị khóa.");
                put("PG_ER19", "Tài khoản không đủ số dư để thanh toán");
                put("PG_ER21", "Thẻ chưa kích hoạt hoặc chưa đăng ký thanh toán online");
                put("PG_ER22", "Tên chủ thẻ không đúng.");
                put("PG_ER23", "Ngân hàng phát hành thẻ từ chối cấp phép cho giao dịch");
                put("PG_ER25", "Giao dịch bị từ chối bởi hệ thống quản lý rủi ro");
                put("PG_ER26", "Dữ liệu không hợp lệ hoặc bị rỗng");
                put("PG_ER28", "Ngân hàng phát hành đang tạm dừng giao dịch. Vui lòng thử lại sau");
                put("PG_ER29", "Giao dịch thất bại do Khách hàng nằm trong danh sách nghi vấn");
                put("PG_ER30", "Giao dịch thất bại - Không thể xác thực được khách hàng");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "Có lỗi xảy ra trong quá trình xử lý";

    }

    @Override
    public String hashKey(Map<String, String> fields, String key) {
        return null;
    }

    private String payParams(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getParameter()) && StringUtils.hasText(p.getValue())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    encodeKey = p.getValue();
                }
            }
        }

        LOGGER.info("secretKey: " + encodeKey);
        if (encodeKey != null && encodeKey.length() > 0) {
            String secureHash = hashAllFields(fields);
            fields.put(MERCHANT_TOKEN, secureHash);
        }
        StringBuffer buf = new StringBuffer();
        buf.append('?');
        buf.append(appendQueryFields(fields));
        return buf.toString();
    }

    private String hashFieldsNotiUrl(Map<String, String> fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        String payToken = "";

        String valueToken = fields.get(MegaPayConstant.RESULT_CD) + fields.get(MegaPayConstant.TIMESTAMP) +
                fields.get(MegaPayConstant.MERTRX_ID) + fields.get(MegaPayConstant.TRX_ID) +
                fields.get(MegaPayConstant.MERID) + fields.get(MegaPayConstant.AMOUNT) +
                payToken + encodeKey;

        String hashValue = encryptSha256(valueToken);
        LOGGER.info("merchantToken: " + hashValue);
        return hashValue;
    }

    private String hashAllFields(Map<String, String> fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);

        String payToken = "";
        String valueToken = fields.get(MegaPayConstant.TIMESTAMP) + fields.get(MegaPayConstant.MERTRX_ID) +
                fields.get(MegaPayConstant.MERID) + fields.get(MegaPayConstant.AMOUNT) + payToken + encodeKey;

        String hashValue = encryptSha256(valueToken);
        LOGGER.info("merchantToken: " + hashValue);
        return hashValue;
    }

    private String appendQueryFields(Map<String, String> fields) {
        try {
            List fieldNames = new ArrayList(fields.keySet());
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    buf.append(URLEncoder.encode(fieldName, "UTF-8"));
                    buf.append('=');
                    buf.append(URLEncoder.encode(fieldValue, "UTF-8"));
                }
                if (itr.hasNext()) {
                    buf.append('&');
                }
            }
            return buf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String encryptSha256(String str) {
        String SHA = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }
}
