package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Function;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.Arrays;
import java.util.List;

/**
 * perform natural logarithm calculation
 * Created by lyan on 27.07.17.
 */
public class LnModifier extends FunctionModifier {

    @Override
    public List<?> translate(Function function) {
        Double value = Double.parseDouble(function.getParameters().get(0).toString());
        return Arrays.asList(
                String.valueOf(Math.log(value))
        );
    }
}
