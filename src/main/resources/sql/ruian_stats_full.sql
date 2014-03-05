CREATE OR REPLACE VIEW ruian_stats_full AS
    SELECT t.table_name, count(*) AS total,
        count(t.definicni_bod) AS bod,
        count(t.cary) AS cary,
        count(CASE WHEN t.cary IS NULL OR st_isvalid(st_curvetoline(t.cary)) THEN NULL ELSE true END) AS invalid_cary,
        ((count(t.definicni_bod) * 100) / count(*)) AS p_bod,
        ((count(t.cary) * 100) / count(*)) AS p_cary
    FROM (((((((((((((((((
        SELECT 'rn_adresni_misto'::text AS table_name,
            rn_adresni_misto.definicni_bod,
            NULL::geometry AS cary
            FROM rn_adresni_misto
            WHERE deleted = false
        UNION ALL SELECT 'rn_cast_obce'::text, rn_cast_obce.definicni_bod,
            rn_cast_obce.hranice
            FROM rn_cast_obce
            WHERE deleted = false)
        UNION ALL SELECT 'rn_katastralni_uzemi'::text,
            rn_katastralni_uzemi.definicni_bod,
            rn_katastralni_uzemi.hranice
            FROM rn_katastralni_uzemi
            WHERE deleted = false)
        UNION ALL SELECT 'rn_kraj_1960'::text,
            rn_kraj_1960.definicni_bod,
            rn_kraj_1960.hranice
            FROM rn_kraj_1960
            WHERE deleted = false)
        UNION ALL SELECT 'rn_momc'::text,
            rn_momc.definicni_bod,
            rn_momc.hranice
            FROM rn_momc
            WHERE deleted = false)
        UNION ALL SELECT 'rn_mop'::text,
            rn_mop.definicni_bod,
            rn_mop.hranice
            FROM rn_mop
            WHERE deleted = false)
        UNION ALL SELECT 'rn_obec'::text,
            rn_obec.definicni_bod,
            rn_obec.hranice
            FROM rn_obec
            WHERE deleted = false)
        UNION ALL SELECT 'rn_okres'::text,
            rn_okres.definicni_bod,
            rn_okres.hranice
            FROM rn_okres
            WHERE deleted = false)
        UNION ALL SELECT 'rn_orp'::text,
            rn_orp.definicni_bod,
            rn_orp.hranice FROM rn_orp
            WHERE deleted = false)
        UNION ALL SELECT 'rn_parcela'::text,
            rn_parcela.definicni_bod,
            rn_parcela.hranice FROM rn_parcela
            WHERE deleted = false)
        UNION ALL SELECT 'rn_pou'::text,
            rn_pou.definicni_bod,
            rn_pou.hranice
            FROM rn_pou
            WHERE deleted = false)
        UNION ALL SELECT 'rn_region_soudrznosti'::text,
            rn_region_soudrznosti.definicni_bod,
            rn_region_soudrznosti.hranice
            FROM rn_region_soudrznosti
            WHERE deleted = false)
        UNION ALL SELECT 'rn_spravni_obvod'::text,
            rn_spravni_obvod.definicni_bod,
            rn_spravni_obvod.hranice
            FROM rn_spravni_obvod
            WHERE deleted = false)
        UNION ALL SELECT 'rn_stat'::text,
            rn_stat.definicni_bod,
            rn_stat.hranice
            FROM rn_stat
            WHERE deleted = false)
        UNION ALL SELECT 'rn_stavebni_objekt'::text,
            rn_stavebni_objekt.definicni_bod,
            rn_stavebni_objekt.hranice
            FROM rn_stavebni_objekt
            WHERE deleted = false)
        UNION ALL SELECT 'rn_ulice'::text,
            NULL::geometry,
            rn_ulice.definicni_cara
            FROM rn_ulice
            WHERE deleted = false)
        UNION ALL SELECT 'rn_vo'::text,
            rn_vo.definicni_bod,
            rn_vo.hranice
            FROM rn_vo
            WHERE deleted = false)
        UNION ALL SELECT 'rn_vusc'::text,
            rn_vusc.definicni_bod,
            rn_vusc.hranice
            FROM rn_vusc
            WHERE deleted = false)
        UNION ALL SELECT 'rn_zsj'::text,
            rn_zsj.definicni_bod,
            rn_zsj.hranice
            FROM rn_zsj
            WHERE deleted = false) t
        GROUP BY t.table_name
        ORDER BY t.table_name;
