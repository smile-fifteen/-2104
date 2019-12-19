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
package org.netbeans.modules.cnd.completion.csm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.cnd.completion.cplusplus.ext.CsmCompletionQuery;
import org.netbeans.modules.cnd.completion.cplusplus.ext.CsmResultItem;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmDeclaration;
import org.netbeans.modules.cnd.api.model.CsmField;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmOffsetableDeclaration;
import org.netbeans.modules.cnd.api.model.services.CsmIncludeResolver;
import org.netbeans.modules.cnd.completion.cplusplus.ext.CsmCompletionQuery.CsmCompletionResult;
import org.netbeans.modules.cnd.completion.impl.xref.FileReferencesContext;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.editor.NbEditorUtilities;

/**
 *
 * @author Vladimir Voskresensky
 */
public class CompletionUtilities {

    /**
     * Constructor is private to prevent instantiation.
     */
    private CompletionUtilities() {}

    public static List<CsmDeclaration> findFunctionLocalVariables(Document doc, int offset, FileReferencesContext fileReferncesContext) {
        CsmFile file = CsmUtilities.getCsmFile(doc, true, false);
        if (file == null || !file.isValid()) {
            return Collections.<CsmDeclaration>emptyList();
        }
        CsmContext context = CsmOffsetResolver.findContext(file, offset, fileReferncesContext);
        return CsmContextUtilities.findFunctionLocalVariables(context);
    }

    public static List<CsmDeclaration> findFileVariables(Document doc, int offset) {
        CsmFile file = CsmUtilities.getCsmFile(doc, true, false);
        if (file == null || !file.isValid()) {
            return Collections.<CsmDeclaration>emptyList();
        }
        CsmContext context = CsmOffsetResolver.findContext(file, offset, null);
        return CsmContextUtilities.findFileLocalVariables(context);
    }

    // TODO: think if we need it?
    public static CsmClass findClassOnPosition(CsmFile file, Document doc, int offset) {
        if (file == null) {
            file = CsmUtilities.getCsmFile(doc, true, false);
        }
        if (file == null || !file.isValid()) {
            return null;
        }
        CsmContext context = CsmOffsetResolver.findContext(file, offset, null);
        CsmClass clazz = CsmContextUtilities.getClass(context, true, false);
        return clazz;
    }
    
    public static CsmOffsetableDeclaration findFunDefinitionOrClassOnPosition(CsmFile file, Document doc, int offset, FileReferencesContext fileReferncesContext) {
        CsmOffsetableDeclaration out = null;
        if (file == null) {
            file = CsmUtilities.getCsmFile(doc, true, false);
        }
        if (file != null) {
            CsmContext context = CsmOffsetResolver.findContext(file, offset, fileReferncesContext);
            out = CsmContextUtilities.getFunctionDefinition(context);
            if (out == null || !CsmContextUtilities.isInFunctionBodyOrInitializerList(context, offset)) {
                out = CsmContextUtilities.getClass(context, false, false);
            }
        }
        return out;
    }

    public static Collection<CsmObject> findItemsReferencedAtCaretPos(JTextComponent target, Document doc, CsmCompletionQuery query, int dotPos) {
        Collection<CsmObject> out = new ArrayList<CsmObject>();
        try {
            BaseDocument baseDoc = null;
            if (doc instanceof BaseDocument) {
                baseDoc = (BaseDocument) doc;
            }
            baseDoc = baseDoc != null ? baseDoc : (BaseDocument) target.getDocument();

            boolean searchFunctionsOnly = false;
            boolean searchSpecializationsOnly = false;
            int[] idBlk = NbEditorUtilities.getIdentifierAndMethodBlock(baseDoc, dotPos);
            searchFunctionsOnly = (idBlk != null) ? (idBlk.length == 3) : false;
            if (idBlk == null || idBlk.length == 2) {
                idBlk = getIdentifierAndInstantiationBlock(baseDoc, dotPos);
                searchSpecializationsOnly = (idBlk != null) ? (idBlk.length == 3) : false;
            }            
            if (idBlk == null) {
                idBlk = new int[]{dotPos, dotPos};
            }
            CsmFile currentFile = query.getCsmFile();
            if (currentFile == null) {
                currentFile = CsmUtilities.getCsmFile(doc, false, false);
            }
            for (int ind = idBlk.length - 1; ind >= 1; ind--) {
                CsmCompletionResult result = query.query(target, baseDoc, idBlk[ind], true, false, false);
                if (result != null && !result.getItems().isEmpty()) {
                    List<CsmObject> filtered = getAssociatedObjects(result.getItems(), searchFunctionsOnly, currentFile);
                    out = !filtered.isEmpty() ? filtered : getAssociatedObjects(result.getItems(), false, currentFile);
                    if (filtered.size() > 1 && searchFunctionsOnly) {
                        // It is overloaded method, lets check for the right one
                        int endOfMethod = findEndOfMethod(baseDoc, idBlk[ind] - 1);
                        if (endOfMethod > -1) {
                            CsmCompletionResult resultx = query.query(target, baseDoc, endOfMethod, true, false, false);
                            if (resultx != null && !resultx.getItems().isEmpty()) {
                                out = getAssociatedObjects(resultx.getItems(), false, currentFile);
                            }
                        }
                    }                    
                    if (filtered.size() > 1 && searchSpecializationsOnly) {
                        int endOfMethod = findEndOfInstantiation(baseDoc, idBlk[ind] - 1);
                        if (endOfMethod > -1) {
                            CsmCompletionResult resultx = query.query(target, baseDoc, endOfMethod, true, false, false);
                            if (resultx != null && !resultx.getItems().isEmpty()) {
                                out = getAssociatedObjects(resultx.getItems(), false, currentFile);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (BadLocationException e) {
        }
        return out;
    }

    private static int[] getIdentifierAndInstantiationBlock(BaseDocument doc, int offset) throws BadLocationException {
        int[] idBlk = Utilities.getIdentifierBlock(doc, offset);
        if (idBlk != null) {
            int[] instBlk = getInstantiationBlock(doc, idBlk);
            if (instBlk != null) {
                return new int[] { idBlk[0], idBlk[1], instBlk[1] };
            }
        }
        return idBlk;
    }

    private static int[] getInstantiationBlock(BaseDocument doc, int[] identifierBlock) throws BadLocationException {
        if (identifierBlock != null) {
            int nwPos = Utilities.getFirstNonWhiteFwd(doc, identifierBlock[1]);
            if ((nwPos >= 0) && (doc.getChars(nwPos, 1)[0] == '<')) {
                return new int[] { identifierBlock[0], nwPos + 1 };
            }
        }
        return null;
    }


    private static List<CsmObject> getAssociatedObjects(List items, boolean wantFuncsOnly, CsmFile contextFile) {
        List<CsmObject> visible = new ArrayList<CsmObject>();
        List<CsmObject> all = new ArrayList<CsmObject>();
        List<CsmObject> funcs = new ArrayList<CsmObject>();
        List<CsmObject> visibleFuncs = new ArrayList<CsmObject>();

        for (Object item : items) {
            if (item instanceof CsmResultItem) {
                CsmObject ret = getAssociatedObject(item);
                boolean isVisible = contextFile == null ? false : CsmIncludeResolver.getDefault().isObjectVisible(contextFile, ret);
                boolean isFunc = CsmKindUtilities.isFunction(ret);
                if (isFunc) {
                    if (isVisible) {
                        visibleFuncs.add(ret);
                    } else {
                        funcs.add(ret);
                    }
                }
                if (isVisible) {
                    visible.add(ret);
                } else {
                    all.add(ret);
                }
            }
        }
        List<CsmObject> out;
        if (wantFuncsOnly) {
            out = !visibleFuncs.isEmpty() ? visibleFuncs : funcs;
        } else {
            out = !visible.isEmpty() ? visible : all;
        }
        return out;
    }

    private static CsmObject getAssociatedObject(Object item) {
        if (item instanceof CsmResultItem) {
            CsmObject ret = (CsmObject) ((CsmResultItem) item).getAssociatedObject();
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public static int findEndOfMethod(Document doc, int startPos) {
        int level = 0;
        CharSequence text = DocumentUtilities.getText(doc);
        for (int i = startPos; i < doc.getLength(); i++) {
            char ch = text.charAt(i);
            if (ch == ';') {
                return -1;
            }
            if (ch == '(') {
                level++;
            }
            if (ch == ')') {
                level--;
                if (level == 0) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    public static int findEndOfInstantiation(Document doc, int startPos) {
        int level = 0;
        CharSequence text = DocumentUtilities.getText(doc);
        for (int i = startPos; i < doc.getLength(); i++) {
            char ch = text.charAt(i);
            if (ch == ';') {
                return -1;
            }
            if (ch == '<') {
                level++;
            }
            if (ch == '>') {
                level--;
                if (level == 0) {
                    return i + 1;
                }
            }
        }
        return -1;
    }
}
