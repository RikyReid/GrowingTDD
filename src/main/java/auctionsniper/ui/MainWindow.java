package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import auctionsniper.SniperSnapshot;

public class MainWindow extends JFrame {
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";
//	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
	public static final String APPLICATION_TITLE = "Auction Sniper";

	private final SnipersTableModel snipers;

	public MainWindow(SnipersTableModel snipers) {
		super("Auction Sniper");
		this.snipers = snipers;
		setName(APPLICATION_TITLE);
		fillContentPanel(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void fillContentPanel(JTable snipersTable) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}
	
	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
			
	public void sniperStatusChanged(SniperSnapshot snapshot) {
		snipers.sniperStateChanged(snapshot);
	}
}
