package chatbot;
import java.util.List;

import org.jivesoftware.smack.XMPPException;

import api.JabberSmackAPI;
import configuration.Conf;
import events.DisconnectInstruction;
import events.Event;
import events.GroupMessageReceived;


public class Chatbot {
	
	JabberSmackAPI api;
	SimpleInteractor simpInt;
	
	static final int MAX_SLEEP_TIME = 1000;
	static final int INCR_SLEEP_TIME = 50;
	
	public void initialise() {
		api = new JabberSmackAPI();
		simpInt = new SimpleInteractor();
		try {
			api.login(Conf.USERNAME, Conf.PASSWORD);
		} catch (XMPPException e) {
			System.out.println("Error in initialising chatbot...\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void processEvents() {
		List<Event> events;
		Event event;
		int sleepTime = 0;
		
		while(true) {
			events = api.getEvents();
			
			if(events.isEmpty()) {
				if(sleepTime < MAX_SLEEP_TIME) sleepTime += INCR_SLEEP_TIME;
				try {
					Thread.sleep(sleepTime);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				sleepTime = 0;
			}
			
			while(!events.isEmpty()) {
				event = events.remove(0);
				switch(event.getEventType()) {
				
				case GRP_MESSAGE_RCV:
					GroupMessageReceived message = (GroupMessageReceived) event;
					processMessage(message.getSender(), message.getRoomName(),
							message.getBody());
					break;
					
				case DISCONNECT_INSTR:
					api.disconnect();
					System.exit(0);
					
				default:
					System.out.println("Unrecognised event type: " +
						event.getEventType());
				}
			}
		}
	}
	
	private void processMessage(String sender, String roomName, String body) {
		String response = null;
		if(sender.equals(Conf.NICKNAME)) return;
		
		System.out.println(roomName + "/" + sender + ": " + body);
		
		// Respond to greetings.
		if(simpInt.checkGreeting(body) && simpInt.checkIdentity(body)) {
			response = simpInt.retGreeting(true) +
					simpInt.respondWithChance(" " + sender, 0.7) + " " +
					simpInt.retSmileEmote(0.5);
		
		// Respond to goodbyes.
		} else if(simpInt.checkGoodbye(body) && simpInt.checkIdentity(body)) {
			response = simpInt.retGoodbye(true) +
					simpInt.respondWithChance(" " + sender, 0.7) + " " +
					simpInt.retSmileEmote(0.5);
		
		// Respond to mood/wellbeing inquiries.
		} else if(simpInt.checkMoodInquiry(body)
				&& simpInt.checkIdentity(body)) {
			response = simpInt.retMood(true) + " " +
					simpInt.retThanks(false) +
					simpInt.respondWithChance(" " + sender, 0.8) + " " +
					simpInt.retSmileEmote(0.7);
		
		// Check for instruction to disconnect (currently from anybody).
		} else if(simpInt.checkPhraseInMessage("disconnect", body)
				&& simpInt.checkIdentity(body)) {
		
			Event disconnectInstr = new DisconnectInstruction();
			api.addEvent(disconnectInstr);
			response = "Disconnecting...";
		
		// Check if person is referencing this chatbot.
		} else if(simpInt.checkIdentity(body)) {
			response = simpInt.retInquiry(true);
			
		} else {
			simpInt.setIdentityCheck(false);
			
		}
		
		if(response != null) sendRoomMessage(response, roomName);
	}

	public void joinRoom(String roomName) {
		try {
			api.joinRoom(roomName);
			String message = "Hey guys. I'm running for test purposes. If I " +
					"become a nuisance, just get my attention and say " +
					"'disconnect' to make me leave the chat " +
					simpInt.retSmileEmote(1);
			
			sendRoomMessage(message, roomName);
			System.out.println("Connected to " + roomName);
		} catch (XMPPException e) {
			System.out.println("Error in connecting chatbot to room...\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void sendRoomMessage(String message, String roomName) {
		try {
			api.sendRoomMessage(message, roomName);
		} catch (XMPPException e) {
			System.out.println("Error posting message in " + roomName);
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Chatbot mofichan = new Chatbot();
		
		mofichan.initialise();
		System.out.println("Mofichan connected");
		
		for(String roomName : Conf.ROOMS) {
			mofichan.joinRoom(roomName);
		}
		
		// While loop.
		mofichan.processEvents();
	}
}
