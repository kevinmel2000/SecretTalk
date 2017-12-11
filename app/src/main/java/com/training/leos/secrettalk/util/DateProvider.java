package com.training.leos.secrettalk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leo on 11/12/2017.
 */

public class DateProvider {
    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
