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

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.python.PythonIsoVisitor;

public class FindFutureImports extends Recipe {
    @Override
    public String getDisplayName() {
        return "Find `__future__` imports";
    }

    @Override
    public String getDescription() {
        return "Find `__future__` imports and add a search marker.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new FindFutureImportsVisitor();
    }

    private static class FindFutureImportsVisitor extends PythonIsoVisitor<ExecutionContext> {
        @Override
        public J.Import visitImport(J.Import _import, ExecutionContext ctx) {
            J.Import im = super.visitImport(_import, ctx);
            J.Identifier target = (J.Identifier) im.getQualid().getTarget();
            if ("__future__".equals(target.getSimpleName())) {
                return SearchResult.found(im, "Future import");
            }
            return im;
        }
    }
}
