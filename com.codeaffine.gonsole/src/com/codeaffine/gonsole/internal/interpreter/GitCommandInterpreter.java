package com.codeaffine.gonsole.internal.interpreter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import com.codeaffine.console.core.ConsoleCommandInterpreter;
import com.codeaffine.console.core.ConsoleOutput;
import com.codeaffine.gonsole.internal.repository.CompositeRepositoryProvider;

public class GitCommandInterpreter implements ConsoleCommandInterpreter {

  private static final Collection<String> WORK_DIR_MODIFY_COMMANDS
    = Arrays.asList( "checkout", "reset", "merge" );

  private final CommandExecutor commandExecutor;
  private final CommandLineParser commandLineParser;

  public GitCommandInterpreter( ConsoleOutput consoleOutput,
                                CompositeRepositoryProvider repositoryProvider )
  {
    this( new CommandExecutor( consoleOutput, repositoryProvider.getCurrentRepositoryLocation() ),
          new CommandLineParser() );
  }

  GitCommandInterpreter( CommandExecutor commandExecutor, CommandLineParser commandLineParser ) {
    this.commandExecutor = commandExecutor;
    this.commandLineParser = commandLineParser;
  }

  @Override
  public boolean isRecognized( String... commandLine ) {
    return commandLineParser.isRecognized( resolveAlias( commandLine ) );
  }

  @Override
  public String execute( String... commandLine ) {
    CommandInfo commandInfo = commandLineParser.parse( resolveAlias( commandLine ) );
    String result = commandExecutor.execute( commandInfo );
    refreshAffectedResources( commandLine );
    return result;
  }

  private String[] resolveAlias( String[] commandLine ) {
    String[] result = commandLine.clone();
    if( result.length >= 1 && commandExecutor.getRepositoryLocation() != null ) {
      AliasConfig aliasConfig = new AliasConfig( commandExecutor.getRepositoryLocation() );
      String command = aliasConfig.getCommand( commandLine[ 0 ] );
      if( command != null ) {
        result[ 0 ] = command;
      }
    }
    return result;
  }

  private void refreshAffectedResources( String... commandLine ) {
    String command = Stream.of( commandLine ).findFirst().orElse( "" );
    if( WORK_DIR_MODIFY_COMMANDS.contains( command ) ) {
      new SharedResourcesRefresher( commandExecutor.getRepositoryLocation() ).refresh();
    }
  }
}