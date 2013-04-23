package events;

public class GroupMessageReceived implements Event {
	
	String sender, body, roomName;
	
	public GroupMessageReceived(String sender, String roomName, String body) {
		this.sender = sender;
		this.roomName = roomName;
		this.body = body;
	}
	
	@Override
	public EventType getEventType() {
		return EventType.GRP_MESSAGE_RCV;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public String getBody() {
		return body;
	}
}
