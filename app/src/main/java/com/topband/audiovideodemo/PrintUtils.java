package com.topband.audiovideodemo;

import java.util.Iterator;
import java.util.List;

public class PrintUtils {
    public PrintUtils() {
    }

    public static String byteArray2String(byte[] bytes) {
        if (bytes == null) {
            return "bytes is null";
        } else if (bytes.length <= 0) {
            return "bytes is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            byte[] var2 = bytes;
            int var3 = bytes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte b = var2[var4];
                sb.append("0x");
                String s = Integer.toHexString(b & 255).toUpperCase();
                if (s.length() == 1) {
                    sb.append("0");
                }

                sb.append(s);
                sb.append(" ");
            }

            return sb.toString();
        }
    }

    public static String byteList2String(List<Byte> bytesList) {
        if (bytesList == null) {
            return "bytesList is null";
        } else if (bytesList.size() <= 0) {
            return "bytesList is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator var2 = bytesList.iterator();

            while(var2.hasNext()) {
                byte b = (Byte)var2.next();
                sb.append("0x");
                String s = Integer.toHexString(b & 255).toUpperCase();
                if (s.length() == 1) {
                    sb.append("0");
                }

                sb.append(s);
                sb.append("\t");
            }

            return sb.toString();
        }
    }

    public static String list2String(List<?> list) {
        if (list == null) {
            return "bytesList is null";
        } else if (list.size() <= 0) {
            return "bytesList is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator var2 = list.iterator();

            while(var2.hasNext()) {
                Object o = var2.next();
                sb.append(o.toString());
                sb.append("\n");
            }

            return sb.toString();
        }
    }
}
