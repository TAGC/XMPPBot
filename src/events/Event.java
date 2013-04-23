package events;

public interface Event {
	public enum EventType {
		GRP_MESSAGE_RCV,
		DISCONNECT_INSTR,
		ROOM_JOIN_INSTR;
	}
	
	public EventType getEventType();
}
