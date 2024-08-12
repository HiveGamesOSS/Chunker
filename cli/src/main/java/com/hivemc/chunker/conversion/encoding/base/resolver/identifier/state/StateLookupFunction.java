package com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state;

import org.jetbrains.annotations.Nullable;

/**
 * Functional interface for looking up states.
 *
 * @param <IK> the input key type.
 * @param <IV> the input value type.
 */
public interface StateLookupFunction<IK, IV> {
    /**
     * Get the value for a state.
     *
     * @param stateKey           the type to lookup.
     * @param returnDefaultValue whether a default value is allowed to be returned if the value isn't present.
     * @return the value or the default if it is allowed or null if default isn't allowed or one isn't present.
     */
    @Nullable
    IV getState(IK stateKey, boolean returnDefaultValue);

    /**
     * Functional interface for lookup up states which doesn't have defaults.
     *
     * @param <IK> the input key type.
     * @param <IV> the input value type.
     */
    interface DefaultlessStateLookupFunction<IK, IV> extends StateLookupFunction<IK, IV> {
        /**
         * Get the value for a state.
         *
         * @param stateKey the type to lookup.
         * @return the value or null if one isn't present.
         */
        @Nullable
        IV getState(IK stateKey);

        @Override
        @Nullable
        default IV getState(IK stateKey, boolean returnDefaultValue) {
            return getState(stateKey);
        }
    }
}
