package org.opensource.redis.objectmapper.serializer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 */
public class StringConverter {
  public static final String dataFormat = "YYYY";

  public StringConverter(String dateFormat) {
  }


  public static String convertToString(Object data) {
    if (data instanceof Date) {

    }
    return String.valueOf(data);
  }

  public static String convertToString(Date date, String dateFormat) {
    return DateFormatUtils.format(date, dateFormat);
  }

  public static <T> T convertFromString(String rowData, Class<T> type) {
    boolean isNotBlankData = StringUtils.isNotBlank(rowData);
    try {
      if (type == String.class) {
        return (T) rowData;
      } else if (type == Integer.TYPE || type == Integer.class) {
        return (T) (Integer) (isNotBlankData ? Integer.parseInt(rowData) : 0);
      } else if (type == Double.TYPE || type == Double.class) {
        return (T) (Double) (isNotBlankData ? Double.parseDouble(rowData) : 0D);
      } else if (type == Long.TYPE || type == Long.class) {
        return (T) (Long) (isNotBlankData ? Long.parseLong(rowData) : 0L);
      } else if (type == Float.TYPE || type == Float.class) {
        return (T) (Float) (isNotBlankData ? Float.parseFloat(rowData) : 0F);
      } else if (type == BigDecimal.class) {
        return (T) (isNotBlankData ? new BigDecimal(rowData) : BigDecimal.ZERO);
      } else if (type == Boolean.TYPE || type == Boolean.class) {
        return (T) (isNotBlankData ? Boolean.valueOf(rowData) : Boolean.FALSE);
      } else if (type == Character.TYPE || type == Character.class) {
        return (T) (Character) (StringUtils.isNotEmpty(rowData) ? rowData.charAt(0) : '\0');
      } else if (type == Byte.TYPE || type == Byte.class) {
        return (T) (Byte) (isNotBlankData ? Byte.parseByte(rowData) : 0);
      } else if (type == Short.TYPE || type == Short.class) {
        return (T) (Short) (isNotBlankData ? Short.parseShort(rowData) : 0);
      } else if (type == BigInteger.class) {
        return (T) (isNotBlankData ? new BigInteger(rowData) : BigInteger.ZERO);
      } else if (type == Date.class) {
        return null;
        //return isNotBlankData ? dateFormatHolder.get().parseObject(rowData) : null;
      } else {
        throw new RuntimeException(
                String.format("Not support convert filed to value of type %s. only support boolean, char, byte, short, int, long, float, double, string, BigDecimal, BigInteger",
                        type.getName()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static void main(String[] args) {
    System.out.println(new Date() instanceof Date);
    System.out.println(new java.sql.Date(System.currentTimeMillis()) instanceof Date);
  }
}
