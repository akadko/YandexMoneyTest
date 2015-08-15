package com.akadko.yandexmoneytest;

import android.content.Context;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by User on 13.08.2015.
 */
public class Utils {

    public static final String SETTINGS = "settings";
    public static final String IS_VISITED = "isVisited";
    public static final String DB_VERSION = "db_version";
    public static final String FORCE_LOAD = "1";
    public static final String FIRST_LOAD = "0";

    public static final String BASE_TABLE_NAME = "shopItems";

    public static String translitRustoEng(String str) {
        SortedMap map = translitGetMap();
        String[] rus = str.split("");
        String key = "";
        String result = "";
        for(int i = 0; i<rus.length; i++) {
            key = rus[i];
            if(map.containsKey(key)) {
                result += map.get(key);
            } else {
                result += key;
            }
        }
        return result;
    }

    public static SortedMap translitGetMap() {
        SortedMap<String, String> map = new TreeMap<String,String>();
        String[] rus = alphabet_rus;
        String[] eng = alphabet_eng;
        for(int i = 0; i<rus.length; i++) {
            map.put(rus[i], eng[i]);
        }
        return map;
    }

    public static final String[] alphabet_rus = new String[]{
            "а","б","в","г","д","е","ё","ж","з","и","й","к","л","м","н","о","п",
            "р","с","т","у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я",
            "А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П",
            "Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я", " ",":","-"
    };

    public static final String[] alphabet_eng = new String[]{
            "a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p",
            "r","s","t","u","f","h","c","ch","sh","sch","","y","","e","yu","ya",
            "A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P",
            "R","S","T","U","F","H","C","Ch","Sh","Sch","","Y","","E","Yu","Ya", "_", "",""
    };

    public static int getPaddingPixels(Context context, int dpValue) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dpValue * scale + 0.5f);
    }
}

