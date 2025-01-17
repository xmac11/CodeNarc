/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.analyzer

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSet
import org.codenarc.source.SourceFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * SourceAnalyzer implementation that processes a list of source files
 *
 * @author Nicolas Vuillamy
 */
class FilesSourceAnalyzer extends AbstractSourceAnalyzer {

    private static final String SEP = '/'
    private static final Logger LOG = LoggerFactory.getLogger(FilesSourceAnalyzer)

    /**
     * The base (root) directory. If not set, user.dir will be used
     */
    String baseDirectory = System.getProperty('user.dir')

    /**
     * List of groovy files that will be analyzed.
     * Paths can be absolute, or relative to base directory
     */
    String[] sourceFiles

    /**
     * Whether to throw an exception if errors occur parsing source files (true), or just log the errors (false)
     */
    boolean failOnError = false

    /**
     * Analyze the source with the input list of files using the specified RuleSet and return the report results.
     * @param ruleset - the RuleSet to apply to each of the (applicable) files in the source directories
     * @return the results from applying the RuleSet to all of the files in the source directories
     */
    @SuppressWarnings(['CatchThrowable'])
    @Override
    Results analyze(RuleSet ruleSet) {
        assert baseDirectory
        assert ruleSet
        DirectoryResults baseDirResults = new DirectoryResults('')
        def baseDirectoryFile = new File(baseDirectory)

        // Get all results from unique named files
        for (def sourceFilePath in sourceFiles) {
            def file = new File(sourceFilePath)
            if (!file.exists()) {
                file = new File(baseDirectory + SEP + sourceFilePath)
                if (!file.exists()) {
                    LOG.error("Unable to find input file: $sourceFilePath")
                    throw new AnalyzerException("Unable to find input file: $sourceFilePath")
                }
            }
            try  {
                FileResults fileRes = processFile(file, baseDirectoryFile, ruleSet)
                baseDirResults.addFileResultRecursive(fileRes)
            }
            catch (Throwable t) {
                LOG.warn("Error processing file: '" + sourceFilePath + "'; " + t)
                if (failOnError) {
                    throw new AnalyzerException("Error analyzing source file: $sourceFilePath; $t")
                }
            }
        }

        // Convert file results into directory results
        def reportResults = new DirectoryResults()
        reportResults.addChild(baseDirResults)
        reportResults
    }

    @Override
    List getSourceDirectories() {
        [baseDirectory]
    }

    // Get violations for a single file
    private FileResults processFile(File file, File baseDirectoryFile, RuleSet ruleSet) {
        def sourceFile = new SourceFile(file)
        List allViolations = collectViolations(sourceFile, ruleSet)
        def fileRelativePath = baseDirectoryFile.toPath().relativize(file.toPath()).toString().replace('\\', SEP)
        def fileResults = new FileResults(fileRelativePath, allViolations, sourceFile)
        fileResults
    }
}
