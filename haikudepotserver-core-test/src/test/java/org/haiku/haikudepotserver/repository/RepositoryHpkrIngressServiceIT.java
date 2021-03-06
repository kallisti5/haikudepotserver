/*
 * Copyright 2014-2016, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.repository;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.fest.assertions.Assertions;
import org.haiku.haikudepotserver.AbstractIntegrationTest;
import org.haiku.haikudepotserver.dataobjects.*;
import org.haiku.haikudepotserver.job.Jobs;
import org.haiku.haikudepotserver.job.model.JobService;
import org.haiku.haikudepotserver.job.model.JobSnapshot;
import org.haiku.haikudepotserver.pkg.model.PkgService;
import org.haiku.haikudepotserver.repository.model.RepositoryHpkrIngressJobSpecification;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>This test will load in a fake repository HPKR file and will then check to see that it imported correctly.</p>
 */

@ContextConfiguration({
        "classpath:/spring/test-context.xml"
})
public class RepositoryHpkrIngressServiceIT extends AbstractIntegrationTest {

    private final static long DELAY_PROCESSSUBMITTEDTESTJOB = 60 * 1000; // 60s

    @Resource
    private JobService jobService;

    @Resource
    private PkgService pkgService;

    private void verifyPackage(
            ObjectContext context,
            String name) {
        Optional<Pkg> pkgOptional = Pkg.tryGetByName(context, name);
        Assertions.assertThat(pkgOptional.isPresent()).isTrue();
        Assertions.assertThat(pkgOptional.get().getActive()).isTrue();
    }

    @Test
    public void testImportThenCheck() throws Exception {

        File temporaryDir = null;
        File temporaryFile = null;

        try {
            temporaryDir = Files.createTempDir();
            temporaryFile = new File(temporaryDir, "repo");

            // get the test hpkr data and copy it into a temporary file that can be used as a source
            // for a repository.

            Files.write(getResourceData("sample-repo.hpkr"), temporaryFile);

            // first setup a fake repository to import that points at the local test HPKR file.

            {
                ObjectContext context = serverRuntime.getContext();

                Repository repository = context.newObject(Repository.class);
                repository.setCode("test");
                repository.setName("Test Repository");

                RepositorySource repositorySource = context.newObject(RepositorySource.class);
                repositorySource.setCode("testsrc_xyz");
                repositorySource.setUrl("file://" + temporaryDir.getAbsolutePath());
                repository.addToManyTarget(Repository.REPOSITORY_SOURCES_PROPERTY, repositorySource, true);

                context.commitChanges();
            }

            // setup another repository that is not related to the import test to check some stuff...

            {
                ObjectContext context = serverRuntime.getContext();

                Repository repository = context.newObject(Repository.class);
                repository.setCode("test2");
                repository.setName("Test 2");

                RepositorySource repositorySource = context.newObject(RepositorySource.class);
                repositorySource.setCode("testsrc2_xyz");
                repositorySource.setUrl("file:///noop.hpkr");
                repository.addToManyTarget(Repository.REPOSITORY_SOURCES_PROPERTY, repositorySource, true);

                context.commitChanges();
            }

            // add a package version from this repository that is known not to be in that example and then
            // latterly check that the package version is no longer active.

            {
                ObjectContext context = serverRuntime.getContext();
                Pkg pkg = context.newObject(Pkg.class);
                pkgService.ensurePkgProminence(context, pkg, Repository.getByCode(context, "test").get());
                pkgService.ensurePkgProminence(context, pkg, Repository.getByCode(context, "test2").get());
                pkg.setName("taranaki");

                // this one should get deactivated
                {
                    PkgVersion pkgVersion = context.newObject(PkgVersion.class);
                    pkgVersion.setPkg(pkg);
                    pkgVersion.setMajor("1");
                    pkgVersion.setMinor("2");
                    pkgVersion.setArchitecture(Architecture.getByCode(context, "x86_64").get());
                    pkgVersion.setIsLatest(true);
                    pkgVersion.setRepositorySource(RepositorySource.getByCode(context, "testsrc_xyz").get());
                }

                // this one should remain
                {
                    PkgVersion pkgVersion = context.newObject(PkgVersion.class);
                    pkgVersion.setPkg(pkg);
                    pkgVersion.setMajor("1");
                    pkgVersion.setMinor("3");
                    pkgVersion.setArchitecture(Architecture.getByCode(context, "x86_64").get());
                    pkgVersion.setIsLatest(true);
                    pkgVersion.setRepositorySource(RepositorySource.getByCode(context, "testsrc2_xyz").get());
                }

                context.commitChanges();
            }

            // add an inactive package version from this repository that is known to be in the repository.  This
            // package should be activated and re-used.

            ObjectId originalFfmpegPkgOid;

            {
                ObjectContext context = serverRuntime.getContext();

                Pkg pkg = context.newObject(Pkg.class);
                pkgService.ensurePkgProminence(context, pkg, Repository.getByCode(context, "test").get());
                pkgService.ensurePkgProminence(context, pkg, Repository.getByCode(context, "test2").get());
                pkg.setName("ffmpeg");

                PkgVersion pkgVersion = context.newObject(PkgVersion.class);
                pkgVersion.setPkg(pkg);
                pkgVersion.setMajor("3");
                pkgVersion.setMinor("3");
                pkgVersion.setMicro("2");
                pkgVersion.setRevision(1);
                pkgVersion.setArchitecture(Architecture.getByCode(context, "x86_64").get());
                pkgVersion.setIsLatest(true);
                pkgVersion.setActive(false); // to be sure!
                pkgVersion.setRepositorySource(RepositorySource.getByCode(context, "testsrc_xyz").get());

                PkgVersionUrl pkgVersionUrl = context.newObject(PkgVersionUrl.class);
                pkgVersionUrl.setPkgUrlType(PkgUrlType.getByCode(context, org.haiku.pkg.model.PkgUrlType.HOMEPAGE.name().toLowerCase()).get());
                pkgVersionUrl.setUrl("http://noop");
                pkgVersion.addToManyTarget(PkgVersion.PKG_VERSION_URLS_PROPERTY, pkgVersionUrl, true);

                PkgVersionCopyright pkgVersionCopyright = context.newObject(PkgVersionCopyright.class);
                pkgVersionCopyright.setBody("Norfolk pine");
                pkgVersion.addToManyTarget(PkgVersion.PKG_VERSION_COPYRIGHTS_PROPERTY, pkgVersionCopyright, true);

                PkgVersionLicense pkgVersionLicense = context.newObject(PkgVersionLicense.class);
                pkgVersionLicense.setBody("Punga");
                pkgVersion.addToManyTarget(PkgVersion.PKG_VERSION_LICENSES_PROPERTY, pkgVersionLicense, true);

                context.commitChanges();

                originalFfmpegPkgOid = pkgVersion.getObjectId();
            }

            // do the import.

            String guid = jobService.submit(
                    new RepositoryHpkrIngressJobSpecification("test"),
                    JobSnapshot.COALESCE_STATUSES_NONE);

            // wait for it to finish.

            {
                long startMs = System.currentTimeMillis();

                while(
                        Jobs.isQueuedOrStarted(jobService.tryGetJob(guid).get())
                                && (System.currentTimeMillis() - startMs) < DELAY_PROCESSSUBMITTEDTESTJOB) {
                    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                }

                if(Jobs.isQueuedOrStarted(jobService.tryGetJob(guid).get())) {
                    throw new IllegalStateException("test processing of the sample repo has taken > "
                            + DELAY_PROCESSSUBMITTEDTESTJOB + "ms");
                }
            }

            // now pull out some known packages and make sure they are imported correctly.
            // TODO - this is a fairly simplistic test; do some more checks.

            {
                ObjectContext context = serverRuntime.getContext();

                verifyPackage(context,"apr");
                verifyPackage(context,"schroedinger");

                // this one is not in the import and so should be inactive afterwards.

                SelectQuery selectQuery = new SelectQuery(
                        PkgVersion.class,
                        ExpressionFactory.matchExp(
                                PkgVersion.PKG_PROPERTY,
                                Pkg.tryGetByName(context, "taranaki").get()
                        )
                );

                List<PkgVersion> pkgVersions = (List<PkgVersion>) context.performQuery(selectQuery);

                Assertions.assertThat(pkgVersions.size()).isEqualTo(2);

                for(PkgVersion pkgVersion : pkgVersions) {
                    boolean isTestRepository = pkgVersion.getRepositorySource().getRepository().getCode().equals("test");
                    Assertions.assertThat(pkgVersion.getActive()).isEqualTo(!isTestRepository);
                }

                // check that the ffmpeg package was re-used and populated; as an example.

                {
                    PkgVersion pkgVersion = PkgVersion.get(context, originalFfmpegPkgOid);
                    Assertions.assertThat(pkgVersion.getActive()).isTrue();
                    Assertions.assertThat(pkgVersion.getIsLatest()).isTrue();
                    Assertions.assertThat(PkgVersion.getForPkg(
                            context,
                            pkgVersion.getPkg(),
                            Repository.getByCode(context, "test").get(),
                            true).size())
                            .isEqualTo(1); // include inactive

                    PkgVersionLocalization localization = pkgVersion.getPkgVersionLocalization(NaturalLanguage.getByCode(context, NaturalLanguage.CODE_ENGLISH).get()).get();
                    Assertions.assertThat(localization.getDescription().get()).startsWith("FFmpeg is a complete, cro");
                    Assertions.assertThat(localization.getSummary().get()).startsWith("Audio and video rec");

                    // the former rubbish copyright is removed
                    List<String> copyrights = pkgVersion.getCopyrights();
                    Assertions.assertThat(copyrights.size()).isEqualTo(2);
                    Assertions.assertThat(ImmutableSet.copyOf(copyrights)).containsOnly("2000-2003 Fabrice Bellard", "2003-2017 the FFmpeg developers");

                    // the former rubbish license is removed
                    List<String> licenses = pkgVersion.getLicenses();
                    Assertions.assertThat(licenses.size()).isEqualTo(2);
                    Assertions.assertThat(ImmutableSet.copyOf(licenses)).containsOnly("GNU LGPL v2.1", "GNU GPL v2");

                    Optional<PkgVersionUrl> pkgVersionUrlOptional = pkgVersion.getPkgVersionUrlForType(PkgUrlType.getByCode(
                            context,
                            org.haiku.pkg.model.PkgUrlType.HOMEPAGE.name().toLowerCase()).get());

                    Assertions.assertThat(pkgVersionUrlOptional.isPresent()).isTrue();
                    Assertions.assertThat(pkgVersionUrlOptional.get().getUrl()).isEqualTo("https://ffmpeg.org/");
                }

            }
        }
        finally {
            if (null != temporaryFile) {
                if (!temporaryFile.delete()) {
                    LOGGER.warn("unable to delete the temporary file");
                }
            }
        }
    }

}
