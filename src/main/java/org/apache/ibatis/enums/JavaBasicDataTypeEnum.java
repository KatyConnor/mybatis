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
package org.apache.ibatis.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Author mingliang
 * @Date 2018-06-11 17:47
 */
public enum JavaBasicDataTypeEnum {
    BYTE(byte.class),
    SHORT(short.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class),
    CHAR(char.class),
    BOOLEAN(boolean.class),
    Byte(java.lang.Byte.class),
    Short(java.lang.Short.class),
    Integer(java.lang.Integer.class),
    Long(java.lang.Long.class),
    Float(java.lang.Float.class),
    Double(java.lang.Double.class),
    Char(Character.class),
    Boolean(java.lang.Boolean.class);

    private Class<?> classzz;

    JavaBasicDataTypeEnum(Class<?> classzz) {
        this.classzz = classzz;
    }

    public boolean contains(Class<?> type){
        List<JavaBasicDataTypeEnum> enumList = getAll();
        return enumList.contains(type);
    }

    public List<JavaBasicDataTypeEnum> getAll(){
        return Arrays.asList(JavaBasicDataTypeEnum.values());
    }
}
