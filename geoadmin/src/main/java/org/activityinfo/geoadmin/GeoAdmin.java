package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.ws.rs.core.MediaType;

import org.activityinfo.geoadmin.model.Country;
import org.activityinfo.geoadmin.tree.CountryTreeListener;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class GeoAdmin extends JFrame {

	private GeoClient client = new GeoClient();
	
	public GeoAdmin() {
		setTitle("ActivityInfo Geo Administrator");
		setSize(540, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initMenu();
		createTree();

	}

	private void initMenu() {

		JMenuBar menubar = new JMenuBar();

		JMenu importMenu = new JMenu("Import");

		JMenuItem importLevel = new JMenuItem("Import new Admin Level");
		importLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				chooseFile();
			}

		});

		importMenu.add(importLevel);

		menubar.add(importMenu);

		setJMenuBar(menubar);
	}

	private void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Shapefiles", ".shp"));
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
		}
	}

	private void createTree() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Countries");

		for(Country country : client.getCountries()) {
			DefaultMutableTreeNode countryNode = new DefaultMutableTreeNode(country);
			countryNode.add(new DefaultMutableTreeNode("Loading..."));
			node.add(countryNode);
		}
		
		JTree tree = new JTree(node);
		tree.addTreeWillExpandListener(new CountryTreeListener(client));
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.i)
			}
			
		});
		getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GeoAdmin ex = new GeoAdmin();
				ex.setVisible(true);
			}
		});
	}
}