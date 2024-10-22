/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

/**
 * This script, when included in a html file, builds a neat breadcrumb trail
 * based on its url. That is, if it doesn't contains bugs (I'm relatively
 * sure it does).
 *
 * Typical usage:
 * <script type="text/javascript" language="JavaScript" src="breadcrumbs.js"></script>
 *
 *@author     <a href="mailto:leosimons@apache.org">Leo Simons</a> (main author)
 *@author     <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a> (integration in skin)
 *@created    July 12, 2002
 *@version    1.0
 */

/* ========================================================================
	CONSTANTS
   ======================================================================== */

/**
 * Two-dimensional array containing extra crumbs to place at the front of
 * the trail. Specify first the name of the crumb, then the URI that belongs
 * to it. You'll need to modify this for every domain or subdomain where
 * you use this script (you can leave it as an empty array if you wish)
 */
var PREPREND_CRUMBS = new Array();
   if(!("apache"=="")){
     PREPREND_CRUMBS.push( new Array( "apache", "http://www.apache.org/" ) );
   }
   if(!("xml.apache"=="")){
     PREPREND_CRUMBS.push( new Array( "ant.apache", "http://ant.apache.org/" ) );
   }
   if(!(""=="")){
     PREPREND_CRUMBS.push( new Array( "", "" ) );
   }

/**
 * String to include between crumbs:
 */
var DISPLAY_SEPARATOR = " &gt; ";
/**
 * String to include at the beginning of the trail
 */
var DISPLAY_PREPREND = "";
/**
 * String to include at the end of the trail
 */
var DISPLAY_POSTPREND = "";

/**
 * CSS Class to use for a single crumb:
 */
var CSS_CLASS_CRUMB = "breadcrumb";

/**
 * CSS Class to use for the complete trail:
 */
var CSS_CLASS_TRAIL = "breadcrumbTrail";

/**
 * CSS Class to use for crumb separator:
 */
var CSS_CLASS_SEPARATOR = "crumbSeparator";

/**
 * Array of strings containing common file extensions. We use this to
 * determine what part of the url to ignore (if it contains one of the
 * string specified here, we ignore it).
 */
var FILE_EXTENSIONS = new Array( ".html", ".htm", ".jsp", ".php", ".php3", ".php4" );

/**
 * String that separates parts of the breadcrumb trail from each other.
 * When this is no longer a slash, I'm sure I'll be old and grey.
 */
var PATH_SEPARATOR = "/";

/* ========================================================================
	UTILITY FUNCTIONS
   ======================================================================== */
/**
 * Capitalize first letter of the provided string and return the modified
 * string.
 */
function sentenceCase( string )
{
	var lower = string.toLowerCase();
	return lower.substr(0,1).toUpperCase() + lower.substr(1);
}

/**
 * Returns an array containing the names of all the directories in the
 * current document URL
 */
function getDirectoriesInURL()
{
	var trail = document.location.pathname.split( PATH_SEPARATOR );

	// check whether last section is a file or a directory
	var lastcrumb = trail[trail.length-1];
	for( var i = 0; i < FILE_EXTENSIONS.length; i++ )
	{
		if( lastcrumb.indexOf( FILE_EXTENSIONS[i] ) )
		{
			// it is, remove it and send results
			return trail.slice( 1, trail.length-1 );
		}
	}

	// it's not; send the trail unmodified
	return trail.slice( 1, trail.length );
}

/* ========================================================================
	BREADCRUMB FUNCTIONALITY
   ======================================================================== */
/**
 * Return a two-dimensional array describing the breadcrumbs based on the
 * array of directories passed in.
 */
function getBreadcrumbs( dirs )
{
	var prefix = "/";
	var postfix = "/";

	// the array we will return
	var crumbs = new Array();

	if( dirs != null )
	{
		for( var i = 0; i < dirs.length; i++ )
		{
			prefix += dirs[i] + postfix;
			crumbs.push( new Array( dirs[i], prefix ) );
		}
	}

	// preprend the PREPREND_CRUMBS
	if(PREPREND_CRUMBS.length > 0 )
	{
		return PREPREND_CRUMBS.concat( crumbs );
	}

	return crumbs;
}

/**
 * Return a string containing a simple text breadcrumb trail based on the
 * two-dimensional array passed in.
 */
function getCrumbTrail( crumbs )
{
	var xhtml = DISPLAY_PREPREND;

	for( var i = 0; i < crumbs.length; i++ )
	{
		xhtml += '<a href="' + crumbs[i][1] + '" >';
		xhtml += sentenceCase( crumbs[i][0] ) + '</a>';
		if( i != (crumbs.length-1) )
		{
			xhtml += DISPLAY_SEPARATOR;
		}
	}

	xhtml += DISPLAY_POSTPREND;

	return xhtml;
}

/**
 * Return a string containing an XHTML breadcrumb trail based on the
 * two-dimensional array passed in.
 */
function getCrumbTrailXHTML( crumbs )
{
	var xhtml = '<span class="' + CSS_CLASS_TRAIL  + '">';
	xhtml += DISPLAY_PREPREND;

	for( var i = 0; i < crumbs.length; i++ )
	{
		xhtml += '<a href="' + crumbs[i][1] + '" class="' + CSS_CLASS_CRUMB + '">';
		xhtml += sentenceCase( crumbs[i][0] ) + '</a>';
		if( i != (crumbs.length-1) )
		{
			xhtml += '<span class="' + CSS_CLASS_SEPARATOR + '">' + DISPLAY_SEPARATOR + '</span>';
		}
	}

	xhtml += DISPLAY_POSTPREND;
	xhtml += '</span>';

	return xhtml;
}

/* ========================================================================
	PRINT BREADCRUMB TRAIL
   ======================================================================== */

// check if we're local; if so, only print the PREPREND_CRUMBS
if( document.location.href.toLowerCase().indexOf( "http://" ) == -1 )
{
	document.write( getCrumbTrail( getBreadcrumbs() ) );
}
else
{
	document.write( getCrumbTrail( getBreadcrumbs( getDirectoriesInURL() ) ) );
}

