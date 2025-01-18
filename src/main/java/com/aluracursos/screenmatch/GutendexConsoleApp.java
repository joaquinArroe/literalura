package com.aluracursos.screenmatch;

import com.aluracursos.screenmatch.model.Author;
import com.aluracursos.screenmatch.model.Book;
import com.aluracursos.screenmatch.model.Person;
import com.aluracursos.screenmatch.services.GutendexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class GutendexConsoleApp implements CommandLineRunner {

	@Autowired
	private GutendexService gutendexService;

	public static void main(String[] args) {
		SpringApplication.run(GutendexConsoleApp.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.println("\n--- Menú ---");
			System.out.println("1. Buscar libros por título");
			System.out.println("2. Listar todos los libros");
			System.out.println("3. Listar autores");
			System.out.println("4. Listar autores vivos en un año específico");
			System.out.println("5. Listar libros en un idioma específico");
			System.out.println("6. Salir");
			System.out.print("Seleccione una opción: ");

			int option = scanner.nextInt();
			scanner.nextLine(); // Limpiar el buffer

			switch (option) {
				case 1: // Buscar libros por título y guardarlos
					System.out.println("Ingrese el título del libro:");
					String title = scanner.nextLine();
					List<Book> books = gutendexService.searchAndSaveBooksByTitle(title);
					if (books.isEmpty()) {
						System.out.println("No se encontraron libros para el título: " + title);
					} else {
						System.out.println("Libros encontrados y guardados:");
						books.forEach(book -> System.out.println(" - " + book.getTitle()));
					}
					break;

				case 2:
					List<Book> allBooks = gutendexService.getAllBooks();
					allBooks.forEach(book -> System.out.println("Título: " + book.getTitle()));
					break;

				case 3:
					List<Person> authors = gutendexService.getAllAuthors();
					authors.forEach(author -> System.out.println("Autor: " + author.getName()));
					break;

				case 4: // Buscar autores vivos en un año y guardarlos
					System.out.println("Ingrese el año:");
					int year = Integer.parseInt(scanner.nextLine());
					List<Author> authors1 = gutendexService.getAndSaveAuthorsAliveInYear(year);
					if (authors1.isEmpty()) {
						System.out.println("No se encontraron autores vivos en el año " + year);
					} else {
						System.out.println("Autores encontrados y guardados:");
						authors1.forEach(author -> System.out.println(" - " + author.getName()));
					}
					break;

				case 5:
					System.out.print(
							"\nes - Español" +
									"\nen- Inglés" +
									"\nfr- Francés" +
									"\npt- Portugués" +
									"\n Ingrese el idioma: ");
					String language = scanner.nextLine();
					List<Book> booksByLanguage = gutendexService.getBooksByLanguage(language);
					if (booksByLanguage.isEmpty()) {
						System.out.println("No books found in the given language.");
					} else {
						booksByLanguage.forEach(book -> System.out.println("- " + book.getTitle()));
					}
					break;

				case 6:
					running = false;
					System.out.println("Saliendo del programa. ¡Hasta luego!");
					break;

				default:
					System.out.println("Opción no válida. Intente de nuevo.");
			}
		}

		scanner.close();
	}
}