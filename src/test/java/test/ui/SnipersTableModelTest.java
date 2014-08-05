package test.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.UserRequestListener.Item;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;

@RunWith(MockitoJUnitRunner.class)
public class SnipersTableModelTest {
	 private static final String ITEM_ID = "item 0";
	@Mock
	TableModelListener listener;

	private SnipersTableModel model = new SnipersTableModel();
	private final AuctionSniper sniper = new AuctionSniper(null, new Item(ITEM_ID, 234)); 
	
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
		SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);

		model.sniperAdded(sniper);
		model.sniperStateChanged(bidding);

		verify(listener).tableChanged(argThat(anyInsertionEvent()));
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
		assertEquals(0, model.getRowCount());

		model.sniperAdded(sniper);
		verify(listener).tableChanged(refEq(insertRowAtEvent(0)));

		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));

	}

	private TableModelEvent insertRowAtEvent(int row) {
		return new TableModelEvent(model, row, row,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
	}


	Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
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

}
