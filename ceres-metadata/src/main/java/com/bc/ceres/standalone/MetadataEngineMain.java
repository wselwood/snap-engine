package com.bc.ceres.standalone;

import com.bc.ceres.metadata.DefaultSimpleFileSystem;
import com.bc.ceres.metadata.MetadataEngine;
import org.apache.velocity.VelocityContext;

import java.util.Arrays;
import java.util.Map;

/**
 * A default executable class for the metadata engine. It implements common usage of the MatadataEngine API.
 * <p/>
 * <b>usage: java -classpath path com.bc.ceres.standalone.MetadataEngineMain -t /path/targetItem.suff -v templateX=/path/metadata.vm.txt [-v templateY=/path/report.vm.xml] [optional options] [arg1] [arg2] ...</b>
 * <p/>
 * <table>
 * <tr>
 * <td>-m &lt;filePath&gt; </td>
 * <td>Optional. The absolute path and name of a text file to be included. E.g. global metadata. Refer to as $metadata in velocity templates.</td>
 * </tr>
 * <tr>
 * <td>-S &lt;source&gt;=&lt;filePath&gt; </td>
 * <td>Optional. The absolute path and name of the source items. Could be several given by key-value-pairs. In the velocity templates the key will give you the content of the associated metadata file. The reference $sourcePaths holds a list of the input item paths.</td>
 * </tr>
 * <tr>
 * <td>-t &lt;filePath&gt; </td>
 * <td>The absolute item path (e.g. a product), the metadata file will be places next to the item with the name 'itemName-templateName.templateSuffix. Refer to as $targetPath in velocity templates.</td>
 * </tr>
 * <tr>
 * <td>-v &lt;template&gt;=&lt;filePath&gt; </td>
 * <td>The absolute path of the velocity templates (*.vm). Could be several given by key-value-pairs.</td>
 * </tr>
 * </table>
 *
 * @author Bettina
 * @since Ceres 0.13.2
 */
public class MetadataEngineMain {
    public static final String KEY_METADATA = "metadata";
    public static final String KEY_SOURCES = "sourcePaths";
    public static final String KEY_TARGET = "targetPath";
    public static final String KEY_SYSTEM = "system";
    public static final String KEY_ARGS = "commandLineArgs";
    private MetadataEngine metadataEngine;
    private CliHandler cliHandler;

    public MetadataEngineMain(MetadataEngine metadataEngine) {
        this.metadataEngine = metadataEngine;
    }

    public static void main(String[] commandLineArgs) {
        MetadataEngineMain metadataEngineMain = null;
        try {
            metadataEngineMain = new MetadataEngineMain(new MetadataEngine(new DefaultSimpleFileSystem()));
            metadataEngineMain.setCliHandler(new CliHandler(commandLineArgs));
            if (commandLineArgs.length < 2) {
                metadataEngineMain.cliHandler.printUsage();
            } else {
                metadataEngineMain.processMetadata();
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            metadataEngineMain.cliHandler.printUsage();
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    void processMetadata() throws Exception {
        VelocityContext velocityContext = metadataEngine.getVelocityContext();
        String metadataPath = cliHandler.fetchGlobalMetadataFile();
        if (metadataPath != null) {
            metadataEngine.readMetadata(KEY_METADATA, metadataPath, false);
        }

        Map<String, String> sourcePaths = cliHandler.fetchSourceItemFiles();
        for (String key : sourcePaths.keySet()) {
            metadataEngine.readSourceMetadata(key, sourcePaths.get(key));
        }
        velocityContext.put(KEY_SOURCES, sourcePaths.values());

        velocityContext.put(KEY_SYSTEM, System.getProperties());
        velocityContext.put(KEY_ARGS, Arrays.asList(cliHandler.fetchArguments()));

        Map<String, String> templatePaths = cliHandler.fetchTemplateFiles();
        String outputItemPath = cliHandler.fetchTargetItemFile();
        velocityContext.put(KEY_TARGET, outputItemPath);
        for (String templateKey : templatePaths.keySet()) {
            metadataEngine.writeTargetMetadata(templatePaths.get(templateKey), outputItemPath);
        }
    }

    void setCliHandler(CliHandler cliHandler) { //only for tests
        this.cliHandler = cliHandler;
    }
}