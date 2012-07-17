DROP TABLE IF EXISTS hlavicka;
DROP TABLE IF EXISTS rn_stat;
DROP TABLE IF EXISTS rn_region_soudrznosti;
DROP TABLE IF EXISTS rn_vusc;
DROP TABLE IF EXISTS rn_kraj_1960;
DROP TABLE IF EXISTS rn_okres;
DROP TABLE IF EXISTS rn_orp;
DROP TABLE IF EXISTS rn_pou;
DROP TABLE IF EXISTS rn_obec;
DROP TABLE IF EXISTS rn_cast_obce;
DROP TABLE IF EXISTS rn_mop;
DROP TABLE IF EXISTS rn_spravni_obvod;
DROP TABLE IF EXISTS rn_momc;
DROP TABLE IF EXISTS rn_parcela;
DROP TABLE IF EXISTS rn_zpusob_ochrany_pozemku;
DROP TABLE IF EXISTS rn_bonit_dily_parcel;
DROP TABLE IF EXISTS rn_ulice;
DROP TABLE IF EXISTS rn_stavebni_objekt;
DROP TABLE IF EXISTS rn_detailni_tea;
DROP TABLE IF EXISTS rn_zpusob_ochrany_objektu;
DROP TABLE IF EXISTS rn_adresni_misto;
DROP TABLE IF EXISTS rn_katastralni_uzemi;
DROP TABLE IF EXISTS rn_zsj;

CREATE TABLE hlavicka (
    typ_zaznamu varchar,
    typ_davky varchar,
    typ_souboru varchar,
    datum date,
    transakce_od_id int,
    transakce_od_zapsano timestamp without time zone,
    transakce_do_id int,
    transakce_do_zapsano timestamp without time zone,
    predchozi_soubor varchar,
    plny_soubor varchar,
    metadata varchar,
    import_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_stat (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    id_trans_ruian bigint,
    nuts_lau varchar,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_region_soudrznosti (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    stat_kod int,
    id_trans_ruian bigint,
    nuts_lau varchar,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_vusc (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    regsoudr_kod int,
    id_trans_ruian bigint,
    nuts_lau varchar,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_kraj_1960 (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    stat_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_okres (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    vusc_kod int,
    kraj_1960_kod int,
    id_trans_ruian bigint,
    nuts_lau varchar,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_orp (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    vusc_kod int,
    spravni_obec_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_pou (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    orp_kod int,
    spravni_obec_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_obec (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    okres_kod int,
    pou_kod int,
    nuts_lau varchar,
    mluv_char_pad_2 varchar,
    mluv_char_pad_3 varchar,
    mluv_char_pad_4 varchar,
    mluv_char_pad_5 varchar,
    mluv_char_pad_6 varchar,
    mluv_char_pad_7 varchar,
    zmena_grafiky boolean,
    cleneni_sm_rozsah_kod int,
    cleneni_sm_typ_kod int,
    status_kod int,
    vlajka_text varchar,
    vlajka_obrazek bytea,
    znak_text varchar,
    znak_obrazek bytea,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_cast_obce (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    mluv_char_pad_2 varchar,
    mluv_char_pad_3 varchar,
    mluv_char_pad_4 varchar,
    mluv_char_pad_5 varchar,
    mluv_char_pad_6 varchar,
    mluv_char_pad_7 varchar,
    id_trans_ruian bigint,
    zmena_grafiky boolean,
    plati_od date,
    nz_id_globalni bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_mop (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_spravni_obvod (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    spravni_momc_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_momc (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    mop_kod int,
    spravobv_kod int,
    mluv_char_pad_2 varchar,
    mluv_char_pad_3 varchar,
    mluv_char_pad_4 varchar,
    mluv_char_pad_5 varchar,
    mluv_char_pad_6 varchar,
    mluv_char_pad_7 varchar,
    zmena_grafiky boolean,
    vlajka_text varchar,
    vlajka_obrazek bytea,
    znak_text varchar,
    znak_obrazek bytea,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_parcela (
    id bigint PRIMARY KEY,
    nespravny boolean,
    katuz_kod int,
    druh_pozemku_kod int,
    druh_cislovani_kod int,
    kmenove_cislo int,
    poddeleni_cisla int,
    vymera_parcely bigint,
    id_trans_ruian bigint,
    zpusob_vyu_poz_kod int,
    rizeni_id bigint,
    plati_od date,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_zpusob_ochrany_pozemku (
    kod int,
    zpusob_ochrany_kod int,
    parcela_id bigint,
    id_trans_ruian bigint,
    rizeni_id bigint,
    deleted boolean DEFAULT false
);

CREATE TABLE rn_bonit_dily_parcel (
    parcela_id bigint,
    bpej_kod int,
    vymera int,
    id_trans_ruian bigint,
    rizeni_id bigint,
    deleted boolean DEFAULT false
);

CREATE TABLE rn_ulice (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    zmena_grafiky boolean,
    definicni_cara geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_stavebni_objekt (
    kod int PRIMARY KEY,
    nespravny boolean,
    identifikacni_parcela_id bigint,
    momc_kod int,
    cobce_kod int,
    budova_id bigint,
    cisla_domovni int[],
    dokonceni date,
    je_vytah_kod int,
    zmena_grafiky boolean,
    druh_konstrukce_kod int,
    zmena_detailu boolean,
    obestaveny_prostor int,
    pocet_bytu int,
    pocet_podlazi int,
    podlahova_plocha int,
    pripoj_el_energie boolean,
    pripoj_kanal_sit_kod int,
    pripoj_plyn_kod int,
    pripoj_vodovod_kod int,
    typ_kod int,
    zastavena_plocha int,
    zpusob_vytapeni_kod int,
    zpusob_vyuziti_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_detailni_tea (
    kod int,
    stavobj_kod int,
    adresni_misto_kod int,
    nespravny boolean,
    pocet_bytu int,
    pocet_podlazi int,
    druh_konstrukce_kod int,
    pripoj_kanal_sit_kod int,
    pripoj_plyn_kod int,
    pripoj_vodovod_kod int,
    pripoj_el_energie boolean,
    zpusob_vytapeni_kod int,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    deleted boolean DEFAULT false
);

CREATE TABLE rn_zpusob_ochrany_objektu (
    stavobj_kod int,
    kod int,
    zpusob_ochrany_kod int,
    id_trans_ruian bigint,
    rizeni_id bigint,
    deleted boolean DEFAULT false
);

CREATE TABLE rn_adresni_misto (
    kod int PRIMARY KEY,
    nespravny boolean,
    adrp_psc int,
    ulice_kod int,
    stavobj_kod int,
    cislo_domovni int,
    cislo_orientacni_hodnota int,
    cislo_orientacni_pismeno varchar,
    id_trans_ruian bigint,
    plati_od date,
    zmena_grafiky boolean,
    nz_id_globalni bigint,
    definicni_bod geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_katastralni_uzemi (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    obec_kod int,
    ma_dkm boolean,
    mluv_char_pad_2 varchar,
    mluv_char_pad_3 varchar,
    mluv_char_pad_4 varchar,
    mluv_char_pad_5 varchar,
    mluv_char_pad_6 varchar,
    mluv_char_pad_7 varchar,
    id_trans_ruian bigint,
    plati_od date,
    nz_id_globalni bigint,
    rizeni_id bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);

CREATE TABLE rn_zsj (
    kod int PRIMARY KEY,
    nazev varchar,
    nespravny boolean,
    katuz_kod int,
    charakter_zsj_kod int,
    mluv_char_pad_2 varchar,
    mluv_char_pad_3 varchar,
    mluv_char_pad_4 varchar,
    mluv_char_pad_5 varchar,
    mluv_char_pad_6 varchar,
    mluv_char_pad_7 varchar,
    vymera bigint,
    plati_od date,
    zmena_grafiky boolean,
    nz_id_globalni bigint,
    id_trans_ruian bigint,
    definicni_bod geometry,
    hranice geometry,
    item_timestamp timestamp without time zone DEFAULT timezone('utc', now()),
    deleted boolean DEFAULT false
);