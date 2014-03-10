DO $$
    BEGIN
        BEGIN
            ALTER TABLE rn_adresni_misto ADD COLUMN vo_kod integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'Column vo_kod already exists in rn_adresni_misto.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_adresni_misto_vo_kod_idx ON rn_adresni_misto (vo_kod);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Index rn_adresni_misto_vo_kod_idx already exists.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE TABLE rn_vo (
                kod int PRIMARY KEY,   -- Kód VO
                cislo int,             -- Číslo VO unikátní v rámci obce nebo MOMC
                nespravny boolean,     -- Příznak nesprávnosti
                obec_kod int,          -- Nadřazená obec k VO
                momc_kod int,          -- Nadřazený MOMC k VO
                poznamka varchar,      -- Poznámka k VO
                plati_od date,         -- Začátek platnosti
                plati_do date,         -- Konec platnosti
                id_trans_ruian bigint, -- ID transakce v RÚIAN
                nz_id_globalni bigint, -- ID návrhu změny v ISÚI
                definicni_bod geometry,
                hranice geometry,
                item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
                deleted boolean DEFAULT false
            );
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Table rn_vo already exists.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_vo_obec_kod_idx ON rn_vo (obec_kod);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Index rn_vo_obec_kod_idx already exists.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_vo_momc_kod_idx ON rn_vo (momc_kod);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Index rn_vo_momc_kod_idx already exists.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_vo_definicni_bod_idx ON rn_vo USING GIST (definicni_bod);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'rn_vo_definicni_bod_idx already exists.';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_vo_hranice_idx ON rn_vo USING GIST (hranice);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Index rn_vo_hranice_idx already exists.';
        END;
    END;
$$;
