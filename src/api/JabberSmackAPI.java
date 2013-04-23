package api;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import configuration.Conf;

import events.Event;
import events.GroupMessageReceived;


public class JabberSmackAPI implements MessageListener {
	
	XMPPConnection connection;
	Map<String, MultiUserChat> chats;
	List<Event> events;
	
	private PacketListener getMUCMessageListener() {
		
		PacketListener MUCMessageListener = new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
				if(message.getBody() != null) {
					String sender = message.getFrom().substring(
							message.getFrom().indexOf('/')+1);
					String roomName = message.getFrom().substring(0,
							message.getFrom().indexOf('@'));
					
					events.add(new GroupMessageReceived(sender, roomName,
							message.getBody()));
				}			
			}
		};
		
		return MUCMessageListener;
	}
	
	public JabberSmackAPI() {
		chats = new HashMap<String, MultiUserChat>();
		events = new LinkedList<Event>();
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	public void addEvent(Event event) {
		events.add(event);
	}

	public void login(String username, String password) throws XMPPException {
		ConnectionConfiguration config = new ConnectionConfiguration(
				Conf.SERVER, 5222);
		
		connection = new XMPPConnection(config);
		connection.connect();
		connection.login(username, password);
	}
	
	public void sendMessage(String message, String recipient)
			throws XMPPException {
		Chat chat = connection.getChatManager().createChat(recipient, this);
		chat.sendMessage(message);
	}
	
	public void displayBuddyList() {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for(RosterEntry r : entries) {
			System.out.println(r.getUser());
		}
	}
	
	public void disconnect() {
		connection.disconnect();
	}
	
	@Override
	public void processMessage(Chat chat, Message message) {
		System.out.println("MESSAGE RECEIVED");
		if(message.getType() == Message.Type.chat) {
			System.out.println(chat.getParticipant() + " says: "
					+ message.getBody());
		}
	}
	
	public boolean joinRoom(String roomName) throws XMPPException {
		if(chats.containsKey(roomName)) {
			return false;
		} else {
			MultiUserChat chat = new MultiUserChat(connection, roomName +
					"@conference." + Conf.SERVER);
			DiscussionHistory history = new DiscussionHistory();
			history.setSeconds(0);
			chat.join(Conf.NICKNAME, null, history, Integer.MAX_VALUE);
			chat.addMessageListener(getMUCMessageListener());
			chats.put(roomName, chat);
			return true;
		}
	}
	
	public boolean leaveRoom(String roomName) {
		if(chats.containsKey(roomName)) {
			MultiUserChat chat = chats.get(roomName);
			chat.leave();
			return true;
		} else {
			return false;
		}
	}
	
	public void sendRoomMessage(String message, String roomName)
			throws XMPPException {
		MultiUserChat chat;
		
		if(!chats.containsKey(roomName)) {
			joinRoom(roomName);
		}
		
		chat = chats.get(roomName);
		chat.sendMessage(message);
	}
}
