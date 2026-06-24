package service;

import dto.WordRequest;
import dto.WordResponse;
import entity.SensitiveWord;
import exceptions.DuplicateWordException;
import exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import repository.SensitiveWordRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SensitiveWordService {
    private final SensitiveWordRepository repository;
    private Pattern compiledPattern;

    public SensitiveWordService(SensitiveWordRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void refreshPatternCache() {
        List<String> words = repository.findAll().stream()
                .map(SensitiveWord::getWord)
                .map(Pattern::quote)
                .toList();

        if (words.isEmpty()) {
            compiledPattern = null;
        } else {
            String regex = "(?i)\\b(" + String.join("|", words) + ")\\b";
            compiledPattern = Pattern.compile(regex);
        }
    }

    @Transactional
    public WordResponse addWord(@Valid WordRequest request) {
        if (repository.existsByWordIgnoreCase(request.word())) {
            throw new DuplicateWordException("Word exists");
        }
        SensitiveWord saved = repository.save(new SensitiveWord(request.word()));
        refreshPatternCache();
        return new WordResponse(saved.getId(), saved.getWord());
    }

    public List<WordResponse> getAllWords() {
        return repository.findAll().stream()
                .map(w -> new WordResponse(w.getId(), w.getWord()))
                .toList();
    }

    public WordResponse getWordById(Long id) {
        SensitiveWord word = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Word not found with ID: " + id));
        return new WordResponse(word.getId(), word.getWord());
    }

    @Transactional
    public WordResponse updateWord(Long id, @Valid WordRequest request) {
        SensitiveWord existingWord = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Word not found with ID: " + id));

        if (!existingWord.getWord().equalsIgnoreCase(request.word()) &&
                repository.existsByWordIgnoreCase(request.word())) {
            throw new DuplicateWordException("Conflict: The word '" + request.word() + "' already exists.");
        }

        existingWord.setWord(request.word());
        SensitiveWord updated = repository.save(existingWord);

        refreshPatternCache();

        return new WordResponse(updated.getId(), updated.getWord());
    }

    @Transactional
    public void deleteWord(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Word not found");
        repository.deleteById(id);
        refreshPatternCache();
    }

    public String sanitizeMessage(String message) {
        if (message == null || message.isBlank() || compiledPattern == null) {
            return message;
        }

        Matcher matcher = compiledPattern.matcher(message);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(result, "*".repeat(matcher.group().length()));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
