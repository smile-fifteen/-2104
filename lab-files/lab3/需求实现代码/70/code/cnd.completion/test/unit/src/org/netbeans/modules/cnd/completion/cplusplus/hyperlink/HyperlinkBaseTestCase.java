/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.completion.cplusplus.hyperlink;

import java.io.File;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.cnd.api.lexer.TokenItem;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.cnd.api.model.CsmOffsetable;
import org.netbeans.modules.cnd.api.model.CsmOffsetable.Position;
import org.netbeans.modules.cnd.modelimpl.test.ProjectBasedTestCase;
import org.netbeans.modules.cnd.test.CndCoreTestUtils;

/**
 * base class for hyperlink tests
 *
 * entry point:
 * - performTest (@see performTest)
 *
 * What should be configured:
 * - the dir with the same name as test class (harness init the project from the dir)
 * i.e for CsmHyperlinkProviderTestCase create
 * ${completion}/test/unit/data/org/netbeans/modules/cnd/completion/cplusplus/hyperlink/CsmHyperlinkProviderTestCase
 * and put there any C/C++ files
 *
 * @author Vladimir Voskresensky
 */
public abstract class HyperlinkBaseTestCase extends ProjectBasedTestCase {
        
    private CsmHyperlinkProvider declarationsProvider;
    private CsmIncludeHyperlinkProvider includeProvider;

    private static boolean GENERATE_GOLDEN_DATA = false;
    public HyperlinkBaseTestCase(String testName) {
        super(testName);
        //System.setProperty("cnd.repository.hardrefs", "true");
    }

    public HyperlinkBaseTestCase(String testName, boolean performInWorkDir) {
        super(testName, performInWorkDir);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();    
        log("CndHyperlinkBaseTestCase.setUp started.");
        
        declarationsProvider = new CsmHyperlinkProvider();
        includeProvider = new CsmIncludeHyperlinkProvider();

        log("CndHyperlinkBaseTestCase.setUp finished.");
        log("Test "+getName()+  " started");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * A test that expects hyperlink target to be null.
     * 
     * @param source
     * @param lineIndex
     * @param colIndex
     * @throws java.lang.Exception
     */
    protected void performNullTargetTest(String source, int lineIndex, int colIndex) throws Exception {
        File testSourceFile = getDataFile(source);
        BaseDocument doc = getBaseDocument(testSourceFile);
        int offset = CndCoreTestUtils.getDocumentOffset(doc, lineIndex, colIndex);
        TokenItem<TokenId> jumpToken = getJumpToken(doc, offset);
        assertNotNull("Hyperlink not found token in file " + testSourceFile + " on position (" + lineIndex + ", " + colIndex + ")", // NOI18N
                        jumpToken);
        CsmOffsetable targetObject = findTargetObject(doc, offset, jumpToken);
        assertNull("Hyperlink target is unexpectedly found for " + jumpToken.text().toString() + //NOI18N
                " in file " + testSourceFile + " on position (" + lineIndex + ", " + colIndex + ")", targetObject);//NOI18N
    }
    
    /**
     * @param source relative path of source file
     * @param lineIndex line where hyperlink is perfomed (1-based)
     * @param colIndex column where hyperlink is perfomed (1-based)
     * @param destGoldenFile relative path of expected destination file
     * @param destGoldenLine start line of expected destination object (1-based)
     * @param destGoldenColumn start column of expected destination object (1-based)
     */
    protected void performTest(String source, int lineIndex, int colIndex, String destGoldenFile, int destGoldenLine, int destGoldenColumn) throws Exception {
        String goldenFileAbsPath = getDataFile(destGoldenFile).getAbsolutePath();
        File testSourceFile = getDataFile(source);
        BaseDocument doc = getBaseDocument(testSourceFile);
        int offset = CndCoreTestUtils.getDocumentOffset(doc, lineIndex, colIndex);
        TokenItem<TokenId> jumpToken = getJumpToken(doc, offset);
        assertNotNull("Hyperlink not found token in file " + testSourceFile + " on position (" + lineIndex + ", " + colIndex + ")", // NOI18N
                        jumpToken);
        CsmOffsetable targetObject = findTargetObject(doc, offset, jumpToken);
        assertNotNull("Hyperlink target is not found for " + jumpToken.text().toString() + //NOI18N
                " in file " + testSourceFile + " on position (" + lineIndex + ", " + colIndex + ")", targetObject);//NOI18N
        String destResultFileAbsPath = targetObject.getContainingFile().getAbsolutePath().toString();
        Position resultPos = targetObject.getStartPosition();
        int resultOffset = resultPos.getOffset();
        int resultLine = resultPos.getLine();
        int resultColumn = resultPos.getColumn();
        if (GENERATE_GOLDEN_DATA) {
            System.err.println("result file " + goldenFileAbsPath);
            System.err.println("result position " + resultPos);
            System.err.println("result line " + resultLine);
            System.err.println("result column " + resultColumn);
        } else {
            String positions = toString(source, lineIndex, colIndex) + " -> " + toString(destGoldenFile, destGoldenLine, destGoldenColumn);
            assertEquals("Different target *FILE* " + positions, goldenFileAbsPath, destResultFileAbsPath);
            assertEquals("Different target *LINE* positions " + positions, destGoldenLine, resultLine);
            assertEquals("Different target *COLUMN* positions " + positions, destGoldenColumn, resultColumn);
        }
    }
    
    private String toString(String file, int line, int column) {
        return "[" + file + ":" + "Line-" + line +"; Col-" + column + "]";
    }
    
    private CsmOffsetable findTargetObject(BaseDocument doc, int offset, TokenItem<TokenId> jumpToken) {
        CsmOffsetable csmItem = null;
        // emulate hyperlinks order
        // first ask includes handler
        if (includeProvider.isValidToken(jumpToken, HyperlinkType.GO_TO_DECLARATION)) {
            csmItem = includeProvider.findTargetObject(doc, offset);
        }
        // if failed => ask declarations handler
        if (csmItem == null && declarationsProvider.isValidToken(jumpToken, HyperlinkType.GO_TO_DECLARATION)) {
            csmItem = (CsmOffsetable) declarationsProvider.findTargetObject(doc, jumpToken, offset, true);
        }
        return csmItem;
    }
    
    private TokenItem<TokenId> getJumpToken(BaseDocument doc, int offset) {
        return CsmAbstractHyperlinkProvider.getToken(doc, offset);
    }  
    
}
