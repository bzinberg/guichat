package server;

import static org.junit.Assert.*;
import org.junit.Test;

public class ConversationTest {
	
	@Test
	public void noUserConstructorTest() {
		Conversation conv = new Conversation("conv");
		assertEquals("conv", conv.toString());
	}

}
