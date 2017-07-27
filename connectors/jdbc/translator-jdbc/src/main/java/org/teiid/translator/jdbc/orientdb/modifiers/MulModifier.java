package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Function;

import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class MulModifier extends BaseArithModifier {
    @Override
    public List<?> translate(Function function) {
        return wrapOperationWithEval("*", function.getParameters());
    }
}
