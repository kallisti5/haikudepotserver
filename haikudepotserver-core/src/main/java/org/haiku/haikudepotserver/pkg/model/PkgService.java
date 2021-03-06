/*
 * Copyright 2016, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.pkg.model;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.haiku.haikudepotserver.dataobjects.*;
import org.haiku.haikudepotserver.support.StoppableConsumer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PkgService {

    /**
     * <p>This appears at the end of the package name to signify that it is a development package
     * for another package.</p>
     */

    String SUFFIX_PKG_DEVELOPMENT = "_devel";

    /**
     * <p>This method will return the latest version for a package in any architecture.</p>
     */

    Optional<PkgVersion> getLatestPkgVersionForPkg(
            ObjectContext context,
            Pkg pkg,
            Repository repository);

    /**
     * <p>This method will return the latest PkgVersion for the supplied package.</p>
     */

    Optional<PkgVersion> getLatestPkgVersionForPkg(
            ObjectContext context,
            Pkg pkg,
            Repository repository,
            final List<Architecture> architectures);

    /**
     * <p>For the given architecture and package, re-establish what is the latest package and correct it.
     * This may be necessary after, for example, adjusting the active flag on a pkg version.</p>
     * @return the updated latest package version or an empty option if there is none.
     */

    Optional<PkgVersion> adjustLatest(
            ObjectContext context,
            Pkg pkg,
            Architecture architecture);

    /**
     * <p>Given a {@link PkgVersion}, see if there is a corresponding source package.</p>
     */

    Optional<PkgVersion> getCorrespondingSourcePkgVersion(
            ObjectContext context,
            PkgVersion pkgVersion);

    List<PkgVersion> search(
            ObjectContext context,
            PkgSearchSpecification search);

    /**
     * <p>This method will provide a total of the package versions.</p>
     */

    long total(
            ObjectContext context,
            PkgSearchSpecification search);

    /**
     * <p>This method will provide a total of the packages.</p>
     */

    long totalPkg(
            ObjectContext context,
            boolean allowSourceOnly);

    /**
     * <p>This will be called for each package in the system.</p>
     * @param c is the callback to invoke.
     * @param allowSourceOnly when true implies that a package can be processed which only has versions that are for
     *                        the source architecture.
     * @return the quantity of packages processed.
     */

    long eachPkg(
            ObjectContext context,
            boolean allowSourceOnly,
            StoppableConsumer<Pkg> c);

    /**
     * <p>Performs necessary modifications to the package so that the changelog is updated
     * with the new content supplied.</p>
     */

    void updatePkgChangelog(
            ObjectContext context,
            Pkg pkg,
            String newContent);

    /**
     * <p>This method will deactivate package versions for a package where the package version is related to the
     * supplied repository.  This is used in the situation where a package was once part of a repository, but has
     * been removed.</p>
     * @return the quantity of package versions that were deactivated.
     */

    int deactivatePkgVersionsForPkgAssociatedWithRepositorySource(
            ObjectContext context,
            Pkg pkg,
            final RepositorySource repositorySource);

    /**
     * <p>This method will return all of the package names that have package versions that are related to a
     * repository.</p>
     */

    Set<String> fetchPkgNamesWithAnyPkgVersionAssociatedWithRepositorySource(
            ObjectContext context,
            RepositorySource repositorySource);
    /**
     * <p>This method will either find the existing pkg prominence with respect to the
     * repository or will create one and return it.</p>
     */

    PkgProminence ensurePkgProminence(
            ObjectContext objectContext,
            Pkg pkg,
            Repository repository);

    PkgProminence ensurePkgProminence(
            ObjectContext objectContext,
            Pkg pkg,
            Repository repository,
            Integer ordering);

    PkgProminence ensurePkgProminence(
            ObjectContext objectContext,
            Pkg pkg,
            Repository repository,
            Prominence prominence);

    /**
     * <p>If this is a development package that has a parent then return it.</p>
     */

    Optional<Pkg> tryGetDevelMainPkg(ObjectContext objectContext, String develPkgName);

    /**
     * <p>If there exists a development package for this package then return it.</p>
     */

    Optional<Pkg> tryGetDevelPkg(ObjectContext objectContext, String mainPkgName);


    String createHpkgDownloadUrl(PkgVersion pkgVersion);

    /**
     * <p>This method will update the {@link PkgCategory} set in the
     * nominated {@link Pkg} such that the supplied set are the
     * categories for the package.  It will do this by adding and removing relationships between the package
     * and the categories.</p>
     * @return true if a change was made.
     */

    boolean updatePkgCategories(ObjectContext context, Pkg pkg, List<PkgCategory> pkgCategories);
    /**
     * <p>This method will increment the view counter on a package version.  If it encounters an optimistic
     * locking problem then it will pause and it will try again in a moment.  It will attempt this a few
     * times and then fail with a runtime exception.</p>
     */

    void incrementViewCounter(ServerRuntime serverRuntime, ObjectId pkgVersionOid);

}
