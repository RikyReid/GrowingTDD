package auctionsniper.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import util.Defect;
import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperCollector;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.SniperPortfolio.PortfolioListener;

public class SnipersTableModel extends AbstractTableModel implements
		SniperListener, PortfolioListener {
	private final static String[] STATUS_TEXT = { "Joining", "Bidding",
			"Winning", "Lost", "Losing", "Won" };
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0,
			0, SniperState.JOINING);

	private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
	private Collection<AuctionSniper> notToBEGCd = new ArrayList<>();

	@Override
	public int getRowCount() {
		return snapshots.size();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	@Override
	public void sniperStateChanged(SniperSnapshot newSnapshot) {
		int row = rowMatching(newSnapshot);
		snapshots.set(row, newSnapshot);
		fireTableRowsUpdated(row, row);
	}

	private int rowMatching(SniperSnapshot snapshot) {
		for (int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}

		throw new Defect("can not find match for " + snapshot);
	}

	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

	private void addSniperSnapshot(SniperSnapshot newSniper) {
		snapshots.add(newSniper);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
	}

	@Override
	public void sniperAdded(AuctionSniper sniper) {
		notToBEGCd.add(sniper);
		addSniperSnapshot(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}

}
