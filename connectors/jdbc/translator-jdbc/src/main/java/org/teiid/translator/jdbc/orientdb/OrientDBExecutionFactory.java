package org.teiid.translator.jdbc.orientdb;

import org.teiid.core.types.BinaryType;
import org.teiid.language.*;
import org.teiid.logging.LogManager;
import org.teiid.metadata.Table;
import org.teiid.translator.*;
import org.teiid.translator.jdbc.AliasModifier;
import org.teiid.translator.jdbc.ConvertModifier;
import org.teiid.translator.jdbc.JDBCExecutionFactory;
import org.teiid.translator.jdbc.Version;
import org.teiid.translator.jdbc.orientdb.modifiers.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.teiid.translator.jdbc.FunctionModifier.*;
import static org.teiid.translator.jdbc.FunctionModifier.BOOLEAN;
import static org.teiid.translator.jdbc.FunctionModifier.BYTE;
import static org.teiid.translator.jdbc.FunctionModifier.DATE;
import static org.teiid.translator.jdbc.FunctionModifier.DOUBLE;
import static org.teiid.translator.jdbc.FunctionModifier.FLOAT;
import static org.teiid.translator.jdbc.FunctionModifier.INTEGER;
import static org.teiid.translator.jdbc.FunctionModifier.LONG;
import static org.teiid.translator.jdbc.FunctionModifier.SHORT;
import static org.teiid.translator.jdbc.orientdb.OrientDBExecutionFactory.ODBNativeTypes.*;

import org.teiid.logging.LogConstants;

/**
 * Created by lyan on 30.05.17.
 */
@Translator(name = "orientdb", description = "Translator for OrientDB database")
public class OrientDBExecutionFactory extends JDBCExecutionFactory {

    /**
     * currently orientdb doesn't support version acquiring
     */

    public static final Version VERSION_0_9_25 = Version.getVersion("0.9.25");
    public static final Version VERSION_0_9_1 = Version.getVersion("0.9.1");
    /**
     * since that supports arith in eval expression
     */
    public static final Version VERSION_1_RC7 = Version.getVersion("1.RC7");
    public static final List<String> SUPPORTED_FUNCTIONS = new ArrayList<>();

    static{
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.ADD_OP);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.DIVIDE_OP);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.MULTIPLY_OP);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.SUBTRACT_OP);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.ABS);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.LOG);
        SUPPORTED_FUNCTIONS.add(SourceSystemFunctions.LOG10);
    }

    public OrientDBExecutionFactory() {
        setUseBindVariables(false);
        setUseCommentsInSourceQuery(true);

    }

    public enum ODBNativeTypes {
        BOOLEAN("Boolean"), BYTE("Byte"), SHORT("Short"), INTEGER("Integer"),
        LONG("Long"), FLOAT("Float"), DOUBLE("Double"), DATETIME("Datetime"),
        BINARY("Binary"), DATE("Date");

        private final String type;

        ODBNativeTypes(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }

        public static String toNative(ODBNativeTypes nativeType) {
            return nativeType.toString();
        }
    }

    public void start() throws TranslatorException {
        super.start();

        registerFunctionModifier(SourceSystemFunctions.ADD_OP, new PlusModifier());
        registerFunctionModifier(SourceSystemFunctions.DIVIDE_OP, new DivModifier());
        registerFunctionModifier(SourceSystemFunctions.MULTIPLY_OP, new MulModifier());
        registerFunctionModifier(SourceSystemFunctions.SUBTRACT_OP, new MinusModifier());
        registerFunctionModifier(SourceSystemFunctions.ABS, new AliasModifier("abs"));
        registerFunctionModifier(SourceSystemFunctions.LOG, new LnModifier());
        registerFunctionModifier(SourceSystemFunctions.LOG10, new LogModifier());

        registerFunctionModifier(SourceSystemFunctions.BITAND, new BitAndModifier());
        registerFunctionModifier(SourceSystemFunctions.BITOR, new BitOrModifier());
        registerFunctionModifier(SourceSystemFunctions.BITXOR, new BitXorModifier());
        registerFunctionModifier(SourceSystemFunctions.BITNOT, new BitNotModifier());

        registerFunctionModifier(SourceSystemFunctions.UCASE, new UpperModifier());


        ConvertModifier convertModifier = new ConvertModifier();
        convertModifier.addTypeMapping("String", STRING);
        // char here
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BOOLEAN), BOOLEAN);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), BYTE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), SHORT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), INTEGER);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), BIGINTEGER);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), DOUBLE);
        convertModifier.addTypeMapping(toNative(DATETIME), DATE);
        convertModifier.addTypeMapping(toNative(BINARY), VARBINARY); // check it
        //convertModifier.addTypeMapping(""); // bigdecimal limitless ??
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DATE), DATE);
        convertModifier.addTypeMapping(toNative(DATETIME), TIME); // need only time from oeirntdb
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DATE), TIMESTAMP); // figure out about dates etc

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BOOLEAN), STRING)
        ;
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), DOUBLE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), STRING);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.INTEGER), SHORT);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), STRING);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), DOUBLE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.SHORT), INTEGER);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), STRING);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), DOUBLE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), SHORT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.LONG), INTEGER);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), STRING);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), INTEGER);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), DOUBLE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.FLOAT), SHORT);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), STRING);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), INTEGER);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DOUBLE), SHORT);

        convertModifier.addTypeMapping(toNative(DATETIME), DATE);
        convertModifier.addTypeMapping(toNative(DATETIME), LONG);
        convertModifier.addTypeMapping(toNative(DATETIME), STRING);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DATE), DATE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DATE), LONG);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.DATE), STRING);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BINARY), STRING);

        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), INTEGER);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), SHORT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), DOUBLE);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), FLOAT);
        convertModifier.addTypeMapping(toNative(ODBNativeTypes.BYTE), STRING);
        // types mapping ready
    }

    @Override
    public MetadataProcessor<Connection> getMetadataProcessor() {
        return new OrientDBMetadataProcessor();
    }

    @Override
    public List<String> getSupportedFunctions() {
        return SUPPORTED_FUNCTIONS;
    }

    @Override
    public void initCapabilities(Connection connection) throws TranslatorException {
        super.initCapabilities(connection);
    }

    @Override
    public List<?> translate(LanguageObject obj, ExecutionContext context) {
        List<?> translate = super.translate(obj, context);
//        LogManager.logInfo(LogConstants.CTX_CONNECTOR, "ctx:" + context);

        if (obj != null) {
            log(obj.getClass());
            if (obj instanceof Select) {
                handleSelect((Select) obj);
            }else if (obj instanceof Update){
                handleUpdate((Update)obj);
            }else if(obj instanceof Delete){
                handleDelete((Delete)obj);
            }
            log("result:" + obj);
        }
        return translate;
    }

    private void handleDelete(Delete delete) {
        Comparison where = (Comparison) delete.getWhere();
        if (where != null) {
            ColumnReference leftExpression = (ColumnReference) where.getLeftExpression();
            leftExpression.setTable(null);
            where.setLeftExpression(leftExpression);
        }
    }

    private void handleUpdate(Update update) {
        Comparison where = (Comparison) update.getWhere();
        if (where != null) {
            ColumnReference leftExpression = (ColumnReference) where.getLeftExpression();
            leftExpression.setTable(null);
            where.setLeftExpression(leftExpression);
        }
    }

    private void handleSelect(Select select) {
        List<TableReference> from = select.getFrom();

        NamedTable fromSelect = (NamedTable) from.get(0);

        fromSelect.setCorrelationName(null);
        fromSelect.setName(fromSelect.getName().split(" ")[0]);

        List<DerivedColumn> derivedColumns = select.getDerivedColumns();
        DerivedColumn derivedColumn = derivedColumns.get(0);

        ColumnReference expression = (ColumnReference) derivedColumn.getExpression();
        NamedTable table = expression.getTable();
        table.setCorrelationName(null);
        expression.setTable(null);
        derivedColumn.setExpression(expression);
        select.setDerivedColumns(Arrays.asList(derivedColumn));

        if (select.getWhere() != null) {
            log("select where:" + select.getWhere());
        }
    }

    private void log(Object any) {
        LogManager.logInfo(LogConstants.CTX_CONNECTOR, String.valueOf(any));
    }

    /**
     * orientdb supports generic arith operations only in eval function
     *
     * @return
     */
    @Override
    public List<String> getDefaultSupportedFunctions() {
        return Arrays.asList(new String[]{});
    }

    @Override
    public NullOrder getDefaultNullOrder() {
        return NullOrder.LOW;
    }

    /**
     * orientdb performs query, but result is not sorted
     *
     * @return
     */
    @Override
    public boolean supportsOrderByUnrelated() {
        return false;
    }

    @Override
    public boolean supportsQuantifiedCompareCriteriaAll() {
        return false;
    }

    @Override
    public boolean supportsScalarSubqueries() {
        return true;
    }

    @Override
    public boolean supportsSearchedCaseExpressions() {
        return false;
    }

    @Override
    public boolean supportsSelfJoins() {
        return false;
    }

    @Override
    public boolean supportsInlineViews() {
        return false;
    }

    @Override
    public boolean supportsQuantifiedCompareCriteriaSome() {
        return true;
    }

    /**
     * todo check about select from multiple tables
     *
     * @return
     */
    @Override
    public boolean supportsUnions() {
        return false;
    }

    // not sure about bulk and batch updates

    @Override
    public boolean supportsHaving() {
        return false;
    }

    // not sure about select expression
    // same for supportsInsertWithQueryExpression

    // supports boolean true

    @Override
    public String translateLiteralBoolean(Boolean boolVal) {
        if (boolVal.booleanValue()) {
            return "true";
        }
        return "false";
    }

    @Override
    public String translateLiteralDate(java.sql.Date date) {
        // todo introduce version comparison
        return "date('" + date.toString() + "').format('YYYY-MM-dd')";
    }

    @Override
    public String translateLiteralTimestamp(Timestamp timestamp) {
        timestamp.setNanos(getTimestampNanoPrecision());
        return "date('" + timestamp.toString() + "')";
    }


    @Override
    public String translateLiteralBinaryType(BinaryType binObj) {
        return String.valueOf(binObj.getBytes()); // todo check for that
    }

    /**
     * orientdb doesn't support alias
     *
     * @return
     */
    @Override
    public boolean useAsInGroupAlias() {
        return false;
    }


    @Override
    public boolean usePreparedStatements() {
        return true;
    }

    @Override
    public String getSourceComment(ExecutionContext context, Command command) {
        return ""; // todo implementme
    }

    @Override
    public int getTimestampNanoPrecision() {
        return 0;
    }


    @Override
    public ResultSet executeStoredProcedure(CallableStatement statement,
                                            List<Argument> preparedValues,
                                            Class<?> returnType) throws SQLException {
        return null; // // FIXME: 22.07.17
    }

    @Override
    public void bindValue(PreparedStatement statement,
                          Object param,
                          Class<?> paramType,
                          int i) throws SQLException {
        super.bindValue(statement, param, paramType, i); // fixme
    }

    @Override
    public boolean useStreamsForLobs() {
        return true; // check that
    }

    @Override
    public Object retrieveValue(ResultSet results, int columnIndex, Class<?> expectedType) throws SQLException {
        return super.retrieveValue(results, columnIndex, expectedType); // fixme
    }
    // found no impls for convertObject and afterInitialConnectionObtained methods
    // also for obtainedConnection


    @Override
    public boolean useSelectLimit(){
        return true;
    }

    @Override
    public String getHibernateDialectClassName(){
        return "org.hibernate.dialect.PostgreSQLDialect";
    }
}
