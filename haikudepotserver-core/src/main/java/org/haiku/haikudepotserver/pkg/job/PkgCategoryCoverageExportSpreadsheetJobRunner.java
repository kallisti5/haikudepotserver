/*
 * Copyright 2014-2016, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.pkg.job;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import org.apache.cayenne.ObjectContext;
import org.haiku.haikudepotserver.dataobjects.PkgVersionLocalization;
import org.haiku.haikudepotserver.dataobjects.Repository;
import org.haiku.haikudepotserver.job.AbstractJobRunner;
import org.haiku.haikudepotserver.job.model.JobService;
import org.haiku.haikudepotserver.job.model.JobDataWithByteSink;
import org.haiku.haikudepotserver.job.model.JobRunnerException;
import org.haiku.haikudepotserver.pkg.model.PkgCategoryCoverageExportSpreadsheetJobSpecification;
import org.haiku.haikudepotserver.repository.model.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>This report is a spreadsheet that covers basic details of each package.</p>
 */

@Component
public class PkgCategoryCoverageExportSpreadsheetJobRunner extends AbstractPkgCategorySpreadsheetJobRunner<PkgCategoryCoverageExportSpreadsheetJobSpecification> {

    private static Logger LOGGER = LoggerFactory.getLogger(PkgCategoryCoverageExportSpreadsheetJobRunner.class);

    @Resource
    private RepositoryService repositoryService;

    @Override
    public void run(
            JobService jobService,
            PkgCategoryCoverageExportSpreadsheetJobSpecification specification)
            throws IOException, JobRunnerException {

        Preconditions.checkArgument(null!= jobService);
        Preconditions.checkArgument(null!=specification);

        final ObjectContext context = serverRuntime.getContext();

        // this will register the outbound data against the job.
        JobDataWithByteSink jobDataWithByteSink = jobService.storeGeneratedData(
                specification.getGuid(),
                "download",
                MediaType.CSV_UTF_8.toString());

        try(
                OutputStream outputStream = jobDataWithByteSink.getByteSink().openBufferedStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                CSVWriter writer = new CSVWriter(outputStreamWriter, ',')
        ) {

            // headers

            final List<String> pkgCategoryCodes = getPkgCategoryCodes();
            String[] headings = getHeadingRow(pkgCategoryCodes);

            long startMs = System.currentTimeMillis();

            writer.writeNext(headings);

            // stream out the packages.

            LOGGER.info("will produce category coverage spreadsheet report");

            long count = pkgService.eachPkg(
                    context,
                    false,
                    pkg -> {
                        List<String> cols = new ArrayList<>();
                        Optional<PkgVersionLocalization> locOptional = Optional.empty();

                        if(null!=pkg) {
                            locOptional = PkgVersionLocalization.getAnyPkgVersionLocalizationForPkg(context, pkg);
                        }

                        cols.add(pkg.getName());
                        cols.add(repositoryService.getRepositoriesForPkg(context, pkg)
                                .stream()
                                .map(Repository::getCode)
                                .collect(Collectors.joining(";")));
                        cols.add(locOptional.isPresent() ? locOptional.get().getSummary().orElse("") : "");
                        cols.add(pkg.getPkgPkgCategories().isEmpty() ? AbstractJobRunner.MARKER : "");

                        for (String pkgCategoryCode : pkgCategoryCodes) {
                            cols.add(pkg.getPkgPkgCategory(pkgCategoryCode).isPresent() ? AbstractJobRunner.MARKER : "");
                        }

                        cols.add(""); // no action
                        writer.writeNext(cols.toArray(new String[cols.size()]));
                        return true; // keep going!
                    }
            );

            LOGGER.info(
                    "did produce category coverage spreadsheet report for {} packages in {}ms",
                    count,
                    System.currentTimeMillis() - startMs);

        }

    }

}
