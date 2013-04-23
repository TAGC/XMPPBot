package events;

public class DisconnectInstruction implements Event {

	@Override
	public EventType getEventType() {
		return EventType.DISCONNECT_INSTR;
	}

}
