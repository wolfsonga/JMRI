// PocketTesterMenu.java

package jmri.jmrix.pricom.pockettester;

import java.util.ResourceBundle;

import javax.swing.JMenu;

/**
 * Create a menu or menu item containing the PRICOM Pocket Tester tools
 *
 * @author	Bob Jacobsen   Copyright 2005
 * @version     $Revision: 1.1 $
 */
public class PocketTesterMenu extends JMenu {
    public PocketTesterMenu(String name) {
        this();
        setText(name);
    }

    public PocketTesterMenu() {

        super();

        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.pricom.pockettester.TesterBundle");

        setText(rb.getString("MenuPocketTester"));

        add(new jmri.jmrix.pricom.pockettester.DataSourceAction());
        
        // rest need to be added dynamically
        
        // add(new jmri.jmrix.pricom.pockettester.MonitorAction());
        // add(new jmri.jmrix.pricom.pockettester.PacketTableAction());

    }

}


