/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitorRule

/**
 * It is unnecessary to instantiate Double objects. Instead just use the double literal or the 'D' identifier to force the type, such as 123.45d or 0.42d.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryDoubleInstantiationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDoubleInstantiation'
    int priority = 2
    Class astVisitorClass = UnnecessaryDoubleInstantiationAstVisitor
}

class UnnecessaryDoubleInstantiationAstVisitor extends UnnecessaryInstantiationAstVisitor {

    UnnecessaryDoubleInstantiationAstVisitor() {
        super(Double, [String, Double], 'd')
    }
}
