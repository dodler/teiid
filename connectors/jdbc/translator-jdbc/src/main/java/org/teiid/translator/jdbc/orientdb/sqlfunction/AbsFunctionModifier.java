package org.teiid.translator.jdbc.orientdb.sqlfunction;

import org.teiid.language.Expression;
import org.teiid.language.Function;
import org.teiid.language.LanguageFactory;
import org.teiid.translator.TypeFacility;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lyan on 18.06.17.
 */
public class AbsFunctionModifier extends FunctionModifier {
    @Override
    public List<?> translate(Function function) {
        final Expression value = function.getParameters().get(0);

        Function result = LanguageFactory.INSTANCE.createFunction("abs", TypeFacility.RUNTIME_TYPES.DOUBLE);
        return Arrays.asList(result);
    }
}
