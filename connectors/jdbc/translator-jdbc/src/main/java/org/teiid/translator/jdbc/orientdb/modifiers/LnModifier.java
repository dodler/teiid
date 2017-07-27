package org.teiid.translator.jdbc.orientdb.modifiers;

import org.teiid.language.Function;
import org.teiid.translator.jdbc.FunctionModifier;

import java.util.List;

/**
 * Created by lyan on 27.07.17.
 */
public class LnModifier extends FunctionModifier {
    @Override
    public List<?> translate(Function function) {
        return null;// todo add ln impl
    }
}
