package com.aluracursos.screenmatch.services;

import com.aluracursos.screenmatch.model.Author;
import com.aluracursos.screenmatch.model.Book;
import com.aluracursos.screenmatch.model.BookResponse;
import com.aluracursos.screenmatch.model.Person;
import com.aluracursos.screenmatch.repository.AuthorRepository;
import com.aluracursos.screenmatch.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class GutendexService {

    private static final String BASE_URL = "https://gutendex.com/";
    private final RestTemplate restTemplate;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;


    public GutendexService(RestTemplate restTemplate, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.restTemplate = restTemplate;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        String url = BASE_URL + "books";
        try {
            BookResponse response = restTemplate.getForObject(url, BookResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults();
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("No se puedo listar los libros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Book> searchAndSaveBooksByTitle(String title) {
        String url = BASE_URL + "books?search=" + title.replace(" ", "%20");
        BookResponse response = restTemplate.getForObject(url, BookResponse.class);

        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .map(this::saveBook)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Book saveBook(Book bookApi) {
        Author author = bookApi.getAuthors().stream()
                .map(this::findOrCreateAuthor)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No author found for book: " + bookApi.getTitle()));

        Book book = new Book();
        book.setTitle(bookApi.getTitle());
        book.setLanguages(Collections.singletonList(bookApi.getLanguages().get(0)));
        book.setDownloadCount(bookApi.getDownloadCount());
        book.setAuthors(author);

        return bookRepository.save(book);
    }

    public List<Person> getAllAuthors() {
        String url = BASE_URL + "books";
        try {
            BookResponse response = restTemplate.getForObject(url, BookResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults().stream()
                        .flatMap(book -> book.getAuthors().stream())
                        .distinct()
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("No se puedo listar los autores: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Author> getAndSaveAuthorsAliveInYear(int year) {
        String url = BASE_URL + "books?author_year_start=" + year + "&author_year_end=" + year;
        BookResponse response = restTemplate.getForObject(url, BookResponse.class);

        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .flatMap(book -> book.getAuthors().stream())
                    .map(this::findOrCreateAuthor)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Author findOrCreateAuthor(Person personApi) {
        return authorRepository.findByNameAndBirthYearAndDeathYear(
                personApi.getName(),
                personApi.getBirthYear(),
                personApi.getDeathYear()
        ).orElseGet(() -> {
            Author author = new Author();
            author.setName(personApi.getName());
            author.setBirthYear(personApi.getBirthYear());
            author.setDeathYear(personApi.getDeathYear());
            return authorRepository.save(author);
        });
    }

    public List<Book> getBooksByLanguage(String language) {
        String url = BASE_URL + "books?languages=" + language;
        try {
            BookResponse response = restTemplate.getForObject(url, BookResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults();
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("No se pudo listar libros por el idioma elegido: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}
