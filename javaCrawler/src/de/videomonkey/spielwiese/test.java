package de.videomonkey.spielwiese;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			String _title = "";
			
            URL url = new URL("http://192.168.2.2/videomonkey/proxy.php?url=http://www.imdb.com/title/tt0947810/");
            /* Gets the input stream from the connection... */
            InputStream stream = url.openStream();
            /* Saves the page data in a string buffer... */
            StringBuffer data = new StringBuffer();
            int buffer;
            while ((buffer=stream.read())!=-1)
            {
                data.append((char)buffer);
            }
            stream.close();
            
            String pattern = "<title>(.*?) (\\([1-2][0-9][0-9][0-9].*?\\))</title>"; 
            String s = data.toString(); 
             
            for ( MatchResult r : findMatches( pattern, s ) ) {
            	System.out.println( r.groupCount() + " -" + r.group(1) + " von " + r.start() + " bis " + r.end() );
            }
              
            
            stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static Iterable<MatchResult> findMatches( String pattern, CharSequence s ) 
	{ 
	  List<MatchResult> results = new ArrayList<MatchResult>(); 
	 
	  for ( Matcher m = Pattern.compile(pattern).matcher(s); m.find(); ) 
	    results.add( m.toMatchResult() ); 
	 
	  return results; 
	}
	
	
    /**
     * Decodes a html string and returns its unicode string.
     **/
    protected static String decodeHTML(String toDecode) {
        String decoded = "";
        try
        {
            int end = 0;
            for (int i=0; i<toDecode.length(); i++)
            {
                if (toDecode.charAt(i)=='&' && toDecode.charAt(i+1)=='#' && (end=toDecode.indexOf(";",i))!=-1)
                {
                    decoded += (char)Integer.parseInt(toDecode.substring(i+2,end));
                    i = end;
                }
                else
                {
                    decoded += toDecode.charAt(i);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception: "+e.getMessage());
        } //finally {
        /* Returns the decoded string... */
        return decoded;
        //}
    }
	
	
}
