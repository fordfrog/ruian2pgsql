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
* server PostgreSQL s nainstalovanou knihovnou PostGIS
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

    Použití: java -jar ruian2pgsql-*-jar-with-dependencies.jar --db-connection-url <hodnota>
        [--create-tables] [--reset-transaction-ids] --input-dir <hodnota> [--log-file <hodnota>]

    Kde:
    --create-tables
            vytvoří databázové tabulky pro data (pokud tabulky se stejným názvem
            již existují, tak jsou nejdříve odstraněny)
    --db-connection-url
            je připojovací URL ve formátu JDBC, například:
            jdbc:postgresql://localhost/ruian?user=ruian&password=p4ssw0rd
    --input-dir
            je adresář, který obsahuje stažené soubory s příponou .xml.gz
    --log-file
            je volitelná specifikace logovacího souboru (pokud není uveden, log
            bude zapsaný na konzoli)
    --ignore-invalid-gml
            pokud je GML definice chybná, aplikace ji ignoruje a uloží objekt
            bez dané definice (pokud tento parametr není uvedený, aplikace
            skončí vyjímkou při pokusu o uložení chybné GML definice, nevýhodou
            tohoto parametru je, že import mírně zpomaluje, protože každá GML
            definice je kontrolovaná dvakrát - jednou při kontrole a podruhé při
            ukládání do databáze)
    --reset-transaction-ids
            zresetuje transakční id systému RÚIAN, takže při následujícím
            importu budou veškerá data přepsána (data jsou aktualizována pouze
            v případě, že nové id transakce je větší než id transakce uložené
            v databázi - tato funkce je užitečná pokud potřebujete zregenerovat
            data z důvodu nějaké chyby při importu, bez nutnosti smazání všech
            dat a importu do čisté databáze)

## To do

Seznam plánovaných funkcí je na [wiki](https://github.com/fordfrog/ruian2pgsql/wiki).

## Licence

ruian2pgsql je distribuovaný pod MIT licencí.

## Changelog

### Verze 1.1.0

* Přidána úprava, která řeší [chybu](http://trac.osgeo.org/postgis/ticket/1928)
  v Postgisu (neparsuje MultiPoint s elementem pointMembers a místo toho ukládá
  prázdný multipoint). Oprava chyby se aplikuje automaticky, pokud cílová
  databáze chybu obsahuje. Původní GML se přepíše tak, že místo pointMembers se
  použije element pointMember.
* Přidán přepínač --reset-transaction-ids.

### Verze 1.0.0

* první vydaná verze