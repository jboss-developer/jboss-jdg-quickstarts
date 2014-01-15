package org.infinispan.quickstart.compatibility.commands;


import java.io.Console;

public interface Command {

   int getNumArgs();

   void execute(Console console, String argLine);

}
