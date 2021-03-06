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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.completion.cplusplus;

import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.cnd.api.lexer.CndLexerUtilities;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateFilter;

/**
 *
 * @author Vladimir Voskresensky
 */
public class CsmCodeTemplateFilter implements CodeTemplateFilter {
    private static boolean enabled = true;
    
    static void enableAbbreviations(boolean enabled) {
        CsmCodeTemplateFilter.enabled = enabled;
    }
    
    private int startOffset;
    private int endOffset;
    private TokenId id;
    
    private CsmCodeTemplateFilter(JTextComponent component, int offset) {
        this.startOffset = offset;
        this.endOffset = component.getSelectionStart() == offset ? component.getSelectionEnd() : startOffset;  
        this.id = getID(component, offset);
    }

    @Override
    public synchronized boolean accept(CodeTemplate template) {
        return enabled && (startOffset == endOffset) && isTemplateContext(template);
    }

    private TokenId getID(JTextComponent component, int offset) {
        TokenSequence<TokenId> ts = CndLexerUtilities.getCppTokenSequence(component, offset, true, false);
        if (ts != null) {
            if (ts.offset() <= offset) {
                if (!ts.movePrevious()) {
                    return CppTokenId.ERROR;
                }
            }
            return ts.token().id();
        } else {
            return CppTokenId.ERROR;
        }
    }

    private boolean isTemplateContext(CodeTemplate template) {
        boolean res = true;
        if(this.id instanceof CppTokenId) {
            switch ((CppTokenId)this.id) {
                case DOT:
                case DOTMBR:
                case SCOPE:
                case ARROW:
                case ARROWMBR:
                    res = false;
            }
        }
        return res;
    }

    public static final class Factory implements CodeTemplateFilter.Factory {
        
        @Override
        public CodeTemplateFilter createFilter(JTextComponent component, int offset) {
            return new CsmCodeTemplateFilter(component, offset);
        }
    }
}
