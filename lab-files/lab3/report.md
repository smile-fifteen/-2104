# 实验三：软件需求的跟踪分析

### 实验目标：

对软件需求进行跟踪分析

### 实验思路：

* 确定开源IDE项目：NetBeans
* 获取项目的需求更改情况
* 确定要跟踪的需求
* 找出该需求的实现代码
* 根据找到的需求更改情况识别需求变更
* 分别找出变更后需求的代码
* 整理需求变更的时间线
  * 代码数据放在 版本更改信息 下

### 实验步骤：

1. 获取项目的需求更改情况：

   * 文件：

     ~~~markdown
     * creeper.py 用于爬取NetBeans相关需求变化情况
     ~~~

   * 获取思路：

     ~~~markdown
     	从  https://netbeans.org/community/releases/  和  http://netbeans.apache.org/download/nb  中爬取相应的项目的版本更改信息，从里面找出其改变的需求情况，再将它们按照各自的版本分类到不同的文件夹中，便于以后数据的处理。
     ~~~

2. 确定要跟踪的需求：

   * 文件：

     ~~~markdown
     * track.py 用于处理爬取的数据，将他们分别整理在各个文件夹中
     * type.txt
     * track.txt
     * completion.txt 包含代码补全方面的版本更改信息文件名
     ~~~
* debug.txt 包含代码调试功能方面的版本更改信息文件名
     * hint.txt 包含代码编辑过程中软件提示功能的版本更改信息文件名
     * “版本更改信息”文件夹下包含了不同版本下项目的版本更改信息
     
     ~~~
     
   * 通过比对能够获取的分别关于completion、debug、hint功能的源代码内容以及需求变更的频繁程度，我们确定跟踪的需求为code completion
   ~~~

3. 找出该需求的实现代码

   - 获取方法

     - 从官网中下载不同版本的NetBeans，根据相关命名规范与习惯找出包含code completion的文件，使用软件对这些文件进行鉴定，再将剩余的文件进行人工查看，确定实现代码

   - 文件

     ```markdown
     * 需求实现代码/70/code/*
     ```

4. 根据找到的需求更改情况识别需求变更

   - 需求变更文本：

     - 获取方法：通过查看版本更改信息中关于code completion内容的更改，确定变更的需求
     - completion.txt记录了有关的文件和相应的修改

     ```markdown
     * 70/feature_0.txt	editor enhancements: code completion, hints
     
     * 70/feature_5.txt	code completion, refactoring and hints 
     
     * 71/feature_3.txt	code completion and documentation for new css3 elements
     
     * 72/feature_3.txt	code completion for java persistence named queries and          *       jpql statements
     
     * 72/feature_6.txt	dramatic improvement of scanning, code completion and         *        go to type action speed
     
     * 72/feature_6.txt	enhancements of code completion and editor hints
     
     * 73/feature_3.txt	code completion, error marking and more 
     
     * 73/feature_4.txt	twig code completion (with documentation)
     
     * 74/feature_1.txt	enhanced code completion with improved accuracy              
     
     * 74/feature_4.txt	code completion, hints and refactoring                improvements 
     
     * 80/feature_0.txt	several java editor enhancements, such as many new                	java hints, javadoc shown as tooltip, instant rename enhancements, and code 	 completion exclusions.
     
     * 80/feature_1.txt	code completion for template sections, and within jsf                composite components.
     
     * 80/feature_1.txt	enhanced cdi integration in beans.xml file, with code completion for alternative classes and stereotypes.
     
     * 80/feature_3.txt	many enhancements for angularjs in code completion                between artifacts, such as code completion in the view to properties defined in 	 controllers.
     
     * 80/feature_3.txt	code completion support in knockout templates. 
     
     * 80/feature_6.txt	new hints and code completions.
     
     * 81/feature_2.txt	enhanced code completion("intellisense")
     
     * 82/feature_0.txt	code completion for generators
     
     ```

   5. 变更后需求的代码

   - 获取方法
     - 通过代码分析软件，比对前后版本代码不同处，通过使用相应需求关键字进行代码筛选，最后获取到相关需求的代码
     - 文件：

   ```markdown
   需求更改代码文件夹下：
   
   * 71/code
   * 72/code
   * 73/code
   * 74/code
   * 80/code
   * 81/code
   * 注：均为包含有关变更需求的整体代码文件
   
   通过人工查看在其中筛选了一下具体变更需求的代码，保存在一下文件中：
   
   * 71/70-71.md
   * 72/71-72.md
   * 73/72-73.md
   * 74/73-74.md
   * 80/74-80.md
   * 81/80-01.md
   
   ```

   - 具体内容

     - 由于实现变更需求的代码比较多，仅举例说明，其他变更实现代码均放于以上文件中

     - 74-80需求变更内容：为JSF临时板块提供code completion功能支持

       - 实现代码

       ```java
       //"代码需求变更"文件夹下80/JsfAttributesCompletionHelper.java
       package org.netbeans.modules.web.jsf.editor.completion;
       
       import java.awt.Color;
       import java.io.IOException;
       import java.util.ArrayList;
       import java.util.Arrays;
       import java.util.Collection;
       import java.util.Collections;
       import java.util.EnumSet;
       import java.util.HashMap;
       import java.util.HashSet;
       import java.util.Iterator;
       import java.util.List;
       import java.util.Locale;
       import java.util.Map;
       import java.util.Set;
       import javax.lang.model.element.NestingKind;
       import javax.lang.model.element.PackageElement;
       import javax.lang.model.element.TypeElement;
       import javax.lang.model.util.ElementScanner6;
       import javax.swing.ImageIcon;
       import org.netbeans.api.java.project.JavaProjectConstants;
       import org.netbeans.api.java.source.ClassIndex;
       import org.netbeans.api.java.source.ClasspathInfo;
       import org.netbeans.api.java.source.CompilationController;
       import org.netbeans.api.java.source.JavaSource;
       import org.netbeans.api.project.FileOwnerQuery;
       import org.netbeans.api.project.Project;
       import org.netbeans.api.project.ProjectUtils;
       import org.netbeans.api.project.SourceGroup;
       import org.netbeans.api.project.Sources;
       import org.netbeans.lib.editor.util.CharSequenceUtilities;
       import org.netbeans.modules.html.editor.api.completion.HtmlCompletionItem;
       import org.netbeans.modules.html.editor.api.gsf.HtmlExtension.CompletionContext;
       import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
       import org.netbeans.modules.html.editor.lib.api.elements.Element;
       import org.netbeans.modules.html.editor.lib.api.elements.ElementType;
       import org.netbeans.modules.html.editor.lib.api.elements.ElementUtils;
       import org.netbeans.modules.html.editor.lib.api.elements.ElementVisitor;
       import org.netbeans.modules.html.editor.lib.api.elements.Node;
       import org.netbeans.modules.html.editor.lib.api.elements.OpenTag;
       import org.netbeans.modules.parsing.api.ParserManager;
       import org.netbeans.modules.parsing.api.ResultIterator;
       import org.netbeans.modules.parsing.api.Source;
       import org.netbeans.modules.parsing.api.UserTask;
       import org.netbeans.modules.parsing.spi.ParseException;
       import org.netbeans.modules.parsing.spi.Parser;
       import org.netbeans.modules.web.api.webmodule.WebModule;
       import org.netbeans.modules.web.common.api.FileReferenceCompletion;
       import org.netbeans.modules.web.common.api.LexerUtils;
       import org.netbeans.modules.web.common.taginfo.AttrValueType;
       import org.netbeans.modules.web.common.taginfo.LibraryMetadata;
       import org.netbeans.modules.web.common.taginfo.TagAttrMetadata;
       import org.netbeans.modules.web.common.taginfo.TagMetadata;
       import org.netbeans.modules.web.jsf.editor.JsfSupportImpl;
       import org.netbeans.modules.web.jsf.editor.JsfUtils;
       import org.netbeans.modules.web.jsf.editor.facelets.CompositeComponentLibrary;
       import org.netbeans.modules.web.jsf.editor.facelets.FaceletsLibraryMetadata;
       import org.netbeans.modules.web.jsf.editor.index.CompositeComponentModel;
       import org.netbeans.modules.web.jsf.editor.index.JsfPageModelFactory;
       import org.netbeans.modules.web.jsfapi.api.Attribute;
       import org.netbeans.modules.web.jsfapi.api.DefaultLibraryInfo;
       import org.netbeans.modules.web.jsfapi.api.Library;
       import org.netbeans.modules.web.jsfapi.api.LibraryComponent;
       import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
       import org.netbeans.modules.web.jsfapi.api.Tag;
       import org.netbeans.modules.web.jsfapi.spi.LibraryUtils;
       import org.netbeans.spi.editor.completion.CompletionItem;
       import org.openide.filesystems.FileObject;
       import org.openide.util.Exceptions;
       
       public class JsfAttributesCompletionHelper{
           ...
       }
       
       
       //JsfCompletion.java
       package org.netbeans.modules.web.jsf.editor.completion;
       
       import java.awt.Color;
       import java.net.URL;
       import java.util.HashMap;
       import java.util.Map;
       import java.util.regex.Matcher;
       import java.util.regex.Pattern;
       import javax.swing.UIManager;
       import javax.swing.text.JTextComponent;
       import org.netbeans.editor.BaseDocument;
       import org.netbeans.modules.html.editor.api.completion.HtmlCompletionItem;
       import org.netbeans.modules.html.editor.lib.api.DefaultHelpItem;
       import org.netbeans.modules.html.editor.lib.api.HelpItem;
       import org.netbeans.modules.web.jsf.editor.facelets.AbstractFaceletsLibrary;
       import org.netbeans.modules.web.jsfapi.api.LibraryComponent;
       import org.netbeans.modules.web.jsfapi.api.Library;
       import org.netbeans.modules.web.jsfapi.spi.LibraryUtils;
       import org.openide.filesystems.FileObject;
       import org.openide.filesystems.FileUtil;
       import org.openide.filesystems.URLMapper;
       import org.openide.util.NbBundle;
       
       
       public class JsfCompletionItem {
           //html items priority varies from 10 to 20
           private static final int JSF_DEFAULT_SORT_PRIORITY = 5;
       
           /** Replacements for tags and attributes to color properly documentation window colors (#232983). */
           private static final Map<String, String> COLORING_REPLACEMENTS = new HashMap<String, String>() {{
               put("<p>", "<p" + getColorStyleAttribute() + ">");      //NOI18N
               put("<div>", "<div" + getColorStyleAttribute() + ">");  //NOI18N
               put("<ol>", "<ol" + getColorStyleAttribute() + ">");    //NOI18N
               put("class=[^>]*", getColorStyleAttribute());           //NOI18N
           }};
           private static final Pattern REPLACEMENT_PATTERN = getPattern();
       
           //----------- Factory methods --------------
           public static JsfTag createTag(int substitutionOffset, LibraryComponent component, String declaredPrefix, boolean autoimport, boolean isJsf22Plus) {
               return new JsfTag(substitutionOffset, component, declaredPrefix, autoimport, isJsf22Plus);
           }
       
           public static JsfTagAttribute createAttribute(String name, int substitutionOffset, Library library, org.netbeans.modules.web.jsfapi.api.Tag tag, org.netbeans.modules.web.jsfapi.api.Attribute attr) {
               return new JsfTagAttribute(name, substitutionOffset, library, tag, attr);
           }
       
       
           private static String convertTextColors(String content) {
               // Hack to recolor HTML defined by JSF library in the metadata.
               //   This recoloring is tuned for Mojarra implementation.
              ...
           }
       
           private static String getFromReplacementMap(String string) {
               String result = COLORING_REPLACEMENTS.get(string);
               return result != null ? result : getColorStyleAttribute();
           }
       
           private static Pattern getPattern() {
               ...
           }
       
           private static String getColorStyleAttribute() {
              ...
           }
       
           public static class JsfTag extends HtmlCompletionItem.Tag {
       
               private static final String BOLD_OPEN_TAG = "<b>"; //NOI18N
               private static final String BOLD_END_TAG = "</b>"; //NOI18N
               
               private static final String AND_HTML_ENTITY = "&amp;"; //NOI18N
               private static final String AND_HTML = "&"; //NOI18N
               
               private LibraryComponent component;
               private boolean autoimport; //autoimport (declare) the tag namespace if set to true
               private boolean isJsf22Plus;
       
               public JsfTag(int substitutionOffset, LibraryComponent component, String declaredPrefix, boolean autoimport, boolean isJsf22Plus) {
                   ...
               }
       
               @Override
               protected String getRightHtmlText() {
                   return component.getLibrary().getDisplayName();
               }
       
               @Override
               public void defaultAction(JTextComponent component) {
                   ...
               }
       
               private void autoimportLibrary(JTextComponent component) {
                  ...
               }
       
               //use bold font
               @Override
               protected String getLeftHtmlText() {
                   ...
               }
       
               @Override
               public int getSortPriority() {
                 ...
               }
       
               private String getHelpContent() {
                   ...
               }
       
               @Override
               public boolean hasHelp() {
                   return true;
               }
       
               @Override
               public HelpItem getHelpItem() {
                  ...
       
       
           }
       
           public static class JsfTagAttribute extends HtmlCompletionItem.Attribute {
       
               ...
               }
       
               private String getHelpContent() {
                   ...
       
           }
       
           private static String getLibraryHelpHeader(Library library) {
               ...
       
           }
       }
       
       //JsfDocumentation.java
       package org.netbeans.modules.web.jsf.editor.completion;
       
       import java.io.BufferedInputStream;
       import java.io.File;
       import java.io.FileNotFoundException;
       import java.io.IOException;
       import java.io.InputStreamReader;
       import java.io.Reader;
       import java.net.MalformedURLException;
       import java.net.URI;
       import java.net.URISyntaxException;
       import java.net.URL;
       import java.net.URLConnection;
       import java.nio.charset.Charset;
       import java.util.Map;
       import java.util.WeakHashMap;
       import java.util.logging.Level;
       import java.util.logging.Logger;
       import org.netbeans.modules.html.editor.lib.api.HelpResolver;
       import org.openide.filesystems.FileUtil;
       import org.openide.modules.InstalledFileLocator;
       import org.openide.util.Exceptions;
       import org.openide.util.Utilities;
       
       public class JsfDocumentation implements HelpResolver {
           private static final Logger LOGGER = Logger.getLogger(JsfDocumentation.class.getName());
           private static final JsfDocumentation SINGLETON = new JsfDocumentation();
           private static final String DOC_ZIP_FILE_NAME = "docs/jsf-api-docs.zip"; //NOI18N
           private static URL DOC_ZIP_URL;
       
           private static final String JAVADOC_FOLDER_NAME = "javadocs/"; //NOI18N
       
           private static Map<String, String> HELP_FILES_CACHE = new WeakHashMap<>();
       
           public static JsfDocumentation getDefault() {
               return SINGLETON;
           }
       
           static URL getZipURL() {
               ...
           }
       
           @Override
           public URL resolveLink(URL baseURL, String relativeLink) {
               ...
           }
       
           private URL getRelativeURL(URL baseurl, String link){
               ...
           }
       
           @Override
           public String getHelpContent(URL url) {
               return getContentAsString(url, null);
           }
       
           static String getContentAsString(URL url, Charset charset) {
               ...
       }
       
       
       //JsfDocumentationTest.java
       package org.netbeans.modules.web.jsf.editor.completion;
       
       import java.io.IOException;
       import java.net.URL;
       import java.net.URLConnection;
       import junit.framework.Test;
       import junit.framework.TestSuite;
       import org.netbeans.junit.NbTestCase;
       
       public class JsfDocumentationTest extends NbTestCase {
       
           public JsfDocumentationTest(String name) {
              ...
           }
       
            public static Test xsuite() {
               ...
           }
       
           @Override
           protected void setUp() throws Exception {
               ...
           }
       
           public void testDocZipPresence() throws IOException {
               ...
           }
       
           public void testResolveLink() throws IOException {
               ...  
           }
       
           public void testResolveFromIndexToHelp() { 
               ...
           }
          
       
       }
       ```

       - 80版本提供更为详细的代码补全提示

         - 代码

           ```java
           public static enum CompletionType {
                   COMPLETION_TYPE_UNKNOWN,
                   COMPLETION_TYPE_ATTRIBUTE,
                   COMPLETION_TYPE_ATTRIBUTE_VALUE,
                   COMPLETION_TYPE_ELEMENT,
                   COMPLETION_TYPE_ELEMENT_VALUE,
                   COMPLETION_TYPE_ENTITY,
                   COMPLETION_TYPE_NOTATION,
                   COMPLETION_TYPE_DTD
               }
               public static abstract class CompletionModel {
                   
                   /**
                    * Returns the suggested prefix to be used for completion.
                    * @return the suggested prefix for this schema model.
                    */
                   public abstract String getSuggestedPrefix();
                   
                   /**
                    * Returns the target namespace for this schema model.
                    * @return the target namespace for this schema model.
                    */
                   public abstract String getTargetNamespace();
                   
                   /**
                    * Returns the schema model.
                    * @return the model defining a schema that applies to the document at
                    * the completion point.
                    */
                   public abstract SchemaModel getSchemaModel();
               }
               
           ```

       - 81版本比80版本更增强了代码补全的智能型

       - 有关代码

         ```java
         //在javaCompletionTask等文件中添加了对java一些词的敏感度，即当输入时可以自动生成代码补全的事件
         public static boolean isAutoConvertible(CsmType from, CsmType to) {
                 return isAutoConvertible(null, from, to);
             }
         
             static boolean isAutoConvertible(Context ctx, CsmType origFrom, CsmType origTo) {
                 AnalyzedType from = AnalyzedType.create(ctx, origFrom, false, false);
                 if (from == null) {
                     return false;
                 }
                 AnalyzedType to = AnalyzedType.create(ctx, origTo, true, true);
                 if (to == null) {
                     return false;
                 }
                 return isAutoConvertible(from.type, from.origType, from.typeInfo, to.type, to.origType, to.typeInfo, from.classifier, to.classifier);
             }
         
             public static boolean isAutoConvertible(CsmType from, CsmType origFrom, TypeInfoCollector fromInfo, CsmType to, CsmType origTo, TypeInfoCollector toInfo, CsmClassifier fromCls, CsmClassifier toCls) {
                 final int fromArrayDepth = howMany(fromInfo, Qualificator.ARRAY);
                 final int toArrayDepth = howMany(toInfo, Qualificator.ARRAY);
                 final int fromPointerDepth = howMany(fromInfo, Qualificator.POINTER);
                 final int toPointerDepth = howMany(toInfo, Qualificator.POINTER);
         
                 // XXX review!
                 if (fromCls.equals(CsmCompletion.NULL_CLASS)) {
                     return toArrayDepth > 0 || !CsmCompletion.isPrimitiveClass(toCls);
                 }
         
                 if (toCls.equals(CsmCompletion.OBJECT_CLASS)) {
                     // everything is object
                     return (fromArrayDepth > toArrayDepth) || (fromArrayDepth == toArrayDepth && !CsmCompletion.isPrimitiveClass(fromCls));
                 }
         
                 if (canBePointer(from) && toPointerDepth > 0) {
                     return true;
                 }
         
                 if (fromPointerDepth > 0 && canBePointer(to)) {
                     return true;
                 }
         
                 if (fromArrayDepth != toArrayDepth ||
                         fromPointerDepth != toPointerDepth) {
                     return false;
                 }
         
                 if (fromCls.equals(toCls)) {
                     return true; // equal classes
                 }
         
                 String tfrom = origFrom.getCanonicalText().toString().replaceAll("const", "").trim(); // NOI18N
                 String tto = origTo.getCanonicalText().toString().replaceAll("const", "").trim(); // NOI18N
         
                 if (tfrom.equals(tto)) {
                     return true;
                 }
         
                 if (CsmCompletion.isPrimitiveClass(fromCls) && CsmCompletion.isPrimitiveClass(toCls)) {
                     return true;
                 }
         
                 if (CsmKindUtilities.isClass(toCls) && CsmKindUtilities.isClass(fromCls)) {
                     return CsmInheritanceUtilities.isAssignableFrom((CsmClass)fromCls, (CsmClass)toCls);
                 }
                 return false;
             }
         
             public final class JavaCompletionTask<T> extends BaseTask {
             ...
             private static final String ABSTRACT_KEYWORD = "abstract"; //NOI18N
             private static final String ASSERT_KEYWORD = "assert"; //NOI18N
             private static final String BOOLEAN_KEYWORD = "boolean"; //NOI18N
             private static final String BREAK_KEYWORD = "break"; //NOI18N
             private static final String BYTE_KEYWORD = "byte"; //NOI18N
             private static final String CASE_KEYWORD = "case"; //NOI18N
             private static final String CATCH_KEYWORD = "catch"; //NOI18N
             private static final String CHAR_KEYWORD = "char"; //NOI18N
             private static final String CLASS_KEYWORD = "class"; //NOI18N
             private static final String CONTINUE_KEYWORD = "continue"; //NOI18N
             private static final String DEFAULT_KEYWORD = "default"; //NOI18N
             private static final String DO_KEYWORD = "do"; //NOI18N
             private static final String DOUBLE_KEYWORD = "double"; //NOI18N
             private static final String ELSE_KEYWORD = "else"; //NOI18N
             private static final String ENUM_KEYWORD = "enum"; //NOI18N
             private static final String EXTENDS_KEYWORD = "extends"; //NOI18N
             private static final String FALSE_KEYWORD = "false"; //NOI18N
             private static final String FINAL_KEYWORD = "final"; //NOI18N
             private static final String FINALLY_KEYWORD = "finally"; //NOI18N
             private static final String FLOAT_KEYWORD = "float"; //NOI18N
             private static final String FOR_KEYWORD = "for"; //NOI18N
             private static final String IF_KEYWORD = "if"; //NOI18N
             private static final String IMPLEMENTS_KEYWORD = "implements"; //NOI18N
             private static final String IMPORT_KEYWORD = "import"; //NOI18N
             private static final String INSTANCEOF_KEYWORD = "instanceof"; //NOI18N
             private static final String INT_KEYWORD = "int"; //NOI18N
             private static final String INTERFACE_KEYWORD = "interface"; //NOI18N
             private static final String LONG_KEYWORD = "long"; //NOI18N
             private static final String NATIVE_KEYWORD = "native"; //NOI18N
             private static final String NEW_KEYWORD = "new"; //NOI18N
             private static final String NULL_KEYWORD = "null"; //NOI18N
             private static final String PACKAGE_KEYWORD = "package"; //NOI18N
             private static final String PRIVATE_KEYWORD = "private"; //NOI18N
             private static final String PROTECTED_KEYWORD = "protected"; //NOI18N
             private static final String PUBLIC_KEYWORD = "public"; //NOI18N
             private static final String RETURN_KEYWORD = "return"; //NOI18N
             private static final String SHORT_KEYWORD = "short"; //NOI18N
             private static final String STATIC_KEYWORD = "static"; //NOI18N
             private static final String STRICT_KEYWORD = "strictfp"; //NOI18N
             private static final String SUPER_KEYWORD = "super"; //NOI18N
             private static final String SWITCH_KEYWORD = "switch"; //NOI18N
             private static final String SYNCHRONIZED_KEYWORD = "synchronized"; //NOI18N
             private static final String THIS_KEYWORD = "this"; //NOI18N
             private static final String THROW_KEYWORD = "throw"; //NOI18N
             private static final String THROWS_KEYWORD = "throws"; //NOI18N
             private static final String TRANSIENT_KEYWORD = "transient"; //NOI18N
             private static final String TRUE_KEYWORD = "true"; //NOI18N
             private static final String TRY_KEYWORD = "try"; //NOI18N
             private static final String VOID_KEYWORD = "void"; //NOI18N
             private static final String VOLATILE_KEYWORD = "volatile"; //NOI18N
             private static final String WHILE_KEYWORD = "while"; //NOI18N
             ...
             }
         ```

         - 并提供了对一些编程语言中的固定搭配语法进行自动补全

           ```java
           //BashTask.java
           ...//部分代码
           private Env getEnvImpl(CompilationController controller, TreePath orig, TreePath path, TreePath pPath, TreePath gpPath, int offset, String prefix, boolean upToOffset) throws IOException {
                   Tree tree = path != null ? path.getLeaf() : null;
                   Tree parent = pPath != null ? pPath.getLeaf() : null;
                   Tree grandParent = gpPath != null ? gpPath.getLeaf() : null;
                   SourcePositions sourcePositions = controller.getTrees().getSourcePositions();
                   CompilationUnitTree root = controller.getCompilationUnit();
                   TreeUtilities tu = controller.getTreeUtilities();
                   if (upToOffset && TreeUtilities.CLASS_TREE_KINDS.contains(tree.getKind())) {
                       controller.toPhase(withinAnonymousOrLocalClass(tu, path) ? JavaSource.Phase.RESOLVED : JavaSource.Phase.ELEMENTS_RESOLVED);
                       return new Env(offset, prefix, controller, orig, sourcePositions, null);
                   } else if (parent != null && tree.getKind() == Tree.Kind.BLOCK
                           && (parent.getKind() == Tree.Kind.METHOD || TreeUtilities.CLASS_TREE_KINDS.contains(parent.getKind()))) {
                       controller.toPhase(withinAnonymousOrLocalClass(tu, path) ? JavaSource.Phase.RESOLVED : JavaSource.Phase.ELEMENTS_RESOLVED);
                       int blockPos = (int) sourcePositions.getStartPosition(root, tree);
                       String blockText = controller.getText().substring(blockPos, upToOffset ? offset : (int) sourcePositions.getEndPosition(root, tree));
                       final SourcePositions[] sp = new SourcePositions[1];
                       final StatementTree block = (((BlockTree) tree).isStatic() ? tu.parseStaticBlock(blockText, sp) : tu.parseStatement(blockText, sp));
                       if (block == null) {
                           return null;
                       }
                       sourcePositions = new SourcePositionsImpl(block, sourcePositions, sp[0], blockPos, upToOffset ? offset : -1);
                       Scope scope = controller.getTrees().getScope(path);
                       path = tu.pathFor(new TreePath(pPath, block), offset, sourcePositions);
                       if (upToOffset) {
                           Tree last = path.getLeaf();
                           List<? extends StatementTree> stmts = null;
                           switch (path.getLeaf().getKind()) {
                               case BLOCK:
                                   stmts = ((BlockTree) path.getLeaf()).getStatements();
                                   break;
                               case FOR_LOOP:
                                   stmts = ((ForLoopTree) path.getLeaf()).getInitializer();
                                   break;
                               case ENHANCED_FOR_LOOP:
                                   stmts = Collections.singletonList(((EnhancedForLoopTree) path.getLeaf()).getStatement());
                                   break;
                               case METHOD:
                                   stmts = ((MethodTree) path.getLeaf()).getParameters();
                                   break;
                               case SWITCH:
                                   CaseTree lastCase = null;
                                   for (CaseTree caseTree : ((SwitchTree) path.getLeaf()).getCases()) {
                                       lastCase = caseTree;
                                   }
                                   if (lastCase != null) {
                                       stmts = lastCase.getStatements();
                                   }
                                   break;
                               case CASE:
                                   stmts = ((CaseTree) path.getLeaf()).getStatements();
                                   break;
                           }
                           if (stmts != null) {
                               for (StatementTree st : stmts) {
                                   if (sourcePositions.getEndPosition(root, st) <= offset) {
                                       last = st;
                                   }
                               }
                           }
                           scope = tu.reattributeTreeTo(block, scope, last);
                       } else {
                           tu.reattributeTreeTo(block, scope, block);
                       }
                       return new Env(offset, prefix, controller, path, sourcePositions, scope);
                   } else if (tree.getKind() == Tree.Kind.LAMBDA_EXPRESSION) {
                       controller.toPhase(JavaSource.Phase.RESOLVED);
                       Tree lambdaBody = ((LambdaExpressionTree) tree).getBody();
                       Scope scope = null;
                       TreePath blockPath = path.getParentPath();
                       while (blockPath != null) {
                           if (blockPath.getLeaf().getKind() == Tree.Kind.BLOCK) {
                               if (blockPath.getParentPath().getLeaf().getKind() == Tree.Kind.METHOD
                                       || TreeUtilities.CLASS_TREE_KINDS.contains(blockPath.getParentPath().getLeaf().getKind())) {
                                   final int blockPos = (int) sourcePositions.getStartPosition(root, blockPath.getLeaf());
                                   final String blockText = controller.getText().substring(blockPos, (int) sourcePositions.getEndPosition(root, blockPath.getLeaf()));
                                   final SourcePositions[] sp = new SourcePositions[1];
                                   final StatementTree block = (((BlockTree) blockPath.getLeaf()).isStatic() ? tu.parseStaticBlock(blockText, sp) : tu.parseStatement(blockText, sp));
                                   if (block == null) {
                                       return null;
                                   }
                                   sourcePositions = new SourcePositionsImpl(block, sourcePositions, sp[0], blockPos, -1);
                                   scope = controller.getTrees().getScope(blockPath);
                                   path = tu.getPathElementOfKind(Tree.Kind.LAMBDA_EXPRESSION, tu.pathFor(new TreePath(blockPath.getParentPath(), block), offset, sourcePositions));
                                   lambdaBody = ((LambdaExpressionTree) path.getLeaf()).getBody();
                                   scope = tu.reattributeTreeTo(block, scope, lambdaBody);
                                   break;
                               }
                           }
                           blockPath = blockPath.getParentPath();
                       }
                       if (scope == null) {
                           scope = controller.getTrees().getScope(new TreePath(path, lambdaBody));
                       }
                       final int bodyPos = (int) sourcePositions.getStartPosition(root, lambdaBody);
                       if (bodyPos >= offset) {
                           TokenSequence<JavaTokenId> ts = controller.getTokenHierarchy().tokenSequence(JavaTokenId.language());
                           ts.move(offset);
                           while (ts.movePrevious()) {
                               switch (ts.token().id()) {
                                   case WHITESPACE:
                                   case LINE_COMMENT:
                                   case BLOCK_COMMENT:
                                   case JAVADOC_COMMENT:
                                       break;
                                   case ARROW:
                                       return new Env(offset, prefix, controller, path, sourcePositions, scope);
                                   default:
                                       return null;
                               }
                           }
                       }
                       String bodyText = controller.getText().substring(bodyPos, upToOffset ? offset : (int) sourcePositions.getEndPosition(root, lambdaBody));
                       final SourcePositions[] sp = new SourcePositions[1];
                       final Tree body = bodyText.charAt(0) == '{' ? tu.parseStatement(bodyText, sp) : tu.parseExpression(bodyText, sp);
                       final Tree fake = body instanceof ExpressionTree ? new ExpressionStatementTree() {
                           @Override
                           public Object accept(TreeVisitor v, Object p) {
                               return v.visitExpressionStatement(this, p);
                           }
           
                           @Override
                           public ExpressionTree getExpression() {
                               return (ExpressionTree) body;
                           }
           
                           @Override
                           public Tree.Kind getKind() {
                               return Tree.Kind.EXPRESSION_STATEMENT;
                           }
                       } : body;
                       sourcePositions = new SourcePositionsImpl(fake, sourcePositions, sp[0], bodyPos, upToOffset ? offset : -1);
                       path = tu.pathFor(new TreePath(path, fake), offset, sourcePositions);
                       if (upToOffset && !(body instanceof ExpressionTree)) {
                           Tree last = path.getLeaf();
                           List<? extends StatementTree> stmts = null;
                           switch (path.getLeaf().getKind()) {
                               case BLOCK:
                                   stmts = ((BlockTree) path.getLeaf()).getStatements();
                                   break;
                               case FOR_LOOP:
                                   stmts = ((ForLoopTree) path.getLeaf()).getInitializer();
                                   break;
                               case ENHANCED_FOR_LOOP:
                                   stmts = Collections.singletonList(((EnhancedForLoopTree) path.getLeaf()).getStatement());
                                   break;
                               case METHOD:
                                   stmts = ((MethodTree) path.getLeaf()).getParameters();
                                   break;
                               case SWITCH:
                                   CaseTree lastCase = null;
                                   for (CaseTree caseTree : ((SwitchTree) path.getLeaf()).getCases()) {
                                       lastCase = caseTree;
                                   }
                                   if (lastCase != null) {
                                       stmts = lastCase.getStatements();
                                   }
                                   break;
                               case CASE:
                                   stmts = ((CaseTree) path.getLeaf()).getStatements();
                                   break;
                           }
           ...
               }
           
           private void completeCreate() {
                   CreateStatement createStatement = (CreateStatement) statement;
                   tablesClause = createStatement.getTablesInEffect(env.getCaretOffset());
                   switch(context) {
                       case CREATE:
                       case CREATE_DATABASE:
                       case CREATE_FUNCTION:
                       case CREATE_PROCEDURE:
                       case CREATE_SCHEMA:
                       case CREATE_TABLE:
                       case CREATE_TEMPORARY_TABLE:
                       case CREATE_VIEW:
                       case CREATE_VIEW_AS:
                           completeKeyword(context);
                           break;
                       default:
                           completeSelect();
                   }
               }            
           
           ```

           

   6. 需求变更时间线

      70版本：提出为java中JDK7 与JSF语言 的code completion的需求

      71版本：code completion增加对CSS3 web语言的支持

      72版本：在java中提供对持续查询语言和JPQL语句的代码补全，并增强对Groovy和Grail代码补全的功能

      73版本：code completion增加对PHP twig模板语言的功能

      74版本：对JavaScript语言的code completion功能进行增强，并且提高精准度

      ​				对java语言的code completion功能进行增强

      80版本：向JSF中一些复合板块的Code completion提供支持

      ​				对java编辑器如Javadoc的code completion提供支持

      ​				向C/C++语言提供全新的code compleiton支持

      ​				

      