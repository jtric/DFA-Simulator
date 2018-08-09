package automata.project01;

import automata.project01.FAReader.DFA;
import automata.project01.FAReader.DFACompiler;
import automata.project01.FAReader.FAType;

public class Test {

   public static void main(String[] args) {
      say( "Assembling and testing all FAs. The files are internal to this program." );
      for( int i = 1; i < 15; ++i ) {
         test( "fa" + (i < 10 ? "0" : "") + i );
      }
      say( "Testing complete! Files will be located at:" );
      say( " - Project Root (Eclipse)");
      say( " - Execution Directory (JAR)" );
      
   }
   
   public static void test( String file ) {
      say( "Testing " + file );
      DFACompiler compiler = new DFACompiler( );
      
      try {
         FAType result = compiler.readFAFromFile( "fa/" + file + ".fa" );
         
         DFA dfa = compiler.getDFA( );
         DFALogger dfal = new DFALogger( file );
         
         // replace with logger
         dfal.logLog( "Alphabet: " + dfa.getAlphabet( ) );
         dfal.logLog( "States: " + dfa.getNumberOfStates( ) );
         dfal.logLog( "Valid: " + result );
         
         if( result == FAType.DFA ) {
            
            InputReader ir = new InputReader( "input/" + file + ".in" );
            String input;
            
            int inputCount = 0;
            int inputsAccepted = 0;
            int inputsRejected = 0;
            
            while( (input = ir.readInput( )) != null ) {
               boolean accepted = dfa.run( input );
               inputCount++;
               if( accepted ) {
                  inputsAccepted++;
                  dfal.logAcc( input );
               }
               else {
                  inputsRejected++;
                  dfal.logRej( input );
               }
            }
            ir.close( );
            
            dfal.logLog( "Strings: " + inputCount );
            dfal.logLog( "Accepted: " + inputsAccepted );
            dfal.logLog( "Rejected: " + inputsRejected );
         }
         
         dfal.close( );
      }
      catch( Exception e ) {
         // we fucked up
         say("you fucked up");
         say( e.getMessage( ) );
         e.printStackTrace( );
      }
      
      compiler.newDFA( );
   }
   
   public static void say( String s ) {
      System.out.println( s );
   }

}
