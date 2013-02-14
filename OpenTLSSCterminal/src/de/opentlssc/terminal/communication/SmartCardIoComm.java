package de.opentlssc.terminal.communication;

import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import de.opentlssc.terminal.utils.Util;

/**
 * Communicate using the native java pcsc stack called SmartCardIO
 * 
 * @author Martin Boonk
 *
 */
public class SmartCardIoComm implements Communicator {
	private Card card;
	private CardChannel channel;
	private boolean isConnected = false;
	
	public SmartCardIoComm() {
		TerminalFactory factory = TerminalFactory.getDefault();

		List<CardTerminal> terminals;
		try {
			terminals = factory.terminals().list();
			System.out.println("Found Terminals: " + terminals);
				for (int i = 0; i < terminals.size(); i++){
					try {
						card = terminals.get(i).connect("*");
						getChannel();
						break;
					} catch (CardException e){
						System.out.println("Reader " + i + " could not connect.");
					}
				}
		} catch (CardException e) {
			System.out.println(e.getMessage());
		}
	}

	private void getChannel(){
		System.out.println("Card: " + card);
		ATR atr = card.getATR();
		System.out.println("ATR: "
				+ Util.byteArrayToString(atr.getBytes()));
		channel = card.getBasicChannel();
		isConnected = true;
	}
	
	public void disconnect() {
		if (isConnected) {
			try {
				card.disconnect(true);
			} catch (CardException e) {
				System.out.println(e.getMessage());
			}
			isConnected = false;
		}

	}

	public ResponseAPDU sendApdu(CommandAPDU apdu) {
		if (isConnected) {
			try {
				return channel.transmit(apdu);
			} catch (CardException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isConnected() {
		return isConnected;
	}

}
