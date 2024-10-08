/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.python.migrate;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.python.PythonParser;
import org.openrewrite.python.tree.Py;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.python.Assertions.python;

class FindFutureImportsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new FindFutureImports());
    }

    @BeforeAll
    static void setup() {
        // Prevent failures while PythonParser returns PlainText when remoting is unavailable
        Assumptions.assumeTrue(PythonParser.builder().build().parse("int i = 1").findFirst().orElseThrow() instanceof Py.CompilationUnit);
    }

    @DocumentExample
    @Test
    void findFutureImports() {
        rewriteRun(
          //language=python
          python(
            """
              from __future__ import print_function
              class Foo:
                def foo() :
                  print("hello")
              """,
            """
              /*~~(Future import)~~>*/from __future__ import print_function
              class Foo:
                def foo() :
                  print("hello")
              """
          )
        );
    }
}
