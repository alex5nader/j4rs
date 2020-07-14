/*
 * Copyright 2018 astonbitecode
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.astonbitecode.j4rs.api;

import org.astonbitecode.j4rs.api.dtos.InvocationArg;
import org.astonbitecode.j4rs.api.invocation.JsonInvocationImpl;
import org.astonbitecode.j4rs.errors.InvocationException;
import org.astonbitecode.j4rs.utils.Utils;

public interface Instance<T> extends ObjectValue, JsonValue {
    /**
     * Invokes a method of the instance of the class that is set for this {@link Instance}
     *
     * @param methodName The method name
     * @param args       The arguments to use for invoking the method
     * @return A {@link Instance} instance containing the result of the invocation
     */
    Instance invoke(String methodName, InvocationArg... args);

    /**
     * Invokes a static method of the class that is set for this {@link Instance}
     *
     * @param methodName The static method name
     * @param args       The arguments to use for invoking the static method
     * @return A {@link Instance} instance containing the result of the invocation
     */
    Instance invokeStatic(String methodName, InvocationArg... args);

    /**
     * Invokes asynchronously a method of the instance of the class that is set for this {@link Instance}.
     * The result of the invocation should be provided later using the performCallback method of a {@link org.astonbitecode.j4rs.api.invocation.NativeCallbackSupport} class.
     * Any possible returned objects from the actual synchronous invocation of the defined method will be dropped.
     *
     * @param functionPointerAddress The address of the function pointer that will be used later in the native side in order to actually paerform the callback.
     * @param methodName             The method name
     * @param args                   The arguments to use when invoking the callback method (the functionPointer)
     */
    void invokeAsync(long functionPointerAddress, String methodName, InvocationArg... args);

    /**
     * Invokes a method of the instance of the class that is set for this {@link Instance}.
     * The result of the invocation should be provided later using the doCallback method of a {@link org.astonbitecode.j4rs.api.invocation.NativeCallbackToRustChannelSupport} class.
     * Any possible returned objects from the actual synchronous invocation of the defined method will be dropped.
     *
     * @param channelAddress The memory address of the channel
     * @param methodName The method name
     * @param args The arguments
     */
    void invokeToChannel(long channelAddress, String methodName, InvocationArg... args);

    /**
     * Initialize a callback channel for this {@link Instance}.
     * The channel can be used by Java to send values to Rust using the doCallback method of a {@link org.astonbitecode.j4rs.api.invocation.NativeCallbackToRustChannelSupport} class.
     * @param channelAddress The memory address of the channel
     */
    void initializeCallbackChannel(long channelAddress);

    /**
     * Retrieves the instance held under the Field fieldName
     * @param fieldName The name of the field to retrieve
     * @return A {@link Instance} instance containing the defined field.
     */
    Instance field(String fieldName);

    /**
     * Casts a the object that is contained in a Instance to an object of class clazz.
     *
     * @param <T>     Generically defined return type
     * @param from    The {@link Instance} to cast.
     * @param toClass The class that the provided {@link Instance} should be casted to
     * @return A {@link Instance} instance containing the result of the cast.
     */
    static <T> Instance cast(Instance from, String toClass) {
        try {
            Class<T> clazz = (Class<T>) Utils.forNameEnhanced(toClass);
            return new JsonInvocationImpl(clazz.cast(from.getObject()), clazz);
        } catch (Exception error) {
            throw new InvocationException("Cannot cast instance of " + from.getObject().getClass().getName() + " to " + toClass, error);
        }
    }

    /**
     * Clones a Instance
     *
     * @param from The object to clone.
     * @param <T>  Generically defined return type
     * @return a {@link Instance} instance.
     */
    static <T> Instance cloneInstance(Instance from) {
        return new JsonInvocationImpl(from.getObject(), from.getObjectClass());
    }
}
