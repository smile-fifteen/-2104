/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.cnd.completion.doxygensupport;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.Action;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmOffsetable;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author Jan Lahoda
 */
public class DoxygenDocumentation {

    private static final Pattern STRIP_STARS = Pattern.compile("^[ \t]*\\*[ \t]?", Pattern.MULTILINE); // NOI18N
    private static final String[] formatItalic = new String[]{"<i>", "</i>"}; // NOI18N

    static String doxygen2HTML(String doxygen, CppTokenId kind) {
        doxygen = doxygen.substring(3, doxygen.length() - 2);
        doxygen = STRIP_STARS.matcher(doxygen).replaceAll("");
        doxygen = doxygen.trim();
        if (kind == CppTokenId.BLOCK_COMMENT) {
            doxygen = "\\verbatim\n"+doxygen+"\n\\endverbatim"; // NOI18N
        }

        StringBuilder output = new StringBuilder();
        List<String> wordEnd = new LinkedList<String>();
        List<String> lineEnd = new LinkedList<String>();
        List<String> parEnd = new LinkedList<String>();
        String[] nextWordFormat = null;

        for (Token t : lex(doxygen)) {
            //System.out.println("---" + t.id + " " + t.image); // NOI18N
            switch (t.id) {
                case WHITESPACE:
                    output.append(t.image);
                    break;
                case WORD:
                    if (nextWordFormat != null) {
                        output.append(nextWordFormat[0]);
                    }
                    output.append(t.image);
                    for (String s : wordEnd) {
                        output.append(s);
                    }
                    wordEnd.clear();
                    if (nextWordFormat != null) {
                        output.append(nextWordFormat[1]);
                    }
                    nextWordFormat = null;
                    break;
                case LINE_END:
                    for (String s : wordEnd) {//should be empty...
                        output.append(s);
                    }
                    wordEnd.clear();
                    for (String s : lineEnd) {
                        output.append(s);
                    }
                    lineEnd.clear();
                    output.append(t.image);
                    output.append("</p><p>\n"); // NOI18N
                    break;
                case PAR_END:
                    for (String s : wordEnd) {//should be empty...
                        output.append(s);
                    }
                    wordEnd.clear();
                    for (String s : lineEnd) {//should be empty...
                        output.append(s);
                    }
                    for (String s : parEnd) {
                        output.append(s);
                    }
                    lineEnd.clear();
                    parEnd.clear();
                    output.append("</p><p>\n"); // NOI18N
                    break;
                case COMMAND:
                    CommandDescription cd = commands.get(t.image);
                    if (cd == null) {
                        // Unknown/unimplemented command. Use generic formatting.
                        cd = new CommandDescription(EndsOn.PAR, "<strong>" + t.image.substring(1) + ":</strong><br>&nbsp; ", ""); // NOI18N
//                        System.err.println("unknown command: " + t.image); // NOI18N
//                        break;
                    }
                    output.append(cd.htmlStart);
                    switch (cd.end) {
                        case WORD:
                            wordEnd.add(cd.htmlEnd);
                            break;
                        case LINE:
                            lineEnd.add(cd.htmlEnd);
                            break;
                        case PAR:
                            parEnd.add(cd.htmlEnd);
                            break;
                    }
                    if (t.image.equals("\\param")) { // NOI18N
                        nextWordFormat = formatItalic;
                    }
                    break;
            }
        }

        return "<html><body><p>" + output.toString() + "</p>"; // NOI18N
    }
    private static final Map<String, CommandDescription> commands = new HashMap<String, CommandDescription>();

    static {
        commands.put("\\c", new CommandDescription(EndsOn.WORD, "<tt>", "</tt>")); // NOI18N
        commands.put("\\p", new CommandDescription(EndsOn.WORD, "<tt>", "</tt>")); // NOI18N
        commands.put("\\a", new CommandDescription(EndsOn.WORD, "<i>", "</i>")); // NOI18N
        commands.put("\\n", new CommandDescription(EndsOn.NONE, "<br/>", "")); // NOI18N
        commands.put("\\author", new CommandDescription(EndsOn.PAR, "<strong>Author:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\exception", new CommandDescription(EndsOn.PAR, "<strong>Exceptions:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\throw", new CommandDescription(EndsOn.PAR, "<strong>Throws:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\return", new CommandDescription(EndsOn.PAR, "<strong>Returns:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\param", new CommandDescription(EndsOn.PAR, "<strong>Parameter:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\sa", new CommandDescription(EndsOn.PAR, "<strong>See Also:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\verbatim", new CommandDescription(EndsOn.NONE, "<pre>", "")); // NOI18N
        commands.put("\\endverbatim", new CommandDescription(EndsOn.NONE, "</pre>", "")); // NOI18N
        commands.put("\\brief", new CommandDescription(EndsOn.PAR, "", "")); // NOI18N
        commands.put("\\date", new CommandDescription(EndsOn.PAR, "<strong>Date:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\bug", new CommandDescription(EndsOn.PAR, "<strong>Bug:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\warning", new CommandDescription(EndsOn.PAR, "<strong>Warning:</strong><br>&nbsp; ", "")); // NOI18N
        commands.put("\\version", new CommandDescription(EndsOn.PAR, "<strong>Version:</strong><br>&nbsp; ", "")); // NOI18N

//        commands.put("\\fn", new CommandDescription(EndsOn.LINE, "<strong>", "</strong>")); // NOI18N
//        commands.put("\\code", new CommandDescription(EndsOn.NONE, "<pre>", ""));//XXX: does not work properly - the content will still be processed, '<', '>' will not be escaped. // NOI18N
//        commands.put("\\endcode", new CommandDescription(EndsOn.NONE, "</pre>", "")); // NOI18N
    }

    static final class CommandDescription {
//        final String command;

        final EndsOn end;
        final String htmlStart;
        final String htmlEnd;

        public CommandDescription(/*String command, */EndsOn end, String htmlStart, String htmlEnd) {
//            this.command = command;
            this.end = end;
            this.htmlStart = htmlStart;
            this.htmlEnd = htmlEnd;
        }
    }

    enum EndsOn {

        WORD, LINE, PAR, NONE;
    }

    public static CompletionDocumentationImpl create(CsmObject csmObject) {
        if (!(csmObject instanceof CsmOffsetable)) {
            return null;
        }

        List<DocCandidate> list = new ArrayList<DocCandidate>();

        getDocText(csmObject, list);
        if (list.isEmpty() ||  getBestDoc(list).kind != CppTokenId.DOXYGEN_COMMENT) {
            if (CsmKindUtilities.isFunctionDeclaration(csmObject)) {
                CsmFunction fun = (CsmFunction) csmObject;
                CsmFunctionDefinition definition = fun.getDefinition();
                if (definition != null && !definition.equals(fun)) {
                    getDocText(definition, list);
                }
            }
        }

        if (list.isEmpty()) {
            return null;
        }
        DocCandidate bestDoc = getBestDoc(list);
        String htmlDocText = doxygen2HTML(bestDoc.text, bestDoc.kind);

        return new CompletionDocumentationImpl(htmlDocText, bestDoc.kind);
    }

    private static DocCandidate getBestDoc(List<DocCandidate> list) {
        DocCandidate candidate = null;
        for(DocCandidate doc : list) {
            if (doc.kind == CppTokenId.DOXYGEN_COMMENT) {
                return doc;
            } else if (doc.kind == CppTokenId.BLOCK_COMMENT) {
                if (candidate == null || candidate.text.length() < doc.text.length()) {
                    candidate = doc;
                }
            }
        }
        return candidate;
    }

    private static void getDocText(CsmObject csmObject, List<DocCandidate> list){
        CsmOffsetable csmOffsetable = (CsmOffsetable) csmObject;
        final CsmFile containingFile = csmOffsetable.getContainingFile();
        if (containingFile == null) {
            return;
        }
        TokenHierarchy<?> h = TokenHierarchy.create(containingFile.getText(), CppTokenId.languageHeader());
        TokenSequence<CppTokenId> ts = h.tokenSequence(CppTokenId.languageHeader());

        // check right after declaration on the same line
        ts.move(csmOffsetable.getEndOffset());
        OUTER:
        while (ts.moveNext()) {
            switch (ts.token().id()) {
                case LINE_COMMENT:
                case NEW_LINE:
                    break OUTER;
                case BLOCK_COMMENT:
                    list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                    continue;
                case DOXYGEN_COMMENT:
                    list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                    break OUTER;
                default:
                    continue;
            }
        }
        
        ts.move(csmOffsetable.getStartOffset());
        OUTER:
        while (ts.movePrevious()) {
            switch (ts.token().id()) {
                case LINE_COMMENT:
                case WHITESPACE:
                case NEW_LINE:
                    continue;
                case BLOCK_COMMENT:
                    list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                    continue;
                case DOXYGEN_COMMENT:
                    list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                    break OUTER;
                case SEMICOLON:
                case RBRACE:
                case LBRACE:
                case PREPROCESSOR_DIRECTIVE:
                    break OUTER;
                default:
                    continue;
            }
        }
        if (CsmKindUtilities.isFunctionDefinition(csmObject)) {
            // K&K does not supported by model
            //CsmFunctionDefinition def = (CsmFunctionDefinition) csmObject;
            //CsmFunctionParameterList parameterList = def.getParameterList();
            //CsmParameterList<CsmKnRName> kernighanAndRitchieParameterList = parameterList.getKernighanAndRitchieParameterList();
            //if (kernighanAndRitchieParameterList != null) {
                ts.move(csmOffsetable.getStartOffset());
                OUTER2:
                while (ts.moveNext()) {
                    switch (ts.token().id()) {
                        case LINE_COMMENT:
                        case WHITESPACE:
                        case NEW_LINE:
                            continue;
                        case BLOCK_COMMENT:
                            list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                            continue;
                        case DOXYGEN_COMMENT:
                            list.add(new DocCandidate(ts.token().text().toString(), ts.token().id()));
                            break OUTER2;
                        case LBRACE:
                            break OUTER2;
                        default:
                            continue;
                    }
                }
            //}
        }
    }

    static Collection<Token> lex(String text) {
        LinkedList<Token> result = new LinkedList<Token>();
        StringBuilder img = new StringBuilder();
        int i = 0;
        boolean wasContent = true;
        boolean verbatimMode = false;
        boolean escapedCommand = false;

        OUTER:
        while (i < text.length()) {
            switch (text.charAt(i)) {
                case '\n': // NOI18N
                    if (i < text.length() - 1) {
                        if (!verbatimMode) {
                            // skip white spaces
                            while (i < (text.length() - 1) && (text.charAt(i + 1) == ' ' || text.charAt(i + 1) == '\t')) { // NOI18N
                                i++;
                            }
                        }
                        if (text.charAt(i + 1) == '@' || text.charAt(i + 1) == '\\' || text.charAt(i + 1) == '\n') {
                            Token last = result.getLast();
                            // Skip multiple empty lines
                            if (last.id != TokenId.LINE_END && last.id != TokenId.PAR_END) {
                                result.add(new Token(wasContent ? TokenId.LINE_END : TokenId.PAR_END, "\n")); // NOI18N
                                wasContent = false;
                            }
                        } else {
                            if (!verbatimMode) {
                                // Convert to space
                                result.add(new Token(TokenId.WHITESPACE, " ")); // NOI18N
                                wasContent = false;
                            }
                        }
                    }
                    i++;
                    break;
                case ' ': // NOI18N
                case '\t': // NOI18N
                    img.append(text.charAt(i++));
                    while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '\t')) { // NOI18N
                        img.append(text.charAt(i++));
                    }
                    result.add(new Token(TokenId.WHITESPACE, img.toString()));
                    img = new StringBuilder();
                    break;
                case '@':
                case '\\': // NOI18N
                    if (escapedCommand) {
                        escapedCommand = false;
                        img.append(text.charAt(i));
                        i++;
                        break;
                    }
                    boolean escaped = false;
                    if (text.charAt(i) == '\\' && (i+1) < text.length()) {
                        // could be escaped predefined symbols 
                        switch (text.charAt(i+1)) {
                            case '\\':// This command writes a backslash character (\) to the output. The backslash has to be escaped in some cases because doxygen uses it to detect commands.
                                escaped = true;
                                escapedCommand = true;
                                i++;
                                break;
                            case '@':// This command writes an at-sign (@) to the output. The at-sign has to be escaped in some cases because doxygen uses it to detect JavaDoc commands.
                                escaped = true;
                                escapedCommand = true;
                                i++;
                                break;
                            case '&':// This command writes the & character to output. This character has to be escaped because it has a special meaning in HTML.
                            case '$':// This command writes the $ character to the output. This character has to be escaped in some cases, because it is used to expand environment variables.
                            case '#':// This command writes the # character to the output. This character has to be escaped in some cases, because it is used to refer to documented entities.
                            case '<':// This command writes the < character to the output. This character has to be escaped because it has a special meaning in HTML.
                            case '>':// This command writes the > character to the output. This character has to be escaped because it has a special meaning in HTML.
                            case '%':// This command writes the % character to the output. This character has to be escaped in some cases, because it is used to prevent auto-linking to word that is also a documented class or struct.
                            case '"':// This command writes the " character to the output. This character has to be escaped in some cases, because it is used in pairs to indicate an unformatted text fragment.
                                i++;
                                img.append(text.charAt(i));
                                escaped = true;
                                break;
                            case ':':
                                if ((i+2) < text.length()) {
                                    if (text.charAt(i+2) == ':') {
                                        //  This command write a double colon (::) to the output. This character sequence has to be escaped in some cases, because it is used to ref to documented entities.
                                        i+=3;
                                        img.append("::"); // NOI18N
                                        escaped = true;
                                        break;
                                    }
                                }
                        }
                        if (escaped) {
                            wasContent = true;
                            break;
                        }
                    }                    
                    img.append('\\');
                    i++;
                    while (i < text.length() && Character.isLetter(text.charAt(i))) {
                        img.append(text.charAt(i++));
                    }
                    result.add(new Token(TokenId.COMMAND, img.toString()));
                    if (img.toString().equals("\\verbatim")) { // NOI18N
                        verbatimMode = true;
                    }
                    if (verbatimMode && img.toString().equals("\\endverbatim")) { // NOI18N
                        verbatimMode = false;
                    }
                    img = new StringBuilder();
                    wasContent = true;
                    break;
                default:
                    img.append(text.charAt(i++));
                    while (i < text.length()) { // NOI18N
                        if (!verbatimMode && (text.charAt(i) == ' ' || text.charAt(i) == '\t' || text.charAt(i) == '\n')) {
                            break;
                        } else if (text.charAt(i) == '\\' || text.charAt(i) == '@') {
                            break;
                        }
                        img.append(text.charAt(i++));
                    }
                    result.add(new Token(TokenId.WORD, img.toString()));
                    img = new StringBuilder();
                    wasContent = true;
                    break;
            }
        }

        return result;
    }

    static class Token {

        final TokenId id;
        final String image;

        public Token(TokenId id, String image) {
            this.id = id;
            this.image = image;
        }

        @Override
        public String toString() {
            return id + ":" + image; // NOI18N
        }
    }

    enum TokenId {

        COMMAND, WHITESPACE, PAR_END, LINE_END, WORD//, LINE_START;
    }

    public static final class CompletionDocumentationImpl implements CompletionDocumentation {

        private final String text;
        private final CppTokenId kind;

        public CompletionDocumentationImpl(String text, CppTokenId kind) {
            this.kind = kind;
            this.text = text;
        }

        public CppTokenId getKind() {
            return kind;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public URL getURL() {
            return null;
        }

        @Override
        public CompletionDocumentation resolveLink(String link) {
            return null;
        }

        @Override
        public Action getGotoSourceAction() {
            return null;
        }
    }

    private static final class DocCandidate {
        private final String text;
        private final CppTokenId kind;

        public DocCandidate(String text, CppTokenId kind) {
            this.text = text;
            this.kind = kind;
        }
    }
}
