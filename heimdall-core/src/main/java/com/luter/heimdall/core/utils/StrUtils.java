/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Str utils.
 *
 * @author Luter
 */
public final class StrUtils {

    /**
     * The constant COLON.
     */
    public static final String COLON = ":";

    /**
     * Is empty boolean.
     *
     * @param charSequence the char sequence
     * @return the boolean
     */
    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.toString().isEmpty();
    }

    /**
     * Is not empty boolean.
     *
     * @param charSequence the char sequence
     * @return the boolean
     */
    public static boolean isNotEmpty(CharSequence charSequence) {
        return !StrUtils.isEmpty(charSequence);
    }

    /**
     * 字符串是否为空，为空的标准是 str==null或 str.length()==0
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Is not empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNotEmpty(String str) {
        return !StrUtils.isEmpty(str);
    }

    /**
     * 字符串是否为空或长度为0或由空白符(whitespace) 构成
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is not blank boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNotBlank(String str) {
        return !StrUtils.isBlank(str);
    }

    /**
     * Cast list list.
     *
     * @param <T>   the type parameter
     * @param obj   the obj
     * @param clazz the clazz
     * @return the list
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
