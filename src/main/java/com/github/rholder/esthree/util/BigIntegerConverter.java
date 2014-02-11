/*
 * Copyright 2014 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rholder.esthree.util;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

import java.math.BigInteger;

/**
 * Converts a String to a BigInteger.
 */
public class BigIntegerConverter extends BaseConverter<BigInteger> {

    public BigIntegerConverter(String optionName) {
        super(optionName);
    }

    public BigInteger convert(String value) {
        try {
            return new BigInteger(value);
        } catch (NumberFormatException nfe) {
            throw new ParameterException(getErrorString(value, "a BigInteger"));
        }
    }
}