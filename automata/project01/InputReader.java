package automata.project01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputReader {

   private InputStream is;
   private InputStreamReader isr;
   private BufferedReader reader;
   
   public InputReader( String inputFileName ) {
      this.is = this.getClass( ).getResourceAsStream( inputFileName );
      this.isr = new InputStreamReader( is );
      this.reader = new BufferedReader( isr );
   }
   
   public void close( ) {
      try {
         this.reader.close( );
         this.isr.close( );
         this.is.close( );
      }
      catch( IOException ioe ) {
         System.out.println( "failed to close input readers" );
      }
   }
   
   public String readInput( ) {
      try {
         return reader.readLine( );
      }
      catch( IOException ioe ) {
         return null;
      }
   }

}
