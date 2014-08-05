package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import util.Announcer;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.UserRequestListener.Item;

public class MainWindow extends JFrame {
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";
	// public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "item id";
	public static final String JOIN_BUTTON_NAME = "join button";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";

	// private final SnipersTableModel snipers;

	private final Announcer<UserRequestListener> userRequests = Announcer
			.to(UserRequestListener.class);

	public MainWindow(SniperPortfolio portfolio) {
		super("Auction Sniper");
		// this.snipers = snipers;
		setName(APPLICATION_TITLE);
		fillContentPanel(makeSnipersTable(portfolio), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void addUserRequestListener(UserRequestListener userRequestListener) {
		userRequests.addListener(userRequestListener);
	}

	private void fillContentPanel(JTable snipersTable, JPanel controls) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(controls, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable(SniperPortfolio portfolio) {
		SnipersTableModel model = new SnipersTableModel();
		portfolio.addPortfolioListener(model);
		final JTable snipersTable = new JTable(model);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	private JPanel makeControls() {
		final JTextField itemIdField = itemIdField();
		final JFormattedTextField stopPriceField = stopPriceField();

		JPanel controls = new JPanel(new FlowLayout());
		controls.add(itemIdField);
		controls.add(stopPriceField);

		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);

		joinAuctionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(
						new Item(itemId(), stopPrice()));
			}

			private String itemId() {
				return itemIdField.getText();
			}

			private int stopPrice() {
				return ((Number) stopPriceField.getValue()).intValue();
			}
		});

		controls.add(joinAuctionButton);

		return controls;
	}

	private JTextField itemIdField() {
		JTextField itemIdField = new JTextField();
		itemIdField.setColumns(10);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		return itemIdField;
	}

	private JFormattedTextField stopPriceField() {
		JFormattedTextField stopPriceField = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		stopPriceField.setColumns(7);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		return stopPriceField;
	}

	// public void sniperStatusChanged(SniperSnapshot snapshot) {
	// snipers.sniperStateChanged(snapshot);
	// }
}
