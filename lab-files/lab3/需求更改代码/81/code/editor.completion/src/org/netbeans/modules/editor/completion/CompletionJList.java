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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.editor.completion;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.LocaleSupport;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompositeCompletionItem;
import org.netbeans.spi.editor.completion.LazyCompletionItem;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
* @author Miloslav Metelka, Dusan Balek
* @version 1.00
*/

public class CompletionJList extends JList {

    private static final int DARKER_COLOR_COMPONENT = 5;
    private static final int SUB_MENU_ICON_GAP = 1;
    private static final ImageIcon subMenuIcon = ImageUtilities.loadImageIcon("org/netbeans/modules/editor/hints/resources/suggestion.gif", false); // NOI18N

    private final RenderComponent renderComponent;
    
    private Graphics cellPreferredSizeGraphics;
    private int fixedItemHeight;
    private int maxVisibleRowCount;
    private JTextComponent editorComponent;
    private int smartIndex;
    
    public CompletionJList(int maxVisibleRowCount, MouseListener mouseListener, JTextComponent editorComponent) {
        this.maxVisibleRowCount = maxVisibleRowCount;
        this.editorComponent = editorComponent;
        addMouseListener(mouseListener);
        setFont(editorComponent.getFont());
        setLayoutOrientation(JList.VERTICAL);
        setFixedCellHeight(fixedItemHeight = Math.max(CompletionLayout.COMPLETION_ITEM_HEIGHT, getFontMetrics(getFont()).getHeight()));
        setModel(new Model(Collections.EMPTY_LIST));
        setFocusable(false);

        renderComponent = new RenderComponent();
        setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        setCellRenderer(new ListCellRenderer() {
            private final ListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if( value instanceof CompletionItem ) {
                    CompletionItem item = (CompletionItem)value;
                    renderComponent.setItem(item);
                    renderComponent.setSelected(isSelected);
                    renderComponent.setSeparator(smartIndex > 0 && smartIndex == index);
                    Color bgColor;
                    Color fgColor;
                    if (isSelected) {
                        bgColor = list.getSelectionBackground();
                        fgColor = list.getSelectionForeground();
                    } else { // not selected
                        bgColor = list.getBackground();
                        if ((index % 2) == 0) { // every second item slightly different
                            bgColor = new Color(
                                    Math.abs(bgColor.getRed() - DARKER_COLOR_COMPONENT),
                                    Math.abs(bgColor.getGreen() - DARKER_COLOR_COMPONENT),
                                    Math.abs(bgColor.getBlue() - DARKER_COLOR_COMPONENT)
                            );
                        }
                        fgColor = list.getForeground();
                    }
                    // quick check Component.setBackground() always fires change
                    if (renderComponent.getBackground() != bgColor) {
                        renderComponent.setBackground(bgColor);
                    }
                    if (renderComponent.getForeground() != fgColor) {
                        renderComponent.setForeground(fgColor);
                    }
                    return renderComponent;

                } else {
                    return defaultRenderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus);
                }
            }
        });
        getAccessibleContext().setAccessibleName(LocaleSupport.getString("ACSN_CompletionView"));
        getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_CompletionView"));
    }

    public @Override void paint(Graphics g) {
        Object value = (Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints")); //NOI18N
        Map renderingHints = (value instanceof Map) ? (java.util.Map)value : null;
        if (renderingHints != null && g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            RenderingHints oldHints = g2d.getRenderingHints();
            g2d.addRenderingHints(renderingHints);
            try {
                super.paint(g2d);
            } finally {
                g2d.setRenderingHints(oldHints);
            }
        } else {
            super.paint(g);
        }
    }
    
    void setData(List data, int selectedIndex) {
        smartIndex = -1;
        if (data != null) {
            int itemCount = data.size();
            ListCellRenderer renderer = getCellRenderer();
            int width = 0;
            int maxWidth = getParent().getParent().getMaximumSize().width;
            boolean stop = false;
            for(int index = 0; index < itemCount; index++) {
                Object value = data.get(index);
                if (value instanceof LazyCompletionItem) {
                    maxWidth = (int)(Utilities.getUsableScreenBounds().width * CompletionLayoutPopup.COMPL_COVERAGE);
                }
                Component c = renderer.getListCellRendererComponent(this, value, index, false, false);
                if (c != null) {
                    Dimension cellSize = c.getPreferredSize();
                    if (cellSize.width > width) {
                        width = cellSize.width;
                        if (width >= maxWidth)
                            stop = true;                    
                    }
                }
                if (smartIndex < 0 && value instanceof CompletionItem && ((CompletionItem)value).getSortPriority() >= 0)
                    smartIndex = index;
                if (stop && smartIndex >= 0)
                    break;
            }
            setFixedCellWidth(width);
            LazyListModel lm = LazyListModel.create( new Model(data), CompletionImpl.filter, 1.0d, LocaleSupport.getString("completion-please-wait") ); //NOI18N
            setModel(lm);
            
            if (itemCount > 0) {
                setSelectedIndex(selectedIndex < 0 ? 0 : lm.findExternalIndex(selectedIndex));
            }
            int visibleRowCount = Math.min(itemCount, maxVisibleRowCount);
            setVisibleRowCount(visibleRowCount);
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (isVisible()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    updateAccessible();
                }
            });
        } else {
            AccessibleContext editorAC = editorComponent.getAccessibleContext();
            if (accessibleLabel != null) {
                editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY, accessibleLabel, null);
                editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, accessibleLabel, null);
            }
            if (accessibleFakeLabel != null) {
                editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, accessibleFakeLabel, null);
            }
        }
    }

    @Override
    public void setSelectedIndex(int index) {
        super.setSelectedIndex(index);
        if (isVisible()) {
            updateAccessible();
        }
    }
    
    private JLabel accessibleLabel;
    private JLabel accessibleFakeLabel;
    private void updateAccessible() {
        AccessibleContext editorAC = editorComponent.getAccessibleContext();
        if (accessibleFakeLabel == null) {
            accessibleFakeLabel = new JLabel(""); //NOI18N
            editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, null, accessibleFakeLabel);
        }
        JLabel orig = accessibleLabel;
        editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY, accessibleLabel, accessibleFakeLabel);
        Object selectedValue = getSelectedValue();
        String accName = null;
        if (selectedValue instanceof Accessible) {
            AccessibleContext ac = ((Accessible) selectedValue).getAccessibleContext();
            if (ac != null) {
                accName = ac.getAccessibleName();
            }
        }
        if (accName == null && selectedValue != null) {
            accName = selectedValue.toString();
        }
        if (accName != null) {
            accessibleLabel = new JLabel(LocaleSupport.getString("ACSN_CompletionView_SelectedItem") + accName); //NOI18N
            editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, null, accessibleLabel);
            editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY, accessibleFakeLabel, accessibleLabel);
            if (orig != null) {
                editorAC.firePropertyChange(AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, orig, null);
            }
        }
    }
    
    public void up() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = (getSelectedIndex() - 1 + size) % size;
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            setSelectedIndex(idx);
            ensureIndexIsVisible(idx);
        }
    }

    public void down() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = (getSelectedIndex() + 1) % size;
            while(idx < size && getModel().getElementAt(idx) == null)
                idx++;
            if (idx == size)
                idx = 0;
            setSelectedIndex(idx);
            ensureIndexIsVisible(idx);
        }
    }

    public void pageUp() {
        if (getModel().getSize() > 0) {
            int pageSize = Math.max(getLastVisibleIndex() - getFirstVisibleIndex(), 0);
            int idx = Math.max(getSelectedIndex() - pageSize, 0);
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            setSelectedIndex(idx);
            ensureIndexIsVisible(idx);
        }
    }

    public void pageDown() {
        int size = getModel().getSize();
        if (size > 0) {
            int pageSize = Math.max(getLastVisibleIndex() - getFirstVisibleIndex(), 0);
            int idx = Math.min(getSelectedIndex() + pageSize, size - 1);
            while(idx < size && getModel().getElementAt(idx) == null)
                idx++;
            if (idx == size) {
                idx = Math.min(getSelectedIndex() + pageSize, size - 1);
                while(idx > 0 && getModel().getElementAt(idx) == null)
                    idx--;
            }
            setSelectedIndex(idx);
            ensureIndexIsVisible(idx);
        }
    }

    public void begin() {
        if (getModel().getSize() > 0) {
            setSelectedIndex(0);
            ensureIndexIsVisible(0);
        }
    }

    public void end() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = size - 1;
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            setSelectedIndex(idx);
            ensureIndexIsVisible(idx);
        }
    }

    private final class Model extends AbstractListModel {

        List data;

        public Model(List data) {
            this.data = data;
        }
        
        @Override
        public int getSize() {
            return data.size();
        }

        @Override
        public Object getElementAt(int index) {
            return (index >= 0 && index < data.size()) ? data.get(index) : null;
        }
    }
    
    private final class RenderComponent extends JComponent {
        
        private CompletionItem item;
        
        private boolean selected;
        private boolean separator;
        
        void setItem(CompletionItem item) {
            this.item = item;
        }
        
        void setSelected(boolean selected) {
            this.selected = selected;
        }
        
        void setSeparator(boolean separator) {
            this.separator = separator;
        }

        public @Override void paintComponent(Graphics g) {
            // Although the JScrollPane without horizontal scrollbar
            // is explicitly set with a preferred size
            // it does not force its items with the only width into which
            // they can render (and still leaves them with the preferred width
            // of the widest item).
            // Therefore the item's render width is taken from the viewport's width.
            JViewport parent = (JViewport)CompletionJList.this.getParent();
            int itemRenderWidth = parent != null ? parent.getWidth() : getWidth();
            Color bgColor = getBackground();
            Color fgColor = getForeground();
            int height = getHeight();

            // Clear the background
            g.setColor(bgColor);
            g.fillRect(0, 0, itemRenderWidth, height);
            g.setColor(fgColor);

            // Render the item
            item.render(g, CompletionJList.this.getFont(), getForeground(), bgColor,
                    itemRenderWidth, getHeight(), selected);
            if (selected && item instanceof CompositeCompletionItem && !((CompositeCompletionItem)item).getSubItems().isEmpty()) {
                g.drawImage(subMenuIcon.getImage(), itemRenderWidth - subMenuIcon.getIconWidth() - SUB_MENU_ICON_GAP, (height - subMenuIcon.getIconHeight()) / 2, null);
            }
            
            if (separator) {
                g.setColor(Color.gray);
                g.drawLine(0, 0, itemRenderWidth, 0);
                g.setColor(fgColor);
            }
        }
        
        public @Override Dimension getPreferredSize() {
            if (cellPreferredSizeGraphics == null) {
                // CompletionJList.this.getGraphics() is null
                cellPreferredSizeGraphics = java.awt.GraphicsEnvironment.
                        getLocalGraphicsEnvironment().getDefaultScreenDevice().
                        getDefaultConfiguration().createCompatibleImage(1, 1).getGraphics();
                assert (cellPreferredSizeGraphics != null);
            }
            return new Dimension(item.getPreferredWidth(cellPreferredSizeGraphics, CompletionJList.this.getFont()),
                    fixedItemHeight);
        }
    }

    public static int arrowSpan() {
        return SUB_MENU_ICON_GAP + subMenuIcon.getIconWidth() + SUB_MENU_ICON_GAP;
    }
}
