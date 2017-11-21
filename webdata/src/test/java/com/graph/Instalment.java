package com.graph;

/**
 * Created by stcas on 2017/11/7.
 */
public class Instalment {
    public static void main(String[] args) {
        float interest = 0.045f;//利率
        int month = 6;
        float pay_money = 450;
        float save = calculator(interest,month,pay_money);
        System.out.println(save);
    }

    private static float calculator(float interest, int month, float pay_money) {
        float money_per_month = pay_money / month;
        float interest_per_month = interest/360*30;
        float result = 0;
        for(int i=1;i<=month;i++){
            result += pay_money*interest_per_month;
            pay_money -= money_per_month;
            System.out.println("----"+pay_money);
        }
        return result;
    }
}
