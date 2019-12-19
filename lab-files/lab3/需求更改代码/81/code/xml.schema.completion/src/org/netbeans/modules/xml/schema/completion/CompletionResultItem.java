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
package org.netbeans.modules.xml.schema.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.*;
import javax.swing.Icon;
import javax.xml.XMLConstants;
import org.netbeans.editor.BaseDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.modules.xml.axi.AXIComponent;
import org.netbeans.modules.xml.schema.completion.spi.CompletionContext;
import org.netbeans.modules.xml.schema.completion.util.CompletionContextImpl;
import org.netbeans.modules.xml.schema.completion.util.CompletionUtil;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.swing.plaf.LFCustoms;

/**
 *
 * @author Samaresh (Samaresh.Panda@Sun.Com)
 */
public abstract class CompletionResultItem implements CompletionItem {
    private static final Logger _logger = Logger.getLogger(CompletionResultItem.class.getName());

    private static final Color COLOR = LFCustoms.shiftColor(new Color(64, 64, 255));
    
    public static final String
        ICON_ELEMENT    = "element.png",     //NOI18N
        ICON_ATTRIBUTE  = "attribute.png",   //NOI18N
        ICON_VALUE      = "value.png",       //NOI18N
        ICON_LOCATION   = "/org/netbeans/modules/xml/schema/completion/resources/"; //NOI18N

    protected boolean shift = false;
    protected String typedChars;
    protected String itemText;
    protected javax.swing.Icon icon;
    protected CompletionPaintComponent component;
    protected AXIComponent axiComponent;
    protected int extraPaintGap = CompletionPaintComponent.DEFAULT_ICON_WIDTH;
    protected TokenSequence tokenSequence;

    private CompletionContextImpl context;

    /**
     * Creates a new instance of CompletionUtil
     */
    public CompletionResultItem(AXIComponent component, CompletionContext context) {
        this(component, context, null);
    }

    public CompletionResultItem(AXIComponent component, CompletionContext context,
        TokenSequence tokenSequence) {
        this.context = (CompletionContextImpl) context;
        this.axiComponent = component;
        setTokenSequence(tokenSequence);
        if (context != null) {
            this.typedChars = context.getTypedChars();
        }
    }

    Icon getIcon(){
        return icon;
    }

    public AXIComponent getAXIComponent() {
        return axiComponent;
    }

    /**
     * The completion item's name.
     */
    public String getItemText() {
        return itemText;
    }

    /**
     * The text user sees in the CC list. Normally some additional info
     * such as cardinality etc. are added to the item's name.
     * 
     */
    public abstract String getDisplayText();

    /**
     * Replacement text is the one that gets inserted into the document when
     * user selects this item from the CC list.
     */
    public abstract String getReplacementText();

    /**
     * Returns the relative caret position.
     * The caller must call this w.r.t. the offset
     * e.g. component.setCaretPosition(offset + getCaretPosition())
     */
    public abstract int getCaretPosition();

    @Override
    public String toString() {
        return getItemText();
    }

    Color getPaintColor() { 
        return LFCustoms.shiftColor(COLOR);
    }

    public int getExtraPaintGap() {
        return extraPaintGap;
    }

    public void setExtraPaintGap(int extraPaintGap) {
        this.extraPaintGap = extraPaintGap;
    }

    public TokenSequence getTokenSequence() {
        return tokenSequence;
    }

    public void setTokenSequence(TokenSequence tokenSequence) {
        this.tokenSequence = tokenSequence;
    }
    
    protected int removeTextLength(JTextComponent component, int offset, int removeLength) {
        if (removeLength <= 0) {
            return 0;
        }
        TokenSequence s = createTokenSequence(component);
        s.move(offset);
        s.moveNext();
        if (s.token().id() == XMLTokenId.TAG || s.token().id() == XMLTokenId.TEXT) {
            // replace entire tag, minus starting >
            if (s.token().text().toString().startsWith(CompletionUtil.TAG_FIRST_CHAR)) {
                return s.token().length() - (offset - s.offset());
            }
        }
        return removeLength;
    }

    /**
     * Actually replaces a piece of document by passes text.
     * @param component a document source
     * @param text a string to be inserted
     * @param offset the target offset
     * @param len a length that should be removed before inserting text
     */
    protected void replaceText(final JTextComponent component, final String text,
        final int offset, final int len) {
        final BaseDocument doc = (BaseDocument) component.getDocument();
        doc.runAtomic(new Runnable() {
            @Override
            public void run() {
                try {
                    int caretPos = component.getCaretPosition();
                    if ((context != null) && (context.canReplace(text))) {
                        int l2 = removeTextLength(component, offset, len);
                        String insertingText = getInsertingText(component, text, l2);
                        if (l2 > 0) doc.remove(offset, l2);
                        doc.insertString(offset, insertingText, null);
                        // fix for issue #186007
                        caretPos = component.getCaretPosition(); // get the caret position
                    } else {
                        caretPos = offset + getCaretPosition(); // change the caret position
                    }
                    int docLength = doc.getLength();
                    if (docLength == 0) {
                        caretPos = 0;
                    } else if (caretPos > doc.getLength()) {
                        caretPos = doc.getLength();
                    }
                    component.setCaretPosition(caretPos);
                    
                    String prefix = CompletionUtil.getPrefixFromTag(text);
                    if (prefix == null) {
                        return;
                    }
                    //insert namespace declaration for the new prefix
                    if ((context != null) && (! context.isSpecialCompletion()) &&
                        (! context.isPrefixBeingUsed(prefix))) {
                        String tns = context.getTargetNamespaceByPrefix(prefix);
                        
                        // CC has made a suggestion, so materialize it:
                        if (tns == null) {
                            tns = context.getSuggestedNamespace().get(prefix);
                        }
                        
                        if (tns != null) {
                            doc.insertString(CompletionUtil.getNamespaceInsertionOffset(doc), " " +
                                    XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix + "=\"" +
                                    tns + "\"", null);
                        }
                    }
                } catch (Exception e) {
                    _logger.log(Level.SEVERE,
                        e.getMessage() == null ? e.getClass().getName() : e.getMessage(), e);
                }
            }
        });
    }
    
    private TokenSequence createTokenSequence(JTextComponent component) {
        if (tokenSequence == null) {
            TokenHierarchy tokenHierarchy = TokenHierarchy.get(component.getDocument());
            this.tokenSequence = tokenHierarchy.tokenSequence();
        }
        return tokenSequence;
    }
    
    private String stripCommonPrefix(String prefix, String replacement, String original) {
        if (replacement.startsWith(prefix) && original.startsWith(prefix)) {
            return replacement.substring(prefix.length());
        } else {
            return replacement;
        }
    }
    
    private void resetTokenSequence() {
        tokenSequence = null;
    }
    
    protected String getInsertingText(JTextComponent component, String primaryText, int removeLen) {
        if ((primaryText == null) || (primaryText.length() < 1)) {
            return primaryText;
        }
        int textPos = component.getCaret().getDot();
        createTokenSequence(component);
        if (tokenSequence.move(textPos) == 0) {
            tokenSequence.movePrevious();
        } else {
            tokenSequence.moveNext();
        }
        Token token = tokenSequence.token();
        boolean isTextTag = CompletionUtil.isTextTag(token);

        if (! (isTextTag || CompletionUtil.isEndTagPrefix(token) ||
            CompletionUtil.isTagFirstChar(token))) {
            return primaryText;
        }

        int tokenOffset = tokenSequence.offset();
        if (isTextTag) {
            String tokenText = token.text().toString();
            boolean isCaretAfterTag =
                (tokenText.startsWith(CompletionUtil.END_TAG_PREFIX) &&
                (textPos == tokenOffset + CompletionUtil.END_TAG_PREFIX.length()))
                ||
                (tokenText.startsWith(CompletionUtil.TAG_FIRST_CHAR) &&
                (textPos == tokenOffset + CompletionUtil.TAG_FIRST_CHAR.length()));
            if (! isCaretAfterTag) {
                return primaryText;
            }
        }
        
        String tokenText = token.text().toString();
        if (removeLen > 0) {
            // in the middle of the tag; must return text without starting / end tag
            primaryText = stripCommonPrefix(CompletionUtil.END_TAG_PREFIX, primaryText, tokenText);
            primaryText = stripCommonPrefix(CompletionUtil.TAG_FIRST_CHAR, primaryText, tokenText);
        }        
        if (primaryText.endsWith(CompletionUtil.TAG_LAST_CHAR)) {
            boolean endPresent = false;
            STOP: while (!endPresent && tokenSequence.moveNext()) {
                Token t = tokenSequence.token();
                switch ((XMLTokenId)t.id()) {
                    case WS:
                    case ARGUMENT:
                    case VALUE:
                    case OPERATOR:
                        break;
                    case TAG: {
                        String tt = t.text().toString();
                        if (tt.equals(CompletionUtil.TAG_LAST_CHAR) || tt.equals("/>")) {
                            endPresent = true;
                            break;
                        }
                    }
                    default:
                        break STOP;
                }
            }
            if (endPresent) {
                primaryText = primaryText.substring(0, primaryText.length() -1);
            }
        }

        if ((tokenOffset > -1) && (tokenOffset < textPos)) {
            textPos = tokenOffset;
        }

        boolean isDifferentTextFound = false;
        int i = 0;
        for (; i < primaryText.length(); ++i, ++textPos) {
            try {
                String strDoc  = component.getText(textPos, 1),
                       strText = primaryText.substring(i, i + 1);
                isDifferentTextFound = (! strDoc.equals(strText));
                if (isDifferentTextFound) break;
            } catch(BadLocationException e) {
                _logger.log(Level.WARNING,
                    e.getMessage() == null ? e.getClass().getName() : e.getMessage(), e);
                isDifferentTextFound = true;
            }
        }
        String text = isDifferentTextFound ? primaryText.substring(Math.max(0, i - removeLen)) : "";
        return text;
    }

    ////////////////////////////////////////////////////////////////////////////////
    ///////////////////methods from CompletionItem interface////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new DocumentationQuery(this));
    }

    @Override
    public CompletionTask createToolTipTask() {
        return new AsyncCompletionTask(new ToolTipQuery(this));
    }

    @Override
    public void defaultAction(JTextComponent component) {
        String selectedText = component.getSelectedText();
        int charsToRemove = selectedText != null ? selectedText.length() :
                            (typedChars == null ? 0 : typedChars.length()),
            substOffset   = selectedText != null ? component.getSelectionStart() :
                            component.getCaretPosition() - charsToRemove;
        if(!shift) Completion.get().hideAll();
        if(getReplacementText().equals(typedChars))
            return;
        replaceText(component, getReplacementText(), substOffset, charsToRemove);
    }

    @Override
    public CharSequence getInsertPrefix() {
        return getItemText();
    }

    public abstract CompletionPaintComponent getPaintComponent();

    @Override
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        CompletionPaintComponent renderComponent = getPaintComponent();
        return renderComponent.getPreferredSize().width;
    //return getPaintComponent().getWidth(getItemText(), defaultFont);
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return getItemText();
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        defaultAction(component);
        return true;
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        shift = (e.getKeyCode() == KeyEvent.VK_ENTER &&
                 e.getID() == KeyEvent.KEY_PRESSED && e.isShiftDown());
    }

    @Override
    public void render(Graphics g, Font defaultFont,
            Color defaultColor, Color backgroundColor,
            int width, int height, boolean selected) {
        CompletionPaintComponent renderComponent = getPaintComponent();
        renderComponent.setFont(defaultFont);
        renderComponent.setForeground(defaultColor);
        renderComponent.setBackground(backgroundColor);
        renderComponent.setBounds(0, 0, width, height);
        renderComponent.setSelected(selected);
        renderComponent.paintComponent(g);
    }
}