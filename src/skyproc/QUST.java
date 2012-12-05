/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.LFlags;

/**
 *
 * @author Justin Swanson
 */
public class QUST extends MajorRecordNamed {

    static final SubRecordsPrototype QUSTproto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    reposition(Type.FULL);
	    add(new DNAM());
	    add(new SubString(Type.ENAM, false));
	    add(new SubString(Type.FLTR, true));
	    add(new SubList<>(new Condition()));
	    add(new SubList<>(new SubString(Type.CIS2, true)));
	    SubData next = new SubData(Type.NEXT);
	    next.forceExport = true;
	    add(next);
	    add(new SubList<>(new INDX()));
	    add(new SubInt(Type.ANAM));
	    add(new SubList<>(new QOBJ()));
	    add(new SubList<>(new ALST()));
	    add(new SubList<>(new ALLS()));
	}
    };
    static Type[] types = { Type.QUST };

    QUST () {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    QUST (Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	subRecords.getSubData(Type.DNAM).setData(0x111,12);
	subRecords.getSubData(Type.NEXT).forceExport(true);
	subRecords.getSubData(Type.ANAM).initialize(4);
    }

    static class DNAM extends SubRecord {

	LFlags flags1 = new LFlags(1);
	LFlags flags2 = new LFlags(1);
	int priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	int questType = 0;
	
	DNAM () {
	    super(Type.DNAM);
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}
	
    }

    static class INDX extends SubShellBulkType {

	SubInt INDX = new SubInt(Type.INDX);
	SubList<SubFlag> QSDTs = new SubList<>(new SubFlag(Type.QSDT, 1));
	SubString CNAM = new SubString(Type.CNAM, true);
	SubList<SubData> SCHRs = new SubList<>(new SubData(Type.SCHR));
	SubList<SubForm> QNAMs = new SubList<>(new SubForm(Type.QNAM));
	SubList<SubString> SCTXs = new SubList<>(new SubString(Type.SCTX, false));
	SubList<Condition> CONDs = new SubList<>(new Condition());
	
	static Type[] types = { Type.QSDT , Type.CNAM, Type.SCHR, Type.QNAM, 
	    Type.SCTX, Type.CTDA, Type.CIS1, Type.CIS2 };
	
	INDX() {
	    super(Type.INDX, types);
	    
	    subRecords.add(INDX);
	    subRecords.add(QSDTs);
	    subRecords.add(CNAM);
	    subRecords.add(SCHRs);
	    subRecords.add(QNAMs);
	    subRecords.add(SCTXs);
	    subRecords.add(CONDs);
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new INDX();
	}
	
    }
    
    static class ALSTALLS extends SubShellBulkType {

	SubList<SubString> ALID = new SubList<>(new SubString(Type.ALID, true));
	SubData ALED = new SubData(Type.ALED);
	SubForm ALUA = new SubForm(Type.ALUA);
	SubForm ALCO = new SubForm(Type.ALCO);
	SubList<SubForm> ALEQs = new SubList<>(new SubForm(Type.ALEQ));
	SubString ALFE = new SubString(Type.ALFE, false);
	SubForm ALFL = new SubForm(Type.ALFL);
	SubForm ALFR = new SubForm(Type.ALFR);
	SubForm ALRT = new SubForm(Type.ALRT);
	SubList<Condition> CONDs = new SubList<>(new Condition());
	SubInt ALCA = new SubInt(Type.ALCA);
	SubInt ALCL = new SubInt(Type.ALCL);
	SubInt ALEA = new SubInt(Type.ALEA);
	SubInt ALFA = new SubInt(Type.ALFA);
	SubInt ALFD = new SubInt(Type.ALFD);
	SubFlag FNAM = new SubFlag(Type.FNAM, 4);
	SubInt ALNA = new SubInt(Type.ALNA);
	SubInt ALNT = new SubInt(Type.ALNT);
	SubForm VTCK = new SubForm(Type.VTCK);
	SubForm ALDN = new SubForm(Type.ALDN);
	SubList<SubForm> ALFCs = new SubList<>(new SubForm(Type.ALFC));
	SubList<SubInt> ALFI = new SubList<>(new SubInt(Type.ALFI));
	SubList<SubForm> ALPCs = new SubList<>(new SubForm(Type.ALPC));
	SubList<SubForm> ALSPs = new SubList<>(new SubForm(Type.ALSP));
	SubList<SubFormInt> CNTOs = new SubList<>(Type.COCT, 4, new SubFormInt(Type.CNTO));
	SubForm ECOR = new SubForm(Type.ECOR);
	SubForm KNAM = new SubForm(Type.KNAM);
	KeywordSet keywords = new KeywordSet();
	SubInt NAM0 = new SubInt(Type.NAM0);
	SubInt QTGL = new SubInt(Type.QTGL);
	
	static Type[] types = { Type.ALID, Type.ALED, Type.ALUA, Type.ALCO, Type.ALEQ, Type.ALFE,
	    Type.ALFL, Type.ALFR, Type.ALRT, Type.CTDA, Type.CIS1, Type.CIS2, Type.ALCA,
	    Type.ALCL, Type.ALEA, Type.ALFA, Type.ALFD, Type.FNAM, Type.ALNA, Type.ALNT, Type.VTCK, Type.ALDN,
	    Type.ALFC, Type.ALFI, Type.ALPC, Type.ALSP, Type.COCT, Type.CNTO, Type.ECOR,
	    Type.KNAM, Type.KSIZ, Type.KWDA, Type.NAM0, Type.QTGL
	};
	
	ALSTALLS(Type t) {
	    super(t, types);
	}
	
	void init() {
	    subRecords.add(ALID);
	    subRecords.add(ALED);
	    subRecords.add(ALUA);
	    subRecords.add(ALCO);
	    subRecords.add(ALEQs);
	    subRecords.add(ALFE);
	    subRecords.add(ALFL);
	    subRecords.add(ALFR);
	    subRecords.add(ALRT);
	    subRecords.add(CONDs);
	    subRecords.add(ALCA);
	    subRecords.add(ALCL);
	    subRecords.add(ALEA);
	    subRecords.add(ALFA);
	    subRecords.add(ALFD);
	    subRecords.add(FNAM);
	    subRecords.add(ALNA);
	    subRecords.add(ALNT);
	    subRecords.add(VTCK);
	    subRecords.add(ALDN);
	    subRecords.add(ALFCs);
	    subRecords.add(ALFI);
	    subRecords.add(ALPCs);
	    subRecords.add(ALSPs);
	    subRecords.add(CNTOs);
	    subRecords.add(ECOR);
	    subRecords.add(KNAM);
	    subRecords.add(keywords);
	    subRecords.add(NAM0);
	    subRecords.add(QTGL);
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new ALSTALLS(type);
	}
	
    }
    
    static class ALST extends ALSTALLS {
	
	SubInt ALST = new SubInt(Type.ALST);
	
	ALST () {
	    super(Type.ALST);
	    init();
	}
	
	@Override
	void init() {
	    subRecords.add(ALST);
	    super.init();
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new ALST();
	}
    }
    
    static class ALLS extends ALSTALLS {
	
	SubInt ALLS = new SubInt(Type.ALLS);
	
	ALLS () {
	    super(Type.ALLS);
	    init();
	}
	
	@Override
	void init() {
	    subRecords.add(ALLS);
	    super.init();
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new ALLS();
	}
    }
    
    static class QOBJ extends SubShellBulkType {

	SubInt QOBJ = new SubInt(Type.QOBJ, 2);
	SubData FNAM = new SubData(Type.FNAM);
	SubStringPointer NNAM = new SubStringPointer(Type.NNAM, SubStringPointer.Files.DLSTRINGS);
	SubList<SubData> QSTAs = new SubList<>(new SubData(Type.QSTA));
	SubList<Condition> CTDAs = new SubList<>(new Condition());
	
	static Type[] types = { Type.FNAM, Type.NNAM, Type.QSTA, Type.CTDA, Type.CIS1, Type.CIS2 };
	
	QOBJ () {
	    super(Type.QOBJ, types);
	    
	    subRecords.add(QOBJ);
	    subRecords.add(FNAM);
	    subRecords.add(NNAM);
	    subRecords.add(QSTAs);
	    subRecords.add(CTDAs);
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new QOBJ();
	}
	
    }
    
    @Override
    Record getNew() {
	return new QUST();
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
