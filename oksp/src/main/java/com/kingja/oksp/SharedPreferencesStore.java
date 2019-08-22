package com.kingja.oksp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description:TODO
 * Create Time:2019/8/22 0022 下午 4:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SharedPreferencesStore implements SharedPreferences {
    SharedPreferences mPrefs;
    private IEncryption mEncryptionManager;

    public SharedPreferencesStore(Context context, String name) {
        this(context, name, Context.MODE_PRIVATE);
    }

    public SharedPreferencesStore(Context context, String name, int mode) {
        mPrefs = context.getSharedPreferences(name, mode);
    }


    @Override
    public Map<String, Object> getAll() {
        Map<String, ?> all = mPrefs.getAll();
        Map<String, Object> dAll = new HashMap<>(all.size());
        if (all.size() > 0) {
            for (String key : all.keySet()) {
                try {
                    Object value = all.get(key);
                    dAll.put(key, mEncryptionManager.decrypt((String) value));
                } catch (Exception e) {
//                    Logger.e(e);
                }
            }
        }
        return dAll;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        try {
            String hashedKey = mEncryptionManager.encrypt(key);
            String value = mPrefs.getString(hashedKey, null);
            if (value != null) return mEncryptionManager.decrypt(value);
        } catch (Exception e) {
//            Logger.e(e);
        }

        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            String hashedKey = mEncryptionManager.encrypt(key);
            Set<String> eSet = mPrefs.getStringSet(hashedKey, null);
            if (eSet != null) {
                Set<String> dSet = new HashSet<>(eSet.size());

                for (String val : eSet) {
                    dSet.add(mEncryptionManager.decrypt(val));
                }

                return dSet;
            }
        } catch (Exception e) {
//                Logger.e(e);
        }

        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Float.parseFloat(value);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return new Editor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }


    public class Editor implements SharedPreferences.Editor {
        SharedPreferences.Editor mEditor;

        public Editor() {
            mEditor = mPrefs.edit();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            try {
                String hashedKey = mEncryptionManager.encrypt(key);
                String evalue = mEncryptionManager.encrypt(value);
                mEditor.putString(hashedKey, evalue);
            } catch (Exception e) {
//                Logger.e(e);
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            try {
                String hashedKey = mEncryptionManager.encrypt(key);
                Set<String> eSet = new HashSet<String>(values.size());
                for (String val : values) {
                    eSet.add(mEncryptionManager.encrypt(val));
                }

                mEditor.putStringSet(hashedKey, eSet);
            } catch (Exception e) {
//                Logger.e(e);
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            String val = Integer.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            String val = Long.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            String val = Float.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            String val = Boolean.toString(value);
            return putString(key, val);
        }


        @Override
        public SharedPreferences.Editor remove(String key) {
            try {
                String hashedKey = mEncryptionManager.encrypt(key);
                mEditor.remove(hashedKey);
            } catch (Exception e) {
//                Logger.e(e);
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            for (String key : mPrefs.getAll().keySet()) {
                mEditor.remove(key);
            }
            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        public void apply() {
            mEditor.apply();
        }
    }
}
