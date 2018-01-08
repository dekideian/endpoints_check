package hello.domain;

public class Greeting {
	
	private final long id;
	private final String greeting;
	
	public Greeting(long id, String greeting) {
		super();
		this.id = id;
		this.greeting = greeting;
	}

	public long getId() {
		return id;
	}

	public String getGreeting() {
		return greeting;
	}
	
}
