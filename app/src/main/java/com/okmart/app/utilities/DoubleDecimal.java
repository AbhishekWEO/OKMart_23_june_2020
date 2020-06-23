package com.okmart.app.utilities;

import java.text.DecimalFormat;

public class DoubleDecimal {

    public static String twoPoints(String value)
    {
        return String.format("%.2f", Double.valueOf(value));
    }


    public static String twoPointsComma(String value)
    {
        if(value.equals("0")) {
            return "0.00";
        }
        else {

            double d = Double.parseDouble(String.format("%.2f", Double.valueOf(value)));

            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            decimalFormat.setGroupingUsed(true);
            decimalFormat.setGroupingSize(3);

            return decimalFormat.format(d);
        }
    }
}