package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Expression;
import org.teiid.language.Function;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class BaseArithModifier extends FunctionModifier {
    @Override
    public List<?> translate(Function function) {
        return null;
    }

    protected List<?> wrapOperationWithEval(String op, List<Expression> params) {
        return Arrays.asList("eval('",
                params.get(0),
                " " + op + " ",
                params.get(1),
                "');");
    }
}
