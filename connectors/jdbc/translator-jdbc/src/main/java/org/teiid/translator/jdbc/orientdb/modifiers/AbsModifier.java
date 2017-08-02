package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Expression;
import org.teiid.language.Function;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class AbsModifier extends FunctionModifier {
    @Override
    public List<?> translate(Function function) {
        List<String> result = new ArrayList<>();
        final List<Expression> parameters = function.getParameters();
        Double value = Double.parseDouble(parameters.get(0).toString());
        result.add(String.valueOf(Math.abs(value)));
        return result;
    }
}
