package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Expression;
import org.teiid.language.Function;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class BitNotModifier extends BaseArithModifier {
    @Override
    public List<?> translate(Function function) {
        List<Expression> params = function.getParameters();
        return Arrays.asList("eval('~ ",
                params.get(0),
                "');");
    }
}
