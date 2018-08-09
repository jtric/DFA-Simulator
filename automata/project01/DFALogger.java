package automata.project01;

import java.io.FileWriter;
import java.io.IOException;

public class DFALogger {

   private FileWriter logWriter;
   private FileWriter accWriter;
   private FileWriter rejWriter;
   
   public DFALogger( String basename ) throws IOException {
      this.logWriter = new FileWriter( basename + ".log" );
      this.accWriter = new FileWriter( basename + ".acc" );
      this.rejWriter = new FileWriter( basename + ".rej" );
   }
   
   public void close( ) {
      try {
         this.logWriter.close( );
         this.accWriter.close( );
         this.rejWriter.close( );
      }
      catch( IOException ioe ) {
         System.out.println( "failed to close writers" );
      }
   }
   
   public boolean logLog( String s ) {
      return this.doLog( this.logWriter, s );
   }
   
   public boolean logAcc( String s ) {
      return this.doLog( this.accWriter, s );
   }
   
   public boolean logRej( String s ) {
      return this.doLog( this.rejWriter, s );
   }
   
   private boolean doLog( FileWriter writer, String s ) {
      try {
         writer.write( s );
         writer.write( "\r\n" );
      }
      catch( IOException ioe ) {
         return false;
      }
      return true;
   }

}
