package events;

public class RoomJoinInstruction implements Event {
	
	String newRoom, currRoom;
	
	public RoomJoinInstruction(String newRoom, String currRoom) {
		this.newRoom = newRoom;
		this.currRoom = currRoom;
	}
	
	@Override
	public EventType getEventType() {
		return EventType.ROOM_JOIN_INSTR;
	}
	
	public String getNewRoom() {
		return newRoom;
	}
	
	public String getCurrRoom() {
		return currRoom;
	}
}
