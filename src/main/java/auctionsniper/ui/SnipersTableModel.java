package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.Main;
import auctionsniper.SniperSnapshot;

public class SnipersTableModel extends AbstractTableModel {
	private final static String[] STATUS_TEXT = { MainWindow.STATUS_JOINING,
			MainWindow.STATUS_BIDDING, MainWindow.STATUS_WINNING };
	private String statusText = Main.JOINING.toString();
	private SniperSnapshot snapshot = null;

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return snapshot.itemId;
		case LAST_PRICE:
			return snapshot.lastPrice;
		case LAST_BID:
			return snapshot.lastBid;
		case SNIPER_STATUS:
			return statusText;
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}

	public void setStatusText(String newStatusText) {
		statusText = newStatusText;
		fireTableRowsUpdated(0, 0);
	}

	public void sniperStatusChanged(SniperSnapshot newSnapshot,
			String newStatusText) {
		this.snapshot = newSnapshot;
		statusText = STATUS_TEXT[newSnapshot.state.ordinal()];
		fireTableRowsUpdated(0, 0);
	}
}
