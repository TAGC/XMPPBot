package chatbot;

import java.util.Random;

import configuration.Conf;

public class SimpleInteractor {
	
	final String[] greetings = new String[]{"hey", "hi", "hello"};
	final String[] goodbyes = new String[]{"bye", "bai", "goodbye",
			"see you later"};
	final String[] identities = Conf.IDENTITIES;
	final String[] smileEmotes = new String[]{"=3", "=]", "=)", "=P"};
	final String[] inquiries = new String[]{"hai? o.o", "yush? o.o", "hm?",
			"yes? o.o"};
	final String[] moodInquiries = new String[]{"how are you", "what's up"};
	final String[] moods = new String[]{"i'm good", "i'm great", "i'm well",
			"i'm pretty well", "i'm alright"};
	final String[] thanks = new String[]{"thanks", "ty"};
	
	final Random random;
	boolean identityCheck;
	
	public SimpleInteractor() {
		random = new Random();
		identityCheck = false;
	}
	
	public String[] parseMessage(String message) {
		String regex = "(!|\\?)";
		return message.toLowerCase().replaceAll(regex, "").split(" ");
	}
	
	private <T> T getRandomElement(T[] list) {
		return list[random.nextInt(list.length)];
	}
	
	private boolean checkMatch(String[] message, String[] list) {
		String[] parsedElem;
		
		for(String listElem : list) {
			parsedElem = parseMessage(listElem);
			
			for(int i=0; i <= message.length-parsedElem.length; i++) {
				for(int j=0; j < parsedElem.length; j++) {
					if(!message[i+j].equals(parsedElem[j])) break;
					if(j == parsedElem.length - 1) return true;
				}
			}
		}
		
		return false;
	}
	
	private String capitalise(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
	public boolean checkPhraseInMessage(String phrase, String message) {
		String[] parsedPhrase, parsedMessage;
		
		parsedPhrase = parseMessage(phrase);
		parsedMessage = parseMessage(message);
		
		for(int i=0; i <= parsedMessage.length - parsedPhrase.length; i++) {
			for(int j=0; j < parsedPhrase.length; j++) {
				if(!parsedMessage[i+j].equals(parsedPhrase[j])) break;
				if(j == parsedPhrase.length - 1) return true;
			}
		}
		
		return false;
	}
	
	public void setIdentityCheck(boolean check) {
		identityCheck = check;
	}
	
	public boolean getIdentityCheck() {
		return identityCheck;
	}
	
	public String respondWithChance(String line, double probability) {
		assert(0 <= probability && probability <= 1);
		return (random.nextDouble() > probability ? "" : line);
	}
	
	public boolean checkIdentity(String message) {
		boolean match = checkMatch(parseMessage(message), identities);
		
		if(getIdentityCheck()) {
			setIdentityCheck(match);
			return true;
		} else if(match) {
			setIdentityCheck(true);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkGreeting(String message) {
		return checkMatch(parseMessage(message), greetings);
	}
	
	public boolean checkGoodbye(String message) {
		return checkMatch(parseMessage(message), goodbyes);
	}
	
	public boolean checkMoodInquiry(String message) {
		return checkMatch(parseMessage(message), moodInquiries);
	}
	
	public String retGreeting(boolean capitalise) {
		String response = getRandomElement(greetings);
		return (capitalise ? capitalise(response) : response);
	}
	
	public String retGoodbye(boolean capitalise) {
		String response = getRandomElement(goodbyes);
		return (capitalise ? capitalise(response) : response);
	}
	
	public String retSmileEmote(double probability) {
		return respondWithChance(getRandomElement(smileEmotes), probability);
	}
	
	public String retInquiry(boolean capitalise) {
		String response = getRandomElement(inquiries);
		return (capitalise ? capitalise(response) : response);
	}
	
	public String retMood(boolean capitalise) {
		String response = getRandomElement(moods);
		return (capitalise ? capitalise(response) : response);
	}
	
	public String retThanks(boolean capitalise) {
		String response = getRandomElement(thanks);
		return (capitalise ? capitalise(response) : response);	
	}
}
