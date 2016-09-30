/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.remotetasks;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.as.quickstarts.datagrid.remotetasks.tasks.BooksQueryingTask;
import org.jboss.as.quickstarts.datagrid.remotetasks.tasks.BooksRemovingTask;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

/**
 * Class managing the user input. The user's input is processed and the corresponding cache operation is done.
 *
 * @author Anna Manukyan
 */
public class LibraryManager {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "jdg.properties";
    private static final String BOOKS_CSV_FILE = "books.csv";

    public static final String CACHE_NAME = "library";
    private static final String SCRIPT_CACHE_NAME = "___script_cache";
    private static final String msgEnterBookAuthor = "Please enter the author: ";
    private static final String msgEnterBookTitle = "Please enter the title: ";
    private static final String msgEnterBookPageNumber = "Please enter the number of pages: ";
    private static final String msgEnterBookPubYear = "Please enter the publication year: ";

    private static final String initialPrompt = "Choose action:\n" + "============= \n"
            + "ab  -  add a book\n"
            + "rb  -  remove a book\n"
            + "p   -  print all books\n"
            + "qb   - query book/s according to inserted parameters\n"
            + "h   - shows the all available options\n"
            + "q   -  quit\n";


    private Console con;
    private RemoteCacheManager cacheManager;
    private RemoteCache<UUID, Book> cache;
    private RemoteCache<String, String> scriptCache;

    public LibraryManager(Console con) throws IOException {
        this.con = con;
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(jdgProperty(JDG_HOST))
                .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
        cacheManager = new RemoteCacheManager(builder.build());
        cache = cacheManager.getCache(CACHE_NAME);

        //Getting access to cache which stores the executable javascripts and add book listing javascript task to it.
        scriptCache = cacheManager.getCache(SCRIPT_CACHE_NAME);
        addExecutableScript("scripts/listingBooks.js");

        //preloading some data into the cache
        loadBooksToCache();
    }

    /**
     * Adds a book into the cache based on the user input.
     */
    public void addBook() {
        String title = con.readLine(msgEnterBookTitle);
        String author = con.readLine(msgEnterBookAuthor);
        int pagesNumber = Integer.parseInt(con.readLine(msgEnterBookPageNumber));
        int publicationYear = Integer.parseInt(con.readLine(msgEnterBookPubYear));

        Book book = new Book(author, title);
        book.setNumberOfPages(pagesNumber);
        book.setPublicationYear(publicationYear);
        book.setId(UUID.randomUUID());

        //putting the created object into the cache
        cache.put(book.getId(), book);
    }

    /**
     * Removes the book from the cache based on the user input. The book object removal is performed via {{BooksRemovingTask}}
     * remote task execution.
     */
    public void removeBook() {
        String title = con.readLine(msgEnterBookTitle);
        String author = con.readLine(msgEnterBookAuthor);

        Map<String, String> parameters = new HashMap<>();
        if (author != null && !author.isEmpty())
            parameters.put("author", author);

        if (title != null && !title.isEmpty())
            parameters.put("title", title);

        //Removing the object from the cache by executing the remote task {{BooksRemovingTask}}.
        int numberOfDeletedObjects = cache.execute(BooksRemovingTask.BOOKS_REMOVING_TASK_NAME, parameters);
        if (numberOfDeletedObjects == 0) {
            System.out.println("No objects were found with specified parameters.");
        } else {
            System.out.println("Success! " + numberOfDeletedObjects + " object/s have been deleted.");
        }
    }

    /**
     * Executes the {{listingBooks.js}} javascript stored in the cache which returns the list of all available books
     * ordered alphabetically.
     */
    public void showAllBooks() {
        //Listing all books by executing javascript
        printBookList(cache.execute("scripts/listingBooks.js", null));
    }

    /**
     * Query books based on the input parameters. The query is performed by calling {{BooksQueryingTask}} remote taks.
     */
    public void queryBooks() {
        String title = con.readLine(msgEnterBookTitle);
        String author = con.readLine(msgEnterBookAuthor);
        String pageNumber = con.readLine(msgEnterBookPageNumber);
        String publicationYear = con.readLine(msgEnterBookPubYear);

        Map<String, Object> parameters = new HashMap<>();
        if (title != null && !title.isEmpty()) {
            parameters.put(BooksQueryingTask.titleParamName, title);
        }

        if (author != null && !author.isEmpty()) {
            parameters.put(BooksQueryingTask.authorParamName, author);
        }

        if (pageNumber != null && !pageNumber.isEmpty()) {
            try {
                parameters.put(BooksQueryingTask.pageNumParamName, Integer.parseInt(pageNumber));
            } catch(NumberFormatException ex) {
                System.out.println("The input for page numbers is not a valid integer. This input is omitted. ");
            }
        }

        if (publicationYear != null && !publicationYear.isEmpty()) {
            try {
                parameters.put(BooksQueryingTask.pubYearParamName, Integer.parseInt(publicationYear));
            } catch(NumberFormatException ex) {
                System.out.println("The input for publication year is not a valid integer. This input is omitted. ");
            }
        }

        //Querying and printing books by executing the remote task by passing corresponding user input.
        printBookList(cache.execute(BooksQueryingTask.BOOKS_QUERYING_TASK_NAME, parameters));
    }

    /**
     * Prints the provided book list to console.
     * @param bookList          the booklist to be printed.
     */
    private void printBookList(List<Book> bookList) {
        System.out.println("----- Available Book List -----");

        for (Book book : bookList) {
            System.out.println(book);
        }
    }

    /**
     * Stops the remote cache client.
     */
    public void stop() {
        cacheManager.stop();
    }

    public static void main(String[] args) throws IOException {
        Console con = System.console();
        LibraryManager manager = new LibraryManager(con);
        con.printf(initialPrompt);

        while (true) {
            String action = con.readLine(">");
            if ("ab".equals(action)) {
                manager.addBook();
            } else if ("rb".equals(action)) {
                manager.removeBook();
            } else if ("p".equals(action)) {
                manager.showAllBooks();
            } else if ("qb".equals(action)) {
                manager.queryBooks();
            } else if ("h".equals(action)) {
                con.printf(initialPrompt);
            } else if ("q".equals(action)) {
                manager.stop();
                break;
            }
        }
    }

    /**
     * Loads the property file with JDG connection properties and returns the one requested.
     * @param name      the name of the property which value should be returned.
     * @return          the property value.
     */
    private static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(LibraryManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }

    /**
     * Loads the file with prefilled books information into the cache.
     */
    private void loadBooksToCache() {
        Scanner scanner = new Scanner(LibraryManager.class.getClassLoader().getResourceAsStream(BOOKS_CSV_FILE));
        scanner.useDelimiter("\n");

        Book b;
        while(scanner.hasNext()){
            String[] lineArr = scanner.next().split(",");
            b = new Book(lineArr[1], lineArr[0]);
            b.setNumberOfPages(Integer.parseInt(lineArr[2]));
            b.setPublicationYear(Integer.parseInt(lineArr[3]));

            cache.put(UUID.randomUUID(), b);
        }

        scanner.close();
    }

    /**
     * Adds the passed javascript to the scripting cache.
     *
     * @param script            the path to the script file
     * @throws IOException
     */
    private void addExecutableScript(String script) throws IOException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(script)) {
            scriptCache.put(script, loadFileAsString(in));
        }
    }

    /**
     * Method which loads the data from inputstream to StringBuffer.
     * @throws IOException
     */
    private static String loadFileAsString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
}
