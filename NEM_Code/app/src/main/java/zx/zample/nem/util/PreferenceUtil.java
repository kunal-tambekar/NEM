package zx.zample.nem.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kunal on 4/16/17.
 */

public class PreferenceUtil {


    private static SharedPreferences getSharedPreferences(Context context, String prefName) {
        return context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
    }

    private static  SharedPreferences.Editor getSharedPrefEditor(Context context, String prefName){
        return getSharedPreferences(context,prefName).edit();
    }

    public static void setString(Context context,String prefName, String key, String value) {
        getSharedPrefEditor(context,prefName).putString(key,value).commit();
    }

    public static String getString(Context context, String prefName,String key){
        return getSharedPreferences(context,prefName).getString(key,"");
    }

    public static void setArrayList(Context context,String prefName, String key, ArrayList<String> values) {
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < values.size(); i++) {
            set.add(values.get(i));
        }
        getSharedPrefEditor(context,prefName).putStringSet(key,set).commit();
    }

    public static ArrayList<String> getArrayList(Context context, String prefName,String key, String defaultValue){
        ArrayList<String> arrayList = new ArrayList<>();
        HashSet<String> hashSet = (HashSet<String>) getSharedPreferences(context,prefName).getStringSet(key,new HashSet<String>());
        for (String id : hashSet) {
            arrayList.add(id);
        }
        return arrayList;
    }

    public static void setBoolean(Context context,String prefName, String key, Boolean value) {
        getSharedPrefEditor(context,prefName).putBoolean(key,value).commit();
    }

    public static Boolean getBoolean(Context context, String prefName,String key, Boolean defaultValue){
        return getSharedPreferences(context,prefName).getBoolean(key,defaultValue);
    }

    public static void setInteger(Context context,String prefName, String key, Integer value) {
        getSharedPrefEditor(context,prefName).putInt(key,value).commit();
    }

    public static Integer getInteger(Context context, String prefName,String key, Integer defaultValue){
        return getSharedPreferences(context,prefName).getInt(key,defaultValue.intValue());
    }

}
