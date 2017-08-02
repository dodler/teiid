package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Function;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class LogModifier extends FunctionModifier {
    @Override
    public List<?> translate(Function function) {
        return Arrays.asList(
                Math.log10(
                        Double.parseDouble(
                                function.getParameters().get(0).toString()
                        )
                )
        );
    }
}
