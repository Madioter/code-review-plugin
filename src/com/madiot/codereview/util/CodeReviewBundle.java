package com.madiot.codereview.util;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class CodeReviewBundle {
    private static Reference<ResourceBundle> ourBundle;
    @NonNls
    private static final String BUNDLE = "com.madiot.codereview.util.CodeReviewBundle";

    private CodeReviewBundle() {
    }

    public static String message(@PropertyKey(
            resourceBundle = "com.madiot.codereview.util.CodeReviewBundle"
    ) String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if(ourBundle != null) {
            bundle = (ResourceBundle)ourBundle.get();
        }

        if(bundle == null) {
            bundle = ResourceBundle.getBundle("com.madiot.codereview.util.CodeReviewBundle");
            ourBundle = new SoftReference(bundle);
        }

        return bundle;
    }
}

