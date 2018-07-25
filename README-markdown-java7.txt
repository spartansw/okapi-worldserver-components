Special README for the Java-7/M36 Markdown Filter
2018-6-25 written by T. Kuro Kurosaka

This README applies to for the custom version of
Okapi Framework and Okapi WorldServer Components
tailored to build a Markdown filter from
post M36 version of Okapi on WorldServer that runs
in Java 7.


MOTIVATION

A client wanted to run the Okapi Markdown filter that with the
features such as HTML subfilter that was available in post M35
release, i.e. the version in the dev branch for 0.36-SNAPSHOT.

This client was using WorldServer version 10.1.0 that
they could managed to upgrade the Java runtime to Java 7,
but not Java 8, due to a database driver incompatibility.
In mean while, Okapi changed their minimum requirement to Java 8
at the beginning of M36 development, i.e. 0.36-SNAPSHOT.

Because of this Java version issue, any bug fixes and 
new features introduced in version 0.36-SNAPSHOT would not be 
available for this client in a usual way.  They needed
a solution.


STRATEGY

For Okapi Framework, create a trimmed down version of the source tree
(0.36-SNAPSHO) that is just enough to build the Markdown filter 
and the HTML filter, that is used from the Markdown filter, and then
downgrade it to work with the Java 7 runtime.

For WorldServer, add a Markdown filter, then adjust pom.xml files
to build the minimum needed to build the Markdown filter,
and use the Java 7 version of Okapi 0.36-SNAPSHOT.
Adjusting pom.xml files was necessary because the trimmed down, Java-7
compatible Okapi only has the Markdown filter.
Please note we are NOT building the WorldServer HTML Filter.
We are using the Okapi HTML filter as a subfilter used only
from the Markdown filter.


BRANCHES, VERSIONS, ETC.

Okapi Framework
---------------
Okapi official repository:
https://bitbucket.org/okapiframework/okapi/src

Our private fork:
https://bitbucket.org/ssikuro/okapi/src

Branch: java7dg (branched from dev commit c7d1537, only in the fork)
	Note: This branch includes bug fixes up to the issue 715. 
	It does not include the bug fix to the issue 686.

Artifact Version: 0.36-java7-SNAPSHOT


Okapi WorldServer Components
----------------------------
Repository: https://github.com/spartansw/okapi-worldserver-components

Branch: markdown-only-M36java7

Artifact Version: 1.8-M36SSjava7-SNAPSHOT


SPECIAL BUILD INSTRUCTIONS

In order to build the pure Java 7 components, 
the values of plugin/configuration/target and
plugin/configuration/source for maven-compiler-plugin
in some pom.xml files have been changed to 1.7.
But this not enough. It seems that if you use
the mvn command on the computer where Java
8 has been installed already, it seems that
the build process still picks up the Java 8 library.

To avoid this, it is best to run the maven itself
with Java 7 runtime.  I do this by defining an alias in .bashrc as:
alias mvn7='JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_25.jdk/Contents/Home MAVEN_OPTS=-Xmx2g mvn'

and use mvn7 instead of mvn.

MAVEN_OPTS=-Xmx2g was necessary probably because the
default heap size differ between Java 7 and Java 8.
The JAVA_HOME location has to be modified to suite your
Java installations.

Because the Okapi WorldServer Components depends on
the Okapi Framework components, and they are a SNAPSHOT
version, they have to be built locally in this order.

Assuming you have the repositories copied in 
okapi and okapi-worldserver-components directories:


cd okapi/
mvn7 clean install


Not this builds the only the Markdown filter in okapi/filters/markdown/target.
It does not build any other components or applications.
After, this, type the followings:


cd ../okapi-worldserver-components
mvn7 clean install

ls -l filters/markdown/target/okapi-ws-filters-markdown-1.8-M36SSjava7-SNAPSHOT-deployable.jar


This JAR is the Markdown Filter for WorldServer.
Note: DO NOT deliver or install the other JAR without
"-deployable" in its name. It won't work.


OPERATIONS

Before Installation
-------------------
Make sure your WorldServer is running with Java 7 runtime.

If you have installed an earlier development version of
WS Markdown Filter, please remove it, the x-markdown MIME-type
definition, any filter configurations for the Markdown
filter, and anythings that refer them, such as filter groups.


Installation
------------
On WorldServer, navigate:
Management -> Administration -> Customization
Choose "Filter" from the drop down list.
Click Add and select:
okapi-ws-filters-markdown-1.8-M36SSjava7-SNAPSHOT-deployable.jar
Press OK.

While still in the Customization window, choose "MIME Types".
Click Add and fill the form as:
MIME Type Name: text/x-markdown
Description: Markdown Filter (or what ever you like)
Is Text? : Checked
Default Extension: md  (WITHOUT ".")
External MIME Type: text/x-markdown
Filter Name: Okapi Markdown Filter
Filter Configuration: Default
Extension: md, markdown (Enter "md", Add, "markdown", Add)

You can leave External Application section blank.


Filter Configuration
--------------------
This is a two-step process.
Management -> Linguistic Tool - Filter Configurations

Find "Okapi Markdown Filter", and click Add next to it.
Enter a Name and some description. Click OK.

Click on the plus button on the left side of "Okapi Markdown Filter".
Then click on the name of the new configuration.

Translate URLs
When checked, the links are extracted for translation.

Pattern to Match Translatable URLs
When the Translate URLs check box is checked, and if you don't
want to translate every URLs, you can limit the URLs to
the ones that the regular expression entered here matches.
The default pattern ".+" matches anything.
Enter "https?://", for example, to extract only the external URLs.

Translate Code Blocks
Uncheck if you'd like the fenced code block to be translated.
Note: Indented code blocks are always translated.

Translate Header Metadata (YAML Values)
This decides whether the values in the YAML block
that might come at the beginning of the Markdown filter
should be extracted.

Translate Image Alt Text
Uncheck this not to extract the values of the alt text of
the image markdown.

HTML Subfilter ID
See the next section

Use Inline Code Finder
Check this in order to use the Inline Code Finder.

Inline Code Finder Rules
Enter one more more regular expressions, one at a time,
by clicking Add.
Any substring matching any of the expressions will become
a code, and will be protected from being translated.


Using Custom HTML Filters
-------------------------
Any of the options you configured above only applies to the
Markdown proper part of the document, which is any part that
are not a run of HTML tags. 

Even if you checked the "Translate URLs" check mark,
the URLs that appear in HTML tags, such as <a href="https://www.google.com"/>
will not be extracted, for example.

In order to extract those, you would need to write
a configuration file for Okapi HTML filter, and
specify its configuration ID in "HTML Subfilter ID" field.

Note that this Markdown Filter cannot use the
WorldServer's native HTML Filter as a subfilter.

The rules of writing an Okapi HTML configuration file is pretty
complicated and is beyond this README. Please read:
http://okapiframework.org/wiki/index.php?title=HTML_Filter

Once you have your customized configuration, save the file
under the name of "okf_html@anyname.fprm". "anyname" part is
arbitrary as long as it is made of alphabet letters,
numbers, underscores. The file must start with "okf_html@"
and must have the suffix ".fprm".

On WorldServer, go to 
Management > Asset Interface System > AIS Mounts
and add a file-system amount named "Customization".
Go to Explorer tab, navigate to the Customization mount,
and make a folder named "okapi_subfilter".
While point to the okapi_subfilter, select
Asset > Upload.
Choose your .fprm file and click OK.

Then go back to
Management -> Linguistic Tool - Filter Configurations
find the Okapi Markdown Filter and click "+",
and choose the configuration you'd like to edit (or make a new one).
Enter the configuration ID to the "HTML Subfilter" field.
The configuration ID is the file name less ".fprm".
So for the above example, it would be "okf_html@anyname".

Click OK.

In order to use the default HTML configuration builtin
to the Markdown filter, leave this field blank.

EOF
