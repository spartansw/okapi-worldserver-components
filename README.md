Okapi Components for WorldServer
================================

This repository contains custom WorldServer components developed using the
WorldServer SDK (WSSDK), based on filters and other components available as
part of the open source [Okapi Framework](http://okapi.opentag.com/).

Currently, this repository contains:

* Filters, including
   * IDML
   * JSON
   * OpenXML (Word, PowerPoint, Excel, Visio)
   * po/pot (gettext)
   * XLIFF
   * YAML
* MT Adapters, including
   * Google MT
   * Microsoft Translator Hub
* Automatic Actions, including
   * An automatic action to improve MT quality of HTML and XML content, when used with the Microsoft Translator Hub
   * An automic action to import target segment data from source XLIFF files, for use when processing bilingual XLIFF as a source format
* Common code, including
   * Classes for generating table-based config UIs
   * Classes to simplify working with `WSAttribute` values

The code also includes a common framework for developing WorldServer filters
that adhere to the Okapi `IFilter` interface, making it easy to add
additional components.  Our intention is that over time, the set of
available components will grow to fill gaps in the WorldServer filter and
MT Adapter using open source code from Okapi.

Using the Code
==============

Using the WorldServer SDK with Maven
------------------------------------
The project builds with maven.  However, the code has a compile-time dependency
on the `wssdk-server.jar` which SDL includes as part of its WorldServer
SDK distribution.  This file is not normally accessible to Maven, so you
must install it in a local repository to use it.

The project `pom.xml` references the dependency via invented artifact
and group IDs:

* Artifact ID: `com.idiominc.wssdk`
* Group ID: `wssdk-server`
* Version: We use the WS Build version, which for us is currently `10.4.3.195`. 

To install your copy of wssdk-server.jar to your local maven repository, you
can run this command from within your WSSDK distribution:

    mvn install:install-file -DgroupId=com.idiominc.wssdk \
                 -DartifactId=wssdk-server \
                 -Dversion=10.4.3.195 \
                 -Dpackaging=jar \
                 -Dfile=lib/server/wssdk-server.jar 

You can also use `mvn deploy:deploy-file` to deploy to a shared repository
for convenience.

WSSDK Compatibility and Java Versions
-------------------------------------
This code was developed and tested against the WSSDK 10.4 SDK, however it
should be expected to work on most 10.x versions of WorldServer (and possibly
even 9.x), due to the stability of the WSSDK.  You will need to update the
`ws.version` property in the root `pom.xml` to match the version you are
compiling against.

All Okapi releases since M24 require Java 7 or later, so these components must 
be deployed to a WorldServer instance running Java 7 or later. (Upcoming M36, which is in development at the time of this writing, will requires Java 7.)

Building
--------
Run `mvn install` from the root directory to build all components.

If you are using an IDE, you should still build once from the command line
in order to generate the `Version.java` file that is referenced from various
places in the code.

Deploying
---------

Components can be deployed individually or in bundles.  All components
are deployed as JARs via the `Management > Administration > Customization`
screen in WorldServer.  (WSSDK components are usually deployed as zip files,
but WorldServer doesn't care about the extension; a JAR file works fine, as
long as it contains a `desc.xml` file, as these do.)

To deploy an individual components, upload any of:

* `filters/idml/target/okapi-ws-filters-idml-[release]-deployable.jar`
* `filters/json/target/okapi-ws-filters-json-[release]-deployable.jar`
* `filters/openxml/target/okapi-ws-filters-openxml-[release]-deployable.jar`
* `filters/po/target/okapi-ws-filters-po-[release]-deployable.jar`
* `filters/xliff/target/okapi-ws-filters-xliff-[release]-deployable.jar`
* `filters/yaml/target/okapi-ws-filters-yaml-[release]-deployable.jar`
* `mt/google/target/okapi-ws-mt-google-[release]-deployable.jar`
* `mt/mshub/target/okapi-ws-mt-mshub-[release]-deployable.jar`
* `autoactions/mshub/target/okapi-ws-autoactions-mshub-[release]-deployable.jar`
* `autoactions/xliff/target/okapi-ws-autoactions-xliff-[release]-deployable.jar`

To deploy all filters at once, upload:

* `filters/bundle/target/okapi-ws-filters-bundle-[release].jar`

To deploy all components (all filters + MT Adapters + AAs), upload:

* `bundle/target/okapi-ws-components-bundle-[release].jar`

These jars include all dependencies, including the necessary Okapi components.

Configuring
-----------

Currently, the filter configurations are hard-coded internally to sensible 
defaults, with some options exposed through the WorldServer UI.  Over time,
we would like to improve this to expose the full configurability of the
Okapi filters to WorldServer users.

The MT Adapters offer no real configuration beyond the credentials to use.
These are exposed through the the normal WorldServer MT Adapter configuration
interface.

About
=====
This project is a joint collaboration between [Spartan Software, Inc.
](http://spartansoftwareinc.com) and [Tableau Software](http://www.tableau.com/).  Additional support has been provided by [VMware](https://vmware.com), [Veritas](https://veritas.com), [Vistatec](http://vistatec.com/), [Intuit](https://www.intuit.com/), and [ebay](http://www.ebay.com/).

License
-------
All code is licensed under the 
[MIT License](https://opensource.org/licenses/MIT).

Contributing
------------

Contributions from the community are welcome.  Issues and pull requests should
be opened through GitHub.  Discussion can be directed to one of the two Okapi
discussion lists:

* [okapitools](https://groups.yahoo.com/neo/groups/okapitools/conversations/messages), for user discussions
* [okapi-devel](https://groups.google.com/forum/#!forum/okapi-devel), for developer discussions

Additional documentation can be found on the
[wiki](https://github.com/spartansw/okapi-worldserver-components/wiki).

Questions or comments can also be sent to [opensource@spartansoftwareinc.com](mailto:opensource@spartansoftwareinc.com).
