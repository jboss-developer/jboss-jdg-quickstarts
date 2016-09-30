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
package org.jboss.as.quickstarts.datagrid.remotetasks.tasks;

import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import org.jboss.as.quickstarts.datagrid.remotetasks.Book;
import org.jboss.as.quickstarts.datagrid.remotetasks.LibraryManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task, which finds and removes the books from the cache, according to it's title and author.
 *
 * @author Anna Manukyan
 */
public class BooksRemovingTask implements ServerTask {
    public static String BOOKS_REMOVING_TASK_NAME = "booksRemovingTask";

    private TaskContext taskContext;
    private Map<String, String> parameters;
    private static String authorParamName = "author";
    private static String titleParamName = "title";

    @Override
    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.parameters = (Map<String, String>) taskContext.getParameters().get();
    }

    @Override
    public String getName() {
        return BOOKS_REMOVING_TASK_NAME;
    }

    @Override
    public Integer call() throws Exception {
        Cache cache = taskContext.getCache().get();

        //This line is a workaround until ISPN-6250 is fixed.
        Cache<UUID, Book> cache1 = cache.getCacheManager().getCache(LibraryManager.CACHE_NAME);

        AtomicInteger i = new AtomicInteger();
        cache1.entrySet().parallelStream().filter( book ->
                (parameters.get(authorParamName) == null || book.getValue().getAuthor().matches(parameters.get(authorParamName)))
                        && (parameters.get(titleParamName) == null || book.getValue().getTitle().matches(parameters.get(titleParamName))))
                .forEach(b -> {
                    cache1.remove(b.getKey());
                    i.addAndGet(1);
                });

        System.out.println("Successfully finished the action.");
        return i.get();
    }
}
