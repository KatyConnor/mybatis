/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.velocity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义模板函数
 * @Author mingliang
 * @Date 2018-01-31 11:09
 */
public class VelocityUtils {

    /**
     * Date 类型时间格式化成字符串
     * @param date  时间
     * @param formatStr 时间格式
     * @return 返回格式化后的 date
     */
    public static String formatDate(Date date,String formatStr){
        SimpleDateFormat sf = new SimpleDateFormat(formatStr);
        return sf.format(date);
    }
}
