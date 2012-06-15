package skyproc;

/**
 * Different record headers in Skyrim that are currently supported in SkyProc.
 * @author Justin Swanson
 */
public enum Type {

    /**
     * A special enum meant to represent a bad/temporary record inside SkyProc.
     */
    NULL,
    /**
     * Used in mod headers
     */
    TES4,
    /**
     * TES4: Used in mod headers
     */
    HEDR,
    /**
     * TES4: A master in the mod.
     */
    MAST,
    /**
     * TES4: Used in headers. Internal version?
     */
    INTV,

    /**
     * Used to mark a group of common records.
     */
    GRUP,
    /**
     * Editor ID for all major records.
     */
    EDID,
    /**
     * Object Bounds?
     * LVLN:
     * NPC_:
     * SPEL:
     */
    OBND, //Object Bounds?  Useless?
    /**
     * PERK: Perk description
     * SPEL: Spell description
     * RACE: Race Desc
     */
    DESC,
    /**
     * NPC_: Name
     * PERK: Name
     * BODT: Name
     */
    FULL,

    /**
     * GRUP: Image Space records
     */
    IMGS,
    /**
     * IMGS: Unknown
     */
    ENAM,
    /**
     * IMGS: HDR data
     */
    HNAM,
    /**
     * IMGS: Tinting data
     */
    TNAM,

    /**
     * GRUP: Perk records
     */
    PERK,
    /**
     * PERK: Conditions
     * SPEL:EFID
     */
    CTDA,
    /**
     * PERK: Unknown?  Points to the next PERK
     * PERK:PRKE: Condition data for the section.
     */
    NNAM,
    /**
     * PERK: Inventory image filename.
     */
    ICON,
    /**
     * PERK: Section type
     */
    PRKE,
    /**
     * PERK: Section end
     */
    PRKF,
    /**
     * PERK: Condition type
     */
    PRKC,
    /**
     * PERK:PRKE:CTDA Script variable
     * SPEL:EFID:CTDA
     */
    CIS1,
    /**
     * PERK:PRKE:CTDA Script variable 2
     * SPEL:EFID:CTDA
     */
    CIS2,
    /**
     * PERK:PRKE: Perk data type
     */
    EPFT,
    /**
     * PERK:PRKE:EPFT: Perk data
     */
    EPFD,
    /**
     * PERK:PRKE:EPFT: Unknown?
     */
    EPF2,
    /**
     * PERK:PRKE:EPFT: Unknown?
     */
    EPF3,

    /**
     * GRUP: Leveled List records
     */
    LVLN,
    /**
     * LVLN: Entry in the Leveled List
     */
    LVLO,
    /**
     * LVLN: Leveled List flags
     */
    LVLF,
    /**
     * LVLN: Leveled List chance none
     */
    LVLD,
    /**
     * LVLN: Number of entries in the LVLN
     */
    LLCT,
    /**
     * LVLN: Model path
     */
    MODL,
    /**
     * LVLN: Unknown?
     * RACE: Model Data
     */
    MODT,
    /**
     * LVLN: Unknown?
     */
    COED,

    /**
     * GRUP: Non-player Character records
     */
    NPC_,
    /**
     * NPC_: Script Group
     * PERK: Script Group
     */
    VMAD,
    /**
     * NPC_: Level, flags, and other misc stats
     */
    ACBS,
    /**
     * NPC_: Death item?
     */
    INAM,
    /**
     * NPC_: Voice type
     * RACE: Default voice types M/F
     */
    VTCK,
    /**
     * NPC_: Tempate?
     */
    TPLT,
    /**
     * NPC_: Race
     */
    RNAM,
    /**
     * NPC_: AI data
     */
    AIDT,
    /**
     * NPC_: Number of spells
     * RACE: Number of race-inherited spells
     */
    SPCT,
    /**
     * NPC_: Spell reference
     * RACE: Race-inherited spells
     */
    SPLO,
    /**
     * NPC_: Number of perks
     */
    PRKZ,
    /**
     * NPC_: Perk reference
     */
    PRKR,
    /**
     * NPC_: Class
     */
    CNAM,
    /**
     * NPC_: Skill stats and Health/magic stats
     */
    DNAM,
    /**
     * NPC_: Head parts
     * RACE: unknown
     */
    PNAM,
    /**
     * NPC_: Hair color
     */
    HCLF,
    /**
     * NPC_: Unknown
     * RACE: impact data set
     */
    NAM5,
    /**
     * NPC_: Scale
     */
    NAM6,
    /**
     * NPC_: Unknown
     * RACE: Decap fx
     */
    NAM7,
    /**
     * NPC_: Unknown
     */
    NAM8,
    /**
     * NPC_: Material lighting
     */
    NAM9,
    /**
     * NPC_: Worn armor
     * RACE: Naked armor
     */
    WNAM,
    /**
     * NPC_: Attack race
     */
    ATKR,
    /**
     * NPC_: Attack data
     * RACE: unknown
     */
    ATKD,
    /**
     * NPC_: Attack events
     * RACE: Attack event
     */
    ATKE,
    /**
     * NPC_: Number of items
     */
    COCT,
    /**
     * NPC_: Item reference
     */
    CNTO,
    /**
     * NPC_: AI package
     */
    PKID,
    /**
     * NPC_: Material Lighting 2
     * RACE: Equip slot
     */
    QNAM,
    /**
     * NPC_: Default Outfit
     */
    DOFT,
    /**
     * NPC_: Sleeping Outfit
     */
    SOFT,
    /**
     * NPC_: Short alias
     */
    SHRT,
    /**
     * NPC_: Combat style
     */
    ZNAM,
    /**
     * NPC_: Crime faction
     */
    CRIF,
    /**
     * NPC_: Feature set / Head texture
     */
    FTST,
    /**
     * NPC_: Audio template
     */
    CSCR,
    /**
     * NPC_: Default package list
     */
    DPLT,
    /**
     * NPC_: Sound placement (footstep, etc)
     */
    CSDT,
    /**
     * NPC_:CSDT: Sound Reference
     */
    CSDI,
    /**
     * NPC_:CSDT:CSDI: Chance for sound to play
     */
    CSDC,
    /**
     * NPC_: Worn armor 2
     * RACE:MNAM: Skeletal Model
     * RACE:FNAM: Skeletal Model
     */
    ANAM,
    /**
     * NPC_: Face parts
     */
    NAMA,
    /**
     * NPC_: Tint mask index?
     * RACE: Tint index
     */
    TINI,
    /**
     * NPC_:TINI: Tint mask color?
     */
    TINC,
    /**
     * NPC_:TINI: Tint mask value?
     */
    TINV,
    /**
     * NPC_:TIAS: Unknown
     */
    TIAS,
    /**
     * NPC_: Number of keywords
     * RACE: Number of keywords
     */
    KSIZ,
    /**
     * NPC_: Keyword reference
     * RACE: Keyword reference
     */
    KWDA,
    /**
     * NPC_: Escape combat override
     */
    ECOR,
    /**
     * NPC_: Destruction data?
     */
    DEST,
    /**
     * NPC_: Destruction data?
     */
    DSTD,
    /**
     * NPC_: Destruction data?
     */
    DSTF,
    /**
     * NPC_: Spell override?
     */
    SPOR,
    /**
     *
     */
    GWOR,
    /**
     *
     */
    OCOR,
    /**
     * NPC_: Unknown
     * RACE: body part data
     */
    GNAM,
    /**
     * NPC_: Faction reference and rank
     */
    SNAM,

    /**
     * GRUP: Spell records
     */
    SPEL,
    /**
     * SPEL: Inventory Model
     */
    MDOB,
    /**
     * SPEL: Equip Slot
     */
    ETYP,
    /**
     * SPEL: Effect FormID
     */
    EFID,
    /**
     * SPEL: Effect Item
     */
    EFIT,
    /**
     * SPEL Spell Item
     */
    SPIT,


    /**
     * GRUP: Race records
     */
    RACE,
    /**
     * RACE: body template
     */
    BODT,
    /**
     * RACE: Male marker
     */
    MNAM,
    /**
     * RACE: Female marker
     */
    FNAM,
    /**
     *
     */
    MTNM,
    /**
     *
     */
    TINL,
    /**
     *
     */
    UNAM,
    /**
     *
     */
    INDX,
    /**
     *
     */
    NAM1,
    /**
     *
     */
    NAM3,
    /**
     *
     */
    NAM4,
    /**
     *
     */
    ONAM,
    /**
     *
     */
    LNAM,
    /**
     *
     */
    NAME,
    /**
     *
     */
    MTYP,
    /**
     *
     */
    SPED,
    /**
     *
     */
    VNAM,
    /**
     *
     */
    UNES,
    /**
     *
     */
    PHTN,
    /**
     *
     */
    PHWT,
    /**
     *
     */
    NAM0,
    /**
     *
     */
    HEAD,
    /**
     *
     */
    MPAI,
    /**
     *
     */
    MPAV,
    /**
     *
     */
    RPRM,
    /**
     *
     */
    RPRF,
    /**
     *
     */
    AHCM,
    /**
     *
     */
    AHCF,
    /**
     *
     */
    FTSM,
    /**
     *
     */
    FTSF,
    /**
     *
     */
    DFTM,
    /**
     *
     */
    DFTF,
    /**
     *
     */
    WKMV,
    /**
     *
     */
    TINT,
    /**
     *
     */
    TINP,
    /**
     *
     */
    TIND,
    /**
     *
     */
    TIRS,
    /**
     *
     */
    RNMV,
    /**
     *
     */
    SWMV,
    /**
     *
     */
    FLMV,
    /**
     *
     */
    SNMV,

    /**
     *
     */
    ARMO,
    /**
     *
     */
    EITM,
    /**
     *
     */
    MOD2,
    /**
     *
     */
    MO2T,
    /**
     *
     */
    MO2S,
    /**
     *
     */
    MOD4,
    /**
     *
     */
    MO4T,
    /**
     *
     */
    MO4S,
    /**
     *
     */
    YNAM,
    /**
     *
     */
    BIDS,
    /**
     *
     */
    BAMT,

    /**
     *
     */
    ARMA,
    /**
     *
     */
    MOD3,
    /**
     *
     */
    MO3T,
    /**
     *
     */
    MO3S,
    /**
     *
     */
    MOD5,
    /**
     *
     */
    MO5T,
    /**
     *
     */
    MO5S,
    /**
     *
     */
    NAM2,
    /**
     *
     */
    SNDD,

    /**
     *
     */
    TXST,
    /**
     *
     */
    TX00,
    /**
     *
     */
    TX01,
    /**
     *
     */
    TX02,
    /**
     *
     */
    TX03,
    /**
     *
     */
    TX04,
    /**
     *
     */
    TX05,
    /**
     *
     */
    TX06,
    /**
     *
     */
    TX07,
    /**
     *
     */
    DODT,

    /**
     *
     */
    WEAP,
    /**
     *
     */
    CRDT,
    /**
     *
     */
    EAMT,
    /**
     *
     */
    MODS,

    /**
     * s
     */
    KYWD,
    /**
     *
     */
    AMMO,
    /**
     *
     */
    FLST,

    /**
     *
     */
    MGEF,
    /**
     *
     */
    ESCE,
    /**
     *
     */
    ALCH,
    /**
     *
     */
    ENIT,
    /**
     *
     */
    MICO,
    /**
     *
     */
    INGR,
    /**
     *
     */
    FACT,
    /**
     *
     */
    XNAM,
    /**
     *
     */
    JAIL,
    /**
     *
     */
    WAIT,
    /**
     *
     */
    STOL,
    /**
     *
     */
    PLCN,
    /**
     *
     */
    CRGR,
    /**
     *
     */
    JOUT,
    /**
     *
     */
    CRVA,
    /**
     *
     */
    VEND,
    /**
     *
     */
    VENC,
    /**
     *
     */
    VENV,
    /**
     *
     */
    CITC,
    /**
     *
     */
    PLVD,

    /**
     *
     */
    GMST,
    ENCH,
    LVLI,
    LVLG,
    AVIF,
    AVSK,

    QUST,
    NEXT,
    
    ECZN,
    
    /**
     * NPC_: Marks DNAM position?
     * PERK: Perk flags
     * PERK:PRKE: Perk package data
     * TES4:MAST: Unknown / Useless
     * RACE: unknown
     */
    DATA;

    /**
     *
     * @param in
     * @return
     */
    public static Type toRecord(Enum in) {
        return Type.valueOf(in.toString());
    }

    /**
     *
     * @param in
     * @return
     */
    public static Type[] toRecord (Enum[] in) {
        Type[] out = new Type[in.length];
        for (int i = 0 ; i < in.length ; i++) {
            out[i]= toRecord(in[i]);
        }
        return out;
    }
}
