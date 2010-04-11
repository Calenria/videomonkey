/**
 * 
 */
package de.videomonkey.webinfos.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.videomonkey.webinfos.interfaces.WebInfo;

/**
 * @author Azrail
 *
 */
public abstract class WebInfosAbs implements WebInfo {

	
	protected static Iterable<MatchResult> findMatches( String pattern, CharSequence s ) 
	{ 
	  List<MatchResult> results = new ArrayList<MatchResult>(); 
	 
	  for ( Matcher m = Pattern.compile(pattern).matcher(s); m.find(); ) 
	    results.add( m.toMatchResult() ); 
	 
	  return results; 
	}
	
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
