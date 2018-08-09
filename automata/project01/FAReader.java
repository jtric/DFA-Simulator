package automata.project01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FAReader {
   
   public enum FAType {
      DFA,
      NFA,
      INVALID
   }
   
   public static class DFACompiler {
      
      private Pattern regex = Pattern.compile( "\\{(\\d+[\\,]?)+\\}" );
      private DFA dfa;
      
      public DFACompiler( ) {
         this.dfa = new DFA( );
      }
      
      public DFA getDFA( ) {
         return this.dfa;
      }
      
      public void newDFA( ) {
         this.dfa = new DFA( );
      }
      
      public FAType readFAFromFile( String fileName ) throws IOException {
         InputStream is = this.getClass( ).getResourceAsStream( fileName );
         InputStreamReader isr = new InputStreamReader( is );
         BufferedReader reader = new BufferedReader( isr );
         
         // We treat the first line specially
         boolean first = true;
         FAType decision = FAType.DFA;
         String line;
         
         while( (line = reader.readLine( )) != null ) {
            
            if( line.isEmpty( ) )
               continue;
            
            // We check that the first line is a set of accept states
            if( first ) {
               Matcher match = regex.matcher( line );
               if( match.matches( ) ) {
                  first = false;
                  for( int i = 1; i <= match.groupCount( ); ++i ) {
                     int state = Integer.parseInt( match.group( i ) );
                     if( this.dfa.isValidState( state, true ) ) 
                        this.dfa.addAcceptState( state );
                     else {
                        decision = FAType.INVALID;
                        break;
                     }
                  }
               }
               else {
                  decision = FAType.INVALID;
                  break;
               }
               // Because double nesting
               if( decision == FAType.INVALID )
                  break;
            }
            // iterate through the lines
            else {
               String[] rule = line.split( "," );
               // ensure the transition rule is the correct size
               if( rule.length != 3 ) {
                  decision = FAType.INVALID;
                  break;
               }
               int state = Integer.parseInt( rule[0] );
               String symbol = rule[1];
               int newState = Integer.parseInt( rule[2] );
               
               // ensure the transition rule uses valid values
               if( !dfa.isValidState( state, false ) || !dfa.isValidState( newState, false ) || !dfa.isSymbolInAlphabet( symbol ) ) {
                  decision = FAType.INVALID;
                  break;
               }
               
               // Epsilon transitions imply NFAs
               if( symbol.isEmpty( ) ) {
                  decision = FAType.NFA;
               }
               
               if( !dfa.addTransition( state, symbol, newState ) ) {
                  decision = FAType.NFA;
               }
               
            }
         }
         
         reader.close( );
         isr.close( );
         is.close( );
         
         return decision;
      }
   }
   
   
   public static class DFA {
      
      private HashSet<Integer> states;
      private HashSet<Integer> acceptStates;
      private HashSet<String> alphabet;
      private HashMap<Integer, HashMap<String, Integer>> tStates;
      
      public DFA( ) {
         this.states = new HashSet<Integer>( );
         this.acceptStates = new HashSet<Integer>( );
         this.alphabet = new HashSet<String>( );
         this.tStates = new HashMap<Integer, HashMap<String, Integer>>( );
         
      }
      
      public String getAlphabet( ) {
         StringBuilder sb = new StringBuilder( );
         sb.append("[");
         // Stupid
         char[] alphabetArr = alphabet.toString( ).replace( "[", "" ).replace( "]", "" ).replace( ", ", "" ).toCharArray( );
         Arrays.sort( alphabetArr );
         for( char c : alphabetArr )
            sb.append( c );
         sb.append("]");
         return sb.toString( );
      }
      
      /**
       * Returns the number of states in the DFA
       * @return
       */
      public int getNumberOfStates( ) {
         return this.states.size( );
      }
      
      public void addAcceptState( int state ) {
         this.acceptStates.add( state );
         this.states.add( state );
      }
      
      public void addSymbolToAlphabet( String symbol ) {
         this.alphabet.add( symbol );
      }
      
      /**
       * Attempts to add a new transition rule. Does not validate rules
       * 
       * @param state
       * @param symbol
       * @param newState
       * 
       * @return true; false if the symbol exists for that state
       */
      public boolean addTransition( int state, String symbol, int newState ) {
         
         //Cheesing it for NFAs
         this.states.add( state );
         this.states.add( newState );
         this.addSymbolToAlphabet( symbol );
         
         HashMap<String, Integer> trans =
               this.tStates.containsKey( state ) ?
                     this.tStates.get( state ) :
                     new HashMap<String,Integer>( )
         ;
                     
         if( trans.containsKey( symbol ) ) return false;
         
         trans.put( symbol, newState );
         this.tStates.put( state, trans );
         return true;
      }
      
      /**
       * Determines whether the numeric state is valid in this machine.
       * Basically, is it between 0 and 255 inclusively?
       * Or, for accept states, 0 and 254, since 255 is a trap state.
       * @param state
       * @param isAccept
       * @return true if valid, else false
       */
      public boolean isValidState( int state, boolean isAccept ) {
         return state >= 0 && state < (isAccept? 255 : 256);
      }
      
      public boolean isSymbolInAlphabet( String symbol ) {
         if( symbol.isEmpty( ) )
            return true;
         final Pattern regex = Pattern.compile( "[\\w\\d ]" );
         return symbol.length() == 1 && regex.matcher( symbol ).matches( );
      }
      
      public boolean run( String input ) {
         int state = 0;
         
         for( int i = 0; i < input.length( ); ++i ) {
            char symbol = input.charAt( i );
            // Okay, this is pretty stupid but if it's stupid and it works, it's only half as stupid.
            state = this.evaluateSymbol( state, String.valueOf( symbol ) );
            // Early kill on trap state
            if( state == 255 )
               return false;
         }
         
         return this.acceptStates.contains( state );
      }
      
      public int evaluateSymbol( int state, String symbol ) {
         HashMap<String, Integer> trans = this.tStates.get( state );
         if( trans != null ) {
            Integer ig = trans.get( symbol );
            if( ig != null )
               return ig;
            else {
               //System.out.println( "For state " + state + " and symbol [" + symbol + "], no transitions could be found!");
               return 255;
            }
         }
         else {
            return 255;
         }
         //return this.tStates.get( state ).get( symbol );
      }
      
   }

}
