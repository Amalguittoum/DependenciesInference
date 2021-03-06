import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.spin.util.JenaUtil;

public class ShaclRuleExecution {
    private static Logger logger = LoggerFactory.getLogger(ShaclRuleExecution.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

    public static void main(String[] args) {
        try {
            Path path = Paths.get(".").toAbsolutePath().normalize();
            String data = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/DependenciesRule/data.ttl";
            String shape = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/DependenciesRule/ServiceRule.ttl";
            Model dataModel = JenaUtil.createDefaultModel();
            dataModel.read(data);
            Model shapeModel = JenaUtil.createDefaultModel();
            shapeModel.read(shape);
            Model inferenceModel = JenaUtil.createDefaultModel();
            inferenceModel = RuleUtil.executeRules(dataModel, shapeModel,
                    inferenceModel, null);
            System.out.println(ModelPrinter.get().print(inferenceModel));
            String inferences = path.toFile().getAbsolutePath() + "/src/main/resources/DependenciesRule/test_inferences.ttl";
            File inferencesFile = new File(inferences);
            inferencesFile.createNewFile();
            OutputStream reportOutputStream = new FileOutputStream(inferencesFile);
            RDFDataMgr.write(reportOutputStream, inferenceModel, RDFFormat.TTL);
        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}