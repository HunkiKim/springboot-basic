package org.prgrms.kdt.command;

import org.prgrms.kdt.command.io.Output;

import java.util.regex.Pattern;

public class ValueValidation {
    private ValueValidation() {
    }

    public static boolean voucherTypeValidation(final String voucherType) {
        if (voucherType.equals("FixedAmountVoucher") || voucherType.equals("PercentDiscountVoucher")) {
            return true;
        } else {
            Output.wrongVoucherTypeMessage(voucherType);
            return false;
        }
    }

    public static boolean numberValidation(final String value) {
        try {
            Long.parseLong(value);
        } catch (final NumberFormatException e) {
            System.out.println("숫자를 입력하세요.");
            return false;
        }
        return true;
    }

    public static boolean rangeValidation(final String voucherType, final String value) {
        final long parsingValue = Long.parseLong(value);
        switch (voucherType) {
            case "FixedAmountVoucher":
                if (parsingValue <= 1000000 && parsingValue > 0)
                    return true;
                else {
                    System.out.println("할인 가격은 0원 초과, 1,000,000원 이하로 설정해주십시오.");
                    return false;
                }
            case "PercentDiscountVoucher":
                if (parsingValue <= 100 && parsingValue > 0)
                    return true;
                else {
                    System.out.println("할인율은 0% 초과, 100% 이하로 설정해주십시오.");
                    return false;
                }
            default:
                System.out.println("잘못된 voucher type 입니다.");
                return false;
        }
    }

    public static boolean nameValidation(final String name) {
        if (name.length() > 10) {
            System.out.println("열 글자 이하로 작성해주세요.");
            return false;
        }
        if (name.length() < 2) {
            System.out.println("두 글자 이상으로 작성해주세요.");
            return false;
        }
        return true;
    }

    public static boolean emailValidation(final String email) {
        if (email.length() > 30) {
            System.out.println("30글자 이하로 작성해주세요.");
            return false;
        }
        if (Pattern.matches("\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b", email))
            return true;
        else {
            System.out.println("email 형식으로 입력해주세요.");
            return false;
        }
    }
}
