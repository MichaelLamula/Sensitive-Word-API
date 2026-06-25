package Sensitive.Words.sensitve_words;

import dto.WordRequest;
import dto.WordResponse;
import entity.SensitiveWord;
import exceptions.DuplicateWordException;
import exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.SensitiveWordRepository;
import service.SensitiveWordService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensitiveWordServiceTest {

    @Mock
    private SensitiveWordRepository repository;

    @InjectMocks
    private SensitiveWordService service;

    @BeforeEach
    void setUp() {
    }

    private void setupMockWords() {
        List<SensitiveWord> mockWords = List.of(
                new SensitiveWord("SELECT"),
                new SensitiveWord("DROP")
        );
        when(repository.findAll()).thenReturn(mockWords);
        service.refreshPatternCache();
    }

    @Test
    void testSanitizeMessage_withSensitiveWords() {
        setupMockWords();
        String input = "SELECT * FROM users; DROP TABLE users;";
        String expected = "****** * FROM users; **** TABLE users;";

        String result = service.sanitizeMessage(input);

        assertEquals(expected, result);
    }

    @Test
    void testSanitizeMessage_caseInsensitive() {
        setupMockWords();
        String input = "Select * from users; drop table;";
        String expected = "****** * from users; **** table;";

        assertEquals(expected, service.sanitizeMessage(input));
    }

    @Test
    void testSanitizeMessage_protectsPartialMatches() {
        setupMockWords();
        String input = "Water DROPLET";
        String expected = "Water DROPLET";

        assertEquals(expected, service.sanitizeMessage(input));
    }

    @Test
    void testAddWord() {
        WordRequest request = new WordRequest("test");
        SensitiveWord savedWord = new SensitiveWord("test");
        savedWord.setId(1L);
        when(repository.existsByWordIgnoreCase("test")).thenReturn(false);
        when(repository.save(any(SensitiveWord.class))).thenReturn(savedWord);
        when(repository.findAll()).thenReturn(List.of(savedWord)); // For refreshPatternCache

        WordResponse response = service.addWord(request);

        assertEquals(1L, response.id());
        assertEquals("test", response.word());
        verify(repository, times(1)).save(any(SensitiveWord.class));
    }

    @Test
    void testAddWord_Duplicate() {
        WordRequest request = new WordRequest("test");
        when(repository.existsByWordIgnoreCase("test")).thenReturn(true);

        assertThrows(DuplicateWordException.class, () -> service.addWord(request));
    }

    @Test
    void testGetAllWords() {
        setupMockWords();
        List<WordResponse> words = service.getAllWords();
        assertEquals(2, words.size());
    }

    @Test
    void testGetWordById() {
        SensitiveWord word = new SensitiveWord("test");
        word.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(word));

        WordResponse response = service.getWordById(1L);

        assertEquals(1L, response.id());
        assertEquals("test", response.word());
    }

    @Test
    void testGetWordById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getWordById(1L));
    }

    @Test
    void testUpdateWord() {
        WordRequest request = new WordRequest("updated");
        SensitiveWord existingWord = new SensitiveWord("test");
        existingWord.setId(1L);
        SensitiveWord updatedWord = new SensitiveWord("updated");
        updatedWord.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existingWord));
        when(repository.existsByWordIgnoreCase("updated")).thenReturn(false);
        when(repository.save(any(SensitiveWord.class))).thenReturn(updatedWord);
        when(repository.findAll()).thenReturn(List.of(updatedWord)); // For refreshPatternCache

        WordResponse response = service.updateWord(1L, request);

        assertEquals(1L, response.id());
        assertEquals("updated", response.word());
    }

    @Test
    void testUpdateWord_NotFound() {
        WordRequest request = new WordRequest("updated");
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateWord(1L, request));
    }

    @Test
    void testUpdateWord_Duplicate() {
        WordRequest request = new WordRequest("DROP");
        SensitiveWord existingWord = new SensitiveWord("test");
        existingWord.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existingWord));
        when(repository.existsByWordIgnoreCase("DROP")).thenReturn(true);

        // This was the fix. The service correctly throws DuplicateWordException.
        assertThrows(DuplicateWordException.class, () -> service.updateWord(1L, request));
    }

    @Test
    void testDeleteWord() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findAll()).thenReturn(List.of()); // For refreshPatternCache
        service.deleteWord(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteWord_NotFound() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteWord(1L));
    }

    @Test
    void refreshPatternCache_WithEmptyWords() {
        when(repository.findAll()).thenReturn(List.of());
        service.refreshPatternCache();
        String result = service.sanitizeMessage("should not change");
        assertEquals("should not change", result);
    }
}
