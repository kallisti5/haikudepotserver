/*
 * Copyright 2013-2017, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.dataobjects;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.haiku.haikudepotserver.dataobjects.auto._PkgUrlType;
import org.haiku.haikudepotserver.dataobjects.support.Coded;
import org.haiku.haikudepotserver.support.SingleCollector;

import java.util.List;
import java.util.Optional;

public class PkgUrlType extends _PkgUrlType implements Coded {

    public static List<PkgUrlType> getAll(ObjectContext context) {
        Preconditions.checkArgument(null != context, "the context must be provided");
        SelectQuery query = new SelectQuery(PkgUrlType.class);
        query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
        query.addOrdering(new Ordering(PkgUrlType.CODE_PROPERTY, SortOrder.ASCENDING));
        return (List<PkgUrlType>) context.performQuery(query);
    }

    public static Optional<PkgUrlType> getByCode(ObjectContext context, String code) {
        Preconditions.checkArgument(null != context, "the context must be supplied");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code), "a code is required to get the url type");
        return getAll(context).stream().filter(put -> put.getCode().equals(code)).collect(SingleCollector.optional());
    }

}
