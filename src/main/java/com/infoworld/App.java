package com.infoworld;

import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.jline.reader.LineReader;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import jline.TerminalFactory;

// consoleui
import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.ConfirmResult;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
//import jline.console.completer.StringsCompleter;

import java.io.IOException;
import java.util.HashMap;

import static org.fusesource.jansi.Ansi.ansi;
import org.fusesource.jansi.AnsiConsole;

public class App {
  public static void main(String[] args) throws IOException {
    Terminal terminal = TerminalBuilder.terminal();
    LineReader reader =
        LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter("describe", "create"))
            .parser(new DefaultParser())
            .build();
    while (true) {
      String line = reader.readLine("> ");
      if (line == null || line.equalsIgnoreCase("exit")) {
        break;
      }
      reader.getHistory().add(line);
      if (line.equalsIgnoreCase("describe")) {
        Path path = Paths.get(".");
        System.out.println(getDirectoryHierarchy(path));
      } else if (line.equalsIgnoreCase("create")) {
        createCommand();
      } else {
        System.out.println("Unknown command: " + line);
      }
    }
  }
  public static void createCommand() { 
     AnsiConsole.systemInstall();
System.out.println(ansi().render("@|red,italic Hello|@ @|green World|@\n@|reset Welcome to the project creator.|@"));

    try {
      ConsolePrompt prompt = new ConsolePrompt();
      PromptBuilder promptBuilder = prompt.getPromptBuilder();

      promptBuilder.createListPrompt().name("projectType").message("What kind of project do you want to create? (java, javascript, python) ")
              .newItem().text("java").add().newItem("javascript").text("javascript").add()
              .newItem("python").text("python").add().addPrompt();

      HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());

      String projectType = ((de.codeshelf.consoleui.prompt.ListResult)result.get("projectType")).getSelectedId(); 
      System.out.println("result = " + ((de.codeshelf.consoleui.prompt.ListResult)result.get("projectType")).getSelectedId());
      System.out.println("Building a new project: " + projectType);

      if (projectType== "java") {
	prompt = new ConsolePrompt();
	promptBuilder = prompt.getPromptBuilder();
        promptBuilder.createCheckboxPrompt()
              .name("features")
              .message("Please select features to add:")

              .newSeparator("Databases:")
              .add()

              .newItem().name("mongo").text("MongoDB").add()
              .newItem("mysql").text("MySQL").add()
              .newItem("db2").text("DB2").disabledText("Sorry, discontinued.").add()

              .newSeparator().text("Rest API").add()

              .newItem("spring-rest").text("Spring REST").check().add()
              .newItem("micronaut").text("Micronaut").add()
              .addPrompt();

        result = prompt.prompt(promptBuilder.build());
        System.out.println("result = " + result);
      }  
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        TerminalFactory.get().restore();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static String getDirectoryHierarchy(Path path) {
    StringBuilder sb = new StringBuilder();
    try (Stream<Path> paths = Files.walk(path)) {
      paths.sorted()
          .forEach(
              p -> {
                int depth = path.relativize(p).getNameCount();
                for (int i = 0; i < depth; i++) {
                  sb.append(" ");
                }
                String fileName = p.getFileName().toString();
                if (p.toFile().isDirectory()) {
                  fileName = "\033[32m" + "/" + fileName + "\033[0m";
                } else {
                  fileName = "\033[34m" + fileName + "\033[0m";
                }
                sb.append(fileName).append("\n");
              });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }
}

