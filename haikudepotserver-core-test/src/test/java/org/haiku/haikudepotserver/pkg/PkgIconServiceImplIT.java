/*
 * Copyright 2016, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.pkg;

import com.google.common.io.Resources;
import org.apache.cayenne.ObjectContext;
import org.fest.assertions.Assertions;
import org.haiku.haikudepotserver.AbstractIntegrationTest;
import org.haiku.haikudepotserver.IntegrationTestSupportService;
import org.haiku.haikudepotserver.dataobjects.MediaType;
import org.haiku.haikudepotserver.dataobjects.PkgIcon;
import org.haiku.haikudepotserver.pkg.model.PkgIconService;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.io.InputStream;

@ContextConfiguration({
        "classpath:/spring/test-context.xml"
})
public class PkgIconServiceImplIT extends AbstractIntegrationTest {

    @Resource
    private PkgIconService pkgIconService;

    @Resource
    private IntegrationTestSupportService integrationTestSupportService;

    /**
     * <p>When a "_devel" package exists and an update is made to the icon of the parent
     * package then the icon should flow down to the "_devel" package too.</p>
     */

    @Test
    public void testStorePkgIconImage_develPkgHandling() throws Exception {

        // setup the two packages.

        integrationTestSupportService.createStandardTestData();

        {
            ObjectContext context = serverRuntime.getContext();
            org.haiku.haikudepotserver.dataobjects.Pkg pkg1Devel = context.newObject(org.haiku.haikudepotserver.dataobjects.Pkg.class);
            pkg1Devel.setActive(true);
            pkg1Devel.setName("pkg1" + PkgServiceImpl.SUFFIX_PKG_DEVELOPMENT);
            context.commitChanges();
        }

        {
            ObjectContext context = serverRuntime.getContext();
            org.haiku.haikudepotserver.dataobjects.Pkg pkg1 =
                    org.haiku.haikudepotserver.dataobjects.Pkg.tryGetByName(context, "pkg1").get();
            MediaType pngMediaType = MediaType.getByCode(context, com.google.common.net.MediaType.PNG.toString()).get();

            try(InputStream inputStream = Resources.asByteSource(Resources.getResource("sample-32x32.png")).openStream()) {

                // ---------------------------------
                pkgIconService.storePkgIconImage(
                        inputStream,
                        pngMediaType,
                        32,
                        context,
                        pkg1);
                // ---------------------------------

            }

            context.commitChanges();
        }

        {
            ObjectContext context = serverRuntime.getContext();
            MediaType pngMediaType = MediaType.getByCode(context, com.google.common.net.MediaType.PNG.toString()).get();
            org.haiku.haikudepotserver.dataobjects.Pkg pkg1Devel =
                    org.haiku.haikudepotserver.dataobjects.Pkg.tryGetByName(
                            context,
                            "pkg1" + PkgServiceImpl.SUFFIX_PKG_DEVELOPMENT).get();

            PkgIcon pkgIcon = pkg1Devel.getPkgIcon(pngMediaType, 32).get();

            Assertions.assertThat(pkgIcon.getSize()).isEqualTo(32);
        }
    }


}
