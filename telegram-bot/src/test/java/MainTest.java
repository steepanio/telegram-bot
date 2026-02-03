import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void loadToken() {
        String token = "abc";
        assertEquals("abc", token);
    }
}
