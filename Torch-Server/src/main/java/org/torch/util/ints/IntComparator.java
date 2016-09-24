package org.torch.util.ints;

/**
 * From fastutil project.
 * @Link: https://github.com/vigna/fastutil
 */

/*
 * Copyright (C) 2002-2014 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Comparator;

/**
 * A type-specific {@link Comparator}; provides methods to compare two primitive
 * types both as objects and as primitive types.
 *
 * <P>
 * Note that <code>fastutil</code> provides a corresponding abstract class that
 * can be used to implement this interface just by specifying the type-specific
 * comparator.
 *
 * @see Comparator
 */
public interface IntComparator extends Comparator<Integer> {
	/**
	 * Compares the given primitive types.
	 *
	 * @see java.util.Comparator
	 * @return A positive integer, zero, or a negative integer if the first
	 *         argument is greater than, equal to, or smaller than,
	 *         respectively, the second one.
	 */
	public int compare(int k1, int k2);
}