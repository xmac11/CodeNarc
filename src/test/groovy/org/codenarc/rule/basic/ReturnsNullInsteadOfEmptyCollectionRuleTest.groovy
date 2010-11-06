/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ReturnsNullInsteadOfEmptyCollectionRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ReturnsNullInsteadOfEmptyCollectionRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ReturnsNullInsteadOfEmptyCollection"
    }

    void testNoViolation() {
        final SOURCE = '''
        	List myMethod() {

                def c = {
                    return null // ignore returns from nested closure
                }
                def o = new Object() {
                    def foo() {
                        return null // ignore returns from nested class
                    }
                }
                return []
            }

            def c =  {
                return []
            }
        '''
        assertNoViolations(SOURCE)
    }


    void testListMethod() {
        final SOURCE = '''
        	List myMethod() {
                if (x) return null
                return foo()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testCollectionMethod() {
        final SOURCE = '''
        	Collection myMethod() {
                if (x) return null
                return foo()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testStringListMethod() {
        final SOURCE = '''
        	List<String> myMethod() {
                if (x) return null
                return foo()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testMapMethod() {
        final SOURCE = '''
        	Map myMethod() {
                if (x) return null
                return foo()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testGenericMapMethod() {
        final SOURCE = '''
        	Map<String, String> myMethod() {
                if (x) return null
                return foo()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testStringListMethodInClass() {
        final SOURCE = '''
            class MyClass {
                List myMethod() {
                    if (x) return null
                    return []
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'null')
    }

    void testDefMethod() {
        final SOURCE = '''
        	def myMethod() {
                if (x) return null
                return []
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testDefMethodCtorCall() {
        final SOURCE = '''
        	def myMethod() {
                if (x) return null
                return new ArrayList();
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testDefMethodCtorCallNotCollection() {
        final SOURCE = '''
        	def myMethod() {
                if (x) return null
                return new java.awt.List();
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testDefMethodCastResult() {
        final SOURCE = '''
        	def myMethod() {
                if (x) return null
                return foo() as List
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testDefMethodInClass() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    if (x) return null
                    return []
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'null')
    }

    // todo: uncomment when groovy 1.7 is supported
//    void testStringListMethodInInnerClass() {
//        final SOURCE = '''
//            def o = new Object() {
//                List myMethod() {
//                    if (x) return null
//                    return []
//                }
//            }
//        '''
//        assertSingleViolation(SOURCE, 4, 'null')
//    }

    void testInClosure() {
        final SOURCE = '''
            def c = {
                if (x) return null      // bad
                return []
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    void testInClosureWithinAClosure() {
        final SOURCE = '''
            def a = {
                def b = {
                    if (x) return null      // bad
                    return []
                }
                def c = {
                    if (x) return null      // bad
                    return []
                }
                return []  // ok
            }
        '''
        assertTwoViolations(SOURCE, 4, 'null', 8, 'null')
    }

    // todo: uncomment when groovy 1.7 is supported
//    void testInAnonymousClassWithinAnonymousClass() {
//        final SOURCE = '''
//            def a = new Object() {
//                List m1() {
//                    def b = new Object() {
//                        List m1() {
//                            return null
//                        }
//                        List m2() {
//                            return null
//                        }
//                    }
//                    return []
//                }
//            }
//        '''
//        assertTwoViolations(SOURCE, 6, 'null', 9, 'null')
//    }

    // todo: uncomment when groovy 1.7 is supported
//    void testClosureInAnonymousClassWithinAnonymousClass() {
//        final SOURCE = '''
//            def a = new Object() {
//                String[] m1() {
//                    def b = new Object() {
//                        void m1() {
//                            def z = {
//                                if (q) {
//                                    return null
//                                } else {
//                                    return []
//                                }
//                            }
//                        }
//                    }
//                    return []
//                }
//            }
//        '''
//        assertSingleViolation(SOURCE, 8, 'null')
//    }

    protected Rule createRule() {
        return new ReturnsNullInsteadOfEmptyCollectionRule()
    }

}