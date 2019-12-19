/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.completion.cplusplus.hyperlink;

/**
 *
 * @author Petr Kudryavtsev <petrk@netbeans.org>
 */
public class Cpp11TooltipsTestCase extends TooltipsBaseTestCase {

    public Cpp11TooltipsTestCase(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        System.setProperty("cnd.modelimpl.tracemodel.project.name", "DummyProject"); // NOI18N
        System.setProperty("parser.report.errors", "true");
        System.setProperty("antlr.exceptions.hideExpectedTokens", "true");
        System.setProperty("cnd.language.flavor.cpp11", "true");         
        super.setUp();
    }

    public void testBug247751() throws Exception {
        // Bug #247751 - Provide tooltips and navigation for auto variables
        performPlainTooltipTest("bug247751.cpp", 75, 15,
            "Variable var\n" + 
            "bug247751::std247751::tuple247751<bug247751::std247751::make_tuple247751::Elements &>"
        );        
        performPlainTooltipTest("bug247751.cpp", 76, 15,
            "Variable elem0\n" + 
            "bug247751::AAA247751 &"
        );  
        performPlainTooltipTest("bug247751.cpp", 77, 15,
            "Variable elem1\n" + 
            "bug247751::BBB247751 &"
        );
        performPlainTooltipTest("bug247751.cpp", 78, 15,
            "Variable elem2\n" + 
            "bug247751::CCC247751 &"
        );
        performPlainTooltipTest("bug247751.cpp", 80, 15,
            "Variable mapElem\n" + 
            "int *&"
        );
        performPlainTooltipTest("bug247751.cpp", 82, 15,
            "Variable stringVar\n" + 
            "bug247751::std247751::string247751"
        );        
    }    
}
