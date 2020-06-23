package com.okmart.app.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondsToDateTime {

    public static String conversion(String _seconds)
    {

        long miliSec = Long.valueOf(_seconds)*1000;
        // Creating date format
        DateFormat simple = new SimpleDateFormat("dd-MM-yyyy, hh:mm a");
        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(miliSec);

        return simple.format(result).toUpperCase();
    }

    public static String conversionDate(String _seconds)
    {

        long miliSec = Long.valueOf(_seconds)*1000;
        // Creating date format
        DateFormat simple = new SimpleDateFormat("dd-MM-yyyy");
        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(miliSec);

        return simple.format(result).toUpperCase();
    }

    public static String millisecToDateTime(String sec)
    {
        long miliSec = Long.valueOf(sec)*1000;

        DateFormat simple = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss");

        Date result = new Date(miliSec);

        return simple.format(result);
    }

    public static String millisecToDateTimee(String sec)
    {
        long miliSec = Long.valueOf(sec)*1000;

        DateFormat simple = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss a");

        Date result = new Date(miliSec);

        return simple.format(result);
    }
}