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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.cnd.api.lexer.TokenItem;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmClassifier;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.CsmNamespace;
import org.netbeans.modules.cnd.api.model.CsmNamespaceDefinition;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmOffsetable;
import org.netbeans.modules.cnd.api.model.CsmOffsetableDeclaration;
import org.netbeans.modules.cnd.api.model.CsmVariable;
import org.netbeans.modules.cnd.api.model.CsmVariableDefinition;
import org.netbeans.modules.cnd.api.model.services.CsmClassifierResolver;
import org.netbeans.modules.cnd.api.model.services.CsmFunctionDefinitionResolver;
import org.netbeans.modules.cnd.api.model.services.CsmInstantiationProvider;
import org.netbeans.modules.cnd.api.model.services.CsmVirtualInfoQuery;
import org.netbeans.modules.cnd.api.model.util.CsmBaseUtilities;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmTypeHierarchyResolver;
import org.netbeans.modules.cnd.completion.impl.xref.ReferencesSupport;
import org.netbeans.modules.cnd.modelutil.CsmDisplayUtilities;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.modelutil.OverridesPopup;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.cnd.utils.ui.PopupUtil;
import org.netbeans.modules.cnd.utils.ui.UIGesturesSupport;
import org.openide.util.Exceptions;

/**
 * Implementation of the hyperlink provider for C/C++ language.
 * <br>
 * The hyperlinks are constructed for identifiers.
 * <br>
 * The click action corresponds to performing the goto-declaration action.
 *
 * @author Jan Lahoda, Vladimir Voskresensky
 */
public final class CsmHyperlinkProvider extends CsmAbstractHyperlinkProvider {

    public CsmHyperlinkProvider() {
    }

    @Override
    protected void performAction(final Document doc, final JTextComponent target, final int offset, final HyperlinkType type) {
        goToDeclaration(doc, target, offset, type);
    }

    @Override
    protected boolean isValidToken(TokenItem<TokenId> token, HyperlinkType type) {
        return isSupportedToken(token, type);
    }

    public static boolean isSupportedToken(TokenItem<TokenId> token, HyperlinkType type) {
        if (token != null) {
            if (type == HyperlinkType.ALT_HYPERLINK) {
                if (CppTokenId.WHITESPACE_CATEGORY.equals(token.id().primaryCategory()) ||
                        CppTokenId.COMMENT_CATEGORY.equals(token.id().primaryCategory())) {
                    return false;
                }
            }
            if(token.id() instanceof CppTokenId) {
                switch ((CppTokenId)token.id()) {
                    case IDENTIFIER:
                    case PREPROCESSOR_IDENTIFIER:
                    case OPERATOR:
                    case PROC_DIRECTIVE:
                        return true;
                    case PREPROCESSOR_INCLUDE:
                    case PREPROCESSOR_INCLUDE_NEXT:
                    case PREPROCESSOR_SYS_INCLUDE:
                    case PREPROCESSOR_USER_INCLUDE:
                        return false;
                }
            }
        }
        return false;
    }

    public boolean goToDeclaration(Document doc, JTextComponent target, int offset, HyperlinkType type) {
        if (!preJump(doc, target, offset, "opening-csm-element", type)) { //NOI18N
            return false;
        }
        TokenItem<TokenId> jumpToken = getJumpToken();
        CsmObject primary = findTargetObject(doc, jumpToken, offset, false);
        CsmFile csmFile = CsmUtilities.getCsmFile(doc, true, false);
        CsmOffsetable item = toJumpObject(primary, csmFile, offset);
        if (type == HyperlinkType.ALT_HYPERLINK) {
            if (CsmKindUtilities.isFunction(item)) {
                CsmFunction decl = CsmBaseUtilities.getFunctionDeclaration((CsmFunction) item);
                Collection<CsmOffsetableDeclaration> baseTemplates = CsmInstantiationProvider.getDefault().getBaseTemplate(decl);
                Collection<CsmOffsetableDeclaration> templateSpecializations = CsmInstantiationProvider.getDefault().getSpecializations(decl);
                boolean inDeclaration = isInDeclaration(decl, csmFile, offset);
                Collection<? extends CsmMethod> baseMethods = new ArrayList<CsmMethod>(0);
                Collection<? extends CsmMethod> overriddenMethods = new ArrayList<CsmMethod>(0);
                if (CsmKindUtilities.isMethod(decl)) {
                    CsmMethod meth = (CsmMethod) CsmBaseUtilities.getFunctionDeclaration(decl);
                    if (inDeclaration) {
                        baseMethods = CsmVirtualInfoQuery.getDefault().getFirstBaseDeclarations(meth);
                    }
                    if (!baseMethods.isEmpty() || CsmVirtualInfoQuery.getDefault().isVirtual(meth)) {
                        overriddenMethods = CsmVirtualInfoQuery.getDefault().getOverriddenMethods(meth, false);
                    }
                    baseMethods.remove(meth); // in the case CsmVirtualInfoQuery added function itself (which was previously the case)
                }
                if (showOverridesPopup(inDeclaration ? null : decl, baseMethods, overriddenMethods, baseTemplates, templateSpecializations, inDeclaration ? CsmKindUtilities.isFunctionDefinition(item) : true, target, offset)) {
                    UIGesturesSupport.submit("USG_CND_HYPERLINK_METHOD", type); //NOI18N
                    return true;
                }
            } else if (CsmKindUtilities.isClass(item)) {
                CsmClass cls = (CsmClass) item;
                Collection<CsmOffsetableDeclaration> baseTemplates = CsmInstantiationProvider.getDefault().getBaseTemplate(cls);
                Collection<CsmOffsetableDeclaration> templateSpecializations = CsmInstantiationProvider.getDefault().getSpecializations(cls);
                Collection<CsmClass> subClasses = new ArrayList<CsmClass>(0);
             
                Collection<CsmReference> subRefs = CsmTypeHierarchyResolver.getDefault().getSubTypes(cls, false);
                if (!subRefs.isEmpty()) {
                    for (CsmReference ref : subRefs) {
                        CsmObject obj = ref.getReferencedObject();
                        CndUtils.assertTrue(obj == null || (obj instanceof CsmClass), "getClassifier() should return either null or CsmClass"); //NOI18N
                        if (CsmKindUtilities.isClass(obj)) {
                            subClasses.add((CsmClass) obj);
                        }
                    }
                }
                if (showOverridesPopup(null, Collections.<CsmClass>emptyList(), subClasses, baseTemplates, templateSpecializations, false, target, offset)) {
                    UIGesturesSupport.submit("USG_CND_HYPERLINK_CLASS", type); //NOI18N
                    return true;
                }
            }
        }
        UIGesturesSupport.submit("USG_CND_HYPERLINK", type); //NOI18N
        return postJump(item, "goto_source_source_not_found", "cannot-open-csm-element"); //NOI18N
    }

    private boolean showOverridesPopup(CsmOffsetableDeclaration mainDeclaration,
            Collection<? extends CsmOffsetableDeclaration> baseDeclarations,
            Collection<? extends CsmOffsetableDeclaration> descendantDeclarations,
            Collection<? extends CsmOffsetableDeclaration> baseTemplates,
            Collection<? extends CsmOffsetableDeclaration> templateSpecializations,
            boolean gotoDefinitions,
            JTextComponent target, int offset) {
        if (!baseDeclarations.isEmpty() || !descendantDeclarations.isEmpty() || !baseDeclarations.isEmpty() || !templateSpecializations.isEmpty()) {
            try {
                final OverridesPopup popup = new OverridesPopup(null, mainDeclaration, baseDeclarations, descendantDeclarations, baseTemplates, templateSpecializations, gotoDefinitions);
                Rectangle rect = target.modelToView(offset);
                final Point point = new Point((int) rect.getX(), (int)(rect.getY() + rect.getHeight()));
                SwingUtilities.convertPointToScreen(point, target);
                Runnable runner = new Runnable() {
                    @Override
                    public void run() {
                        PopupUtil.showPopup(popup, null, point.x, point.y, true, 0);
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runner.run();
                } else {
                    SwingUtilities.invokeLater(runner);
                }
                return true;
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }

    /*package*/ CsmObject findTargetObject(final Document doc, final TokenItem<TokenId> jumpToken, final int offset, boolean toOffsetable) {
        CsmObject item = null;
        assert jumpToken != null;
        CsmFile file = CsmUtilities.getCsmFile(doc, true, false);
        CsmObject csmObject = file == null ? null : ReferencesSupport.findDeclaration(file, doc, jumpToken, offset);
        if (csmObject != null) {
            // convert to jump object
            item = toOffsetable ? toJumpObject(csmObject, file, offset) : csmObject;
        }
        return item;
    }

    private boolean isInDeclaration(CsmFunction func, CsmFile csmFile, int offset) {
        CsmFunctionDefinition def;
        CsmFunction decl;
        if (CsmKindUtilities.isFunctionDefinition(func)) {
            def = (CsmFunctionDefinition) func;
            decl = def.getDeclaration();
        } else {
            decl = func;
            def = func.getDefinition();
        }
        if (def != null) {
            if (csmFile.equals(def.getContainingFile()) &&
                    (def.getStartOffset() <= offset &&
                    offset <= def.getBody().getStartOffset())) {
                return true;
            }
        }
        if (decl != null) {
            // just declaration
            if (csmFile.equals(decl.getContainingFile()) &&
                    (decl.getStartOffset() <= offset &&
                    offset <= decl.getEndOffset())) {
                return true;
            }
        }
        return false;
    }

    private CsmOffsetable toJumpObject(CsmObject csmObject, CsmFile csmFile, int offset) {
        CsmOffsetable item = null;
        if (CsmKindUtilities.isOffsetable(csmObject)) {
            item = (CsmOffsetable) csmObject;
            if (CsmKindUtilities.isFunctionDeclaration(csmObject)) {
                // check if we are in function definition name => go to declaration
                // else it is more useful to jump to definition of function
                CsmFunctionDefinition definition = ((CsmFunction) csmObject).getDefinition();
                if (definition != null) {
                    if (csmFile.equals(definition.getContainingFile()) &&
                            (definition.getStartOffset() <= offset &&
                            offset <= definition.getBody().getStartOffset())) {
                        // it is ok to jump to declaration
                        if (definition.getDeclaration() != null) {
                            item = definition.getDeclaration();
                        } else if (csmObject.equals(definition)) {
                            item = (CsmOffsetable) csmObject;
                        }
                    } else {
                        // it's better to jump to definition
                        item = definition;
                    }
                } else {
                    CsmReference ref = CsmFunctionDefinitionResolver.getDefault().getFunctionDefinition((CsmFunction) csmObject);
                    if (ref != null) {
                        item = ref;
                    }
                }
            } else if (CsmKindUtilities.isFunctionDefinition(csmObject)) {
                CsmFunctionDefinition definition = (CsmFunctionDefinition) csmObject;
                if (csmFile.equals(definition.getContainingFile()) &&
                        (definition.getStartOffset() <= offset &&
                        offset <= definition.getBody().getStartOffset())) {
                    // it is ok to jump to declaration
                    if (definition.getDeclaration() != null) {
                        item = definition.getDeclaration();
                    } else {
                        item = definition;
                    }
                }
            } else if (CsmKindUtilities.isVariableDeclaration(csmObject)) {
                // check if we are in variable definition name => go to declaration
                CsmVariableDefinition definition = ((CsmVariable) csmObject).getDefinition();
                if (definition != null) {
                    item = definition;
                    if (csmFile.equals(definition.getContainingFile()) &&
                            (definition.getStartOffset() <= offset &&
                            offset <= definition.getEndOffset())) {
                        item = (CsmVariable) csmObject;
                    }
                }
            } else if (CsmClassifierResolver.getDefault().isForwardClassifier(csmObject)) {
                CsmClassifier cls = CsmClassifierResolver.getDefault().getOriginalClassifier((CsmClassifier)csmObject, csmFile);
                if (CsmKindUtilities.isOffsetable(cls)) {
                    item = (CsmOffsetable) cls;
                }
            }
        } else if (CsmKindUtilities.isNamespace(csmObject)) {
            // get all definitions of namespace, but prefer the definition in this file
            CsmNamespace nmsp = (CsmNamespace) csmObject;
            Collection<CsmNamespaceDefinition> defs = nmsp.getDefinitions();
            CsmNamespaceDefinition bestDef = null;
            for (CsmNamespaceDefinition def : defs) {
                if (bestDef == null) {
                    // first time initialization
                    bestDef = def;
                }
                CsmFile container = def.getContainingFile();
                if (csmFile.equals(container)) {
                    // this is the best choice
                    bestDef = def;
                    break;
                }
            }
            item = bestDef;
        }
        return item;
    }

    @Override
    protected String getTooltipText(Document doc, TokenItem<TokenId> token, int offset, HyperlinkType type) {
        CsmObject item = findTargetObject(doc, token, offset, false);
        CharSequence msg = item == null ? null : CsmDisplayUtilities.getTooltipText(item);
        if (msg != null) {
            if (CsmKindUtilities.isMacro(item)) {
                msg = getAlternativeHyperlinkTip(doc, "AltMacroHyperlinkHint", msg); // NOI18N
            } else if (CsmKindUtilities.isMethod(item)) {
                msg = getAlternativeHyperlinkTip(doc, "AltMethodHyperlinkHint", msg); // NOI18N
            } else if (CsmKindUtilities.isClass(item)) {
                msg = getAlternativeHyperlinkTip(doc, "AltClassHyperlinkHint", msg); // NOI18N
            }
        }
        return msg == null ? null : msg.toString();
    }
}
