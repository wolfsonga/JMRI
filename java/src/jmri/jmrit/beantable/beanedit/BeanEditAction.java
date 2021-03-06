package jmri.jmrit.beantable.beanedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import jmri.NamedBean;
import jmri.util.JmriJFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the basic information and structure for for a editing the details of
 * a bean object
 *
 * @author	Kevin Dickerson Copyright (C) 2011
 */
abstract class BeanEditAction extends AbstractAction {

    public BeanEditAction(String s) {
        super(s);
    }

    public BeanEditAction() {
        super("Bean Edit");
    }

    jmri.NamedBean bean;

    public void setBean(jmri.NamedBean bean) {
        this.bean = bean;
    }

    /**
     * Call to create all the different tabs that will be added to the frame
     */
    protected void initPanels() {
        basicDetails();
    }

    protected void initPanelsFirst() {

    }

    protected void initPanelsLast() {
        usageDetails();
        propertiesDetails();
    }

    JTextField userNameField = new JTextField(20);
    JTextArea commentField = new JTextArea(3, 30);
    JScrollPane commentFieldScroller = new JScrollPane(commentField);

    /**
     * Creates a generic panel that holds the basic bean information System
     * Name, User Name and Comment
     */
    BeanItemPanel basicDetails() {
        BeanItemPanel basic = new BeanItemPanel();

        basic.setName(Bundle.getMessage("Basic"));
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));

        basic.addItem(new BeanEditItem(new JLabel(bean.getSystemName()), Bundle.getMessage("ColumnSystemName"), null));

        basic.addItem(new BeanEditItem(userNameField, Bundle.getMessage("ColumnUserName"), null));

        basic.addItem(new BeanEditItem(commentFieldScroller, Bundle.getMessage("ColumnComment"), null));

        basic.setSaveItem(new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = -1823311798750191527L;

            public void actionPerformed(ActionEvent e) {
                saveBasicItems(e);
            }
        });
        basic.setResetItem(new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = 2590436299984618901L;

            public void actionPerformed(ActionEvent e) {
                resetBasicItems(e);
            }
        });
        bei.add(basic);
        return basic;
    }

    BeanItemPanel usageDetails() {
        BeanItemPanel usage = new BeanItemPanel();

        usage.setName(Bundle.getMessage("Usage"));
        usage.setLayout(new BoxLayout(usage, BoxLayout.Y_AXIS));

        usage.addItem(new BeanEditItem(null, null, Bundle.getMessage("UsageText", bean.getDisplayName())));

        ArrayList<String> listeners = new ArrayList<String>();
        for (String ref : bean.getListenerRefs()) {
            if (!listeners.contains(ref)) {
                listeners.add(ref);
            }
        }

        Object[] strArray = new Object[listeners.size()];
        listeners.toArray(strArray);
        JList<Object> list = new JList<Object>(strArray);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black)));
        usage.addItem(new BeanEditItem(listScroller, Bundle.getMessage("ColumnLocation"), null));

        bei.add(usage);
        return usage;
    }
    BeanPropertiesTableModel propertiesModel;

    BeanItemPanel propertiesDetails() {
        BeanItemPanel properties = new BeanItemPanel();
        properties.setName(Bundle.getMessage("Properties"));
        properties.addItem(new BeanEditItem(null, null, Bundle.getMessage("NamedBeanPropertiesTableDescription")));
        properties.setLayout(new BoxLayout(properties, BoxLayout.Y_AXIS));
        propertiesModel = new BeanPropertiesTableModel();
        JTable jtAttributes = new JTable();
        jtAttributes.setModel(propertiesModel);
        JScrollPane jsp = new JScrollPane(jtAttributes);
        Dimension tableDim = new Dimension(400, 200);
        jsp.setMinimumSize(tableDim);
        jsp.setMaximumSize(tableDim);
        jsp.setPreferredSize(tableDim);
        properties.addItem(new BeanEditItem(jsp, "", null));
        properties.setSaveItem(new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = -5627203723098157467L;

            public void actionPerformed(ActionEvent e) {
                propertiesModel.updateModel(bean);
            }
        });
        properties.setResetItem(new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = -956489116413677732L;

            public void actionPerformed(ActionEvent e) {
                propertiesModel.setModel(bean);
            }
        });

        bei.add(properties);
        return properties;
    }

    protected void saveBasicItems(ActionEvent e) {
        String uname = bean.getUserName();
        if (uname == null && !userNameField.getText().equals("")) {
            renameBean(userNameField.getText());
        } else if (uname != null && !uname.equals(userNameField.getText())) {
            if (userNameField.getText().equals("")) {
                removeName();
            } else {
                renameBean(userNameField.getText());
            }
        }
        bean.setComment(commentField.getText());
    }

    protected void resetBasicItems(ActionEvent e) {
        userNameField.setText(bean.getUserName());
        commentField.setText(bean.getComment());
    }

    abstract protected String helpTarget();

    protected ArrayList<BeanItemPanel> bei = new ArrayList<BeanItemPanel>(5);
    JmriJFrame f;

    protected Component selectedTab = null;
    private JTabbedPane detailsTab = new JTabbedPane();

    public void setSelectedComponent(Component c) {
        selectedTab = c;
    }

    public void actionPerformed(ActionEvent e) {
        if (bean == null) {
            log.error("No bean set so unable to edit a null bean");  //IN18N
            return;
        }
        if (f == null) {
            f = new JmriJFrame("Edit " + getBeanType() + " " + bean.getDisplayName(), false, false);
            f.addHelpMenu(helpTarget(), true);
            java.awt.Container containerPanel = f.getContentPane();
            initPanelsFirst();
            initPanels();
            initPanelsLast();

            for (BeanItemPanel bi : bei) {
                addToPanel(bi, bi.getListOfItems());
                detailsTab.addTab(bi.getName(), bi);
            }

            containerPanel.add(detailsTab, BorderLayout.CENTER);
            JPanel buttons = new JPanel();
            JButton applyBut = new JButton(Bundle.getMessage("ButtonApply"));
            applyBut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applyButtonAction(e);
                }
            });
            JButton okBut = new JButton(Bundle.getMessage("ButtonOK"));
            okBut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applyButtonAction(e);
                    f.dispose();
                }
            });
            JButton cancelBut = new JButton(Bundle.getMessage("ButtonCancel"));
            cancelBut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonAction(e);
                }
            });
            buttons.add(applyBut);
            buttons.add(okBut);
            buttons.add(cancelBut);
            containerPanel.add(buttons, BorderLayout.SOUTH);
        }
        for (BeanItemPanel bi : bei) {
            bi.resetField();
        }
        if (selectedTab != null) {
            detailsTab.setSelectedComponent(selectedTab);
        }
        f.pack();
        f.setVisible(true);
    }

    protected void applyButtonAction(ActionEvent e) {
        save();
    }

    protected void cancelButtonAction(ActionEvent e) {
        f.dispose();
    }

    /**
     * Sets out the panel based upon the items passed in via the ArrayList
     */
    protected void addToPanel(JPanel panel, List<BeanEditItem> items) {
        GridBagLayout gbLayout = new GridBagLayout();
        GridBagConstraints cL = new GridBagConstraints();
        GridBagConstraints cD = new GridBagConstraints();
        GridBagConstraints cR = new GridBagConstraints();
        cL.fill = GridBagConstraints.HORIZONTAL;
        cL.insets = new Insets(2, 0, 0, 15);
        cR.insets = new Insets(0, 10, 15, 15);
        cD.insets = new Insets(2, 0, 0, 0);
        cD.anchor = GridBagConstraints.NORTHWEST;
        cL.anchor = GridBagConstraints.NORTHWEST;

        int y = 0;
        JPanel p = new JPanel();

        for (BeanEditItem it : items) {
            if (it.getDescription() != null && it.getComponent() != null) {
                JLabel decript = new JLabel(it.getDescription() + ":", JLabel.LEFT);
                if (it.getDescription().equals("")) {
                    decript.setText("");
                }
                cL.gridx = 0;
                cL.gridy = y;
                cL.ipadx = 3;

                gbLayout.setConstraints(decript, cL);
                p.setLayout(gbLayout);
                p.add(decript, cL);

                cD.gridx = 1;
                cD.gridy = y;

                gbLayout.setConstraints(it.getComponent(), cD);

                p.add(it.getComponent(), cD);

                cR.gridx = 2;
                cR.gridwidth = 1;
                cR.anchor = GridBagConstraints.WEST;

            } else {
                cR.anchor = GridBagConstraints.CENTER;
                cR.gridx = 0;
                cR.gridwidth = 3;
            }
            cR.gridy = y;
            if (it.getHelp() != null) {
                JTextPane help = new JTextPane();
                help.setText(it.getHelp());
                gbLayout.setConstraints(help, cR);
                formatTextAreaAsLabel(help);
                p.add(help, cR);
            }
            y++;
        }

        panel.add(p);
    }

    void formatTextAreaAsLabel(JTextPane pane) {
        pane.setOpaque(false);
        pane.setEditable(false);
        pane.setBorder(null);
    }

    public void save() {
        for (BeanItemPanel bi : bei) {
            bi.saveItem();
        }
    }

    static boolean validateNumericalInput(String text) {
        if (text.length() != 0) {
            try {
                Integer.parseInt(text);
            } catch (java.lang.NumberFormatException ex) {
                return false;
            }
        }
        return true;
    }

    jmri.NamedBeanHandleManager nbMan = jmri.InstanceManager.getDefault(jmri.NamedBeanHandleManager.class);

    abstract protected String getBeanType();
    /*abstract protected NamedBean getBySystemName(String name);*/

    abstract protected NamedBean getByUserName(String name);

    /**
     * Generic method to change the user name of a Bean
     */
    public void renameBean(String _newName) {
        NamedBean nBean = bean;
        String oldName = nBean.getUserName();

        String value = _newName;

        if (value.equals(oldName)) {
            //name not changed.
            return;
        } else {
            NamedBean nB = getByUserName(value);
            if (nB != null) {
                log.error("User name is not unique " + value); // NOI18N
                String msg;
                msg = java.text.MessageFormat.format(Bundle.getMessage("WarningUserName"),
                        new Object[]{("" + value)});
                JOptionPane.showMessageDialog(null, msg,
                        Bundle.getMessage("WarningTitle"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        nBean.setUserName(value);
        if (!value.equals("")) {
            if (oldName == null || oldName.equals("")) {
                if (!nbMan.inUse(nBean.getSystemName(), nBean)) {
                    return;
                }
                String msg = java.text.MessageFormat.format(Bundle.getMessage("UpdateToUserName"),
                        new Object[]{getBeanType(), value, nBean.getSystemName()});
                int optionPane = JOptionPane.showConfirmDialog(null,
                        msg, Bundle.getMessage("UpdateToUserNameTitle"),
                        JOptionPane.YES_NO_OPTION);
                if (optionPane == JOptionPane.YES_OPTION) {
                    //This will update the bean reference from the systemName to the userName
                    try {
                        nbMan.updateBeanFromSystemToUser(nBean);
                    } catch (jmri.JmriException ex) {
                        //We should never get an exception here as we already check that the username is not valid
                    }
                }

            } else {
                nbMan.renameBean(oldName, value, nBean);
            }

        } else {
            //This will update the bean reference from the old userName to the SystemName
            nbMan.updateBeanFromUserToSystem(nBean);
        }
    }

    /**
     * Generic method to remove the user name from a bean.
     */
    public void removeName() {
        String msg = java.text.MessageFormat.format(Bundle.getMessage("UpdateToSystemName"),
                new Object[]{getBeanType()});
        int optionPane = JOptionPane.showConfirmDialog(null,
                msg, Bundle.getMessage("UpdateToSystemNameTitle"),
                JOptionPane.YES_NO_OPTION);
        if (optionPane == JOptionPane.YES_OPTION) {
            nbMan.updateBeanFromUserToSystem(bean);
        }
        bean.setUserName(null);
    }

    //At this stage we purely use this to allow the user to delete properties, but not add them, changing is possible but only for strings
    //Based upon the code from the RosterMediaPane
    private static class BeanPropertiesTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = -7466799526127205872L;
        Vector<KeyValueModel> attributes;
        String titles[];
        boolean wasModified;

        private static class KeyValueModel {

            public KeyValueModel(String k, Object v) {
                key = k;
                value = v;
            }
            public String key;
            public Object value;
        }

        public BeanPropertiesTableModel() {
            titles = new String[2];
            titles[0] = Bundle.getMessage("NamedBeanPropertyName");
            titles[1] = Bundle.getMessage("NamedBeanPropertyValue");
        }

        public void setModel(NamedBean nb) {
            attributes = new Vector<KeyValueModel>(nb.getPropertyKeys().size());
            Iterator<String> ite = nb.getPropertyKeys().iterator();
            while (ite.hasNext()) {
                String key = ite.next();
                KeyValueModel kv = new KeyValueModel(key, nb.getProperty(key));
                attributes.add(kv);
            }
            wasModified = false;
        }

        public void updateModel(NamedBean nb) {
            if (!wasModified()) {
                return; //No changed made
            }			// add and update keys
            for (int i = 0; i < attributes.size(); i++) {
                KeyValueModel kv = attributes.get(i);
                if ((kv.key != null) && // only update if key value defined, will do the remove to
                        ((nb.getProperty(kv.key) == null) || (!kv.value.equals(nb.getProperty(kv.key))))) {
                    nb.setProperty(kv.key, kv.value);
                }
            }
            //remove undefined keys

            Iterator<String> ite = nb.getPropertyKeys().iterator();
            while (ite.hasNext()) {
                if (!keyExist(ite.next())) // not very efficient algorithm!
                {
                    ite.remove();
                }
            }
            wasModified = false;
        }

        private boolean keyExist(Object k) {
            if (k == null) {
                return false;
            }
            for (int i = 0; i < attributes.size(); i++) {
                if (k.equals(attributes.get(i).key)) {
                    return true;
                }
            }
            return false;
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return attributes.size();
        }

        public String getColumnName(int col) {
            return titles[col];
        }

        public Object getValueAt(int row, int col) {
            if (row < attributes.size()) {
                if (col == 0) {
                    return attributes.get(row).key;
                }
                if (col == 1) {
                    return attributes.get(row).value;
                }
            }
            return "...";
        }

        public void setValueAt(Object value, int row, int col) {
            KeyValueModel kv;

            if (row < attributes.size()) // already exist?
            {
                kv = attributes.get(row);
            } else {
                kv = new KeyValueModel("", "");
            }

            if (col == 0) // update key
            //Force keys to be save as a single string with no spaces
            {
                if (!keyExist(((String) value).replaceAll("\\s", ""))) // if not exist
                {
                    kv.key = ((String) value).replaceAll("\\s", "");
                } else {
                    setValueAt(value + "-1", row, col); // else change key name
                    return;
                }
            }

            if (col == 1) // update value
            {
                kv.value = value;
            }
            if (row < attributes.size()) // existing one
            {
                attributes.set(row, kv);
            } else {
                attributes.add(row, kv); // new one
            }
            if ((col == 0) && (kv.key.equals(""))) {
                attributes.remove(row); // actually maybe remove
            }
            wasModified = true;
            fireTableCellUpdated(row, col);
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public boolean wasModified() {
            return wasModified;
        }
    }

    private final static Logger log = LoggerFactory.getLogger(BeanEditAction.class);
}
