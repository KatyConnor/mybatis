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
package org.apache.ibatis.asm;

import com.sun.scenario.effect.impl.prism.PrImage;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author mingliang
 * @Date 2018-06-11 17:26
 */
public class ArgNameMethodVisitor extends MethodVisitor {
    /** 变量名 */
    private List<String> variables;
    private List<String> argumentNames;
    private int argLen; //变量个数

    public ArgNameMethodVisitor(int api,List<String> argumentNames,int argLen) {
        super(api);
        this.argumentNames=argumentNames;
        this.argLen=argLen;
    }

    /**
     * asm遍历局部变量
     * @param name
     * @param desc
     * @param signature
     * @param start
     * @param end
     * @param index
     */
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        //如果是this变量，则掠过
        if("this".equals(name)) {
            return;
        }

        System.out.println("name = "+name);

        if(argLen-- > 0) {
            System.out.println("name = "+name);
            argumentNames.add(name);
        }
    }

    @Override
    public void visitParameter(String name, int access) {
//        super.visitParameter(name, access);
        System.out.println("paramName = "+name);
    }
}
