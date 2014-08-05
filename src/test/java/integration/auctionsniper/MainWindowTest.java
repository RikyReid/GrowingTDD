package integration.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.UserRequestListener.Item;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import endtoend.auctionsniper.AuctionSniperDriver;

public class MainWindowTest {
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	public void makeUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(
				equalTo(new Item("an item-id", 789)), "join request");
		
		mainWindow.addUserRequestListener(new UserRequestListener() {
			
			@Override
			public void joinAuction(Item item) {
				itemProbe.setReceivedValue(item);
				
			}
		});
		
		driver.startBiddingFor("an item-id", 789);
		driver.check(itemProbe);
	}
}
