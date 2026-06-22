package Sensitive.Words.sensitve_words;

import entity.SensitiveWord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.SensitiveWordRepository;
import service.SensitiveWordService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensitiveWordServiceTest {

    @Mock
    private SensitiveWordRepository repository;

    @InjectMocks
    private SensitiveWordService service;

    @BeforeEach
    void setUp() {
        // Mock DB returning SQL keywords as requested
        List<SensitiveWord> mockWords = List.of(
                new SensitiveWord("SELECT"),
                new SensitiveWord("DROP")
        );
        when(repository.findAll()).thenReturn(mockWords);
        service.refreshPatternCache();
    }

    @Test
    void testSanitizeMessage_withSensitiveWords() {
        String input = "SELECT * FROM users; DROP TABLE users;";
        String expected = "****** * FROM users; **** TABLE users;";

        String result = service.sanitizeMessage(input);

        assertEquals(expected, result);
    }

    @Test
    void testSanitizeMessage_caseInsensitive() {
        String input = "Select * from users; drop table;";
        String expected = "****** * from users; **** table;";

        assertEquals(expected, service.sanitizeMessage(input));
    }

    @Test
    void testSanitizeMessage_protectsPartialMatches() {
        // The word "DROPLET" should not be censored just because it contains "DROP"
        String input = "Water DROPLET";
        String expected = "Water DROPLET"; // Boundaries (\b) prevent this

        assertEquals(expected, service.sanitizeMessage(input));
    }
}
