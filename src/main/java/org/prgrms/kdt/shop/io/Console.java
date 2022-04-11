package org.prgrms.kdt.shop.io;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Console implements Input, Output {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void menu( ) {
        System.out.println("=== Voucher Program ===\nType exit to exit the program\nType create to create a new voucher.\nType list to list all vouchers.\n");
    }

    @Override
    public void inputError( ) {
        System.out.println("input error");
    }

    @Override
    public void selectVoucher( ) {
        System.out.println("1. FixedAmountVoucher 2. PercentDiscountVoucher");
        System.out.print("입력 : ");
    }

    @Override
    public void selectDiscount( ) {
        System.out.print("할인 입력 : ");
    }

    @Override
    public void selectMenu( ) {
        System.out.print("메뉴 입력 : ");
    }

    @Override
    public String input( ) {
        return scanner.nextLine().trim();
    }
}
