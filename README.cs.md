# ruian2pgsql

Převádí data z RÚIAN výměnného formátu do PostgreSQL databáze.

## Co přesně dělá?

ruian2pgsql je aplikace pro příkazový řádek. Čte data z XML souborů RÚIAN a
ukládá jejich obsah do databáze PostgreSQL. Neukládá historické záznamy, udržuje
pouze nejnovější záznamy všech objektů. Pomocí ruian2pgsql můžete importovat
jeden soubor stejně tak jako více souborů. Pokud jeden soubor naimportujete
několikrát, nebo se obsah jednoho souboru překrývá s obsahem jiného souboru,
ruian2pgsql naimportuje všechny objekty jen jednou (jsou jednoznačně určeny svým
unikátním klíčem přiřazeným v RÚIAN).

## Požadavky

* JRE nebo JDK 7+
* server PostgreSQL s nainstalovanou knihovnou PostGIS nebo MySQL server (pokud
  bude při importu použit přepínač --no-gis)
* Apache Maven 3+ (pouze v případě, že si chcete sami aplikaci zkompilovat)

## Kompilace

Není nutné, abyste aplikaci kompilovali sami, nejnovější zkompilovanou verzi si
můžete stáhnout z https://github.com/fordfrog/ruian2pgsql/downloads. Nicméně
kompilace ruian2pgsql je snadná. Jakmile nainstalujete Apache Maven, stačí
spustit příkaz `mvn package` v hlavním adresáři zdrojových souborů, ve kterém je
umístěný soubor pom.xml.

## Spuštění

Zde jsou informace o použití programu, které vypisuje ruian2pgsql, pokud ho
spustíte bez parametrů (výpis je v angličtině, zde je přeložený):

    Použití: java -cp ruian2pgsql-*.jar:jdbc-driver.jar com.fordfrog.ruian2pgsql.App <volby>

    Kde:
    jdbc-driver.jar
            je soubor JAR obsahující databázový ovladač (bud PostgreSQL nebo MySQL)
    --convert-to-ewkt
            pokud je použito, tak jsou nejprve GML řetězce převedeny ze zdrojového
            XML do EWKT formátu a následně uloženy do databáze, jinak jsou do
            databáze ukládány původní GML řetězce (použijte tuto volbu v závislosti
            na úrovni GML implementace ve vámi používané verzi Postgisu)
    --linearize-ewkt
            pokud je použito, tak jsou nejprve obloukové geometrie v EWKT linerizovány
            před uložením do databáze; je podporováno pouze v kombinaci s volbou
            --convert-to-ewkt
    --create-tables
            vytvoří databázové tabulky pro data (pokud tabulky se stejným názvem
            již existují, tak jsou nejdříve odstraněny)
    --db-connection-url <hodnota>
            je připojovací URL ve formátu JDBC, například:
            jdbc:postgresql://localhost/ruian?user=ruian&password=p4ssw0rd
    --debug
            pokud je použito, jsou vypisovány ladící informace
    --dest-srid <value>
            volitelný celočíselný identifikátor souřadnicového systému, do kterého
            mají být geometrie převedeny
    --dry-run
            zpracuje všechny uvedené soubory, ale žádná data nezapíše do
            databáze
    --ignore-invalid-gml
            pokud je GML definice chybná, aplikace ji ignoruje a uloží objekt
            bez dané definice (pokud tento parametr není uvedený, aplikace
            skončí vyjímkou při pokusu o uložení chybné GML definice, nevýhodou
            tohoto parametru je, že import mírně zpomaluje, protože každá GML
            definice je kontrolovaná dvakrát - jednou při kontrole a podruhé při
            ukládání do databáze)
    --input-dir <hodnota>
            je adresář, který obsahuje stažené soubory s příponou .xml.gz nebo .xml.zip
    --log-file <hodnota>
            je volitelná specifikace logovacího souboru (pokud není uveden, log
            bude zapsaný na konzoli)
    --no-gis
            ignoruje všechny GIS informace (element Geometrie) ve vstupním XML
            souboru
    --reset-transaction-ids
            zresetuje transakční id systému RÚIAN, takže při následujícím
            importu budou veškerá data přepsána (data jsou aktualizována pouze
            v případě, že nové id transakce je větší než id transakce uložené
            v databázi - tato funkce je užitečná pokud potřebujete zregenerovat
            data z důvodu nějaké chyby při importu, bez nutnosti smazání všech
            dat a importu do čisté databáze)
    --truncate-all
            odstraní data ze všech tabulek

## To do

Seznam plánovaných funkcí je na [wiki](https://github.com/fordfrog/ruian2pgsql/wiki).

## Licence

ruian2pgsql je distribuovaný pod MIT licencí.

## Changelog
## Version 1.7.0

* Přidána podpora pro VFR ver. 3.1 (David Pavlíček)
* Přidána podpora pro mazání prvků ze všech importovaných tabulek (David Pavlíček) 
* Přidán skript pro aktualizaci schématu PostgreSQL databáze na verzi 1.6.0
  (David Pavlíček)

## Verze 1.6.1

* optimalizace rychlosti při inicializaci databáze (Dusan Stloukal)
* opraveno mazání parcel (Martin Kokeš)
* aktualizována verze PostgreSQL ovladače (Dusan Stloukal)

## Verze 1.6.0

* Přidána podpora pro OriginalniHraniceOmpv. (Martin Kokeš)
* Přidána podpora pro VolebniOkrsek. (Martin Kokeš)
* Přidána podpora pro VOKod v objektu AdresniMisto. (Martin Kokeš)
* Přidán skript pro aktualizaci schématu PostgreSQL databáze na verzi 1.6.0.
  (Martin Kokeš)
* Opravena aktualizace dat vzhledem k faktu, že id transakce není změněno při každé
  změně dat. (Petr Morávek)

## Verze 1.5.0

* Přidán přepínač --linearize-ewkt, který zapíná interní linearizaci geometrií
  s křivkami, pokud je použito EWKT.
* Přidán přepínač --truncate-all, který odstraní data ze všech tabulek, ale
  zachová databázovou strukturu.
* Opravena chyba, kdy změny v databázi nebyly uloženy, když byly provedeny změny
  z důvodu použití přepínače na příkazové řádce, ale žádná data nebyla
  naimportována.
* Opravena chyba při opětovném vytvoření již existující databázové struktury.

## Verze 1.4.0

* Přidán přepínač --no-gis, který vypíná import GIS dat.
* Přidána podpora pro databáze MySQL, pokud je použit přepínač --no-gis.
* Přidán přepínač --dest-srid, který umožnuje za běhu převést geometrie do
  požadovaného SRID.
* Přidána podpora pro EPSG::5514 souřadnicový systém při parsování XML souborů.
* Předpřipravené statementy jsou nyní inicializovány pouze jednou, ne pokaždé
  když se zpracovává nový soubor.

## Verze 1.3.0

* Změněny DDL příkazy pro vytváření indexů tak, aby byly kompatibilní i s
  PostgreSQL verzemi menšími než 9.0.
* Přidán přepínač --dry-run pro spuštění aplikace v režimu, kdy žádná data
  nejsou zapsána do databáze.
* Přidán přepínač --convert-to-ewkt, který převádí GML řetězce do formátu EWKT
  před uložením geometrií do databáze. To proto, že úroveň podpory GML v
  Postgisu není dostatečná pro GML data obsažená v RÚIAN souborech.
* Přidán přepínač --debug, který vypisuje ladící informace.

## Verze 1.2.0

* Přidán pohled ruian_stats, který zobrazuje statistiky RÚIAN dat a jejich
  geometrií.
* Pokud je použitý přepínač --ignore-invalid-gml, tak pokud program narazí na
  chybný GML řetězec, tak ho vypíše do logu.

### Verze 1.1.0

* Přidána úprava, která řeší [chybu](http://trac.osgeo.org/postgis/ticket/1928)
  v Postgisu (neparsuje MultiPoint s elementem pointMembers a místo toho ukládá
  prázdný multipoint). Oprava chyby se aplikuje automaticky, pokud cílová
  databáze chybu obsahuje. Původní GML se přepíše tak, že místo pointMembers se
  použije element pointMember.
* Přidán přepínač --reset-transaction-ids.

### Verze 1.0.0

* první vydaná verze
