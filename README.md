# ruian2pgsql

Converts RÚIAN data to PostgreSQL database.

## What it does exactly?

ruian2pgsql is a command line application. It reads data from RÚIAN XML files
and stores them in PostgreSQL database. It does not keep the history records, it
just keeps the latest state of all the objects. It is possible to import just
one file as well as many files. If one file is imported several times, or in
case content of some files overlaps, ruian2pgsql imports all the objects only
once (they are tracked by their unique id assigned in RÚIAN).

## Prerequisities

* JRE or JDK 7+
* PostgreSQL server with installed PostGIS or MySQL server (if --no-gis command
  line switch will be used)
* Apache Maven 3+ (just in case you want to compile the application yourself)

## Compilation

You are not required to compile the application yourself, you can download
latest binary from https://github.com/fordfrog/ruian2pgsql/downloads. Anyway,
compilation of ruian2pgsql is easy. Once you install Apache Maven, you just need
to run `mvn package` in the root directory of the sources, where pom.xml file is
located.

## Running

Here is the usage information that ruian2pgsql outputs if run without
parameters:

    Usage: java -cp ruian2pgsql-*.jar:jdbc-driver.jar com.fordfrog.ruian2pgsql.App <options>

    Where:
    jdbc-driver.jar
            is JAR file containing database driver (either PostgreSQL or MySQL)
    --convert-to-ewkt
            if enabled, GML strings from the source XML files are first converted to
            EWKT and then stored in database, otherwise original GML strings are
            stored in database (use this option based on the level of GML
            implementation in Postgis version you use)
    --linearize-ewkt
            if enabled, curved geometries in parsed EWKT are linearized before they
            are stored in database; supported only if the switch --convert-to-ewkt
            is enabled
    --create-tables
            creates database tables for the data (if tables with the same name
            already exists, it is first dropped)
    --db-connection-url <value>
            is JDBC connection URL, for example:
            jdbc:postgresql://localhost/ruian?user=ruian&password=p4ssw0rd
    --debug
            if used, debug information are output
    --dest-srid <value>
            optional integer identifier of coordinate system to which the geometries
            should be transformed
    --dry-run
            processes all specified files but no data are written to database
    --ignore-invalid-gml
            if GML definition is not valid, application ignores the definition and
            saves the object without the definition (if this parameter is not
            specified, application throws exception and exits while trying to save
            invalid GML definition, the drawback of this parameter is that it makes
            the import little bit slower because each GML definition is checked
            twice - once during the check and the other time during saving in
            database)
    --input-dir <value>
            is directory that contains downloaded .xml.gz files
    --log-file <value>
            is optional specification of log file (if not specified, log will be
            output to console)
    --no-gis
            ignores all GIS information (Geometrie element) in the input XML file
    --reset-transaction-ids
            resets RÚIAN transaction ids so that following data import will update
            all data (data are updated only if new transaction id is greater than
            transaction id that is stored in database - this feature is useful in
            case you want to regenerate your data because of some issue with
            previous import, without deleting all data and starting with fresh
            database)
    --truncate-all
            removes data from all tables

## To do

List of planned features is at [wiki](https://github.com/fordfrog/ruian2pgsql/wiki).

## License

ruian2pgsql is distributed under MIT license.

## Changelog

## Version 1.6.1

* Fixed integer overflow in deleteParcela (Petr Vejsada)

## Version 1.6.0

* Added support for OriginalniHraniceOmpv. (Martin Kokeš)
* Added support for VolebniOkrsek. (Martin Kokeš)
* Added support for VOKod for AdresniMisto. (Martin Kokeš)
* Added PostgreSQL database schema upgrade scripts for upgrade to version 1.6.0.
  (Martin Kokeš)
* Fixed update of data with regard to the fact that transaction id is not changed
  on each data update. (Petr Morávek)

## Version 1.5.0

* Added command line switch --linearize-ewkt which enables internal linearization
  of curved geometries when EWKT is used.
* Added command line switch --truncate-all which removes data from all tables
  but prevents the database structure.
* Fixed problem with database changes not being committed to database when
  database modification was performed due to a command but no import was
  performed.
* Fixed re-creation of database schema when schema is already defined.

## Version 1.4.0

* Added --no-gis command line switch that disables import of GIS data.
* Added support for MySQL databases if --no-gis is used.
* Added --dest-srid command line switch that enables on the fly transformation
  of geometries to a desired SRID.
* Added support for EPSG::5514 coordinate system when parsing XML files.
* Prepared statements are now initialized only once and not each time new file
  is processed.

## Version 1.3.0

* Changed index creation DDL statements to make it compatible with PostgreSQL
  versions lower than 9.0.
* Added command line switch --dry-run for running the application without making
  any modification in database.
* Added command line switch --convert-to-ewkt which converts GML strings to EWKT
  before the geometries are stored in database. This is because the level of GML
  support in Postgis is not adequate to the GML geometries contained in RÚIAN
  files.
* Added command line switch --debug which outputs extra information.

## Version 1.2.0

* Added ruian_stats view that displays statistics of RÚIAN data and its
  geometries.
* When --ignore-invalid-gml is specified, if invalid GML is encountered, it is
  printed to the log.

## Version 1.1.0

* Added workaround for Postgis [bug](http://trac.osgeo.org/postgis/ticket/1928)
  (it does not parse MultiPoint with pointMembers and instead saves empty
  multipoint). The workaround is applied automatically if the target database is
  affected. Original GML is rewritten so that instead of pointMembers element
  pointMember element is used.
* Added command line switch --reset-transaction-ids.

## Version 1.0.0

* first release
