package org.teiid.translator.jdbc.orientdb;

import org.teiid.core.types.BinaryType;
import org.teiid.language.AggregateFunction;
import org.teiid.language.LanguageObject;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import org.teiid.translator.*;
import org.teiid.translator.jdbc.ConvertModifier;
import org.teiid.translator.jdbc.JDBCExecutionFactory;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.teiid.translator.jdbc.FunctionModifier.*;

/**
 * Created by lyan on 30.05.17.
 */
@Translator(name = "orientdb", description = "Translator for OrientDB database")
public class OrientDBExecutionFactory extends JDBCExecutionFactory {

    public void start() throws TranslatorException{
        super.start();

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
        //convertModifier.addTypeMapping(""); // bigdecimal limitless ??
        convertModifier.addTypeMapping("Date", DATE);
        convertModifier.addTypeMapping("Datetime", TIME); // need only time from oeirntdb
        convertModifier.addTypeMapping("Date", TIMESTAMP); // figure out about dates etc
    }

    @Override
    public List<String> getSupportedFunctions(){
        return Collections.emptyList();
    }

    @Override
    public void initCapabilities(Connection connection) throws TranslatorException {
        super.initCapabilities(connection);
        // todo add version retriving
    }

    @Override // not sure on this
    public int getTimestampNanoPrecision(){
        return 0;
    }

    @Override
    public String translateLiteralBoolean(Boolean boolVal){
        if(boolVal.booleanValue()){
            return "true";
        }
        return "false";
    }

    /**
     * orientdb supports generic arith operations only in eval function
     * @return
     */
    @Override
    public List<String> getDefaultSupportedFunctions(){
        return Arrays.asList(new String[]{});
    }

    @Override
    public NullOrder getDefaultNullOrder(){
        return NullOrder.LOW;
    }

    @Override
    public List<?> translate(LanguageObject obj, ExecutionContext context){
        AggregateFunction t = (AggregateFunction) obj;
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, t.getName());
        return super.translate(obj, context);
    }

    /*
    started here group by
    finished here supportsOrCriteria,

    * */

    /**
     * orientdb performs query, but result is not sorted
     * @return
     */
    @Override
    public boolean supportsOrderByUnrelated(){
        return false;
    }

    @Override
    public boolean supportsQuantifiedCompareCriteriaAll(){
        return false;
    }

    /**
     * this can be bug actually, because afaik orientdb doesn't support having
     * @return
     */
    @Override
    public boolean supportsScalarSubqueries(){
        return true;
    }

    @Override
    public boolean supportsSearchedCaseExpressions(){
        return false;
    }

    @Override
    public boolean supportsSelfJoins(){
        return false;
    }

    @Override
    public boolean supportsInlineViews(){
        return true;
    }

    @Override
    public boolean supportsQuantifiedCompareCriteriaSome(){
        return true;
    }



}
