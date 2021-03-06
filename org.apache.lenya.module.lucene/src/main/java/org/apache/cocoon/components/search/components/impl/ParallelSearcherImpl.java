/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search.components.impl;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.store.Directory;

/**
 * use lucene ParallelMultiSearcher Class 
 * @author Nicolas Maisonneuve
 */
public class ParallelSearcherImpl extends AbstractSearcher {

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.search.components.impl.AbstractSearcher#getLuceneSearcher()
     */
    protected void getLuceneSearcher() throws IOException {
        if (directories.size() > 1) {
                IndexSearcher[] searchers = new IndexSearcher[directories
                        .size()];
                for (int i = 0; i < searchers.length; i++) {
                    searchers[i]= new IndexSearcher((Directory)(directories
                        .get(i)));
                }
                luceneSearcher = new ParallelMultiSearcher(searchers);
            } else {
                luceneSearcher = new IndexSearcher((Directory) (directories
                        .get(0)));
            }
    }

}
