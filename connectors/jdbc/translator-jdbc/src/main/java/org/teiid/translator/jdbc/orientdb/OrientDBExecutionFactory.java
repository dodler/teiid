package org.teiid.translator.jdbc.orientdb;

import org.teiid.translator.SourceSystemFunctions;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.jdbc.ConvertModifier;
import org.teiid.translator.jdbc.JDBCExecutionFactory;
import org.teiid.translator.jdbc.orientdb.sqlfunction.AbsFunctionModifier;

import static org.teiid.translator.jdbc.FunctionModifier.*;

/**
 * Created by lyan on 30.05.17.
 */
@Translator(name = "orientdb", description = "Translator for OrientDB database")
public class OrientDBExecutionFactory extends JDBCExecutionFactory {
    public void start throws TranslatorException{
        super.start();

        registerFunctionModifier(SourceSystemFunctions.ABS, new AbsFunctionModifier());

        ConvertModifier convertModifier = new ConvertModifier();
        convertModifier.addTypeMapping("String", STRING);
        // char here
        convertModifier.addTypeMapping("Boolean", BOOLEAN);
        convertModifier.addTypeMapping("Byte", BYTE);
        convertModifier.addTypeMapping("Short", SHORT);
        convertModifier.addTypeMapping("Integer", INTEGER);
        convertModifier.addTypeMapping("Long", LONG);
        convertModifier.addTypeMapping("Long", BIGINTEGER);
        convertModifier.addTypeMapping("Float", FLOAT);
        convertModifier.addTypeMapping("Double", DOUBLE);
        convertModifier.addTypeMapping("Datetime", DATE);
        convertModifier.addTypeMapping("Binary", VARBINARY); // check it
        convertModifier.addTypeMapping(""); // bigdecimal limitless ??
        convertModifier.addTypeMapping("Date", DATE);
        convertModifier.addTypeMapping("Datetime", TIME); // need only time from oeirntdb
        convertModifier.addTypeMapping("Date", TIMESTAMP); // figure out about dates etc
    }

    @Override
    public String translateLiteralBoolean(Boolean boolVal){
        if(boolVal.booleanValue()){
            return "true";
        }
        return "false";
    }

}
