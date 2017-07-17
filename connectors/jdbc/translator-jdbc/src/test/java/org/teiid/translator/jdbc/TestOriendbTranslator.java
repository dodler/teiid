package org.teiid.translator.jdbc;

import org.junit.BeforeClass;
import org.teiid.translator.jdbc.orientdb.OrientDBExecutionFactory;

/**
 * Created by lyan on 03.07.17.
 */
public class TestOriendbTranslator {
    private static OrientDBExecutionFactory TRANSLATOR;

    @BeforeClass
    public static void setupOnce(){
        TRANSLATOR = new OrientDBExecutionFactory();
    }

    public String getTestVDB(){
        return TranslationHelper.PARTS_VDB;
    }

    public String getTestBQTVDB(){
        return TranslationHelper.BQT_VDB;
    }


}

