package test.ui;

import static auctionsniper.ui.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.argThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;

@RunWith(MockitoJUnitRunner.class)
public class SnipersTableModelTest {
	@Mock
	TableModelListener listener;

	private SnipersTableModel model = new SnipersTableModel();

	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}

	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}

	@Test
	public void setsSniperValuesInColumns() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		SniperSnapshot bidding = joining.bidding(555, 666);

		model.addSniperSnapshot(joining);
		model.sniperStateChanged(bidding);

		verify(listener).tableChanged(argThat(anyInsertionEvent2()));
		verify(listener).tableChanged(argThat(aChangeInRow(0)));

		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	public void setsUpColumnHeadings() {
		for (Column column : Column.values()) {
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}

	@Test
	public void notifiesListenerWhenAddingASniper() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		assertEquals(0, model.getRowCount());

		model.addSniperSnapshot(joining);
		verify(listener).tableChanged(refEq(insertRowAtEvent(0)));

		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);

	}

	private TableModelEvent insertRowAtEvent(int row) {
		return new TableModelEvent(model, row, row,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
	}

	private TableModelEvent anyInsertEvent(int row) {
		return new TableModelEvent(model, row, row,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
	}

	Matcher<TableModelEvent> anyInsertionEvent2() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
	}

	private void assertColumnEquals(Column column, Object expected) {
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}

	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
		assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
		assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
		assertEquals(SnipersTableModel.textFor(snapshot.state),
				cellValue(row, Column.SNIPER_STATE));
	}

	private Object cellValue(int rowIndex, Column column) {
		return model.getValueAt(rowIndex, column.ordinal());
	}

	// Matcher<TableModelEvent> anInsertionAtRow(final int row) {
	// return samePropertyValuesAs(new TableModelEvent(model, row, row,
	// TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	// }
}
