package org.noamichael.utils.jsf;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author michael
 */
public final class JSFFunctions {
    
    public static String getCurrentDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    public static Object createObject(String name) throws Exception {
        return Class.forName(name).getConstructor().newInstance();
    }

}
