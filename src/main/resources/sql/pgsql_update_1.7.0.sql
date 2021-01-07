DO $$
    BEGIN
        BEGIN
            ALTER TABLE rn_orp ADD COLUMN okres_kod integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'Column okres_kod already exists in rn_orp';
        END;
    END;
$$;
DO $$
    BEGIN
        BEGIN
            CREATE INDEX rn_orp_okres_kod_idx ON rn_orp (okres_kod);
        EXCEPTION
            WHEN duplicate_table THEN RAISE NOTICE 'Index rn_orp_okres_kod_idx already exists.';
        END;
    END;
$$;