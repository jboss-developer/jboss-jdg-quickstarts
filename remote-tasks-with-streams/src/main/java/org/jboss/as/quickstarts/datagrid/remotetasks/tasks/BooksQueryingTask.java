package org.jboss.as.quickstarts.datagrid.remotetasks.tasks;

import org.infinispan.Cache;
import org.infinispan.stream.CacheCollectors;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import org.jboss.as.quickstarts.datagrid.remotetasks.Book;
import org.jboss.as.quickstarts.datagrid.remotetasks.LibraryManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Task necessary for querying over the existing Books.
 * The query is done based on the user input - title, author, pageNumbers and publicationYear.
 * If user hasn't entered any of them, then all books will be listed.
 * The title and the author can be entered as a regular expression.
 *
 * @author Anna Manukyan
 */
public class BooksQueryingTask implements ServerTask {
    public static String BOOKS_QUERYING_TASK_NAME = "booksQueryingTask";

    private TaskContext taskContext;
    private Map<String, Object> parameters;
    public static String authorParamName = "author";
    public static String titleParamName = "title";
    public static String pubYearParamName = "pubYear";
    public static String pageNumParamName = "pageNum";

    @Override
    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.parameters = (Map<String, Object>) taskContext.getParameters().get();
    }

    @Override
    public String getName() {
        return BOOKS_QUERYING_TASK_NAME;
    }

    @Override
    public List<Book> call() throws Exception {
        Cache cache = taskContext.getCache().get();

        //This line is a workaround until ISPN-6250 is fixed.
        Cache<UUID, Book> cache1 = cache.getCacheManager().getCache(LibraryManager.CACHE_NAME);

        return cache1.entrySet().parallelStream()
                .map(Map.Entry::getValue)
                .filter(book ->
                        (parameters.get(authorParamName) == null || book.getAuthor().matches((String) parameters.get(authorParamName)))
                                && (parameters.get(titleParamName) == null || book.getTitle().matches((String) parameters.get(titleParamName)))
                                && (parameters.get(pageNumParamName) == null || parameters.get(pageNumParamName).equals(book.getNumberOfPages()))
                                && (parameters.get(pubYearParamName) == null || parameters.get(pubYearParamName).equals(book.getPublicationYear())))
                .collect(Collectors.toList());
    }
}
